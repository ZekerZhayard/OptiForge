package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer;

import java.util.Objects;

import io.github.zekerzhayard.optiforge.asm.transformers.ITransformer;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class OverlayRendererTransformer implements ITransformer {
    @Override
    public boolean isTargetClass(String className) {
        return className.equals("net.minecraft.client.renderer.OverlayRenderer");
    }

    @Override
    public ClassNode postTransform(ClassNode cn, String mixinClassName) {
        ASMUtils.replaceRedirectSurrogateMethod(cn, mixinClassName);
        cn.methods.remove(Bytecode.findMethod(cn, "getOverlayBlock", "(Lnet/minecraft/entity/player/PlayerEntity;)Lorg/apache/commons/lang3/tuple/Pair;"));
        MethodNode mn1 = Objects.requireNonNull(Bytecode.findMethod(cn, "func_230018_a_", "(Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;"));
        mn1.name = "getOverlayBlock";
        mn1.desc = "(Lnet/minecraft/entity/player/PlayerEntity;)Lorg/apache/commons/lang3/tuple/Pair;";
        AbstractInsnNode ain = Objects.requireNonNull(Bytecode.findInsn(mn1, Opcodes.ARETURN));
        mn1.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 1));
        mn1.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos$Mutable", "func_185334_h", "()Lnet/minecraft/util/math/BlockPos;"));
        mn1.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "org/apache/commons/lang3/tuple/Pair", "of", "(Ljava/lang/Object;Ljava/lang/Object;)Lorg/apache/commons/lang3/tuple/Pair;"));
        MethodNode mn2 = Objects.requireNonNull(Bytecode.findMethod(cn, "getViewBlockingState", "(Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;"));
        mn2.name = "func_230018_a_";
        return cn;
    }
}
