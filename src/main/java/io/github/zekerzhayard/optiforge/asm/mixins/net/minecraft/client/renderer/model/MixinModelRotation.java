package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.model;

import javax.vecmath.Matrix4f;

import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraftforge.common.model.TRSRTransformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModelRotation.class)
public abstract class MixinModelRotation {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/model/ModelRotation;getMatrixVec()Ljavax/vecmath/Matrix4f;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/common/model/TRSRTransformation;getMatrix()Ljavax/vecmath/Matrix4f;"
        ),
        remap = false,
        require = 1,
        allow = 1
    )
    private Matrix4f redirect$getMatrixVec$0(TRSRTransformation trsrTransformation) {
        return trsrTransformation.getMatrixVec();
    }
}
