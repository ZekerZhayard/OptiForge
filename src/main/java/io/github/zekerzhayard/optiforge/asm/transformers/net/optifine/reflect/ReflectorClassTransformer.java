package io.github.zekerzhayard.optiforge.asm.transformers.net.optifine.reflect;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class ReflectorClassTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.optifine.reflect.ReflectorClass";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        MethodNode getTargetClass = Objects.requireNonNull(Bytecode.findMethod(input, "getTargetClass", "()Ljava/lang/Class;"));

        ASMAPI.insertInsnList(getTargetClass, ASMAPI.MethodType.STATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;",
            ASMAPI.listOf(
                new InsnNode(Opcodes.ICONST_0),
                new VarInsnNode(Opcodes.ALOAD, 0),
                new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/optifine/reflect/ReflectorClass", "getClass", "()Ljava/lang/Class;"),
                new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;"),
                new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;")
            ),
            ASMAPI.InsertMode.REMOVE_ORIGINAL
        );

        return input;
    }
}
