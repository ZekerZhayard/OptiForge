package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.model;

import java.util.function.Function;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModelBakery.class)
public abstract class MixinModelBakery {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/model/ModelBakery;getBakedModel(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/texture/ISprite;Ljava/util/function/Function;Lnet/minecraft/client/renderer/vertex/VertexFormat;)Lnet/minecraft/client/renderer/model/IBakedModel;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/model/IUnbakedModel;bake(Lnet/minecraft/client/renderer/model/ModelBakery;Ljava/util/function/Function;Lnet/minecraft/client/renderer/texture/ISprite;)Lnet/minecraft/client/renderer/model/IBakedModel;"
        ),
        require = 1,
        allow = 1
    )
    private IBakedModel redirect$getBakedModel$0(IUnbakedModel unbakedModel, ModelBakery modelBakery, Function<ResourceLocation, TextureAtlasSprite> _textureGetter, ISprite _sprite, ResourceLocation resourceLocation, ISprite sprite, Function<ResourceLocation, TextureAtlasSprite> textureGetter, VertexFormat format) {
        return unbakedModel.bake(modelBakery, _textureGetter, _sprite, format);
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/model/ModelBakery;getBakedModel(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/texture/ISprite;Ljava/util/function/Function;Lnet/minecraft/client/renderer/vertex/VertexFormat;)Lnet/minecraft/client/renderer/model/IBakedModel;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorClass;exists()Z",
            ordinal = 1
        ),
        remap = false,
        require = 1,
        allow = 1
    )
    private boolean redirect$getBakedModel$1(@Coerce Object clazz) {
        return false;
    }
}
