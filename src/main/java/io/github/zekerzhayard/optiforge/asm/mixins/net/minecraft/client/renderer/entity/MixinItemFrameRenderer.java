package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.entity;

import java.util.concurrent.ConcurrentHashMap;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemFrameRenderer.class)
public abstract class MixinItemFrameRenderer {
    private ConcurrentHashMap<Thread, ItemStack> optiforge_itemStackMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Thread, MapData> optiforge_mapDataMap = new ConcurrentHashMap<>();

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/ItemFrameRenderer;render(Lnet/minecraft/entity/item/ItemFrameEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
            ordinal = 1
        ),
        require = 1,
        allow = 1
    )
    private Item redirect$render$0(ItemStack itemStack) {
        this.optiforge_itemStackMap.put(Thread.currentThread(), itemStack);
        return null;
    }

    @ModifyVariable(
        method = "Lnet/minecraft/client/renderer/entity/ItemFrameRenderer;render(Lnet/minecraft/entity/item/ItemFrameEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
        at = @At("STORE"),
        ordinal = 0,
        require = 1,
        allow = 1
    )
    private boolean modifyVariable$render$0(boolean flag, ItemFrameEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        Thread currentThread = Thread.currentThread();
        MapData mapdata = FilledMapItem.getMapData(this.optiforge_itemStackMap.remove(currentThread), entityIn.world);
        boolean b = mapdata != null;
        if (b) {
            this.optiforge_mapDataMap.put(currentThread, mapdata);
        }
        return b;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/ItemFrameRenderer;render(Lnet/minecraft/entity/item/ItemFrameEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorForge;getMapData(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;)Lnet/minecraft/world/storage/MapData;",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private MapData redirect$render$1(ItemStack itemStack, World world) {
        return this.optiforge_mapDataMap.remove(Thread.currentThread());
    }
}
