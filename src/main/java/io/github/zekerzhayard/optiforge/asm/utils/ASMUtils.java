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

public class ASMUtils {
    public static LocalVariableNode findLocalVariable(MethodNode mn, String desc, int ordinal) {
        List<LocalVariableNode> localVariables = Lists.newArrayList(mn.localVariables);
        localVariables.sort(Comparator.comparingInt(o -> o.index));
        for (LocalVariableNode lvn : localVariables) {
            if (lvn.desc.equals(desc)) {
                if (ordinal == 0) {
                    return lvn;
                }
                ordinal--;
            }
        }
        return null;
    }

    /**
     * Find the target local variable index by specific desc and ordinal.
     * @param mn the method to search
     * @param desc the local variable desc
     * @param ordinal the local variable ordinal
     * @return the local variable index
     */
    public static int findLocalVariableIndex(MethodNode mn, String desc, int ordinal) {
        LocalVariableNode lvn = findLocalVariable(mn, desc, ordinal);
        return lvn == null ? -1 : lvn.index;
    }

    public static void insertLocalVariable(MethodNode mn, LocalVariableNode lvn, int index) {
        insertLocalVariable(mn.localVariables, mn.instructions.toArray(), lvn, index);
    }

    public static void insertLocalVariable(List<LocalVariableNode> lvns, AbstractInsnNode[] ains, LocalVariableNode lvn, int index) {
        int shift = lvn.desc.equals("J") || lvn.desc.equals("D") ? 2 : 1;
        for (LocalVariableNode node : lvns) {
            if (node.index >= lvn.index) {
                node.index += shift;
            }
        }
        for (AbstractInsnNode ain : ains) {
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
        lvns.add(index, lvn);
    }

    public static int getNextOfMaxLocalVariableIndex(List<LocalVariableNode> lvns) {
        LocalVariableNode maxIndexLvn = null;
        for (LocalVariableNode lvn : lvns) {
            if (maxIndexLvn == null || maxIndexLvn.index < lvn.index) {
                maxIndexLvn = lvn;
            }
        }
        return maxIndexLvn == null ? 0 : maxIndexLvn.index + (maxIndexLvn.desc.equals("J") || maxIndexLvn.desc.equals("D") ? 2 : 1);
    }
}
