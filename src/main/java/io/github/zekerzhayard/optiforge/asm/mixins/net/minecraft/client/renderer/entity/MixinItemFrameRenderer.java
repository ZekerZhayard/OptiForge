package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.entity;

import io.github.zekerzhayard.optiforge.asm.utils.RedirectSurrogate;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemFrameRenderer.class)
public abstract class MixinItemFrameRenderer {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/ItemFrameRenderer;renderItem(Lnet/minecraft/entity/item/ItemFrameEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"
        ),
        require = 1,
        allow = 1
    )
    private Item redirect$renderItem$0(ItemStack itemStack) {
        return null;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/ItemFrameRenderer;renderItem(Lnet/minecraft/entity/item/ItemFrameEntity;)V",
        at = @At(
            value = "CONSTANT",
            args = "classValue=net/minecraft/item/FilledMapItem"
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$renderItem$1(Object item, Class<?> filledMapItemClass, ItemFrameEntity itemFrame) {
        return false;
    }

    @RedirectSurrogate(loacalVariableOrdinals = 0)
    private MapData redirect$renderItem$1(Object item, Class<?> filledMapItemClass, ItemFrameEntity itemFrame, ItemStack itemStack) {
        return FilledMapItem.getMapData(itemStack, itemFrame.world);
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/ItemFrameRenderer;renderItem(Lnet/minecraft/entity/item/ItemFrameEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorForge;getMapData(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;)Lnet/minecraft/world/storage/MapData;",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private MapData redirect$renderItem$2(ItemStack itemStack, World world) {
        return null;
    }
}
