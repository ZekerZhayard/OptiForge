package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client;

import java.util.ArrayList;
import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class MainWindowTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.MainWindow";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/MainWindow.java.patch#L16
        //
        //        this.field_198131_r = aint[0];
        //        this.field_198132_s = aint1[0];
        // +      if (this.field_198132_s == 0 || this.field_198131_r==0) net.minecraftforge.fml.loading.progress.EarlyProgressVisualization.INSTANCE.updateFBSize(w->this.field_198131_r=w, h->this.field_198132_s=h);
        //     }
        //

        MethodNode updateFramebufferSize = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_198103_w"), "()V"));

        for (AbstractInsnNode ain : updateFramebufferSize.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.RETURN) {
                LabelNode label_0 = new LabelNode();
                LabelNode label_1 = new LabelNode();

                InsnList il = new InsnList();
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/MainWindow", ASMAPI.mapField("field_198132_s"), "I"));
                il.add(new JumpInsnNode(Opcodes.IFEQ, label_0));
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/MainWindow", ASMAPI.mapField("field_198131_r"), "I"));
                il.add(new JumpInsnNode(Opcodes.IFNE, label_1));

                il.add(label_0);
                il.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/fml/loading/progress/EarlyProgressVisualization", "INSTANCE", "Lnet/minecraftforge/fml/loading/progress/EarlyProgressVisualization;"));
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new InvokeDynamicInsnNode("accept", "(Lnet/minecraft/client/MainWindow;)Ljava/util/function/IntConsumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(I)V"), new Handle(Opcodes.H_INVOKESPECIAL, "net/minecraft/client/MainWindow", "lambda$updateFramebufferSize$4", "(I)V", false), Type.getType("(I)V")));
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new InvokeDynamicInsnNode("accept", "(Lnet/minecraft/client/MainWindow;)Ljava/util/function/IntConsumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(I)V"), new Handle(Opcodes.H_INVOKESPECIAL, "net/minecraft/client/MainWindow", "lambda$updateFramebufferSize$5", "(I)V", false), Type.getType("(I)V")));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/loading/progress/EarlyProgressVisualization", "updateFBSize", "(Ljava/util/function/IntConsumer;Ljava/util/function/IntConsumer;)V", false));
                il.add(label_1);

                updateFramebufferSize.instructions.insertBefore(ain, il);
            }
        }


        MethodNode lambda$updateFramebufferSize$5 = new MethodNode();
        lambda$updateFramebufferSize$5.access = Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC;
        lambda$updateFramebufferSize$5.name = "lambda$updateFramebufferSize$5";
        lambda$updateFramebufferSize$5.desc = "(I)V";

        LabelNode lambda$updateFramebufferSize$5_label_0 = new LabelNode();
        LabelNode lambda$updateFramebufferSize$5_label_1 = new LabelNode();
        lambda$updateFramebufferSize$5.localVariables = new ArrayList<>();
        lambda$updateFramebufferSize$5.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/client/MainWindow;", null, lambda$updateFramebufferSize$5_label_0, lambda$updateFramebufferSize$5_label_1, 0));
        lambda$updateFramebufferSize$5.localVariables.add(new LocalVariableNode("h", "I", null, lambda$updateFramebufferSize$5_label_0, lambda$updateFramebufferSize$5_label_1, 1));

        lambda$updateFramebufferSize$5.instructions.add(lambda$updateFramebufferSize$5_label_0);
        lambda$updateFramebufferSize$5.instructions.add(new LineNumberNode(274, lambda$updateFramebufferSize$5_label_0));
        lambda$updateFramebufferSize$5.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        lambda$updateFramebufferSize$5.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
        lambda$updateFramebufferSize$5.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/MainWindow", ASMAPI.mapField("field_198132_s"), "I"));
        lambda$updateFramebufferSize$5.instructions.add(new InsnNode(Opcodes.RETURN));
        lambda$updateFramebufferSize$5.instructions.add(lambda$updateFramebufferSize$5_label_1);

        input.methods.add(input.methods.indexOf(Bytecode.findMethod(input, "lambda$new$3", "(Lnet/minecraft/client/Monitor;)J")), lambda$updateFramebufferSize$5);


        MethodNode lambda$updateFramebufferSize$4 = new MethodNode();
        lambda$updateFramebufferSize$4.access = Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC;
        lambda$updateFramebufferSize$4.name = "lambda$updateFramebufferSize$4";
        lambda$updateFramebufferSize$4.desc = "(I)V";

        LabelNode lambda$updateFramebufferSize$4_label_0 = new LabelNode();
        LabelNode lambda$updateFramebufferSize$4_label_1 = new LabelNode();
        lambda$updateFramebufferSize$4.localVariables = new ArrayList<>();
        lambda$updateFramebufferSize$4.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/client/MainWindow;", null, lambda$updateFramebufferSize$4_label_0, lambda$updateFramebufferSize$4_label_1, 0));
        lambda$updateFramebufferSize$4.localVariables.add(new LocalVariableNode("w", "I", null, lambda$updateFramebufferSize$4_label_0, lambda$updateFramebufferSize$4_label_1, 1));

        lambda$updateFramebufferSize$4.instructions.add(lambda$updateFramebufferSize$4_label_0);
        lambda$updateFramebufferSize$4.instructions.add(new LineNumberNode(274, lambda$updateFramebufferSize$4_label_0));
        lambda$updateFramebufferSize$4.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        lambda$updateFramebufferSize$4.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
        lambda$updateFramebufferSize$4.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/MainWindow", ASMAPI.mapField("field_198131_r"), "I"));
        lambda$updateFramebufferSize$4.instructions.add(new InsnNode(Opcodes.RETURN));
        lambda$updateFramebufferSize$4.instructions.add(lambda$updateFramebufferSize$4_label_1);

        input.methods.add(input.methods.indexOf(Bytecode.findMethod(input, "lambda$new$3", "(Lnet/minecraft/client/Monitor;)J")), lambda$updateFramebufferSize$4);

        return input;
    }
}
