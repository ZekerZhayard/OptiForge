package io.github.zekerzhayard.optiforge.asm;

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class ModCompatibility {
    private static boolean obfuscate = false;

    static {
        for (ModInfo mi : FMLLoader.getLoadingModList().getMods()) {
            if (mi.getModId().equals("obfuscate")) {
                obfuscate = true;
            }
        }
    }

    public static boolean obfuscate() {
        return obfuscate;
    }
}
