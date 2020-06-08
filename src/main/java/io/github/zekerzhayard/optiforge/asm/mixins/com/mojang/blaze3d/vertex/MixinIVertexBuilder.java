package io.github.zekerzhayard.optiforge.asm.mixins.com.mojang.blaze3d.vertex;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.zekerzhayard.optiforge.asm.imixins.com.mojang.blaze3d.vertex.IMixinIVertexBuilder;
import io.github.zekerzhayard.optiforge.asm.imixins.net.minecraft.client.renderer.IMixinBlockModelRenderer;
import io.github.zekerzhayard.optiforge.asm.imixins.net.minecraft.client.renderer.model.IMixinBakedQuad;
import io.github.zekerzhayard.optiforge.asm.utils.annotations.MarkStatic;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.extensions.IForgeVertexBuilder;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IVertexBuilder.class)
public interface MixinIVertexBuilder extends IForgeVertexBuilder, IMixinIVertexBuilder {
    @Shadow
    void addVertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ);

    /**
     * @reason Mixin can't allow mods inject into interface default methods, we can only use {@link Overwrite}
     * @author ZekerZhayard
     */
    @MarkStatic(
        markers = @MarkStatic.Marker(
            before = IMixinBlockModelRenderer.class,
            after = BlockModelRenderer.class
        )
    )
    @Overwrite
    default void addQuad(MatrixStack.Entry matrixEntryIn, BakedQuad quadIn, float[] colorMuls, float redIn, float greenIn, float blueIn, int[] combinedLightsIn, int combinedOverlayIn, boolean mulColor) {
        int[] aint = this.isMultiTexture() ? ((IMixinBakedQuad) quadIn).getVertexDataSingle() : quadIn.getVertexData();
        this.putSprite(((IMixinBakedQuad) quadIn).getSprite());
        boolean separateAoInAlpha = IMixinBlockModelRenderer.isSeparateAoLightValue();
        Vec3i vec3i = quadIn.getFace().getDirectionVec();
        Vector3f vector3f = new Vector3f((float) vec3i.getX(), (float) vec3i.getY(), (float) vec3i.getZ());
        Matrix4f matrix4f = matrixEntryIn.getMatrix();
        vector3f.transform(matrixEntryIn.getNormal());
        int vertexIntSize = DefaultVertexFormats.BLOCK.getIntegerSize();
        int j = aint.length / vertexIntSize;

        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            ByteBuffer bytebuffer = memorystack.malloc(vertexIntSize * 4);
            IntBuffer intbuffer = bytebuffer.asIntBuffer();

            for (int k = 0; k < j; ++k) {
                intbuffer.clear();
                intbuffer.put(aint, k * vertexIntSize, vertexIntSize);
                float f = bytebuffer.getFloat(0);
                float f1 = bytebuffer.getFloat(4);
                float f2 = bytebuffer.getFloat(8);
                float colorMulAo = separateAoInAlpha ? 1.0F : colorMuls[k];
                float f3;
                float f4;
                float f5;
                if (mulColor) {
                    float f6 = (float) (bytebuffer.get(12) & 255) / 255.0F;
                    float f7 = (float) (bytebuffer.get(13) & 255) / 255.0F;
                    float f8 = (float) (bytebuffer.get(14) & 255) / 255.0F;
                    f3 = f6 * colorMulAo * redIn;
                    f4 = f7 * colorMulAo * greenIn;
                    f5 = f8 * colorMulAo * blueIn;
                } else {
                    f3 = colorMulAo * redIn;
                    f4 = colorMulAo * greenIn;
                    f5 = colorMulAo * blueIn;
                }

                int l = this.applyBakedLighting(combinedLightsIn[k], bytebuffer);
                float f9 = bytebuffer.getFloat(16);
                float f10 = bytebuffer.getFloat(20);
                Vector4f vector4f = new Vector4f(f, f1, f2, 1.0F);
                vector4f.transform(matrix4f);
                this.applyBakedNormals(vector3f, bytebuffer, matrixEntryIn.getNormal());
                this.addVertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), f3, f4, f5, separateAoInAlpha ? colorMuls[k] : 1.0F, f9, f10, combinedOverlayIn, l, vector3f.getX(), vector3f.getY(), vector3f.getZ());
            }
        }
    }
}
