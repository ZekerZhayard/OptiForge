package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.zekerzhayard.optiforge.asm.utils.annotations.RedirectSurrogate;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraftforge.common.extensions.IForgeEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * {@link io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.gui.IngameGuiTransformer}
 */
@Mixin(IngameGui.class)
public abstract class MixinIngameGui extends AbstractGui {
    @Redirect(
        method = "Lnet/minecraft/client/gui/IngameGui;func_238444_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorMethod;exists()Z",
            ordinal = 1,
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$func_238444_a_$0(@Coerce Object method) {
        return true;
    }

    @Redirect(
        method = "Lnet/minecraft/client/gui/IngameGui;func_238444_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;call(Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Ljava/lang/Object;",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private Object redirect$func_238444_a_$1(Object obj, @Coerce Object method, Object[] params, MatrixStack matrixStackIn) {
        ((IForgeEffectInstance) obj).renderHUDEffect((AbstractGui) params[0], matrixStackIn, (int) params[1], (int) params[2], (int) params[3], (float) params[4]);
        return null;
    }

    @ModifyArg(
        method = "Lnet/minecraft/client/gui/IngameGui;func_238453_b_(Lcom/mojang/blaze3d/matrix/MatrixStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;func_238414_a_(Lnet/minecraft/util/text/ITextProperties;)I",
            ordinal = 0
        ),
        require = 1,
        allow = 1
    )
    private ITextProperties modifyArg$func_238453_b_$0(ITextProperties text) {
        return null;
    }

    @RedirectSurrogate(loacalVariableOrdinals = 0)
    private ITextProperties modifyArg$func_238453_b_$0(ITextProperties text, ITextComponent highlightTip) {
        return highlightTip;
    }

    @Redirect(
        method = "Lnet/minecraft/client/gui/IngameGui;tick()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;callString(Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Ljava/lang/String;",
            remap = false
        ),
        require = 2,
        allow = 2
    )
    private String redirect$tick$0(Object obj, @Coerce Object method, Object[] params) {
        return null;
    }

    @RedirectSurrogate(loacalVariableOrdinals = 0)
    private ITextComponent redirect$tick$0(Object obj, @Coerce Object method, Object[] params, ItemStack itemstack) {
        return ((ItemStack) obj).getHighlightTip((ITextComponent) params[0]);
    }
}
