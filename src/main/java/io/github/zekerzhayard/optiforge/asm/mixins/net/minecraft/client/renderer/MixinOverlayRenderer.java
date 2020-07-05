package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.zekerzhayard.optiforge.asm.utils.annotations.RedirectSurrogate;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.OverlayRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * {@link io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.OverlayRendererTransformer}
 */
@Mixin(OverlayRenderer.class)
public abstract class MixinOverlayRenderer {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/OverlayRenderer;renderOverlays(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/matrix/MatrixStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/BlockModelShapes;getTexture(Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;",
            ordinal = 0
        ),
        require = 1,
        allow = 1
    )
    private static TextureAtlasSprite redirect$renderOverlays$0(BlockModelShapes blockModelShapes, BlockState state, Minecraft minecraftIn, MatrixStack matrixStackIn) {
        return null;
    }

    @RedirectSurrogate(loacalVariableOrdinals = 0)
    private static TextureAtlasSprite redirect$renderOverlays$0(BlockModelShapes blockModelShapes, BlockState state, Minecraft minecraftIn, MatrixStack matrixStackIn, Pair<BlockState, BlockPos> overlay) {
        return blockModelShapes.getTexture(state, minecraftIn.world, overlay.getRight());
    }
}
