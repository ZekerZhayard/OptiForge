package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/model/IBakedModel;getItemCameraTransforms()Lnet/minecraft/client/renderer/model/ItemCameraTransforms;"
        ),
        require = 1,
        allow = 1
    )
    @SuppressWarnings("deprecation")
    private net.minecraft.client.renderer.model.ItemCameraTransforms redirect$renderItem$0(IBakedModel iBakedModel) {
        return null;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/model/ItemCameraTransforms;getTransform(Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;)Lnet/minecraft/client/renderer/model/ItemTransformVec3f;"
        ),
        require = 1,
        allow = 1
    )
    @SuppressWarnings("deprecation")
    private net.minecraft.client.renderer.model.ItemTransformVec3f redirect$renderItem$1(net.minecraft.client.renderer.model.ItemCameraTransforms itemCameraTransforms, net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType type) {
        return null;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/model/ItemTransformVec3f;apply(ZLcom/mojang/blaze3d/matrix/MatrixStack;)V"
        ),
        require = 1,
        allow = 1
    )
    @SuppressWarnings("deprecation")
    private void redirect$renderItem$2(net.minecraft.client.renderer.model.ItemTransformVec3f transform, boolean leftHand, MatrixStack matrixStackIn) {

    }

    @ModifyVariable(
        method = "Lnet/minecraft/client/renderer/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/matrix/MatrixStack;translate(DDD)V"
        ),
        ordinal = 0,
        require = 1,
        allow = 1
    )
    @SuppressWarnings("deprecation")
    private IBakedModel modifyVariable$renderItem$0(IBakedModel _modelIn, ItemStack itemStackIn, net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType transformTypeIn, boolean leftHand, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, IBakedModel modelIn) {
        return ForgeHooksClient.handleCameraTransforms(matrixStackIn, _modelIn, transformTypeIn, leftHand);
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorMethod;exists()Z",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$renderItem$3(@Coerce Object method) {
        return true;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;call(Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Ljava/lang/Object;",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private Object redirect$renderItem$4(Object obj, @Coerce Object method, Object[] params) {
        return ((Item) obj).getItemStackTileEntityRenderer();
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/ItemRenderer;renderQuads(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;Ljava/util/List;Lnet/minecraft/item/ItemStack;II)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/IVertexBuilder;addQuad(Lcom/mojang/blaze3d/matrix/MatrixStack$Entry;Lnet/minecraft/client/renderer/model/BakedQuad;FFFII)V"
        ),
        require = 1,
        allow = 1
    )
    private void redirect$renderQuads$0(IVertexBuilder bufferIn, MatrixStack.Entry matrixEntryIn, BakedQuad quadIn, float redIn, float greenIn, float blueIn, int combinedLightIn, int combinedOverlayIn) {
        bufferIn.addVertexData(matrixEntryIn, quadIn, redIn, greenIn, blueIn, combinedLightIn, combinedOverlayIn, true);
    }
}
