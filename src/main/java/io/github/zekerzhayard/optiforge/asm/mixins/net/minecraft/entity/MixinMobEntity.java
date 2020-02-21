package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.entity;

import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobEntity.class)
public abstract class MixinMobEntity {
    private ConcurrentHashMap<Thread, ItemStack> optiforge_itemStackMap = new ConcurrentHashMap<>();

    @Redirect(
        method = "Lnet/minecraft/entity/MobEntity;getSlotForItemStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/inventory/EquipmentSlotType;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorMethod;exists()Z",
            remap = false
        ),
        require = 2,
        allow = 2
    )
    private static boolean redirect$getSlotForItemStack$0(@Coerce Object method) {
        return true;
    }

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

    @ModifyVariable(
        method = "Lnet/minecraft/entity/MobEntity;attackEntityAsMob(Lnet/minecraft/entity/Entity;)Z",
        at = @At("STORE"),
        ordinal = 0,
        require = 1,
        allow = 1
    )
    private ItemStack modifyVariable$attackEntityAsMob$0(ItemStack itemStack) {
        this.optiforge_itemStackMap.put(Thread.currentThread(), itemStack);
        return itemStack;
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
    private Item redirect$attackEntityAsMob$0(ItemStack itemStack) {
        return this.optiforge_itemStackMap.remove(Thread.currentThread()).getItem();
    }
}
