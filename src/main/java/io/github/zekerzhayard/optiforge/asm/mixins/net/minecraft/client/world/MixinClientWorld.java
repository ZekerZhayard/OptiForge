package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.world;

import java.util.function.Supplier;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World {
    private MixinClientWorld(ClientWorld.ClientWorldInfo worldInfoIn, RegistryKey<World> keyWorld, RegistryKey<DimensionType> keyDimension, DimensionType dimType, Supplier<IProfiler> profilerIn, boolean isRemote, boolean debugIn, long seedIn) {
        super(worldInfoIn, keyWorld, keyDimension, dimType, profilerIn, isRemote, debugIn, seedIn);
    }

    @Redirect(
        method = "Lnet/minecraft/client/world/ClientWorld;<init>(Lnet/minecraft/client/network/play/ClientPlayNetHandler;Lnet/minecraft/client/world/ClientWorld$ClientWorldInfo;Lnet/minecraft/util/RegistryKey;Lnet/minecraft/util/RegistryKey;Lnet/minecraft/world/DimensionType;ILjava/util/function/Supplier;Lnet/minecraft/client/renderer/WorldRenderer;ZJ)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorMethod;exists()Z",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$_init_$0(@Coerce Object method) {
        return true;
    }

    @Redirect(
        method = "Lnet/minecraft/client/world/ClientWorld;<init>(Lnet/minecraft/client/network/play/ClientPlayNetHandler;Lnet/minecraft/client/world/ClientWorld$ClientWorldInfo;Lnet/minecraft/util/RegistryKey;Lnet/minecraft/util/RegistryKey;Lnet/minecraft/world/DimensionType;ILjava/util/function/Supplier;Lnet/minecraft/client/renderer/WorldRenderer;ZJ)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;call(Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Ljava/lang/Object;",
            ordinal = 0,
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private Object redirect$_init_$1(Object obj, @Coerce Object method, Object[] params) {
        return null;
    }

    @Redirect(
        method = "Lnet/minecraft/client/world/ClientWorld;<init>(Lnet/minecraft/client/network/play/ClientPlayNetHandler;Lnet/minecraft/client/world/ClientWorld$ClientWorldInfo;Lnet/minecraft/util/RegistryKey;Lnet/minecraft/util/RegistryKey;Lnet/minecraft/world/DimensionType;ILjava/util/function/Supplier;Lnet/minecraft/client/renderer/WorldRenderer;ZJ)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;call(Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Ljava/lang/Object;",
            ordinal = 1,
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private Object redirect$_init_$2(Object obj, @Coerce Object method, Object[] params) {
        this.gatherCapabilities();
        return null;
    }
}
