package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.gui;

import io.github.zekerzhayard.optiforge.asm.utils.RedirectSurrogate;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IngameGui.class)
public abstract class MixinIngameGui {
    @Shadow
    protected ItemStack highlightingItemStack;

    @Redirect(
        method = {
            "Lnet/minecraft/client/gui/IngameGui;renderPotionEffects()V",
            "Lnet/minecraft/client/gui/IngameGui;renderSelectedItem()V"
        },
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorMethod;exists()Z",
            remap = false
        ),
        require = 4,
        allow = 4
    )
    private boolean redirect$renderPotionEffects_renderSelectedItem$0(@Coerce Object method) {
        return true;
    }

    @Redirect(
        method = "Lnet/minecraft/client/gui/IngameGui;renderPotionEffects()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;callBoolean(Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Z",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$renderPotionEffects$1(Object obj, @Coerce Object method, Object[] params) {
        return ((EffectInstance) params[0]).shouldRenderHUD();
    }

    @Redirect(
        method = "Lnet/minecraft/client/gui/IngameGui;renderPotionEffects()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;call(Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Ljava/lang/Object;",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private Object redirect$renderPotionEffects$2(Object obj, @Coerce Object method, Object[] params) {
        ((EffectInstance) params[0]).renderHUDEffect((AbstractGui) params[1], (int) params[2], (int) params[3], (int) params[4], (float) params[5]);
        return null;
    }

    @Redirect(
        method = "Lnet/minecraft/client/gui/IngameGui;renderSelectedItem()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;callString(Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Ljava/lang/String;",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private String redirect$renderSelectedItem$1(Object obj, @Coerce Object method, Object[] params) {
        return ((ItemStack) params[0]).getHighlightTip((String) params[1]);
    }

    @Redirect(
        method = "Lnet/minecraft/client/gui/IngameGui;renderSelectedItem()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;call(Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Ljava/lang/Object;",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private Object redirect$renderSelectedItem$2(Object obj, @Coerce Object method, Object[] params) {
        return ((Item) obj).getFontRenderer((ItemStack) params[0]);
    }

    @Redirect(
        method = "Lnet/minecraft/client/gui/IngameGui;tick()V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Object;equals(Ljava/lang/Object;)Z",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$tick$0(Object itemStackDisplayName, Object highlightingItemStackDisplayName) {
        return false;
    }

    @RedirectSurrogate(loacalVariableOrdinals = 0)
    private boolean redirect$tick$0(Object itemStackDisplayName, Object highlightingItemStackDisplayName, ItemStack itemStack) {
        return itemStackDisplayName.equals(highlightingItemStackDisplayName) && itemStack.getHighlightTip(itemStack.getDisplayName().getUnformattedComponentText()).equals(this.highlightingItemStack.getHighlightTip(this.highlightingItemStack.getDisplayName().getUnformattedComponentText()));
    }
}
