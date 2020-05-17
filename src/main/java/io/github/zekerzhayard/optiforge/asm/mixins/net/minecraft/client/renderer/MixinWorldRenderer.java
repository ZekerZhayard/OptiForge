package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import io.github.zekerzhayard.optiforge.asm.utils.annotations.DynamicShadow;
import io.github.zekerzhayard.optiforge.asm.utils.annotations.LazyOverwrite;
import io.github.zekerzhayard.optiforge.asm.utils.annotations.RedirectSurrogate;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * {@link io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.WorldRendererTransformer}
 */
@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/WorldRenderer;setupTerrain(Lnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/client/renderer/culling/ClippingHelperImpl;ZIZ)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$ChunkRender;needsImmediateUpdate()Z"
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$setupTerrain$0(ChunkRenderDispatcher.ChunkRender render) {
        return !ForgeConfig.CLIENT.alwaysSetupTerrainOffThread.get() && render.needsImmediateUpdate();
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/WorldRenderer;updateCameraAndRender(Lcom/mojang/blaze3d/matrix/MatrixStack;FJZLnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/Matrix4f;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/Texture;setBlurMipmapDirect(ZZ)V"
        ),
        require = 1,
        allow = 1
    )
    private void redirect$updateCameraAndRender$0(Texture texture, boolean blurIn, boolean mipmapIn) {
        texture.setBlurMipmap(blurIn, mipmapIn);
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/WorldRenderer;playRecord(Lnet/minecraft/util/SoundEvent;Lnet/minecraft/util/math/BlockPos;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/MusicDiscItem;getBySound(Lnet/minecraft/util/SoundEvent;)Lnet/minecraft/item/MusicDiscItem;"
        ),
        require = 1,
        allow = 1
    )
    private MusicDiscItem redirect$playRecord$0(SoundEvent _soundIn) {
        return null;
    }

    @RedirectSurrogate(loacalVariableOrdinals = 0)
    private MusicDiscItem redirect$playRecord$0(SoundEvent _soundIn, MusicDiscItem musicDiscItem) {
        return musicDiscItem;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/WorldRenderer;playEvent(Lnet/minecraft/entity/player/PlayerEntity;ILnet/minecraft/util/math/BlockPos;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/WorldRenderer;playRecord(Lnet/minecraft/util/SoundEvent;Lnet/minecraft/util/math/BlockPos;)V",
            ordinal = 0
        ),
        require = 1,
        allow = 1
    )
    private void redirect$playEvent$0(WorldRenderer worldRenderer, SoundEvent soundIn, BlockPos pos, PlayerEntity player, int type, BlockPos blockPosIn, int data) {
        this.playRecord(soundIn, pos, (MusicDiscItem) Item.getItemById(data));
    }

    // playRecord
    @LazyOverwrite(prefix = "optiforge_")
    public void optiforge_func_184377_a(SoundEvent soundIn, BlockPos pos) {
        this.playRecord(soundIn, pos, soundIn == null ? null : MusicDiscItem.getBySound(soundIn));
    }

    @DynamicShadow
    public abstract void playRecord(SoundEvent soundIn, BlockPos pos, MusicDiscItem musicDiscItem);
}
