package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.particle;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Particle.class)
public abstract class MixinParticle {
    public boolean shouldCull() {
        return true;
    }
}
