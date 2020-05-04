
package io.github.zekerzhayard.optiforge.asm.imixins.com.mojang.blaze3d.vertex;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

// These methods are all added by OptiFine, and OptiForge will be invoked.
public interface IMixinIVertexBuilder {
    boolean isMultiTexture();

    boolean isSeparateAoInAlpha();

    void putSprite(TextureAtlasSprite sprite);
}
