package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.world.server;

import java.util.Objects;

import io.github.zekerzhayard.optiforge.asm.transformers.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.util.Bytecode;

public class ChunkManagerTransformer implements ITransformer {
    @Override
    public boolean isTargetClass(String className) {
        return className.equals("net.minecraft.world.server.ChunkManager");
    }

    @Override
    public ClassNode postTransform(ClassNode cn, String mixinClassName) {
        MethodNode mn = Objects.requireNonNull(Bytecode.findMethod(cn, "lambda$null$25", "(Lnet/minecraft/world/server/ChunkHolder;Lnet/minecraft/world/chunk/IChunk;)Lnet/minecraft/world/chunk/IChunk;"));
        LabelNode ln = null, newLn = new LabelNode();
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.INVOKEINTERFACE) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("it/unimi/dsi/fastutil/longs/LongSet") && min.name.equals("add") && min.desc.equals("(J)Z")) {
                    ln = ((JumpInsnNode) min.getNext()).label;
                } else if (ln != null && min.owner.equals("java/util/List") && min.name.equals("forEach") && min.desc.equals("(Ljava/util/function/Consumer;)V")) {
                    mn.instructions.insert(min, newLn);
                }
            } else if (ln != null && ain.getOpcode() == Opcodes.IFNULL) {
                JumpInsnNode jin = (JumpInsnNode) ain;
                if (jin.label.equals(ln)) {
                    jin.label = newLn;
                }
            }
        }
        return cn;
    }
}
