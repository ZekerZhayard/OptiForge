package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityRendererDispatcher.class)
public abstract class MixinTileEntityRendererDispatcher {
    @Inject(
        method = "Lnet/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher;getRenderer(Lnet/minecraft/tileentity/TileEntity;)Lnet/minecraft/client/renderer/tileentity/TileEntityRenderer;",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Object;getClass()Ljava/lang/Class;",
            remap = false
        ),
        cancellable = true,
        require = 1,
        allow = 1
    )
    private <T extends TileEntity> void inject$getRenderer$0(TileEntity tileEntityIn, CallbackInfoReturnable<TileEntityRenderer<T>> cir) {
        if (tileEntityIn.isRemoved()) {
            cir.setReturnValue(null);
        }
    }
}
