package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.texture;

import io.github.zekerzhayard.optiforge.asm.utils.annotations.RedirectSurrogate;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextureAtlasSprite.class)
public abstract class MixinTextureAtlasSprite {
    @Shadow
    protected NativeImage[] frames;

    @Shadow
    protected int[] framesX;

    @Shadow
    protected int[] framesY;

    @Shadow
    public abstract int getColor(int frameIndex, int levelIn, int xIn, int yIn);

    @ModifyVariable(
        method = "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;generateMipmapsUnchecked(I)V",
        at = @At(
            value = "LOAD",
            ordinal = 3
        ),
        name = "nativeimage",
        require = 1,
        allow = 1
    )
    private NativeImage modifyVariable$generateMipmapsUnchecked$0(NativeImage nativeImage) {
        return null;
    }

    @RedirectSurrogate(
        loacalVariableOrdinals = {
            0,
            3,
            4
        }
    )
    private NativeImage modifyVariable$generateMipmapsUnchecked$0(NativeImage nativeImage, NativeImage nativeImage1, int k, int l) {
        if (k > 0 && l > 0) {
            return nativeImage;
        } else {
            return nativeImage1;
        }
    }

    @Inject(
        method = "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;getColor(IIII)I",
        at = @At("HEAD"),
        cancellable = true,
        require = 1,
        allow = 1
    )
    private void inject$getColor$0(int frameIndex, int levelIn, int xIn, int yIn, CallbackInfoReturnable<Integer> cir) {
        if (this.framesX == null || this.framesY == null) {
            cir.setReturnValue(this.frames[levelIn].getPixelRGBA(xIn, yIn));
        }
    }

    @Inject(
        method = "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;isPixelTransparent(III)Z",
        at = @At("HEAD"),
        cancellable = true,
        require = 1,
        allow = 1
    )
    private void inject$isPixelTransparent$0(int frameIndex, int pixelX, int pixelY, CallbackInfoReturnable<Boolean> cir) {
        if (this.framesX == null || this.framesY == null) {
            cir.setReturnValue((this.frames[0].getPixelRGBA(pixelX, pixelY) >> 24 & 255) == 0);
        }
    }

    @Inject(
        method = "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;getPixelRGBA(III)I",
        at = @At("HEAD"),
        remap = false,
        cancellable = true,
        require = 1,
        allow = 1
    )
    private void inject$getPixelRGBA$0(int frameIndex, int x, int y, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.getColor(frameIndex, 0, x, y));
    }
}
