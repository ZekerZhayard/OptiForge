package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.texture;

import io.github.zekerzhayard.optiforge.asm.utils.annotations.RedirectSurrogate;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextureAtlasSprite.class)
public abstract class MixinTextureAtlasSprite {
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
}
