package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.chunk;

import java.util.Map;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$ChunkRender$ChunkRenderTask")
public abstract class MixinChunkRenderDispatcher$ChunkRender$ChunkRenderTask {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$ChunkRender$ChunkRenderTask;<init>(Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$ChunkRender;Lnet/minecraft/util/math/ChunkPos;D)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/client/model/ModelDataManager;getModelData(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/math/ChunkPos;)Ljava/util/Map;",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private Map<BlockPos, IModelData> redirect$_init_$0(ClientWorld world, ChunkPos pos) {
        return ModelDataManager.getModelData(world, pos);
    }
}
