package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.model;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BakedQuadRetextured;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BakedQuadRetextured.class)
public abstract class MixinBakedQuadRetextured {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/model/BakedQuadRetextured;<init>(Lnet/minecraft/client/renderer/model/BakedQuad;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/model/BakedQuad;applyDiffuseLighting:Z",
            ordinal = 0,
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private static boolean redirect$_init_$0(BakedQuad bakedQuad) {
        return bakedQuad.shouldApplyDiffuseLighting();
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/model/BakedQuadRetextured;<init>(Lnet/minecraft/client/renderer/model/BakedQuad;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/model/BakedQuad;format:Lnet/minecraft/client/renderer/vertex/VertexFormat;",
            ordinal = 0,
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private static VertexFormat redirect$_init_$1(BakedQuad bakedQuad) {
        return bakedQuad.getFormat();
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/model/BakedQuadRetextured;<init>(Lnet/minecraft/client/renderer/model/BakedQuad;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/model/BakedQuad;applyDiffuseLighting:Z",
            ordinal = 1,
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$_init_$2(BakedQuad bakedQuad) {
        return bakedQuad.shouldApplyDiffuseLighting();
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/model/BakedQuadRetextured;<init>(Lnet/minecraft/client/renderer/model/BakedQuad;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/model/BakedQuad;format:Lnet/minecraft/client/renderer/vertex/VertexFormat;",
            ordinal = 1,
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private VertexFormat redirect$_init_$3(BakedQuad bakedQuad) {
        return bakedQuad.getFormat();
    }
}
