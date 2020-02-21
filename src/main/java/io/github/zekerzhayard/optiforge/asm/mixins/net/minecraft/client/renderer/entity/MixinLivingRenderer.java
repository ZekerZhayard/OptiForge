package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingRenderer.class)
public abstract class MixinLivingRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow
    public M entityModel;

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/LivingRenderer;render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorConstructor;exists()Z",
            ordinal = 0,
            remap = false
        ),
        require = 1,
        allow = 1
    )
    @SuppressWarnings("unchecked")
    private boolean redirect$render$0(@Coerce Object constructor, T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        return MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre<>(entityIn, (LivingRenderer<T, M>) (Object) this, partialTicks, matrixStackIn, bufferIn, packedLightIn));
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/LivingRenderer;render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;postForgeBusEvent(Lnet/optifine/reflect/ReflectorConstructor;[Ljava/lang/Object;)Z",
            ordinal = 0,
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$render$1(@Coerce Object constructor, Object[] params) {
        return true;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/LivingRenderer;render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;isPassenger()Z",
            ordinal = 2
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$render$2(LivingEntity entityIn) {
        return this.entityModel.isSitting;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/LivingRenderer;render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorConstructor;exists()Z",
            ordinal = 1,
            remap = false
        ),
        require = 1,
        allow = 1
    )
    @SuppressWarnings("unchecked")
    private boolean redirect$render$3(@Coerce Object constructor, T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post<>(entityIn, (LivingRenderer<T, M>) (Object) this, partialTicks, matrixStackIn, bufferIn, packedLightIn));
        return false;
    }
}
