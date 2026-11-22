package com.emoniph.witchery.updater;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.Side;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class WitcheryAutoUpdater {

    private static final String GITHUB_REPO = "brianjcc2-stack/Witchery";
    private static final String GITHUB_BRANCH = "witchery-expansion";
    private static final String JAR_FILENAME = "witchery-1.7.10-0.24.1.jar";
    private static final String SHA_FILENAME = "witchery.sha256";

    // Two sources: GitHub Releases (latest) and Raw branch fallback
    private static final String RELEASE_SHA_URL = "https://github.com/" + GITHUB_REPO + "/releases/latest/download/" + SHA_FILENAME;
    private static final String RELEASE_JAR_URL = "https://github.com/" + GITHUB_REPO + "/releases/latest/download/" + JAR_FILENAME;

    private static final String RAW_SHA_URL = "https://raw.githubusercontent.com/" + GITHUB_REPO + "/" + GITHUB_BRANCH + "/release/" + SHA_FILENAME;
    private static final String RAW_JAR_URL = "https://raw.githubusercontent.com/" + GITHUB_REPO + "/" + GITHUB_BRANCH + "/release/" + JAR_FILENAME;

    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 15000;
    private static final long MIN_VALID_JAR_BYTES = 500 * 1024L; // 500 KB minimum sanity check

    private static class RemoteReleaseInfo {
        final String sha256;
        final long timestamp;
        final String jarUrl;
        final String sourceName;

        RemoteReleaseInfo(String sha256, long timestamp, String jarUrl, String sourceName) {
            this.sha256 = sha256;
            this.timestamp = timestamp;
            this.jarUrl = jarUrl;
            this.sourceName = sourceName;
        }
    }

    public static void checkAndUpdate(File currentJar, File mcDir) {
        if (!isAutoUpdateEnabled()) {
            logInfo("Auto-actualizacion desactivada por parametro del sistema.");
            return;
        }

        // Ensure TLSv1.2 is enabled on older Java 8 runtimes
        try {
            System.setProperty("https.protocols", "TLSv1.2");
        } catch (Throwable ignored) {
        }

        cleanupLeftoverTempFiles(currentJar.getParentFile());

        String localSha256 = computeSha256(currentJar);
        if (localSha256 == null) {
            logWarn("No se pudo calcular el SHA-256 del JAR actual.");
            return;
        }
        long localTimestamp = readBuildTimestamp(currentJar);

        logInfo("Verificando actualizaciones de Witchery (JAR local: " + shortHash(localSha256) + ", build: " + localTimestamp + ")...");

        RemoteReleaseInfo remote = resolveLatestRemoteInfo();
        if (remote == null) {
            logInfo("No se encontro informacion de actualizacion remota (o sin conexion). Continuando arranque normal.");
            return;
        }

        if (localSha256.equalsIgnoreCase(remote.sha256)) {
            logInfo("Witchery esta al dia (" + shortHash(localSha256) + " via " + remote.sourceName + ").");
            return;
        }

        if (localTimestamp > 0L && remote.timestamp > 0L && localTimestamp > remote.timestamp) {
            logInfo("El JAR local (" + localTimestamp + ") es mas reciente que el publicado en GitHub (" + remote.timestamp + "). Omitiendo actualizacion.");
            return;
        }

        logInfo("¡Nueva version detectada en " + remote.sourceName + "! (Remoto: " + shortHash(remote.sha256) + ", Local: " + shortHash(localSha256) + ")");
        logInfo("Descargando actualizacion desde: " + remote.jarUrl);

        File tempJar = new File(currentJar.getParentFile(), currentJar.getName() + ".update.tmp");
        if (!downloadFile(remote.jarUrl, tempJar)) {
            logWarn("Fallo la descarga de la actualizacion. Se continuara con la version actual.");
            safeDelete(tempJar);
            return;
        }

        if (tempJar.length() < MIN_VALID_JAR_BYTES) {
            logWarn("El archivo descargado es demasiado pequeno (" + tempJar.length() + " bytes). Cancelando actualizacion.");
            safeDelete(tempJar);
            return;
        }

        String downloadedSha256 = computeSha256(tempJar);
        if (downloadedSha256 == null || !downloadedSha256.equalsIgnoreCase(remote.sha256)) {
            logWarn("El hash SHA-256 del archivo descargado (" + downloadedSha256 + ") no coincide con el esperado (" + remote.sha256 + "). Cancelando actualizacion.");
            safeDelete(tempJar);
            return;
        }

        logInfo("Descarga completada y verificada correctamente (SHA-256: " + shortHash(downloadedSha256) + "). Aplicando actualizacion...");
        applyUpdateAndExit(currentJar, tempJar);
    }

    private static boolean isAutoUpdateEnabled() {
        String prop = System.getProperty("witchery.autoupdate", "true");
        if ("false".equalsIgnoreCase(prop) || "0".equals(prop)) {
            return false;
        }
        String env = System.getenv("WITCHERY_AUTOUPDATE");
        if (env != null && ("false".equalsIgnoreCase(env) || "0".equals(env))) {
            return false;
        }
        return true;
    }

    private static void cleanupLeftoverTempFiles(File modsDir) {
        if (modsDir == null || !modsDir.isDirectory()) {
            return;
        }
        File[] files = modsDir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isFile() && f.getName().endsWith(".update.tmp")) {
                safeDelete(f);
            }
        }
    }

    private static RemoteReleaseInfo resolveLatestRemoteInfo() {
        RemoteReleaseInfo releaseInfo = fetchRemoteInfo(RELEASE_SHA_URL, RELEASE_JAR_URL, "GitHub Releases");
        RemoteReleaseInfo rawInfo = fetchRemoteInfo(RAW_SHA_URL, RAW_JAR_URL, "GitHub Branch");

        if (releaseInfo != null && rawInfo != null) {
            if (rawInfo.timestamp > releaseInfo.timestamp) {
                return rawInfo;
            }
            return releaseInfo;
        }
        return releaseInfo != null ? releaseInfo : rawInfo;
    }

    private static RemoteReleaseInfo fetchRemoteInfo(String shaUrl, String jarUrl, String sourceName) {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            String cacheBustedUrl = shaUrl + "?t=" + System.currentTimeMillis();
            conn = openConnectionWithRedirects(cacheBustedUrl, CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS);
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
            String line = reader.readLine();
            if (line == null) {
                return null;
            }
            line = line.trim();
            if (line.isEmpty()) {
                return null;
            }
            String[] parts = line.split("\\s+");
            String sha = parts[0].trim().toLowerCase(Locale.ROOT);
            if (!isValidSha256Hex(sha)) {
                return null;
            }
            long timestamp = 0L;
            if (parts.length >= 2) {
                try {
                    timestamp = Long.parseLong(parts[1].trim());
                } catch (NumberFormatException ignored) {
                }
            }
            return new RemoteReleaseInfo(sha, timestamp, jarUrl, sourceName);
        } catch (Throwable ignored) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static boolean downloadFile(String urlStr, File destination) {
        HttpURLConnection conn = null;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            String cacheBustedUrl = urlStr + "?t=" + System.currentTimeMillis();
            conn = openConnectionWithRedirects(cacheBustedUrl, CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS);
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                logWarn("Respuesta HTTP " + status + " al descargar " + urlStr);
                return false;
            }
            in = conn.getInputStream();
            out = new FileOutputStream(destination);
            byte[] buffer = new byte[32 * 1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
            return true;
        } catch (Throwable t) {
            logWarn("Error descargando actualizacion: " + t.getMessage());
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static HttpURLConnection openConnectionWithRedirects(String urlStr, int connectTimeout, int readTimeout) throws IOException {
        String currentUrl = urlStr;
        for (int redirects = 0; redirects < 6; redirects++) {
            URL url = new URL(currentUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", "Witchery-AutoUpdater/1.7.10");
            conn.setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
            conn.setRequestProperty("Pragma", "no-cache");

            int status = conn.getResponseCode();
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER
                    || status == 307
                    || status == 308) {
                String location = conn.getHeaderField("Location");
                conn.disconnect();
                if (location == null || location.isEmpty()) {
                    throw new IOException("Redirect without Location header");
                }
                currentUrl = new URL(url, location).toString();
                continue;
            }
            return conn;
        }
        throw new IOException("Too many HTTP redirects");
    }

    private static void applyUpdateAndExit(File currentJar, File tempJar) {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        boolean isWindows = osName.contains("win");
        boolean isMac = osName.contains("mac");
        boolean isClient = true;
        try {
            Side side = FMLLaunchHandler.side();
            isClient = (side == null || side.isClient());
        } catch (Throwable ignored) {
        }

        if (isWindows) {
            spawnWindowsUpdaterScript(tempJar, currentJar, isClient);
        } else {
            // On macOS / Linux, POSIX allows atomic replacement of an open file directly
            boolean replacedDirectly = false;
            try {
                Files.move(tempJar.toPath(), currentJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
                replacedDirectly = true;
            } catch (Throwable t) {
                logWarn("No se pudo reemplazar directamente el JAR, usando script auxiliar: " + t.getMessage());
            }
            spawnPosixUpdaterScript(tempJar, currentJar, isClient, isMac, replacedDirectly);
        }

        System.out.println("======================================================================");
        System.out.println("[Witchery AutoUpdater] ¡ACTUALIZACION DE WITCHERY INSTALADA CON EXITO!");
        if (isClient) {
            System.out.println("[Witchery AutoUpdater] El juego se cerrara para aplicar la nueva version.");
            System.out.println("[Witchery AutoUpdater] Por favor, vuelve a darle a 'Jugar' en tu Launcher.");
        } else {
            System.out.println("[Witchery AutoUpdater] El servidor se cerrara para cargar el nuevo JAR.");
        }
        System.out.println("======================================================================");

        try {
            FMLCommonHandler.instance().exitJava(0, true);
        } catch (Throwable ignored) {
        }
        Runtime.getRuntime().halt(0);
    }

    private static void spawnWindowsUpdaterScript(File tempJar, File currentJar, boolean isClient) {
        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir", "."));
            File batFile = new File(tempDir, "witchery_updater_" + System.currentTimeMillis() + ".bat");

            StringBuilder sb = new StringBuilder();
            sb.append("@echo off\r\n");
            sb.append("setlocal\r\n");
            sb.append("set \"SRC=").append(tempJar.getAbsolutePath()).append("\"\r\n");
            sb.append("set \"DST=").append(currentJar.getAbsolutePath()).append("\"\r\n");
            sb.append("for /L %%i in (1,1,25) do (\r\n");
            sb.append("    ping 127.0.0.1 -n 2 > nul\r\n");
            sb.append("    move /Y \"%SRC%\" \"%DST%\" > nul 2>&1\r\n");
            sb.append("    if not exist \"%SRC%\" goto :updated\r\n");
            sb.append(")\r\n");
            sb.append(":updated\r\n");
            if (isClient) {
                sb.append("start \"\" mshta \"javascript:var sh=new ActiveXObject('WScript.Shell'); sh.Popup('Witchery se ha actualizado automaticamente a una nueva version.\\n\\nPor favor, vuelve a darle a Jugar en tu Launcher.', 20, 'Witchery Auto-Updater', 64); close();\"\r\n");
            }
            sb.append("del \"%~f0\" > nul 2>&1\r\n");

            FileOutputStream fos = new FileOutputStream(batFile);
            try {
                fos.write(sb.toString().getBytes(Charset.forName("UTF-8")));
            } finally {
                fos.close();
            }

            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start", "\"\"", "/b", batFile.getAbsolutePath());
            pb.start();
        } catch (Throwable t) {
            logWarn("No se pudo iniciar el script de actualizacion en Windows: " + t.getMessage());
        }
    }

    private static void spawnPosixUpdaterScript(File tempJar, File currentJar, boolean isClient, boolean isMac, boolean alreadyReplaced) {
        try {
            String src = escapeSingleQuotes(tempJar.getAbsolutePath());
            String dst = escapeSingleQuotes(currentJar.getAbsolutePath());
            StringBuilder cmd = new StringBuilder();
            cmd.append("sleep 1; ");
            if (!alreadyReplaced) {
                cmd.append("mv -f '").append(src).append("' '").append(dst).append("'; ");
            } else {
                cmd.append("if [ -f '").append(src).append("' ]; then mv -f '").append(src).append("' '").append(dst).append("'; fi; ");
            }
            if (isClient) {
                if (isMac) {
                    cmd.append("osascript -e 'display dialog \"¡Witchery se ha actualizado automáticamente a una nueva versión!\\n\\nPor favor, vuelve a darle a Jugar en tu Launcher.\" with title \"Witchery Auto-Updater\" buttons {\"Aceptar\"} default button 1' >/dev/null 2>&1 &");
                } else {
                    cmd.append("(zenity --info --title='Witchery Auto-Updater' --text='¡Witchery se ha actualizado automáticamente a una nueva versión!\\n\\nPor favor, vuelve a darle a Jugar en tu Launcher.' || notify-send 'Witchery Auto-Updater' 'Witchery se ha actualizado. Vuelve a darle a Jugar.') >/dev/null 2>&1 &");
                }
            }
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", cmd.toString());
            pb.start();
        } catch (Throwable t) {
            logWarn("No se pudo iniciar el comando auxiliar POSIX: " + t.getMessage());
        }
    }

    private static String escapeSingleQuotes(String s) {
        return s.replace("'", "'\"'\"'");
    }

    private static long readBuildTimestamp(File jarFile) {
        JarFile jar = null;
        try {
            jar = new JarFile(jarFile);
            Manifest manifest = jar.getManifest();
            if (manifest == null) {
                return 0L;
            }
            Attributes attrs = manifest.getMainAttributes();
            String val = attrs.getValue("Witchery-Build-Timestamp");
            if (val != null && !val.trim().isEmpty()) {
                return Long.parseLong(val.trim());
            }
        } catch (Throwable ignored) {
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException ignored) {
                }
            }
        }
        return 0L;
    }

    private static String computeSha256(File file) {
        FileInputStream fis = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[32 * 1024];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
            byte[] hashBytes = digest.digest();
            StringBuilder hex = new StringBuilder(hashBytes.length * 2);
            for (byte b : hashBytes) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) {
                    hex.append('0');
                }
                hex.append(h);
            }
            return hex.toString();
        } catch (Throwable t) {
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static boolean isValidSha256Hex(String s) {
        if (s == null || s.length() != 64) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            boolean isHex = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
            if (!isHex) {
                return false;
            }
        }
        return true;
    }

    private static String shortHash(String hash) {
        if (hash == null || hash.length() < 12) {
            return String.valueOf(hash);
        }
        return hash.substring(0, 12);
    }

    private static void safeDelete(File f) {
        try {
            if (f != null && f.exists()) {
                f.delete();
            }
        } catch (Throwable ignored) {
        }
    }

    private static void logInfo(String msg) {
        try {
            FMLRelaunchLog.info("[Witchery AutoUpdater] %s", msg);
        } catch (Throwable t) {
            System.out.println("[Witchery AutoUpdater] " + msg);
        }
    }

    private static void logWarn(String msg) {
        try {
            FMLRelaunchLog.warning("[Witchery AutoUpdater] %s", msg);
        } catch (Throwable t) {
            System.err.println("[Witchery AutoUpdater] WARN: " + msg);
        }
    }
}
