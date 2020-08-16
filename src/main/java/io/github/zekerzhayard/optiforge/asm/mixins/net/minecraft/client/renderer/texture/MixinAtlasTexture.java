package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.texture;

import java.util.stream.Stream;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AtlasTexture.class)
public abstract class MixinAtlasTexture {
    @ModifyVariable(
        method = "Lnet/minecraft/client/renderer/texture/AtlasTexture;stitch(Lnet/minecraft/resources/IResourceManager;Ljava/util/stream/Stream;Lnet/minecraft/profiler/IProfiler;I)Lnet/minecraft/client/renderer/texture/AtlasTexture$SheetData;",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/util/math/MathHelper;log2(I)I",
            ordinal = 2
        ),
        index = 14,
        require = 1,
        allow = 1
    )
    private int modifyVariable$stitch$0(int j1, IResourceManager resourceManagerIn, Stream<ResourceLocation> resourceLocationsIn, IProfiler profilerIn, int maxMipmapLevelIn) {
        return maxMipmapLevelIn;
    }
}
