package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.zekerzhayard.optiforge.asm.imixins.net.minecraft.client.renderer.IMixinActiveRenderInfo;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/GameRenderer;renderWorld(FJLcom/mojang/blaze3d/matrix/MatrixStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;update(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/entity/Entity;ZZF)V"
        ),
        require = 1,
        allow = 1
    )
    private void redirect$renderWorld$0(ActiveRenderInfo activeRenderInfo, IBlockReader worldIn, Entity renderViewEntity, boolean thirdPersonIn, boolean thirdPersonReverseIn, float _partialTicks, float partialTicks, long finishTimeNano, MatrixStack matrixStackIn) {
        activeRenderInfo.update(worldIn, renderViewEntity, thirdPersonIn, thirdPersonReverseIn, _partialTicks);
        EntityViewRenderEvent.CameraSetup cameraSetup = ForgeHooksClient.onCameraSetup((GameRenderer) (Object) this, activeRenderInfo, partialTicks);
        ((IMixinActiveRenderInfo) activeRenderInfo).setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(cameraSetup.getRoll()));
    }
}
