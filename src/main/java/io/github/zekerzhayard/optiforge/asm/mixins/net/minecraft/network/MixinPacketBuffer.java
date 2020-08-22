package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.network;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PacketBuffer.class)
public abstract class MixinPacketBuffer {
    @Redirect(
        method = "Lnet/minecraft/network/PacketBuffer;writeItemStack(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/network/PacketBuffer;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/Item;isDamageable()Z"
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$writeItemStack$0(Item item, ItemStack stack, boolean limitedTag) {
        return item.isDamageable(stack);
    }
}
