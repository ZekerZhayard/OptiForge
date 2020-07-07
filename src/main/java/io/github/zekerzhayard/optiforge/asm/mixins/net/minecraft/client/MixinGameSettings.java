package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client;

import net.minecraft.client.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameSettings.class)
public abstract class MixinGameSettings {
    @Redirect(
        method = "Lnet/minecraft/client/GameSettings;setForgeKeybindProperties()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;call(Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Ljava/lang/Object;",
            ordinal = 13
        ),
        remap = false,
        require = 1,
        allow = 1
    )
    private Object redirect$setForgeKeybindProperties$0(Object obj, @Coerce Object method, Object[] params) {
        return null;
    }
}
