package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.ForgeHooksClient;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MainMenuScreen.class)
public abstract class MixinMainMenuScreen extends Screen {
    private MixinMainMenuScreen(ITextComponent titleIn) {
        super(titleIn);
    }

    @Redirect(
        method = "Lnet/minecraft/client/gui/screen/MainMenuScreen;func_230430_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;IIF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorMethod;exists()Z",
            ordinal = 0,
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$func_230430_a_$0(@Coerce Object method) {
        return true;
    }

    @Redirect(
        method = "Lnet/minecraft/client/gui/screen/MainMenuScreen;func_230430_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;IIF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/Reflector;callString(Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Ljava/lang/String;",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private String redirect$func_230430_a_$1(@Coerce Object method, Object[] params) {
        ForgeHooksClient.renderMainMenu((MainMenuScreen) params[0], (MatrixStack) params[1], this.field_230712_o_, (int) params[3], (int) params[4]);
        return null;
    }

    @Redirect(
        method = "Lnet/minecraft/client/gui/screen/MainMenuScreen;func_230430_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;IIF)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screen/MainMenuScreen;splashText:Ljava/lang/String;",
            opcode = Opcodes.PUTFIELD
        ),
        require = 1,
        allow = 1
    )
    private void redirect$func_230430_a_$2(MainMenuScreen _this, String value) {

    }
}
