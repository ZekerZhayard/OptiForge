package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.model;

import io.github.zekerzhayard.optiforge.asm.imixins.net.minecraft.client.renderer.model.IMixinBakedQuad;
import net.minecraft.client.renderer.model.BakedQuad;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BakedQuad.class)
public abstract class MixinBakedQuad implements IMixinBakedQuad {

}
