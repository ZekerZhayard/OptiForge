package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer;

import io.github.zekerzhayard.optiforge.asm.transformers.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class Matrix4fTransformer implements ITransformer {
    @Override
    public boolean isTargetClass(String className) {
        return className.equals("net.minecraft.client.renderer.Matrix4f");
    }

    @Override
    public ClassNode preTransform(ClassNode cn) {
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "([F)V", null, null);
        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V"));
        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/client/renderer/Matrix4f", "optiforge_newInstance", "([F)V"));
        mn.instructions.add(new InsnNode(Opcodes.RETURN));
        cn.methods.add(mn);
        return cn;
    }
}
