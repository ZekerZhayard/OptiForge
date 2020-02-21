package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import net.minecraft.client.renderer.RenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RenderState.LineState.class)
public abstract class MixinRenderState$LineState {
    @ModifyArg(
        method = "Lnet/minecraft/client/renderer/RenderState$LineState;<init>(Ljava/util/OptionalDouble;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderState;<init>(Ljava/lang/String;Ljava/lang/Runnable;Ljava/lang/Runnable;)V"
        ),
        require = 1,
        allow = 1
    )
    private static String modifyArg$_init_$0(String nameIn) {
        return "line_width";
    }
}
