package io.github.zekerzhayard.optiforge.asm.imixins.net.minecraft.client.renderer.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

// These methods are all added by OptiFine and OptiForge will be invoked.
public interface IMixinBakedQuad {
    TextureAtlasSprite getSprite();

    int[] getVertexDataSingle();
}
