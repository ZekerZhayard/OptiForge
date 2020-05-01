package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.texture;

import net.minecraft.client.renderer.texture.AtlasTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(AtlasTexture.class)
public abstract class MixinAtlasTexture {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/texture/AtlasTexture;stitch(Lnet/minecraft/resources/IResourceManager;Ljava/lang/Iterable;Lnet/minecraft/profiler/IProfiler;)Lnet/minecraft/client/renderer/texture/AtlasTexture$SheetData;",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/util/math/MathHelper;log2(I)I"
            )
        ),
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/texture/AtlasTexture;mipmapLevels:I",
            ordinal = 0
        ),
        require = 1,
        allow = 1
    )
    private int redirect$stitch$0(AtlasTexture atlasTexture) {
        return Integer.MIN_VALUE;
    }
}
