package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.gui;

import net.minecraft.client.gui.ResourceLoadProgressGui;
import net.minecraft.util.Util;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResourceLoadProgressGui.class)
public abstract class MixinResourceLoadProgressGui {
    @Shadow
    private long fadeOutStart;

    @Inject(
        method = "Lnet/minecraft/client/gui/ResourceLoadProgressGui;render(IIF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/resources/IAsyncReloader;join()V",
            shift = At.Shift.BEFORE
        ),
        require = 1,
        allow = 1
    )
    private void inject$render$0(CallbackInfo ci) {
        this.fadeOutStart = Util.milliTime();
    }

    @Redirect(
        method = "Lnet/minecraft/client/gui/ResourceLoadProgressGui;render(IIF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Util;milliTime()J",
            ordinal = 1
        ),
        require = 1,
        allow = 1
    )
    private long redirect$render$0() {
        return 0;
    }

    @Redirect(
        method = "Lnet/minecraft/client/gui/ResourceLoadProgressGui;render(IIF)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/ResourceLoadProgressGui;fadeOutStart:J",
            opcode = Opcodes.PUTFIELD
        ),
        require = 1,
        allow = 1
    )
    private void redirect$render$1(ResourceLoadProgressGui _this, long value) {

    }
}
