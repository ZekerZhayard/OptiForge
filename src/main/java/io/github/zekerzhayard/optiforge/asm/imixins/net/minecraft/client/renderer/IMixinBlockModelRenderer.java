package io.github.zekerzhayard.optiforge.asm.imixins.net.minecraft.client.renderer;

// These methods are all added by OptiFine, and OptiForge will be invoked.
public interface IMixinBlockModelRenderer {
    static boolean isSeparateAoLightValue() {
        return false;
    }
}
