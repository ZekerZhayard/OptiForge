package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.texture;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraftforge.fml.loading.AdvancedLogMessageAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Stitcher.class)
public abstract class MixinStitcher {
    private static final Logger LOGGER = LogManager.getLogger();

    @Inject(
        method = "Lnet/minecraft/client/renderer/texture/Stitcher;doStitch()V",
        at = @At(
            value = "JUMP",
            shift = At.Shift.AFTER,
            opcode = Opcodes.IFNE
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        require = 1,
        allow = 1
    )
    private void inject$doStitch$0(CallbackInfo ci, List<?> list, Iterator<?> iterator, Stitcher.Holder stitcher$holder) {
        LOGGER.info(new AdvancedLogMessageAdapter(sb->{
            sb.append("Unable to fit: ").append(stitcher$holder.sprite.getName());
            sb.append(" - size: ").append(stitcher$holder.sprite.getWidth()).append("x").append(stitcher$holder.sprite.getHeight());
            sb.append(" - Maybe try a lower resolution resourcepack?\n");
            list.forEach(h-> sb.append("\t").append(h).append("\n"));
        }));
    }
}
