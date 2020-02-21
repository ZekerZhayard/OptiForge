package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.texture;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextureAtlasSprite.class)
public abstract class MixinTextureAtlasSprite {
    @Final
    @Shadow
    protected NativeImage[] frames;

    @Final
    @Shadow
    private int[] framesX;

    @Final
    @Shadow
    private int[] framesY;

    @Shadow
    public abstract int getWidth();

    @Shadow
    public abstract int getHeight();

    public int getPixelRGBA(int frameIndex, int x, int y) {
        return this.frames[0].getPixelRGBA(x + this.framesX[frameIndex] * this.getWidth(), y + this.framesY[frameIndex] * this.getHeight());
    }
}
