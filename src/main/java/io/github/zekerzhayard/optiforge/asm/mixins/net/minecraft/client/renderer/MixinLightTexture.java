package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightTexture.class)
public abstract class MixinLightTexture {
    @Inject(
        method = "Lnet/minecraft/client/renderer/LightTexture;getLightBlock(I)I",
        at = @At("HEAD"),
        cancellable = true,
        require = 1,
        allow = 1
    )
    private static void inject$getLightBlock$0(int packedLightIn, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((packedLightIn & 0xFFFF) >> 4);
    }
}
