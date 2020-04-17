package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.zekerzhayard.optiforge.asm.utils.RedirectSurrogate;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.ArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorLayer.class)
public abstract class MixinArmorLayer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> {
    @Shadow
    protected abstract ResourceLocation getArmorResource(ArmorItem armor, boolean legSlotIn, String suffixOverlayIn);

    @Shadow(remap = false)
    public abstract ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlotType slot, String type);

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/layers/ArmorLayer;renderArmorPart(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/entity/LivingEntity;FFFFFFLnet/minecraft/inventory/EquipmentSlotType;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/layers/ArmorLayer;renderModel(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/item/ArmorItem;ZLnet/minecraft/client/renderer/entity/model/BipedModel;ZFFFLjava/lang/String;)V"
        ),
        require = 3,
        allow = 3
    )
    private void redirect$renderArmorPart$0(ArmorLayer<T, M, A> armorLayer, MatrixStack _matrixStackIn, IRenderTypeBuffer _bufferIn, int _packedLightIn, ArmorItem armorItemIn, boolean glintIn, A modelIn, boolean legSlotIn, float red, float green, float blue, String overlayIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, EquipmentSlotType slotIn, int packedLightIn) {

    }

    @RedirectSurrogate(loacalVariableOrdinals = 0)
    private void redirect$renderArmorPart$0(ArmorLayer<T, M, A> armorLayer, MatrixStack _matrixStackIn, IRenderTypeBuffer _bufferIn, int _packedLightIn, ArmorItem armorItemIn, boolean glintIn, A modelIn, boolean legSlotIn, float red, float green, float blue, String overlayIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, EquipmentSlotType slotIn, int packedLightIn, ItemStack itemStack) {
        this.renderArmor(_matrixStackIn, _bufferIn, _packedLightIn, glintIn, modelIn, red, green, blue, this.getArmorResource(entityLivingBaseIn, itemStack, slotIn, overlayIn));
    }

    @Inject(
        method = "Lnet/minecraft/client/renderer/entity/layers/ArmorLayer;renderModel(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/item/ArmorItem;ZLnet/minecraft/client/renderer/entity/model/BipedModel;ZFFFLjava/lang/String;)V",
        at = @At("HEAD"),
        cancellable = true,
        require = 1,
        allow = 1
    )
    private void inject$renderModel$0(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, ArmorItem armorItemIn, boolean glintIn, A modelIn, boolean legSlotIn, float red, float green, float blue, String overlayIn, CallbackInfo ci) {
        this.renderArmor(matrixStackIn, bufferIn, packedLightIn, glintIn, modelIn, red, green, blue, this.getArmorResource(armorItemIn, legSlotIn, overlayIn));
        ci.cancel();
    }

    private void renderArmor(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, boolean glintIn, A modelIn, float red, float green, float blue, ResourceLocation armorResource) {
        IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(bufferIn, RenderType.getEntityCutoutNoCull(armorResource), false, glintIn);
        modelIn.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
    }
}
