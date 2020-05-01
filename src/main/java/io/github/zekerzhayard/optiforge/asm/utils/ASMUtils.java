package io.github.zekerzhayard.optiforge.asm.utils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;

public class ASMUtils {
    @SuppressWarnings("deprecation")
    public static void replaceRedirectSurrogateMethod(ClassNode cn, String mixinClassName) {
        cn.methods.forEach(mn -> Stream.of(mn.instructions.toArray()).filter(ain -> ain.getOpcode() == Opcodes.INVOKESPECIAL || ain.getOpcode() == Opcodes.INVOKESTATIC).map(ain -> (MethodInsnNode) ain).filter(min -> isMixinMethodNodeInTargetClass(cn, min.name, min.desc, mixinClassName)).forEach(min -> findFirstOverLoadMethod(cn, min.name, min.desc, mixinClassName).ifPresent(redirectSurrogateMethod -> {
            InsnList il = createRedirectSurrogateVarInsnList(mn, redirectSurrogateMethod, min.desc);
            mn.instructions.insertBefore(min, il);
            mn.instructions.set(min, new MethodInsnNode(min.getOpcode(), min.owner, redirectSurrogateMethod.name, redirectSurrogateMethod.desc));
        })));
    }

    public static Optional<MethodNode> findFirstOverLoadMethod(ClassNode classNode, String methodName, String methodDesc, String mixinClassName) {
        return classNode.methods.stream().filter(mn -> {
            String[] methodUniqueNames = methodName.split("\\$", 3);
            return methodUniqueNames.length == 3 && methodUniqueNames[2].equals(mn.name) && !mn.desc.equals(methodDesc) && isRedirectSurrogateMethod(mn, mixinClassName);
        }).findFirst();
    }

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

    @SuppressWarnings("unchecked")
    public static InsnList createRedirectSurrogateVarInsnList(MethodNode targetMethod, MethodNode surrogateMethod, String redirectMethodDesc) {
        Integer[] ordinals = surrogateMethod.visibleAnnotations.stream().filter(an -> an.desc.equals(Type.getDescriptor(RedirectSurrogate.class))).flatMap(an -> ((List<Integer>) an.values.get(1)).stream()).toArray(Integer[]::new);
        Type[] surrogateTypes = Type.getArgumentTypes(surrogateMethod.desc);
        int redirectMethodDescTypesLength = Type.getArgumentTypes(redirectMethodDesc).length;
        InsnList il = new InsnList();
        IntStream.range(redirectMethodDescTypesLength, surrogateTypes.length).forEachOrdered(i -> il.add(new VarInsnNode(surrogateTypes[i].getOpcode(Opcodes.ILOAD), findLocalVariableIndex(targetMethod, surrogateTypes[i].getDescriptor(), ordinals[i - redirectMethodDescTypesLength]))));
        return il;
    }

    public static boolean isMixinMethodNodeInTargetClass(ClassNode cn, String methodName, String methodDesc, String mixinClassName) {
        return cn.methods.stream().anyMatch(mn -> mn.name.equals(methodName) && mn.desc.equals(methodDesc) && isMixinMethod(mn, mixinClassName));
    }

    public static boolean isMixinMethod(MethodNode mn, String mixinClassName) {
        return mn.visibleAnnotations != null && mn.visibleAnnotations.stream().anyMatch(an -> an.desc.equals(Type.getDescriptor(MixinMerged.class)) && an.values.contains(mixinClassName));
    }

    public static boolean isRedirectSurrogateMethod(MethodNode mn, String mixinClassName) {
        return isMixinMethod(mn, mixinClassName) && mn.visibleAnnotations.stream().anyMatch(an -> an.desc.equals(Type.getDescriptor(RedirectSurrogate.class)));
    }

    public static FieldInsnNode findFirstFieldInsnNode(MethodNode mn, int opcode, String owner, String name, String desc) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain instanceof FieldInsnNode && ain.getOpcode() == opcode) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.owner.equals(owner) && fin.name.equals(name) && fin.desc.equals(desc)) {
                    return fin;
                }
            }
        }
        return null;
    }
}
