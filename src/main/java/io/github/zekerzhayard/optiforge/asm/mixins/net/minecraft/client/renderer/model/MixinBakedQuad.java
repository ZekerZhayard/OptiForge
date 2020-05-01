package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.model;

import io.github.zekerzhayard.optiforge.asm.imixins.net.minecraft.client.renderer.model.IMixinBakedQuad;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BakedQuad.class)
public abstract class MixinBakedQuad implements IMixinBakedQuad {
    @Shadow
    protected TextureAtlasSprite sprite;

    public TextureAtlasSprite func_187508_a() {
        return this.sprite;
    }
}
