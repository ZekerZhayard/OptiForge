package io.github.zekerzhayard.optiforge.asm.utils;

import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;
import org.spongepowered.asm.util.Annotations;

public class ASMUtils {
    /**
     * Find the target local variable index by specific desc and ordinal.
     * @param mn the method to search
     * @param desc the local variable desc
     * @param ordinal the local variable ordinal
     * @return the local variable index
     */
    public static int findLocalVariableIndex(MethodNode mn, String desc, int ordinal) {
        List<LocalVariableNode> localVariables = Lists.newArrayList(mn.localVariables);
        localVariables.sort(Comparator.comparingInt(o -> o.index));
        for (LocalVariableNode lvn : localVariables) {
            if (lvn.desc.equals(desc)) {
                if (ordinal == 0) {
                    return lvn.index;
                }
                ordinal--;
            }
        }
        return -1;
    }

    public static void insertLocalVariable(MethodNode mn, LocalVariableNode lvn) {
        int shift = lvn.desc.equals("J") || lvn.desc.equals("D") ? 2 : 1;
        for (LocalVariableNode node : mn.localVariables) {
            if (node.index >= lvn.index) {
                node.index += shift;
            }
        }
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if ((ain.getOpcode() >= Opcodes.ILOAD && ain.getOpcode() <= Opcodes.ALOAD) || (ain.getOpcode() >= Opcodes.ISTORE && ain.getOpcode() <= Opcodes.ASTORE)) {
                VarInsnNode vin = (VarInsnNode) ain;
                if (vin.var >= lvn.index) {
                    vin.var += shift;
                }
            } else if (ain.getOpcode() == Opcodes.IINC) {
                IincInsnNode iin = (IincInsnNode) ain;
                if (iin.var >= lvn.index) {
                    iin.var += shift;
                }
            }
        }
        mn.localVariables.add(lvn);
    }

    public static boolean isMixinMethod(MethodNode mn, String mixinClassName) {
        return mixinClassName.equals(Annotations.getValue(Annotations.getVisible(mn, MixinMerged.class), "mixin"));
    }
}
