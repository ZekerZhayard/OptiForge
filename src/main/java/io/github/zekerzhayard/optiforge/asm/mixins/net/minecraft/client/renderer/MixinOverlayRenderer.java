package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.zekerzhayard.optiforge.asm.utils.RedirectSurrogate;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.OverlayRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.event.ForgeEventFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OverlayRenderer.class)
public abstract class MixinOverlayRenderer {
    @Shadow
    private static void renderTexture(Minecraft minecraftIn, TextureAtlasSprite spriteIn, MatrixStack matrixStackIn) {

    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/OverlayRenderer;renderOverlays(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/matrix/MatrixStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/OverlayRenderer;getViewBlockingState(Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;"
        ),
        require = 1,
        allow = 1
    )
    private static BlockState redirect$renderOverlays$0(PlayerEntity playerEntity, Minecraft minecraftIn, MatrixStack matrixStackIn) {
        Pair<BlockState, BlockPos> overlay = MixinOverlayRenderer.getOverlayBlock(playerEntity);
        if (overlay != null && !ForgeEventFactory.renderBlockOverlay(playerEntity, matrixStackIn, RenderBlockOverlayEvent.OverlayType.BLOCK, overlay.getLeft(), overlay.getRight())) {
            MixinOverlayRenderer.renderTexture(minecraftIn, minecraftIn.getBlockRendererDispatcher().getBlockModelShapes().getTexture(overlay.getLeft(), minecraftIn.world, overlay.getRight()), matrixStackIn);
        }
        return null;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/OverlayRenderer;renderOverlays(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/matrix/MatrixStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;areEyesInFluid(Lnet/minecraft/tags/Tag;)Z"
        ),
        require = 1,
        allow = 1
    )
    private static boolean redirect$renderOverlays$1(ClientPlayerEntity player, Tag<Fluid> tagIn, Minecraft minecraftIn, MatrixStack matrixStackIn) {
        return false;
    }

    @RedirectSurrogate(loacalVariableOrdinals = 0)
    private static boolean redirect$renderOverlays$1(ClientPlayerEntity player, Tag<Fluid> tagIn, Minecraft minecraftIn, MatrixStack matrixStackIn, PlayerEntity playerEntity) {
        return player.areEyesInFluid(tagIn) && !ForgeEventFactory.renderWaterOverlay(playerEntity, matrixStackIn);
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/OverlayRenderer;renderOverlays(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/matrix/MatrixStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;isBurning()Z"
        ),
        require = 1,
        allow = 1
    )
    private static boolean redirect$renderOverlays$2(ClientPlayerEntity player, Minecraft minecraftIn, MatrixStack matrixStackIn) {
        return false;
    }

    @RedirectSurrogate(loacalVariableOrdinals = 0)
    private static boolean redirect$renderOverlays$2(ClientPlayerEntity player, Minecraft minecraftIn, MatrixStack matrixStackIn, PlayerEntity playerEntity) {
        return player.isBurning() && !ForgeEventFactory.renderFireOverlay(playerEntity, matrixStackIn);
    }

    private static BlockState optiforge_getViewBlockingState(PlayerEntity playerIn) {
        return MixinOverlayRenderer.getOverlayBlock(playerIn).getLeft();
    }

    private static Pair<BlockState, BlockPos> getOverlayBlock(PlayerEntity playerIn) {
        return null;
    }
}
