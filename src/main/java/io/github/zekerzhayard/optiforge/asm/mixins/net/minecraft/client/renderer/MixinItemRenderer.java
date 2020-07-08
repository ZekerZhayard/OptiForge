package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.zekerzhayard.optiforge.asm.utils.annotations.RedirectSurrogate;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@link io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.ItemRendererTransformer}
 */
@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
    @Inject(
        method = "Lnet/minecraft/client/renderer/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.BY,
            by = -2,
            target = "Lnet/minecraft/client/renderer/RenderTypeLookup;func_239219_a_(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/client/renderer/RenderType;"
        ),
        require = 1,
        allow = 1
    )
    private void inject$renderItem$0(ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, boolean leftHand, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, IBakedModel modelIn, CallbackInfo ci) {

    }

    @RedirectSurrogate(loacalVariableOrdinals = 1)
    private boolean inject$renderItem$0(ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, boolean leftHand, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, IBakedModel modelIn, CallbackInfo ci, boolean flag1) {
        if (modelIn.isLayered()) {
            ForgeHooksClient.drawItemLayered((ItemRenderer) (Object) this, modelIn, itemStackIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, flag1);
            return true;
        }
        return false;
    }
}
