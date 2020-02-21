package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import java.util.concurrent.ConcurrentHashMap;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockModelRenderer.class)
public abstract class MixinBlockModelRenderer {
//    private ConcurrentHashMap<Thread, float[]> optiforge_floatArrayMap = new ConcurrentHashMap<>();

    @Redirect(
        method = "Lnet/minecraft/client/renderer/BlockModelRenderer;renderModel(Lnet/minecraft/world/ILightReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorForge;getLightValue(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/ILightReader;Lnet/minecraft/util/math/BlockPos;)I"
        ),
        remap = false,
        require = 1,
        allow = 1
    )
    private int redirect$renderModel$0(BlockState stateIn, ILightReader worldIn, BlockPos posIn) {
        return stateIn.getLightValue(worldIn, posIn);
    }
/*
    @Inject(
        method = "Lnet/minecraft/client/renderer/BlockModelRenderer;renderQuadSmooth(Lnet/minecraft/world/ILightReader;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lcom/mojang/blaze3d/matrix/MatrixStack$Entry;Lnet/minecraft/client/renderer/model/BakedQuad;FFFFIIIIILnet/optifine/render/RenderEnv;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/IVertexBuilder;getTempFloat4(FFFF)[F"
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false,
        require = 1,
        allow = 1
    )
    private void inject$renderQuadSmooth$0(ILightReader blockAccessIn, BlockState stateIn, BlockPos posIn, IVertexBuilder buffer, MatrixStack.Entry matrixEntry, BakedQuad quadIn, float colorMul0, float colorMul1, float colorMul2, float colorMul3, int brightness0, int brightness1, int brightness2, int brightness3, int combinedOverlayIn, @Coerce Object renderEnv, CallbackInfo ci, float f, float f1, float f2) {
        if (quadIn.shouldApplyDiffuseLighting()) {
            float l = LightUtil.diffuseLight(quadIn.getFace());
            f *= l;
            f1 *= l;
            f2 *= l;
        }
        this.optiforge_floatArrayMap.put(Thread.currentThread(), new float[] { f, f1, f2 });
    }

    @ModifyVariable(
        method = "Lnet/minecraft/client/renderer/BlockModelRenderer;renderQuadSmooth(Lnet/minecraft/world/ILightReader;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lcom/mojang/blaze3d/matrix/MatrixStack$Entry;Lnet/minecraft/client/renderer/model/BakedQuad;FFFFIIIIILnet/optifine/render/RenderEnv;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/IVertexBuilder;getTempFloat4(FFFF)[F"
        ),
        ordinal = 4,
        remap = false,
        require = 1,
        allow = 1
    )
    private float modifyVariable$renderQuadSmooth$0(float f) {
        return this.optiforge_floatArrayMap.get(Thread.currentThread())[0];
    }

    @ModifyVariable(
        method = "Lnet/minecraft/client/renderer/BlockModelRenderer;renderQuadSmooth(Lnet/minecraft/world/ILightReader;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lcom/mojang/blaze3d/matrix/MatrixStack$Entry;Lnet/minecraft/client/renderer/model/BakedQuad;FFFFIIIIILnet/optifine/render/RenderEnv;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/IVertexBuilder;getTempFloat4(FFFF)[F"
        ),
        ordinal = 5,
        remap = false,
        require = 1,
        allow = 1
    )
    private float modifyVariable$renderQuadSmooth$1(float f1) {
        return this.optiforge_floatArrayMap.get(Thread.currentThread())[1];
    }

    @ModifyVariable(
        method = "Lnet/minecraft/client/renderer/BlockModelRenderer;renderQuadSmooth(Lnet/minecraft/world/ILightReader;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lcom/mojang/blaze3d/matrix/MatrixStack$Entry;Lnet/minecraft/client/renderer/model/BakedQuad;FFFFIIIIILnet/optifine/render/RenderEnv;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/IVertexBuilder;getTempFloat4(FFFF)[F"
        ),
        ordinal = 6,
        remap = false,
        require = 1,
        allow = 1
    )
    private float modifyVariable$renderQuadSmooth$2(float f2) {
        return this.optiforge_floatArrayMap.remove(Thread.currentThread())[2];
    }
*/
}
