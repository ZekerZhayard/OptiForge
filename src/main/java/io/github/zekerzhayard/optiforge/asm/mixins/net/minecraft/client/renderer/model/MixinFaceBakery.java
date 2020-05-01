package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.model;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FaceBakery.class)
public abstract class MixinFaceBakery {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/model/FaceBakery;storeVertexData([IIILnet/minecraft/client/renderer/Vector3f;ILnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/minecraft/client/renderer/model/BlockFaceUV;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;getInterpolatedU(D)F"
        ),
        require = 1,
        allow = 1
    )
    private float redirect$storeVertexData$0(TextureAtlasSprite _sprite, double u, int[] faceData, int storeIndex, int vertexIndex, Vector3f position, int shadeColor, TextureAtlasSprite sprite, BlockFaceUV faceUV) {
        return _sprite.getInterpolatedU(u * .999 + faceUV.getVertexU((vertexIndex + 2) % 4) * .001);
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/model/FaceBakery;storeVertexData([IIILnet/minecraft/client/renderer/Vector3f;ILnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/minecraft/client/renderer/model/BlockFaceUV;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;getInterpolatedV(D)F"
        ),
        require = 1,
        allow = 1
    )
    private float redirect$storeVertexData$1(TextureAtlasSprite _sprite, double v, int[] faceData, int storeIndex, int vertexIndex, Vector3f position, int shadeColor, TextureAtlasSprite sprite, BlockFaceUV faceUV) {
        return _sprite.getInterpolatedV(v * .999 + faceUV.getVertexV((vertexIndex + 2) % 4) * .001);
    }
}

