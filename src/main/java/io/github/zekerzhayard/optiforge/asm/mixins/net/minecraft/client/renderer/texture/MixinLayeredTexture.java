package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.texture;

import java.io.IOException;
import java.io.InputStream;

import io.github.zekerzhayard.optiforge.asm.utils.RedirectSurrogate;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LayeredTexture.class)
public abstract class MixinLayeredTexture {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/texture/LayeredTexture;loadTexture(Lnet/minecraft/resources/IResourceManager;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/resources/IResource;getInputStream()Ljava/io/InputStream;",
            ordinal = 0
        ),
        require = 1,
        allow = 1
    )
    private InputStream redirect$loadTexture$0(IResource iResource) {
        return null;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/texture/LayeredTexture;loadTexture(Lnet/minecraft/resources/IResourceManager;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/NativeImage;read(Ljava/io/InputStream;)Lnet/minecraft/client/renderer/texture/NativeImage;",
            ordinal = 0
        ),
        require = 1,
        allow = 1
    )
    private NativeImage redirect$loadTexture$1(InputStream inputStreamIn, IResourceManager manager) {
        return null;
    }

    @RedirectSurrogate(loacalVariableOrdinals = 0)
    private NativeImage redirect$loadTexture$1(InputStream inputStreamIn, IResourceManager manager, String s) throws IOException {
        return MinecraftForgeClient.getImageLayer(new ResourceLocation(s), manager);
    }
}
