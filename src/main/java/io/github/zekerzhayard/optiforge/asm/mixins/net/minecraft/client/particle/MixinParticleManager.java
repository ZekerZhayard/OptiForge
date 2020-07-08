package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.ParticleManager;
import org.lwjgl.opengl.GL13;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public abstract class MixinParticleManager {
    @Inject(
        method = "Lnet/minecraft/client/particle/ParticleManager;lambda$renderParticles$9", // ()V
        at = @At("RETURN"),
        require = 1,
        allow = 1
    )
    private static void inject$lambda$renderParticles$9$0(CallbackInfo ci) {
        RenderSystem.activeTexture(GL13.GL_TEXTURE2);
        RenderSystem.enableTexture();
        RenderSystem.activeTexture(GL13.GL_TEXTURE0);
    }
}
