package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.model;

import java.util.function.Function;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ModelBakery.class)
public abstract class MixinModelBakery {
    @Inject(
        method = "Lnet/minecraft/client/renderer/model/ModelBakery;processLoading(Lnet/minecraft/profiler/IProfiler;I)V",
        at = @At("HEAD"),
        remap = false,
        require = 1,
        allow = 1
    )
    private void inject$processLoading$0(CallbackInfo ci) {
        ModelLoaderRegistry.onModelLoadingStart();
    }

    @Inject(
        method = "Lnet/minecraft/client/renderer/model/ModelBakery;lambda$uploadTextures$12", // (Lnet/minecraft/util/ResourceLocation;)V
        at = @At(
            value = "INVOKE",
            target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false,
        require = 1,
        allow = 1
    )
    private void inject$uploadTextures$0(ResourceLocation location, CallbackInfo ci, IBakedModel model, Exception exception) {
        exception.printStackTrace();
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/model/ModelBakery;getBakedModel(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/model/IModelTransform;Ljava/util/function/Function;)Lnet/minecraft/client/renderer/model/IBakedModel;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/model/IUnbakedModel;bakeModel(Lnet/minecraft/client/renderer/model/ModelBakery;Ljava/util/function/Function;Lnet/minecraft/client/renderer/model/IModelTransform;Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/renderer/model/IBakedModel;",
            ordinal = 0
        ),
        require = 1,
        allow = 1
    )
    private IBakedModel redirect$getBakedModel$0(IUnbakedModel model, ModelBakery modelBakeryIn, Function<RenderMaterial, TextureAtlasSprite> spriteGetterIn, IModelTransform transformIn, ResourceLocation locationIn) {
        return null;
    }
}
