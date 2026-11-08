package com.emoniph.witchery.common;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Random;

/**
 * WeeklyQuestEngine: Generador Semanal Inteligente de Grandes Misiones.
 * Genera actividades narrativas que rotan semanalmente de forma procedural,
 * y permite opcionalmente inyectar misiones generadas por IA (Gemini, GPT, etc.)
 * a través de un archivo JSON en la carpeta config (config/witchery_weekly.json).
 */
public class WeeklyQuestEngine {

    private static class WeeklyData {
        int weekNumber = -1;
        String title = "";
        String lore = "";
        String objective = "";
        int masteryBonus = 75;
    }

    private static WeeklyData currentWeekly = null;

    public static int getCurrentWeekNumber() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.WEEK_OF_YEAR);
    }

    public static synchronized WeeklyData getWeeklyQuest() {
        int currentWeek = getCurrentWeekNumber();
        if (currentWeekly != null && currentWeekly.weekNumber == currentWeek) {
            return currentWeekly;
        }

        WeeklyData data = loadFromFile(currentWeek);
        if (data == null) {
            data = generateProceduralWeekly(currentWeek);
        }
        currentWeekly = data;
        return currentWeekly;
    }

    private static WeeklyData generateProceduralWeekly(int weekNumber) {
        WeeklyData d = new WeeklyData();
        d.weekNumber = weekNumber;

        Random rand = new Random((long) weekNumber * 314159L);

        String[] prefixes = new String[]{
                "El Gran Decreto de", "La Profecía Oculta de", "El Despertar de", "El Juicio de",
                "La Convergencia de", "El Pacto de Sangre de", "La Rebelión de"
        };
        String[] subjects = new String[]{
                "la Luna de Sangre", "las Sombras de Baba Yaga", "los Aldeanos Olvidados",
                "los Espíritus del Velo", "la Noche Eterna", "los Cazadores Caídos",
                "la Niebla de Lilith", "el Aquelarre Esmeralda"
        };

        d.title = prefixes[rand.nextInt(prefixes.length)] + " " + subjects[rand.nextInt(subjects.length)];

        String[] lores = new String[]{
                "Las brujas ancestrales han visto presagios en el humo de la salvia. La energía del plano espiritual se desborda y las aldeas humanas están al borde del colapso...",
                "Una sombra antigua recorre las fronteras del mundo. El aquelarre debe unir sus fuerzas para contener a las bestias y preservar los secretos de la magia...",
                "Lilith y los espíritus reclaman un gran tributo. Durante esta semana, las corrientes mágicas conceden doble resonancia a los círculos de poder...",
                "Los inquisidores de los Witch Hunters han jurado destruir todo caldero en pie. Debemos mostrar el verdadero poder de la Bruja Suprema..."
        };
        d.lore = lores[rand.nextInt(lores.length)];

        String[] objectives = new String[]{
                "Completa 3 tareas del aquelarre, realiza un Rito de la Tormenta y protege a una aldea de MCA purificando a sus habitantes.",
                "Somete a 2 espíritus errantes en el Reino Onírico y entrega un Corazón de Demonio a tu bruja del aquelarre.",
                "Destila 3 Lágrimas de la Diosa bajo la luz de la medianoche y fortalece tu Altar hasta superar 5,000 de energía.",
                "Recluta a una nueva hermana para tu aquelarre o mantén el título de Bruja Suprema frente a tus rivales."
        };
        d.objective = objectives[rand.nextInt(objectives.length)];
        d.masteryBonus = 80 + rand.nextInt(40);

        return d;
    }

    private static WeeklyData loadFromFile(int weekNumber) {
        try {
            File configFile = new File("config/witchery_weekly.json");
            if (!configFile.exists()) {
                return null;
            }
            // If exists, read simple key values
            java.io.BufferedReader reader = new java.io.BufferedReader(new FileReader(configFile));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            String content = sb.toString();
            WeeklyData d = new WeeklyData();
            d.weekNumber = weekNumber;
            d.title = extractJsonString(content, "title", "Decreto de la IA del Aquelarre");
            d.lore = extractJsonString(content, "lore", "Una nueva semana mística comienza.");
            d.objective = extractJsonString(content, "objective", "Cumple los mandatos del aquelarre.");
            d.masteryBonus = 100;
            return d;
        } catch (Throwable t) {
            return null;
        }
    }

    private static String extractJsonString(String json, String key, String def) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return def;
        int start = json.indexOf("\"", idx + search.length());
        if (start == -1) return def;
        int end = json.indexOf("\"", start + 1);
        if (end == -1) return def;
        return json.substring(start + 1, end);
    }

    public static void displayWeekly(EntityPlayer player, String witchName) {
        if (player == null) return;
        WeeklyData d = getWeeklyQuest();

        SoundEffect.NOTE_HARP.playAtPlayer(player.worldObj, player, 0.8F, 0.9F);
        player.addChatMessage(new ChatComponentText("§6§m====================================================="));
        player.addChatMessage(new ChatComponentText("§6§l🌙 [GRAN DECRETO SEMANAL DEL AQUELARRE]"));
        player.addChatMessage(new ChatComponentText("§eTítulo: §f§l" + d.title));
        player.addChatMessage(new ChatComponentText("§7§o\"" + d.lore + "\""));
        player.addChatMessage(new ChatComponentText("§aObjetivo Semanal: §d" + d.objective));
        player.addChatMessage(new ChatComponentText("§5★ Recompensa de Gloria: §6+" + d.masteryBonus + " pts de Maestría Suprema + Favor del Aquelarre"));
        player.addChatMessage(new ChatComponentText("§6§m====================================================="));
    }
}
