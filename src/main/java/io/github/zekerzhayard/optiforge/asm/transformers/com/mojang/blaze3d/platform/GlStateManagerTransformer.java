package io.github.zekerzhayard.optiforge.asm.transformers.com.mojang.blaze3d.platform;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class GlStateManagerTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "com.mojang.blaze3d.platform.GlStateManager";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.17.x/patches/minecraft/com/mojang/blaze3d/platform/GlStateManager.java.patch#L7-L16
        //
        //        f_84077_[f_84076_].f_84800_ = false;
        //     }
        //
        // +   /* Stores the last values sent into glMultiTexCoord2f */
        // +   public static float lastBrightnessX = 0.0f;
        // +   public static float lastBrightnessY = 0.0f;
        //     public static void m_84160_(int p_84161_, int p_84162_, float p_84163_) {
        //        RenderSystem.m_69393_(RenderSystem::m_69587_);
        //        GL11.glTexParameterf(p_84161_, p_84162_, p_84163_);
        // +      if (p_84161_ == GL13.GL_TEXTURE1) {
        // +          lastBrightnessX = p_84162_;
        // +          lastBrightnessY = p_84163_;
        // +       }
        //     }
        //
        //     public static void m_84331_(int p_84332_, int p_84333_, int p_84334_) {
        //

        MethodNode _texParameter = Objects.requireNonNull(ASMUtils.findMethod(input, ASMAPI.mapMethod("m_84160_"), "(IIF)V"));
        for (AbstractInsnNode ain : _texParameter.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.RETURN) {
                InsnList il = new InsnList();
                LabelNode label_0 = new LabelNode();

                il.add(new VarInsnNode(Opcodes.ILOAD, 0));
                il.add(new LdcInsnNode(33985));
                il.add(new JumpInsnNode(Opcodes.IF_ICMPNE, label_0));
                il.add(new LabelNode());

                il.add(new VarInsnNode(Opcodes.ILOAD, 1));
                il.add(new InsnNode(Opcodes.I2F));
                il.add(new FieldInsnNode(Opcodes.PUTSTATIC, "com/mojang/blaze3d/platform/GlStateManager", "lastBrightnessX", "F"));
                il.add(new LabelNode());

                il.add(new VarInsnNode(Opcodes.FLOAD, 2));
                il.add(new FieldInsnNode(Opcodes.PUTSTATIC, "com/mojang/blaze3d/platform/GlStateManager", "lastBrightnessY", "F"));
                il.add(label_0);
            }
        }

        return input;
    }
}
