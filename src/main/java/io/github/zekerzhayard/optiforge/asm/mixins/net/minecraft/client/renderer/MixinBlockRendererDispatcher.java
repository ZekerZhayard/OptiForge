package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockRendererDispatcher.class)
public abstract class MixinBlockRendererDispatcher {
    @Shadow(remap = false)
    @SuppressWarnings("target")
    public abstract void renderModel(BlockState blockStateIn, BlockPos posIn, IBlockDisplayReader lightReaderIn, MatrixStack matrixStackIn, IVertexBuilder vertexBuilderIn, IModelData modelData);

    @Unique
    public void renderBlockDamage(BlockState blockStateIn, BlockPos posIn, IBlockDisplayReader lightReaderIn, MatrixStack matrixStackIn, IVertexBuilder vertexBuilderIn, IModelData modelData) {
        this.renderModel(blockStateIn, posIn, lightReaderIn, matrixStackIn, vertexBuilderIn, modelData);
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;renderBlock(Lnet/minecraft/block/BlockState;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraftforge/client/model/data/IModelData;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/tileentity/ItemStackTileEntityRenderer;func_239207_a_(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;II)V"
        ),
        require = 1,
        allow = 1
    )
    private void redirect$renderBlock$0(ItemStackTileEntityRenderer instance, ItemStack itemStackIn, ItemCameraTransforms.TransformType transformIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        itemStackIn.getItem().getItemStackTileEntityRenderer().func_239207_a_(itemStackIn, transformIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }
}
