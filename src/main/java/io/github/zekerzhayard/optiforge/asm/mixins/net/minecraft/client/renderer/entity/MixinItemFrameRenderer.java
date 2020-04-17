package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.zekerzhayard.optiforge.asm.utils.RedirectSurrogate;
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
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemFrameRenderer.class)
public abstract class MixinItemFrameRenderer {
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
        return null;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/ItemFrameRenderer;render(Lnet/minecraft/entity/item/ItemFrameEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
        at = @At(
            value = "CONSTANT",
            args = "classValue=net/minecraft/item/FilledMapItem",
            ordinal = 1
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$render$1(Object item, Class<?> filledMapItemClass, ItemFrameEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        return false;
    }

    @RedirectSurrogate(loacalVariableOrdinals = 0)
    private MapData redirect$render$1(Object item, Class<?> filledMapItemClass, ItemFrameEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, ItemStack itemStack) {
        return FilledMapItem.getMapData(itemStack, entityIn.world);
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
    private MapData redirect$render$2(ItemStack itemStack, World world) {
        return null;
    }
}
