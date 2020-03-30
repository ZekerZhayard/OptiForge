package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client;

import net.minecraft.client.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameSettings.class)
public abstract class MixinGameSettings {
    @Redirect(
        method = "Lnet/minecraft/client/GameSettings;fillResourcePackList(Lnet/minecraft/resources/ResourcePackList;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorMethod;exists()Z",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$fillResourcePackList$0(@Coerce Object method) {
        return false;
    }
}
