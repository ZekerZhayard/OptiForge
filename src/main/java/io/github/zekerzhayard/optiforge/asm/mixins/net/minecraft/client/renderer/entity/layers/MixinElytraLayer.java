package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.zekerzhayard.optiforge.asm.utils.annotations.RedirectSurrogate;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ElytraLayer.class)
public abstract class MixinElytraLayer<T extends LivingEntity> {
    @Final
    @Shadow
    private static ResourceLocation TEXTURE_ELYTRA;

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/layers/ElytraLayer;render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"
        ),
        require = 1,
        allow = 1
    )
    private Item redirect$render$0(ItemStack itemStack, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        return this.shouldRender(itemStack, entitylivingbaseIn) ? Items.ELYTRA : null;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/layers/ElytraLayer;render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/entity/layers/ElytraLayer;TEXTURE_ELYTRA:Lnet/minecraft/util/ResourceLocation;"
        ),
        require = 2,
        allow = 2
    )
    private ResourceLocation redirect$render$1(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        return null;
    }

    @RedirectSurrogate(loacalVariableOrdinals = 0)
    private ResourceLocation redirect$render$1(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack itemStack) {
        return this.getElytraTexture(itemStack, entitylivingbaseIn);
    }

    public boolean shouldRender(ItemStack stack, T entity) {
        return stack.getItem() == Items.ELYTRA;
    }

    public ResourceLocation getElytraTexture(ItemStack stack, T entity) {
        return TEXTURE_ELYTRA;
    }
}
