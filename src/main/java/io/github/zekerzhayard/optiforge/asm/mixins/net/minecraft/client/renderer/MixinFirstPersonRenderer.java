package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FirstPersonRenderer.class)
public abstract class MixinFirstPersonRenderer {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/FirstPersonRenderer;renderItemInFirstPerson(FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/entity/player/ClientPlayerEntity;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorMethod;exists()Z",
            remap = false
        ),
        require = 2,
        allow = 2
    )
    private boolean redirect$renderItemInFirstPerson$0(@Coerce Object method) {
        return true;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/FirstPersonRenderer;renderItemInFirstPerson(FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/entity/player/ClientPlayerEntity;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;callBoolean(Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Z",
            remap = false
        ),
        require = 2,
        allow = 2
    )
    private boolean redirect$renderItemInFirstPerson$1(@Coerce Object method, Object[] params, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, ClientPlayerEntity playerEntityIn, int combinedLightIn) {
        return ForgeHooksClient.renderSpecificFirstPersonHand((Hand) params[0], matrixStackIn, bufferIn, combinedLightIn, partialTicks, (float) params[2], (float) params[3], (float) params[4], (ItemStack) params[5]);
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/FirstPersonRenderer;renderItemInFirstPerson(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
            ordinal = 1
        ),
        require = 1,
        allow = 1
    )
    private Item redirect$renderItemInFirstPerson$0(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof CrossbowItem) {
            return Items.CROSSBOW;
        }
        return item;
    }
}
