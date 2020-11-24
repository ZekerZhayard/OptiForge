package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.model;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class FaceBakeryTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.model.FaceBakery";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.15.x/patches/minecraft/net/minecraft/client/renderer/model/FaceBakery.java.patch#L26-L29
        //
        //        p_228826_1_[i + 3] = p_228826_4_;
        // -      p_228826_1_[i + 4] = Float.floatToRawIntBits(p_228826_5_.func_94214_a((double)p_228826_6_.func_178348_a(p_228826_2_)));
        // -      p_228826_1_[i + 4 + 1] = Float.floatToRawIntBits(p_228826_5_.func_94207_b((double)p_228826_6_.func_178346_b(p_228826_2_)));
        // +      p_228826_1_[i + 4] = Float.floatToRawIntBits(p_228826_5_.func_94214_a((double)p_228826_6_.func_178348_a(p_228826_2_)  * .999 + p_228826_6_.func_178348_a((p_228826_2_ + 2) % 4) * .001));
        // +      p_228826_1_[i + 4 + 1] = Float.floatToRawIntBits(p_228826_5_.func_94207_b((double)p_228826_6_.func_178346_b(p_228826_2_) * .999 + p_228826_6_.func_178346_b((p_228826_2_ + 2) % 4) * .001));
        //     }
        //

        MethodNode fillVertex = Objects.requireNonNull(ASMUtils.findMethod(input, ASMAPI.mapMethod("func_228826_a_"), "([IILnet/minecraft/client/renderer/Vector3f;ILnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/minecraft/client/renderer/model/BlockFaceUV;)V"));

        for (AbstractInsnNode ain : fillVertex.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("net/minecraft/client/renderer/texture/TextureAtlasSprite") && (min.name.equals(ASMAPI.mapMethod("func_94214_a")) || min.name.equals(ASMAPI.mapMethod("func_94207_b"))) && min.desc.equals("(D)F")) {
                    InsnList il = new InsnList();
                    il.add(new LdcInsnNode(0.999D));
                    il.add(new InsnNode(Opcodes.DMUL));
                    il.add(new VarInsnNode(Opcodes.ALOAD, 6));
                    il.add(new VarInsnNode(Opcodes.ILOAD, 2));
                    il.add(new InsnNode(Opcodes.ICONST_2));
                    il.add(new InsnNode(Opcodes.IADD));
                    il.add(new InsnNode(Opcodes.ICONST_4));
                    il.add(new InsnNode(Opcodes.IREM));
                    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/model/BlockFaceUV", ASMAPI.mapMethod(min.name.equals(ASMAPI.mapMethod("func_94214_a")) ? "func_178348_a" : "func_178346_b"), "(I)F", false));
                    il.add(new InsnNode(Opcodes.F2D));
                    il.add(new LdcInsnNode(0.001D));
                    il.add(new InsnNode(Opcodes.DMUL));
                    il.add(new InsnNode(Opcodes.DADD));
                    fillVertex.instructions.insertBefore(min, il);
                }
            }
        }

        return input;
    }
}
