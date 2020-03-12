package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.texture;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LayeredTexture.class)
public abstract class MixinLayeredTexture {
    private ConcurrentMap<Thread, String> optiforge_stringMap = new MapMaker().initialCapacity(1).concurrencyLevel(1).weakKeys().weakValues().makeMap();

    @ModifyVariable(
        method = "Lnet/minecraft/client/renderer/texture/LayeredTexture;loadTexture(Lnet/minecraft/resources/IResourceManager;)V",
        at = @At("STORE"),
        ordinal = 0,
        require = 1,
        allow = 1
    )
    private String modifyVariable$loadTexture$0(String s) {
        this.optiforge_stringMap.put(Thread.currentThread(), s);
        return s;
    }

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
    private NativeImage redirect$loadTexture$1(InputStream inputStreamIn, IResourceManager manager) throws IOException {
        return MinecraftForgeClient.getImageLayer(new ResourceLocation(this.optiforge_stringMap.get(Thread.currentThread())), manager);
    }
}
