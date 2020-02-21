package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import net.minecraft.client.renderer.Matrix3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix3f.class)
public abstract class MixinMatrix3f {
    @Shadow
    public abstract void set(Matrix3f matrix);

    public void multiplyBackward(Matrix3f other) {
        Matrix3f copy = other.copy();
        copy.mul((Matrix3f) (Object) this);
        this.set(copy);
    }
}
