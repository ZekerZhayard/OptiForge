package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.network.datasync;

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
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class EntityDataManagerTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.network.datasync.EntityDataManager";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.15.x/patches/minecraft/net/minecraft/network/datasync/EntityDataManager.java.patch#L7-L8
        //
        //     public static <T> DataParameter<T> func_187226_a(Class<? extends Entity> p_187226_0_, IDataSerializer<T> p_187226_1_) {
        // -      if (field_190303_a.isDebugEnabled()) {
        // +      if (true || field_190303_a.isDebugEnabled()) { // Forge: This is very useful for mods that register keys on classes that are not their own
        //           try {
        //

        MethodNode createKey = Objects.requireNonNull(ASMUtils.findMethod(input, ASMAPI.mapMethod("func_187226_a"), "(Ljava/lang/Class;Lnet/minecraft/network/datasync/IDataSerializer;)Lnet/minecraft/network/datasync/DataParameter;"));

        int loggerCount = 0;
        LabelNode label_0 = new LabelNode();
        for (AbstractInsnNode ain : createKey.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.GETSTATIC) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.owner.equals("net/minecraft/network/datasync/EntityDataManager") && fin.name.equals(ASMAPI.mapField("field_190303_a")) && fin.desc.equals("Lorg/apache/logging/log4j/Logger;")) {
                    loggerCount++;
                    if (loggerCount == 1) {

                        // label 0
                        // line 52
                        // remove -> getstatic Logger EntityDataManager.field_190303_a
                        // remove -> invokeinterface boolean Logger.isDebugEnabled()
                        // remove -> ifeq 6
                        // label 1
                        // line 56
                        // invokestatic Thread Thread.currentThread()
                        AbstractInsnNode ain0 = fin;
                        while (!(ain0 instanceof LabelNode)) {
                            ain0 = ain0.getNext();
                            createKey.instructions.remove(ain0.getPrevious());
                        }
                    } else if (loggerCount == 2) {

                        // https://github.com/MinecraftForge/MinecraftForge/blob/1.15.x/patches/minecraft/net/minecraft/network/datasync/EntityDataManager.java.patch#L12-L15
                        //
                        //              if (!oclass.equals(p_187226_0_)) {
                        // -               field_190303_a.debug("defineId called for: {} from {}", p_187226_0_, oclass, new RuntimeException());
                        // +               // Forge: log at warn, mods should not add to classes that they don't own, and only add stacktrace when in debug is enabled as it is mostly not needed and consumes time
                        // +               if (field_190303_a.isDebugEnabled()) field_190303_a.warn("defineId called for: {} from {}", p_187226_0_, oclass, new RuntimeException());
                        // +               else field_190303_a.warn("defineId called for: {} from {}", p_187226_0_, oclass);
                        //              }
                        //

                        InsnList il = new InsnList();
                        il.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/network/datasync/EntityDataManager", ASMAPI.mapField("field_190303_a"), "Lorg/apache/logging/log4j/Logger;"));
                        il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "isDebugEnabled", "()Z", true));
                        il.add(new JumpInsnNode(Opcodes.IFEQ, label_0));

                        createKey.instructions.insertBefore(fin, il);
                    }
                }
            } else if (ain.getOpcode() == Opcodes.INVOKEINTERFACE) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("org/apache/logging/log4j/Logger") && min.name.equals("debug") && min.desc.equals("(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V")) {
                    min.name = "warn";

                    AbstractInsnNode ain0 = min;
                    while (!(ain0 instanceof LabelNode)) {
                        ain0 = ain0.getNext();
                    }
                    LabelNode ln = (LabelNode) ain0;

                    InsnList il = new InsnList();
                    il.add(new JumpInsnNode(Opcodes.GOTO, ln));
                    il.add(label_0);
                    il.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/network/datasync/EntityDataManager", ASMAPI.mapField("field_190303_a"), "Lorg/apache/logging/log4j/Logger;"));
                    il.add(new LdcInsnNode("defineId called for: {} from {}"));
                    il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    il.add(new VarInsnNode(Opcodes.ALOAD, ASMUtils.findLocalVariableIndex(createKey, "Ljava/lang/Class;", 1)));
                    il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "warn", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", true));

                    createKey.instructions.insert(min, il);

                    break;
                }
            }
        }

        return input;
    }
}
