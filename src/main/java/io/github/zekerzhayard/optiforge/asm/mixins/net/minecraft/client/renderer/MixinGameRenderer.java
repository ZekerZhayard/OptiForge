package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.zekerzhayard.optiforge.asm.imixins.net.minecraft.client.renderer.IMixinActiveRenderInfo;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    private ConcurrentMap<Thread, Matrix4f> optiforge_matrix4fMap = new MapMaker().initialCapacity(1).concurrencyLevel(1).weakKeys().weakValues().makeMap();

    @ModifyVariable(
        method = "Lnet/minecraft/client/renderer/GameRenderer;renderWorld(FJLcom/mojang/blaze3d/matrix/MatrixStack;)V",
        at = @At("STORE"),
        ordinal = 0,
        require = 1,
        allow = 1
    )
    private Matrix4f modifyVariable$renderWorld$0(Matrix4f matrix4f) {
        this.optiforge_matrix4fMap.put(Thread.currentThread(), matrix4f);
        return matrix4f;
    }

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
        ForgeHooksClient.dispatchRenderLast((WorldRenderer) params[0], (MatrixStack) params[1], partialTicks, this.optiforge_matrix4fMap.get(Thread.currentThread()), (long) params[2]);
    }
}
