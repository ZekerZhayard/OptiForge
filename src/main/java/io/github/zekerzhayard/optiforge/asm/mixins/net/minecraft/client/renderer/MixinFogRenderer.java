package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public abstract class MixinFogRenderer {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/FogRenderer;updateFogColor(Lnet/minecraft/client/renderer/ActiveRenderInfo;FLnet/minecraft/client/world/ClientWorld;IF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorConstructor;exists()Z",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private static boolean redirect$updateFogColor$0(@Coerce Object constructor) {
        return true;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/FogRenderer;updateFogColor(Lnet/minecraft/client/renderer/ActiveRenderInfo;FLnet/minecraft/client/world/ClientWorld;IF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;newInstance(Lnet/optifine/reflect/ReflectorConstructor;[Ljava/lang/Object;)Ljava/lang/Object;",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private static Object redirect$updateFogColor$1(@Coerce Object constructor, Object[] params) {
        return new EntityViewRenderEvent.FogColors((ActiveRenderInfo) params[0], (float) params[1], (float) params[2], (float) params[3], (float) params[4]);
    }
}
