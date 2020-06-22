package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.OverlayRenderer;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OverlayRenderer.class)
public abstract class MixinOverlayRenderer {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/OverlayRenderer;getOverlayBlock(Lnet/minecraft/entity/player/PlayerEntity;)Lorg/apache/commons/lang3/tuple/Pair;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/apache/commons/lang3/tuple/Pair;of(Ljava/lang/Object;Ljava/lang/Object;)Lorg/apache/commons/lang3/tuple/Pair;",
            ordinal = 1
        ),
        remap = false,
        require = 1,
        allow = 1
    )
    private static Pair<BlockState, BlockPos> redirect$getOverlayBlock$0(Object left, Object right) {
        return null;
    }
}
