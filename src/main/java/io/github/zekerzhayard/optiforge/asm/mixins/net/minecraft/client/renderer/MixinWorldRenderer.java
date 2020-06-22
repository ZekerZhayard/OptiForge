package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraftforge.common.ForgeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
}
