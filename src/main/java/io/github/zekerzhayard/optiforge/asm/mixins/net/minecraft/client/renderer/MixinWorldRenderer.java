package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.zekerzhayard.optiforge.asm.imixins.net.minecraft.client.renderer.texture.IMixinTexture;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
    @Final
    @Shadow
    private Minecraft mc;

    @Shadow
    private ClientWorld world;

    private ConcurrentMap<Thread, IRenderTypeBuffer.Impl> optiforge_iRenderTypeBuffer$ImplMap = new MapMaker().initialCapacity(1).concurrencyLevel(1).weakKeys().weakValues().makeMap();
    private ConcurrentMap<Thread, BlockPos> optiforge_blockPosMap = new MapMaker().initialCapacity(1).concurrencyLevel(1).weakKeys().weakValues().makeMap();

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
        ((IMixinTexture) texture).setBlurMipmap(blurIn, mipmapIn);
    }

    @ModifyVariable(
        method = "Lnet/minecraft/client/renderer/WorldRenderer;updateCameraAndRender(Lcom/mojang/blaze3d/matrix/MatrixStack;FJZLnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/Matrix4f;)V",
        at = @At("STORE"),
        ordinal = 0,
        require = 1,
        allow = 1
    )
    private IRenderTypeBuffer.Impl modifyVariable$updateCameraAndRender$0(IRenderTypeBuffer.Impl bufferSource) {
        this.optiforge_iRenderTypeBuffer$ImplMap.put(Thread.currentThread(), bufferSource);
        return bufferSource;
    }

    @ModifyVariable(
        method = "Lnet/minecraft/client/renderer/WorldRenderer;updateCameraAndRender(Lcom/mojang/blaze3d/matrix/MatrixStack;FJZLnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/Matrix4f;)V",
        at = @At(
            value = "STORE",
            ordinal = 3
        ),
        ordinal = 0,
        require = 1,
        allow = 1
    )
    private BlockPos modifyVariable$updateCameraAndRender$1(BlockPos blockPos) {
        this.optiforge_blockPosMap.put(Thread.currentThread(), blockPos);
        return blockPos;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/WorldRenderer;updateCameraAndRender(Lcom/mojang/blaze3d/matrix/MatrixStack;FJZLnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/Matrix4f;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;isAir()Z"
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$updateCameraAndRender$1(BlockState blockState, MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn) {
        return ForgeHooksClient.onDrawBlockHighlight((WorldRenderer) (Object) this, activeRenderInfoIn, this.mc.objectMouseOver, partialTicks, matrixStackIn, this.optiforge_iRenderTypeBuffer$ImplMap.get(Thread.currentThread())) || blockState.isAir(this.world, this.optiforge_blockPosMap.get(Thread.currentThread()));
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/WorldRenderer;updateCameraAndRender(Lcom/mojang/blaze3d/matrix/MatrixStack;FJZLnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/Matrix4f;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorMethod;exists()Z",
            ordinal = 1,
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$updateCameraAndRender$2(@Coerce Object method) {
        return false;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/WorldRenderer;getPackedLightmapCoords(Lnet/minecraft/world/ILightReader;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;getLightValue()I"
        ),
        require = 1,
        allow = 1
    )
    private static int redirect$getPackedLightmapCoords$0(BlockState _blockStateIn, ILightReader lightReaderIn, BlockState blockStateIn, BlockPos blockPosIn) {
        return _blockStateIn.getLightValue(lightReaderIn, blockPosIn);
    }
}
