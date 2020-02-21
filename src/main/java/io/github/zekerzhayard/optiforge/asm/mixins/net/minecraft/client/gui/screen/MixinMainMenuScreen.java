package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.gui.screen;

import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;
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
        method = {
            "Lnet/minecraft/client/gui/screen/MainMenuScreen;init()V",
            "Lnet/minecraft/client/gui/screen/MainMenuScreen;addSingleplayerMultiplayerButtons(II)V"
        },
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorConstructor;exists()Z",
            remap = false
        ),
        require = 2,
        allow = 2
    )
    private boolean redirect$init_addSingleplayerMultiplayerButtons$0(@Coerce Object constructor) {
        return true;
    }

    @Redirect(
        method = "Lnet/minecraft/client/gui/screen/MainMenuScreen;init()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorForge;makeButtonMods(Lnet/minecraft/client/gui/screen/MainMenuScreen;II)Lnet/minecraft/client/gui/widget/button/Button;",
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private Button redirect$init$1(MainMenuScreen guiMainMenu, int yIn, int rowHeightIn) {
        return new Button(this.width / 2 - 100, yIn + rowHeightIn * 2, 98, 20, I18n.format("fml.menu.mods"), button -> this.minecraft.displayGuiScreen(new ModListScreen(this)));
    }

    @Redirect(
        method = "Lnet/minecraft/client/gui/screen/MainMenuScreen;render(IIF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/optifine/reflect/ReflectorMethod;exists()Z",
            ordinal = 0,
            remap = false
        ),
        require = 1,
        allow = 1
    )
    private boolean redirect$render$0(@Coerce Object method) {
        ForgeHooksClient.renderMainMenu((MainMenuScreen) (Object) this, this.font, this.width, this.height);
        return false;
    }
}
