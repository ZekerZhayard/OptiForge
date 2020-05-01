package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ActiveRenderInfo.class)
public abstract class MixinActiveRenderInfo {
    @Shadow
    private float pitch;

    @Shadow
    private float yaw;

    @Inject(
        method = "Lnet/minecraft/client/renderer/ActiveRenderInfo;update(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/entity/Entity;ZZF)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/platform/GlStateManager;rotatef(FFFF)V",
            ordinal = 0
        ),
        require = 1,
        allow = 1
    )
    private void inject$update$0(IBlockReader worldIn, Entity renderViewEntity, boolean thirdPersonIn, boolean thirdPersonReverseIn, float partialTicks, CallbackInfo ci) {
        EntityViewRenderEvent.CameraSetup cameraSetup = ForgeHooksClient.onCameraSetup(Minecraft.getInstance().gameRenderer, (ActiveRenderInfo) (Object) this, partialTicks, this.yaw, this.pitch, 0f);
        this.pitch = cameraSetup.getPitch();
        this.yaw = cameraSetup.getYaw();
        GlStateManager.rotatef(cameraSetup.getRoll(), 0, 0, 1);
    }
}
