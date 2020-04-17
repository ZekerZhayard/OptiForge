package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.zekerzhayard.optiforge.asm.imixins.net.minecraft.client.renderer.IMixinActiveRenderInfo;
import io.github.zekerzhayard.optiforge.asm.utils.RedirectSurrogate;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
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

    @Redirect(
        method = "Lnet/minecraft/client/renderer/GameRenderer;renderWorld(FJLcom/mojang/blaze3d/matrix/MatrixStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorMethod;exists()Z",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$renderWorld$1(@Coerce Object method) {
        return true;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/GameRenderer;renderWorld(FJLcom/mojang/blaze3d/matrix/MatrixStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;callVoid(Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)V",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private void redirect$renderWorld$2(@Coerce Object method, Object[] params, float partialTicks, long finishTimeNano, MatrixStack matrixStackIn) {

    }

    @RedirectSurrogate(loacalVariableOrdinals = 0)
    private void redirect$renderWorld$2(Object method, Object[] params, float partialTicks, long finishTimeNano, MatrixStack matrixStackIn, Matrix4f matrix4f) {
        ForgeHooksClient.dispatchRenderLast((WorldRenderer) params[0], (MatrixStack) params[1], partialTicks, matrix4f, (long) params[2]);
    }
}
