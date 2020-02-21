package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.world;

import java.util.Collection;
import java.util.function.BiFunction;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.profiler.IProfiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World {
    private MixinClientWorld(WorldInfo info, DimensionType dimType, BiFunction<World, Dimension, AbstractChunkProvider> provider, IProfiler profilerIn, boolean remote) {
        super(info, dimType, provider, profilerIn, remote);
    }

    @Redirect(
        method = "Lnet/minecraft/client/world/ClientWorld;onChunkUnloaded(Lnet/minecraft/world/chunk/Chunk;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorField;exists()Z",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$onChunkUnloaded$0(@Coerce Object field) {
        return true;
    }

    @Redirect(
        method = "Lnet/minecraft/client/world/ClientWorld;onChunkUnloaded(Lnet/minecraft/world/chunk/Chunk;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;getFieldValue(Ljava/lang/Object;Lnet/optifine/reflect/ReflectorField;)Ljava/lang/Object;",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private Object redirect$onChunkUnloaded$1(Object obj, @Coerce Object field) {
        return this.tileEntitiesToBeRemoved;
    }
}
