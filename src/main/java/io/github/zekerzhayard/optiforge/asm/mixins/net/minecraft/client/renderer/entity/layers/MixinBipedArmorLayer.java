package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BipedArmorLayer.class)
public abstract class MixinBipedArmorLayer<T extends LivingEntity, A extends BipedModel<T>> {
    @Shadow(remap = false)
    @SuppressWarnings("target")
    protected abstract void renderModel(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, boolean hasEffect, A bipedModelIn, float red, float green, float blue, ResourceLocation armorResource);

    @Unique
    private void func_241738_a_(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, boolean hasEffect, A bipedModelIn, float red, float green, float blue, ResourceLocation armorResource) {
        this.renderModel(matrixStackIn, bufferIn, packedLightIn, hasEffect, bipedModelIn, red, green, blue, armorResource);
    }
}
