package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.texture;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.util.Bytecode;

public class AtlasTextureTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.texture.AtlasTexture";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.15.x/patches/minecraft/net/minecraft/client/renderer/texture/AtlasTexture.java.patch#L23-L25
        //
        //        int j1 = MathHelper.func_151239_c(i1);
        // -      int k1;
        // +      int k1 = p_229220_4_;
        // +      if (false) // FORGE: do not lower the mipmap level
        //        if (j1 < p_229220_4_) {
        //

        MethodNode stitch = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_229220_a_"), "(Lnet/minecraft/resources/IResourceManager;Ljava/util/stream/Stream;Lnet/minecraft/profiler/IProfiler;I)Lnet/minecraft/client/renderer/texture/AtlasTexture$SheetData;"));

        int warnCount = 0;
        for (AbstractInsnNode ain : stitch.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.GETSTATIC) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.owner.equals("net/minecraft/client/renderer/texture/AtlasTexture") && fin.name.equals(ASMAPI.mapField("field_147635_d")) && fin.desc.equals("Lorg/apache/logging/log4j/Logger;")) {
                    warnCount++;
                    if (warnCount == 2) {

                        // label 57
                        // line 343
                        // f_new 15 0
                        // remove -> iload 14
                        // remove -> iload 4
                        // remove -> if_icmpge 61
                        // remove -> label 58
                        // remove -> line 345
                        // remove -> getstatic Logger AtlasTexture.field_147635_d
                        // remove -> ldc String "{}: dropping miplevel from {} to {}, because of minimum power of two: {}"
                        // remove -> aload 0
                        // remove -> getfield ResourceLocation AtlasTexture.field_229214_j_
                        // remove -> iload 4
                        // remove -> invokestatic Integer Integer.valueOf(int)
                        // remove -> iload 14
                        // remove -> invokestatic Integer Integer.valueOf(int)
                        // remove -> iload 13
                        // remove -> invokestatic Integer Integer.valueOf(int)
                        // remove -> invokeinterface void Logger.warn(String, Object, Object, Object, Object)
                        // remove -> label 59
                        // remove -> line 346
                        // remove -> iload 14
                        // remove -> istore 15
                        // remove -> label 60
                        // remove -> goto 62
                        // remove -> label 61
                        // remove -> line 350
                        // remove -> f_new 15 0
                        // iload 4
                        // istore 15
                        AbstractInsnNode ain0 = fin;
                        while (!(ain0 instanceof FrameNode)) {
                            ain0 = ain0.getPrevious();
                        }
                        while (!(ain0.getNext() instanceof FrameNode)) {
                            stitch.instructions.remove(ain0.getNext());
                        }
                        stitch.instructions.remove(ain0.getNext());
                    }
                }
            }
        }

        return input;
    }
}
