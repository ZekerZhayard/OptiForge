package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import net.minecraft.client.renderer.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix4f.class)
public abstract class MixinMatrix4f {
    @Shadow
    public float m00;
    @Shadow
    public float m01;
    @Shadow
    public float m02;
    @Shadow
    public float m03;
    @Shadow
    public float m10;
    @Shadow
    public float m11;
    @Shadow
    public float m12;
    @Shadow
    public float m13;
    @Shadow
    public float m20;
    @Shadow
    public float m21;
    @Shadow
    public float m22;
    @Shadow
    public float m23;
    @Shadow
    public float m30;
    @Shadow
    public float m31;
    @Shadow
    public float m32;
    @Shadow
    public float m33;

    @Shadow(remap = false)
    public abstract void set(Matrix4f matrix);

    /**
     *  This method is invoked by the new constructor which added by Matrix4fTransformer
     */
    private void optiforge_newInstance(float[] values) {
        this.m00 = values[0];
        this.m01 = values[1];
        this.m02 = values[2];
        this.m03 = values[3];
        this.m10 = values[4];
        this.m11 = values[5];
        this.m12 = values[6];
        this.m13 = values[7];
        this.m20 = values[8];
        this.m21 = values[9];
        this.m22 = values[10];
        this.m23 = values[11];
        this.m30 = values[12];
        this.m31 = values[13];
        this.m32 = values[14];
        this.m33 = values[15];
    }

    public void add(Matrix4f other) {
        this.m00 += other.m00;
        this.m01 += other.m01;
        this.m02 += other.m02;
        this.m03 += other.m03;
        this.m10 += other.m10;
        this.m11 += other.m11;
        this.m12 += other.m12;
        this.m13 += other.m13;
        this.m20 += other.m20;
        this.m21 += other.m21;
        this.m22 += other.m22;
        this.m23 += other.m23;
        this.m30 += other.m30;
        this.m31 += other.m31;
        this.m32 += other.m32;
        this.m33 += other.m33;
    }

    public void multiplyBackward(Matrix4f other) {
        Matrix4f copy = other.copy();
        copy.mul((Matrix4f) (Object) this);
        this.set(copy);
    }

    public void setTranslation(float x, float y, float z) {
        this.m00 = 1.0F;
        this.m11 = 1.0F;
        this.m22 = 1.0F;
        this.m33 = 1.0F;
        this.m03 = x;
        this.m13 = y;
        this.m23 = z;
    }
}
