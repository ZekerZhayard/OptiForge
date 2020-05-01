package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import net.minecraft.client.renderer.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix4f.class)
public abstract class MixinMatrix4f {
    @Final
    @Shadow
    private float[] elements;

    public float get(int col, int row) {
        return this.elements[col + 4 * row];
    }
}
