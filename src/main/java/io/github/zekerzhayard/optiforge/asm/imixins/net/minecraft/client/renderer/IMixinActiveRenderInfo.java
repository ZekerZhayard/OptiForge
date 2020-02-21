package io.github.zekerzhayard.optiforge.asm.imixins.net.minecraft.client.renderer;

public interface IMixinActiveRenderInfo {
    void setAnglesInternal(float yaw, float pitch);
}
