package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.entity;

import io.github.zekerzhayard.optiforge.asm.utils.RedirectSurrogate;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobEntity.class)
public abstract class MixinMobEntity {
    @Redirect(
        method = "Lnet/minecraft/entity/MobEntity;getSlotForItemStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/inventory/EquipmentSlotType;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;call(Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Ljava/lang/Object;",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private static Object redirect$getSlotForItemStack$1(Object obj, @Coerce Object method, Object[] params) {
        return ((ItemStack) params[0]).getEquipmentSlot();
    }

    @Redirect(
        method = "Lnet/minecraft/entity/MobEntity;getSlotForItemStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/inventory/EquipmentSlotType;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;callBoolean(Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Z",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private static boolean redirect$getSlotForItemStack$2(Object obj, @Coerce Object method, Object[] params) {
        return ((ItemStack) params[0]).isShield(null);
    }

    @Redirect(
        method = "Lnet/minecraft/entity/MobEntity;attackEntityAsMob(Lnet/minecraft/entity/Entity;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorForge;isShield(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;)Z",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$attackEntityAsMob$0(ItemStack itemStack, PlayerEntity playerEntity) {
        return itemStack.isShield(playerEntity);
    }

    @Redirect(
        method = "Lnet/minecraft/entity/MobEntity;attackEntityAsMob(Lnet/minecraft/entity/Entity;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"
        ),
        require = 1,
        allow = 1
    )
    private Item redirect$attackEntityAsMob$1(ItemStack itemStack) {
        return null;
    }

    @RedirectSurrogate(loacalVariableOrdinals = 0)
    private Item redirect$attackEntityAsMob$1(ItemStack itemStack2, ItemStack itemStack) {
        return itemStack.getItem();
    }
}
