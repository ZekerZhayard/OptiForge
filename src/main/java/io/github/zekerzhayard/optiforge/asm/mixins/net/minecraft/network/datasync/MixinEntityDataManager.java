package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.network.datasync;

import net.minecraft.network.datasync.EntityDataManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityDataManager.class)
public abstract class MixinEntityDataManager {
    @Redirect(
        method = "Lnet/minecraft/network/datasync/EntityDataManager;createKey(Ljava/lang/Class;Lnet/minecraft/network/datasync/IDataSerializer;)Lnet/minecraft/network/datasync/DataParameter;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/apache/logging/log4j/Logger;isDebugEnabled()Z",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private static boolean redirect$createKey$0(Logger logger) {
        return true;
    }

    @Redirect(
        method = "Lnet/minecraft/network/datasync/EntityDataManager;createKey(Ljava/lang/Class;Lnet/minecraft/network/datasync/IDataSerializer;)Lnet/minecraft/network/datasync/DataParameter;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/apache/logging/log4j/Logger;debug(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private static void redirect$createKey$1(Logger logger, String message, Object p0, Object p1, Object p2) {
        if (logger.isDebugEnabled()) {
            logger.warn(message, p0, p1, p2);
        } else {
            logger.warn(message, p0, p1);
        }
    }
}
