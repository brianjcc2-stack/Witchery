package com.emoniph.witchery.updater;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.io.File;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.Name("WitcheryAutoUpdater")
@IFMLLoadingPlugin.SortingIndex(1001)
public class WitcheryCorePlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        try {
            Boolean deobf = (Boolean) data.get("runtimeDeobfuscationEnabled");
            File coremodLocation = (File) data.get("coremodLocation");
            File mcLocation = (File) data.get("mcLocation");

            // Only run auto-updater in a real production JAR (not in a dev directory environment)
            if (deobf != null && deobf.booleanValue() && coremodLocation != null && coremodLocation.isFile()) {
                WitcheryAutoUpdater.checkAndUpdate(coremodLocation, mcLocation);
            }
        } catch (Throwable t) {
            System.err.println("[Witchery AutoUpdater] Error inesperado durante la verificacion de actualizacion: " + t.getMessage());
            t.printStackTrace();
        }
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
