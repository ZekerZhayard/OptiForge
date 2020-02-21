package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.world.server;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChunkManager.class)
public abstract class MixinChunkManager {
    @Final
    @Shadow
    private Long2ObjectLinkedOpenHashMap<ChunkHolder> loadedChunks;

    @Final
    @Shadow
    private ServerWorld world;

    private ConcurrentHashMap<Thread, Boolean> optiforge_booleanMap = new ConcurrentHashMap<>();

    @Inject(
        method = "Lnet/minecraft/world/server/ChunkManager;tick(Ljava/util/function/BooleanSupplier;)V",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = "Lnet/minecraft/world/server/ChunkManager;scheduleUnloads(Ljava/util/function/BooleanSupplier;)V"
        ),
        require = 1,
        allow = 1
    )
    private void inject$tick$0(CallbackInfo ci) {
        if (this.loadedChunks.isEmpty()) {
            DimensionManager.unloadWorld(this.world);
        }
    }

    @Dynamic
    @Inject(
        method = "Lnet/minecraft/world/server/ChunkManager;lambda$scheduleSave$10(Lnet/minecraft/world/server/ChunkHolder;Ljava/util/concurrent/CompletableFuture;JLnet/minecraft/world/chunk/IChunk;)V",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = "Lnet/minecraft/world/chunk/Chunk;setLoaded(Z)V"
        ),
        require = 1,
        allow = 1
    )
    private void inject$lambda$scheduleSave$10$0(ChunkHolder chunkHolderIn, CompletableFuture<IChunk> completableFuture, long chunkPosIn, IChunk chunk, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new ChunkEvent.Unload(chunk));
    }

    @Dynamic
    @Inject(
        method = "Lnet/minecraft/world/server/ChunkManager;lambda$func_223172_f$14(Lnet/minecraft/util/math/ChunkPos;)Lcom/mojang/datafixers/util/Either;",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = "Lnet/minecraft/world/chunk/IChunk;setLastSaveTime(J)V"
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        require = 1,
        allow = 1
    )
    private void inject$lambda$func_223172_f$14$0(ChunkPos chunkPos, CallbackInfoReturnable<Either<IChunk, ChunkHolder.IChunkLoadingError>> cir, CompoundNBT compoundNBT, boolean flag, IChunk chunk) {
        MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(chunk));
    }

    @Dynamic
    @Redirect(
        method = "Lnet/minecraft/world/server/ChunkManager;lambda$null$25(Lnet/minecraft/world/server/ChunkHolder;Lnet/minecraft/world/chunk/IChunk;)Lnet/minecraft/world/chunk/IChunk;",
        at = @At(
            value = "INVOKE",
            target = "Lit/unimi/dsi/fastutil/longs/LongSet;add(J)Z"
        ),
        remap = false,
        require = 1,
        allow = 1
    )
    private boolean redirect$lambda$null$25$0(LongSet loadedPositions, long key) {
        boolean b = loadedPositions.add(key);
        this.optiforge_booleanMap.put(Thread.currentThread(), b);
        return b;
    }

    @Dynamic
    @Inject(
        method = "Lnet/minecraft/world/server/ChunkManager;lambda$null$25(Lnet/minecraft/world/server/ChunkHolder;Lnet/minecraft/world/chunk/IChunk;)Lnet/minecraft/world/chunk/IChunk;",
        at = @At("TAIL"),
        remap = false,
        require = 1,
        allow = 1
    )
    private void inject$lambda$null$25$0(CallbackInfoReturnable<IChunk> cir) {
        if (this.optiforge_booleanMap.remove(Thread.currentThread())) {
            MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(cir.getReturnValue()));
        }
    }

    @ModifyVariable(
        method = "Lnet/minecraft/world/server/ChunkManager;func_219229_a(Lnet/minecraft/world/chunk/IChunk;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/server/ChunkManager;writeChunk(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/CompoundNBT;)V"
        ),
        require = 1,
        allow = 1
    )
    private CompoundNBT modifyVariable$func_219229_a$0(CompoundNBT compound, IChunk chunkIn) {
        MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Save(chunkIn, compound));
        return compound;
    }

    @Inject(
        method = "Lnet/minecraft/world/server/ChunkManager;setChunkLoadedAtClient(Lnet/minecraft/entity/player/ServerPlayerEntity;Lnet/minecraft/util/math/ChunkPos;[Lnet/minecraft/network/IPacket;ZZ)V",
        at = @At(
            value = "FIELD",
            shift = At.Shift.BY,
            by = 2,
            target = "Lnet/minecraft/world/server/ChunkManager;world:Lnet/minecraft/world/server/ServerWorld;",
            ordinal = 0
        ),
        require = 1,
        allow = 1
    )
    private void inject$setChunkLoadedAtClient$0(ServerPlayerEntity player, ChunkPos chunkPosIn, IPacket<?>[] packetCache, boolean wasLoaded, boolean load, CallbackInfo ci) {
        ForgeEventFactory.fireChunkWatch(wasLoaded, load, player, chunkPosIn, this.world);
    }
}
