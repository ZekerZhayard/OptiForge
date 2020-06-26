package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import io.github.zekerzhayard.optiforge.asm.utils.annotations.RedirectSurrogate;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
    @ModifyArg(
        method = "Lnet/minecraft/client/renderer/WorldRenderer;setupTerrain(Lnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/client/renderer/culling/ICamera;IZ)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/culling/ICamera;isBoundingBoxInFrustum(Lnet/minecraft/util/math/AxisAlignedBB;)Z"
        ),
        require = 1,
        allow = 1
    )
    private AxisAlignedBB modifyArg$setupTerrain$0(AxisAlignedBB aabb) {
        return null;
    }

    @RedirectSurrogate(loacalVariableOrdinals = 0)
    private AxisAlignedBB modifyArg$setupTerrain$0(AxisAlignedBB aabb, BlockPos blockPos1) {
        return aabb.expand(0.0, blockPos1.getY() > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY, 0.0);
    }
}
