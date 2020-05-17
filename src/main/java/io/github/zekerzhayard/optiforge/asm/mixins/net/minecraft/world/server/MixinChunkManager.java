package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.world.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkManager.class)
public abstract class MixinChunkManager {
    @Final
    @Shadow
    private ServerWorld world;

    @Redirect(
        method = "Lnet/minecraft/world/server/ChunkManager;lambda$func_223172_f$14", // (Lnet/minecraft/util/math/ChunkPos;)Lcom/mojang/datafixers/util/Either;
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorConstructor;exists()Z"
        ),
        remap = false,
        require = 1,
        allow = 1
    )
    private boolean redirect$lambda$func_223172_f$14$0(@Coerce Object constructor) {
        return false;
    }

    @Redirect(
        method = "Lnet/minecraft/world/server/ChunkManager;func_219229_a(Lnet/minecraft/world/chunk/IChunk;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorConstructor;exists()Z",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$func_219229_a$0(@Coerce Object constructor) {
        return true;
    }

    @Redirect(
        method = "Lnet/minecraft/world/server/ChunkManager;func_219229_a(Lnet/minecraft/world/chunk/IChunk;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;postForgeBusEvent(Lnet/optifine/reflect/ReflectorConstructor;[Ljava/lang/Object;)Z",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$func_219229_a$1(@Coerce Object constructor, Object[] params) {
        return MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Save((IChunk) params[0], ((IChunk) params[0]).getWorldForge() != null ? ((IChunk) params[0]).getWorldForge() : this.world, (CompoundNBT) params[1]));
    }
}
