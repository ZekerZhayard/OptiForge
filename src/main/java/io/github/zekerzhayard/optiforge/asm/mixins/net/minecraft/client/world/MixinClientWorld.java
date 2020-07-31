package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.world;

import java.util.function.Supplier;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World {
    private MixinClientWorld(ClientWorld.ClientWorldInfo worldInfoIn, RegistryKey<World> keyWorld, RegistryKey<DimensionType> keyDimension, DimensionType dimType, Supplier<IProfiler> profilerIn, boolean isRemote, boolean debugIn, long seedIn) {
        super(worldInfoIn, keyWorld, keyDimension, dimType, profilerIn, isRemote, debugIn, seedIn);
    }

    @Inject(
        method = "Lnet/minecraft/client/world/ClientWorld;removeEntity(Lnet/minecraft/entity/Entity;)V",
        at = @At("TAIL"),
        require = 1,
        allow = 1
    )
    private void inject$removeEntity$0(Entity entityIn, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new EntityLeaveWorldEvent(entityIn, this));
    }
}
