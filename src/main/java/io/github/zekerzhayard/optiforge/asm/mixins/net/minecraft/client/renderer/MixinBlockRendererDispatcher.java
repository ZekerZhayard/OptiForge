package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockRendererDispatcher.class)
public abstract class MixinBlockRendererDispatcher {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;<init>(Lnet/minecraft/client/renderer/BlockModelShapes;Lnet/minecraft/client/renderer/color/BlockColors;)V",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/client/renderer/color/BlockColors;)Lnet/minecraft/client/renderer/BlockModelRenderer;"
        ),
        require = 1,
        allow = 1
    )
    private BlockModelRenderer redirect$_init_$0(BlockColors blockColorsIn) {
        return new ForgeBlockModelRenderer(blockColorsIn);
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;renderBlock(Lnet/minecraft/block/BlockState;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraftforge/client/model/data/IModelData;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/tileentity/ItemStackTileEntityRenderer;render(Lnet/minecraft/item/ItemStack;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;II)V"
        ),
        require = 1,
        allow = 1
    )
    private void redirect$renderBlock$0(ItemStackTileEntityRenderer instance, ItemStack itemStackIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        itemStackIn.getItem().getItemStackTileEntityRenderer().render(itemStackIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }
}
