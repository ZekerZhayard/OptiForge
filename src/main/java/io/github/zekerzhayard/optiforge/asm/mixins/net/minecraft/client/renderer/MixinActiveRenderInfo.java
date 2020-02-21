package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import io.github.zekerzhayard.optiforge.asm.imixins.net.minecraft.client.renderer.IMixinActiveRenderInfo;
import net.minecraft.client.renderer.ActiveRenderInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ActiveRenderInfo.class)
public abstract class MixinActiveRenderInfo implements IMixinActiveRenderInfo {
    @Shadow
    private float pitch;

    @Shadow
    private float yaw;

    @Override
    public void setAnglesInternal(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
