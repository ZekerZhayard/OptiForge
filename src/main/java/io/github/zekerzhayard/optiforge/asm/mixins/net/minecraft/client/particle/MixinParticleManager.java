package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.zekerzhayard.optiforge.asm.utils.annotations.DynamicShadow;
import io.github.zekerzhayard.optiforge.asm.utils.annotations.LazyOverwrite;
import io.github.zekerzhayard.optiforge.asm.utils.annotations.RedirectSurrogate;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.ClippingHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@link io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.particle.ParticleManagerTransformer}
 */
@Mixin(ParticleManager.class)
public abstract class MixinParticleManager {
    @Inject(
        method = "Lnet/minecraft/client/particle/ParticleManager;renderParticles(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;F)V",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.BY,
            by = 3,
            target = "Ljava/util/Iterator;next()Ljava/lang/Object;",
            ordinal = 1,
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private void inject$renderParticles$0(CallbackInfo ci) {

    }

    @RedirectSurrogate(
        loacalVariableOrdinals = {
            0,
            0
        }
    )
    private boolean inject$renderParticles$0(CallbackInfo ci, ClippingHelper clippingHelper, Particle particle) {
        return clippingHelper != null && particle.shouldCull() && !clippingHelper.isBoundingBoxInFrustum(particle.getBoundingBox());
    }

    @LazyOverwrite(prefix = "optiforge_")
    public void optiforge_func_228345_a_(MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, LightTexture lightTextureIn, ActiveRenderInfo activeRenderInfoIn, float partialTicks) {
        this.renderParticles(matrixStackIn, bufferIn, lightTextureIn, activeRenderInfoIn, partialTicks, null);
    }

    @DynamicShadow
    public void renderParticles(MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, LightTexture lightTextureIn, ActiveRenderInfo activeRenderInfoIn, float partialTicks, ClippingHelper clippingHelper) {

    }
}
