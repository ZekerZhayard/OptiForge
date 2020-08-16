package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingRenderer.class)
public abstract class MixinLivingRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow
    protected M entityModel;

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
}
