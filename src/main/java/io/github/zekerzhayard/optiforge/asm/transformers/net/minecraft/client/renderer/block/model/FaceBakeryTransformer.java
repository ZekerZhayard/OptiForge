package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.block.model;

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
        return "net.minecraft.client.renderer.block.model.FaceBakery";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.17.x/patches/minecraft/net/minecraft/client/renderer/block/model/FaceBakery.java.patch#L15-L18
        //
        //        p_111615_[i + 1] = Float.floatToRawIntBits(p_111617_.m_122260_());
        //        p_111615_[i + 2] = Float.floatToRawIntBits(p_111617_.m_122269_());
        //        p_111615_[i + 3] = -1;
        // -      p_111615_[i + 4] = Float.floatToRawIntBits(p_111618_.m_118367_((double)p_111619_.m_111392_(p_111616_)));
        // -      p_111615_[i + 4 + 1] = Float.floatToRawIntBits(p_111618_.m_118393_((double)p_111619_.m_111396_(p_111616_)));
        // +      p_111615_[i + 4] = Float.floatToRawIntBits(p_111618_.m_118367_((double)p_111619_.m_111392_(p_111616_) * .999 + p_111619_.m_111392_((p_111616_ + 2) % 4) * .001));
        // +      p_111615_[i + 4 + 1] = Float.floatToRawIntBits(p_111618_.m_118393_((double)p_111619_.m_111396_(p_111616_) * .999 + p_111619_.m_111396_((p_111616_ + 2) % 4) * .001));
        //     }
        //
        //     private void m_111586_(Vector3f p_111587_, @Nullable BlockElementRotation p_111588_) {
        //

        MethodNode fillVertex = Objects.requireNonNull(ASMUtils.findMethod(input, ASMAPI.mapMethod("m_111614_"), "([IILcom/mojang/math/Vector3f;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/minecraft/client/renderer/block/model/BlockFaceUV;)V"));

        for (AbstractInsnNode ain : fillVertex.instructions.toArray()) {
            if (ain instanceof MethodInsnNode min && min.getOpcode() == Opcodes.INVOKEVIRTUAL && Objects.equals(min.owner, "net/minecraft/client/renderer/texture/TextureAtlasSprite") && (Objects.equals(min.name, ASMAPI.mapMethod("m_118367_")) || Objects.equals(min.name, ASMAPI.mapMethod("m_118393_"))) && Objects.equals(min.desc, "(D)F")) {
                    InsnList il = new InsnList();

                    il.add(new LdcInsnNode(0.999D));
                    il.add(new InsnNode(Opcodes.DMUL));
                    il.add(new VarInsnNode(Opcodes.ALOAD, 5));
                    il.add(new VarInsnNode(Opcodes.ILOAD, 2));
                    il.add(new InsnNode(Opcodes.ICONST_2));
                    il.add(new InsnNode(Opcodes.IADD));
                    il.add(new InsnNode(Opcodes.ICONST_4));
                    il.add(new InsnNode(Opcodes.IREM));
                    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/block/model/BlockFaceUV", ASMAPI.mapMethod(min.name.equals(ASMAPI.mapMethod("m_118367_")) ? "m_111392_" : "m_111396_"), "(I)F", false));
                    il.add(new InsnNode(Opcodes.F2D));
                    il.add(new LdcInsnNode(0.001D));
                    il.add(new InsnNode(Opcodes.DMUL));
                    il.add(new InsnNode(Opcodes.DADD));

                    fillVertex.instructions.insertBefore(min, il);
            }
        }

        return input;
    }
}
