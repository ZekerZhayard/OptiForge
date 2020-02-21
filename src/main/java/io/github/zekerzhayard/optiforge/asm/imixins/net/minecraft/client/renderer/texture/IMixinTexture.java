package io.github.zekerzhayard.optiforge.asm.imixins.net.minecraft.client.renderer.texture;

public interface IMixinTexture {
    void setBlurMipmap(boolean blur, boolean mipmap);
}
