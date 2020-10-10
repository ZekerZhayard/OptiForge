package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.util;

import java.util.ArrayList;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class ResourceLocationTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.util.ResourceLocation";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/util/ResourceLocation.java.patch#L7-L12
        //
        // +   // Normal compare sorts by path first, this compares namespace first.
        // +   public int compareNamespaced(ResourceLocation o) {
        // +      int ret = this.field_110626_a.compareTo(o.field_110626_a);
        // +      return ret != 0 ? ret : this.field_110625_b.compareTo(o.field_110625_b);
        // +   }
        // +
        //

        MethodNode compareNamespaced = new MethodNode();
        compareNamespaced.access = Opcodes.ACC_PUBLIC;
        compareNamespaced.name = "compareNamespaced";
        compareNamespaced.desc = "(Lnet/minecraft/util/ResourceLocation;)I";

        LabelNode label_0 = new LabelNode();
        LabelNode label_1 = new LabelNode();
        LabelNode label_2 = new LabelNode();
        LabelNode label_3 = new LabelNode();
        LabelNode label_4 = new LabelNode();
        compareNamespaced.localVariables = new ArrayList<>();
        compareNamespaced.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/util/ResourceLocation;", null, label_0, label_4, 0));
        compareNamespaced.localVariables.add(new LocalVariableNode("o", "Lnet/minecraft/util/ResourceLocation;", null, label_0, label_4, 1));
        compareNamespaced.localVariables.add(new LocalVariableNode("ret", "I", null, label_1, label_4, 2));

        // method body start
        compareNamespaced.instructions.add(label_0);
        compareNamespaced.instructions.add(new LineNumberNode(117, label_0));
        compareNamespaced.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        compareNamespaced.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/util/ResourceLocation", ASMAPI.mapField("field_110626_a"), "Ljava/lang/String;"));
        compareNamespaced.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        compareNamespaced.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/util/ResourceLocation", ASMAPI.mapField("field_110626_a"), "Ljava/lang/String;"));
        compareNamespaced.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "compareTo", "(Ljava/lang/String;)I", false));
        compareNamespaced.instructions.add(new VarInsnNode(Opcodes.ISTORE, 2));

        compareNamespaced.instructions.add(label_1);
        compareNamespaced.instructions.add(new LineNumberNode(118, label_1));
        compareNamespaced.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
        compareNamespaced.instructions.add(new JumpInsnNode(Opcodes.IFEQ, label_2));
        compareNamespaced.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
        compareNamespaced.instructions.add(new JumpInsnNode(Opcodes.GOTO, label_3));

        compareNamespaced.instructions.add(label_2);
        compareNamespaced.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        compareNamespaced.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/util/ResourceLocation", ASMAPI.mapField("field_110625_b"), "Ljava/lang/String;"));
        compareNamespaced.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        compareNamespaced.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/util/ResourceLocation", ASMAPI.mapField("field_110625_b"), "Ljava/lang/String;"));
        compareNamespaced.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "compareTo", "(Ljava/lang/String;)I", false));

        compareNamespaced.instructions.add(label_3);
        compareNamespaced.instructions.add(new InsnNode(Opcodes.IRETURN));
        compareNamespaced.instructions.add(label_4);
        // method body end

        input.methods.add(input.methods.indexOf(Bytecode.findMethod(input, ASMAPI.mapMethod("func_195826_a"), "(Lcom/mojang/brigadier/StringReader;)Lnet/minecraft/util/ResourceLocation;")), compareNamespaced);

        return input;
    }
}
