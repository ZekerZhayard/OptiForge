package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.texture;

import io.github.zekerzhayard.optiforge.asm.imixins.net.minecraft.client.renderer.texture.IMixinTexture;
import net.minecraft.client.renderer.texture.Texture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Texture.class)
public abstract class MixinTexture implements IMixinTexture {
    @Shadow
    protected boolean blur;

    @Shadow
    protected boolean mipmap;

    private boolean lastBlur;
    private boolean lastMipmap;

    @Shadow
    public abstract void setBlurMipmapDirect(boolean blurIn, boolean mipmapIn);

    @Inject(
        method = "Lnet/minecraft/client/renderer/texture/Texture;restoreLastBlurMipmap()V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1,
        allow = 1
    )
    private void inject$restoreLastBlurMipmap$0(CallbackInfo ci) {
        this.setBlurMipmapDirect(this.lastBlur, this.lastMipmap);
        ci.cancel();
    }

    @Override
    public void setBlurMipmap(boolean blur, boolean mipmap) {
        this.lastBlur = this.blur;
        this.lastMipmap = this.mipmap;
        this.setBlurMipmapDirect(blur, mipmap);
    }
}
