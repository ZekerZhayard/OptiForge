package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.texture;

import java.util.ArrayList;
import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class StitcherTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.texture.Stitcher";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/texture/Stitcher.java.patch#L7
        //
        //  @OnlyIn(Dist.CLIENT)
        //  public class Stitcher {
        // +   private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger();
        // +
        //

        input.fields.add(0, new FieldNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, "LOGGER", "Lorg/apache/logging/log4j/Logger;", null, null));

        MethodNode _clinit_ = Objects.requireNonNull(ASMUtils.findMethod(input, "<clinit>", "()V"));
        InsnList il$_clinit_ = new InsnList();
        il$_clinit_.add(new LabelNode());
        il$_clinit_.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/apache/logging/log4j/LogManager", "getLogger", "()Lorg/apache/logging/log4j/Logger;", false));
        il$_clinit_.add(new FieldInsnNode(Opcodes.PUTSTATIC, "net/minecraft/client/renderer/texture/Stitcher", "LOGGER", "Lorg/apache/logging/log4j/Logger;"));
        _clinit_.instructions.insert(il$_clinit_);

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.15.x/patches/minecraft/net/minecraft/client/renderer/texture/Stitcher.java.patch#L16-L21
        //
        //           if (!this.func_94310_b(stitcher$holder)) {
        // +            LOGGER.info(new net.minecraftforge.fml.loading.AdvancedLogMessageAdapter(sb->{
        // +               sb.append("Unable to fit: ").append(stitcher$holder.field_229213_a_.func_229248_a_());
        // +               sb.append(" - size: ").append(stitcher$holder.field_229213_a_.func_229250_b_()).append("x").append(stitcher$holder.field_229213_a_.func_229252_c_());
        // +               sb.append(" - Maybe try a lower resolution resourcepack?\n");
        // +               list.forEach(h-> sb.append("\t").append(h).append("\n"));
        // +            }));
        //              throw new StitcherException(stitcher$holder.field_229213_a_, list.stream().map((p_229212_0_) -> {
        //

        MethodNode doStitch = Objects.requireNonNull(ASMUtils.findMethod(input, ASMAPI.mapMethod("func_94305_f"), "()V"));
        for (AbstractInsnNode ain : doStitch.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.NEW) {
                TypeInsnNode tin = (TypeInsnNode) ain;
                if (tin.desc.equals("net/minecraft/client/renderer/StitcherException")) {
                    InsnList il = new InsnList();
                    il.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/texture/Stitcher", "LOGGER", "Lorg/apache/logging/log4j/Logger;"));
                    il.add(new TypeInsnNode(Opcodes.NEW, "net/minecraftforge/fml/loading/AdvancedLogMessageAdapter"));
                    il.add(new InsnNode(Opcodes.DUP));
                    il.add(new VarInsnNode(Opcodes.ALOAD, ASMUtils.findLocalVariableIndex(doStitch, "Lnet/minecraft/client/renderer/texture/Stitcher$Holder;", 0)));
                    il.add(new VarInsnNode(Opcodes.ALOAD, ASMUtils.findLocalVariableIndex(doStitch, "Ljava/util/List;", 0)));
                    il.add(new InvokeDynamicInsnNode("accept", "(Lnet/minecraft/client/renderer/texture/Stitcher$Holder;Ljava/util/List;)Ljava/util/function/Consumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)V"), new Handle(Opcodes.H_INVOKESTATIC, "net/minecraft/client/renderer/texture/Stitcher", "lambda$doStitch$4", "(Lnet/minecraft/client/renderer/texture/Stitcher$Holder;Ljava/util/List;Ljava/lang/StringBuilder;)V", false), Type.getType("(Ljava/lang/StringBuilder;)V")));
                    il.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraftforge/fml/loading/AdvancedLogMessageAdapter", "<init>", "(Ljava/util/function/Consumer;)V", false));
                    il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "info", "(Lorg/apache/logging/log4j/message/Message;)V", true));
                    il.add(new LabelNode());

                    doStitch.instructions.insertBefore(tin, il);
                    break;
                }
            }
        }


        MethodNode lambda$doStitch$4 = new MethodNode();
        lambda$doStitch$4.access = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC;
        lambda$doStitch$4.name = "lambda$doStitch$4";
        lambda$doStitch$4.desc = "(Lnet/minecraft/client/renderer/texture/Stitcher$Holder;Ljava/util/List;Ljava/lang/StringBuilder;)V";

        LabelNode label_0 = new LabelNode();
        LabelNode label_1 = new LabelNode();
        LabelNode label_2 = new LabelNode();
        LabelNode label_3 = new LabelNode();
        LabelNode label_4 = new LabelNode();
        LabelNode label_5 = new LabelNode();
        lambda$doStitch$4.localVariables = new ArrayList<>();
        lambda$doStitch$4.localVariables.add(new LocalVariableNode("stitcher$holder", "Lnet/minecraft/client/renderer/texture/Stitcher$Holder;", null, label_0, label_5, 0));
        lambda$doStitch$4.localVariables.add(new LocalVariableNode("list", "Ljava/util/List;", null, label_0, label_5, 1));
        lambda$doStitch$4.localVariables.add(new LocalVariableNode("sb", "Ljava/lang/StringBuilder;", null, label_0, label_5, 2));

        // method body start
        lambda$doStitch$4.instructions.add(label_0);
        lambda$doStitch$4.instructions.add(new LineNumberNode(60, label_0));
        lambda$doStitch$4.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        lambda$doStitch$4.instructions.add(new LdcInsnNode("Unable to fit: "));
        lambda$doStitch$4.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
        lambda$doStitch$4.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        lambda$doStitch$4.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/texture/Stitcher$Holder", ASMAPI.mapField("field_229213_a_"), "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;"));
        lambda$doStitch$4.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/texture/TextureAtlasSprite$Info", ASMAPI.mapMethod("func_229248_a_"), "()Lnet/minecraft/util/ResourceLocation;", false));
        lambda$doStitch$4.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false));
        lambda$doStitch$4.instructions.add(new InsnNode(Opcodes.POP));

        lambda$doStitch$4.instructions.add(label_1);
        lambda$doStitch$4.instructions.add(new LineNumberNode(61, label_1));
        lambda$doStitch$4.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        lambda$doStitch$4.instructions.add(new LdcInsnNode(" - size: "));
        lambda$doStitch$4.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
        lambda$doStitch$4.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        lambda$doStitch$4.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/texture/Stitcher$Holder", ASMAPI.mapField("field_229213_a_"), "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;"));
        lambda$doStitch$4.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/texture/TextureAtlasSprite$Info", ASMAPI.mapMethod("func_229250_b_"), "()I", false));
        lambda$doStitch$4.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false));
        lambda$doStitch$4.instructions.add(new LdcInsnNode("x"));
        lambda$doStitch$4.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
        lambda$doStitch$4.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        lambda$doStitch$4.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/texture/Stitcher$Holder", ASMAPI.mapField("field_229213_a_"), "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;"));
        lambda$doStitch$4.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/texture/TextureAtlasSprite$Info", ASMAPI.mapMethod("func_229252_c_"), "()I", false));
        lambda$doStitch$4.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false));
        lambda$doStitch$4.instructions.add(new InsnNode(Opcodes.POP));

        lambda$doStitch$4.instructions.add(label_2);
        lambda$doStitch$4.instructions.add(new LineNumberNode(62, label_2));
        lambda$doStitch$4.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        lambda$doStitch$4.instructions.add(new LdcInsnNode(" - Maybe try a lower resolution resourcepack?\n"));
        lambda$doStitch$4.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
        lambda$doStitch$4.instructions.add(new InsnNode(Opcodes.POP));

        lambda$doStitch$4.instructions.add(label_3);
        lambda$doStitch$4.instructions.add(new LineNumberNode(63, label_3));
        lambda$doStitch$4.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        lambda$doStitch$4.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        lambda$doStitch$4.instructions.add(new InvokeDynamicInsnNode("accept", "(Ljava/lang/StringBuilder;)Ljava/util/function/Consumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)V"), new Handle(Opcodes.H_INVOKESTATIC, "net/minecraft/client/renderer/texture/Stitcher", "lambda$null$3", "(Ljava/lang/StringBuilder;Lnet/minecraft/client/renderer/texture/Stitcher$Holder;)V", false), Type.getType("(Lnet/minecraft/client/renderer/texture/Stitcher$Holder;)V")));
        lambda$doStitch$4.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "forEach", "(Ljava/util/function/Consumer;)V", true));

        lambda$doStitch$4.instructions.add(label_4);
        lambda$doStitch$4.instructions.add(new LineNumberNode(64, label_4));
        lambda$doStitch$4.instructions.add(new InsnNode(Opcodes.RETURN));
        lambda$doStitch$4.instructions.add(label_5);
        // method body end

        input.methods.add(input.methods.indexOf(ASMUtils.findMethod(input, "lambda$doStitch$3", "(Lnet/minecraft/client/renderer/texture/Stitcher$Holder;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;")), lambda$doStitch$4);


        MethodNode lambda$null$3 = new MethodNode();
        lambda$null$3.access = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC;
        lambda$null$3.name = "lambda$null$3";
        lambda$null$3.desc = "(Ljava/lang/StringBuilder;Lnet/minecraft/client/renderer/texture/Stitcher$Holder;)V";

        LabelNode label_0_ = new LabelNode();
        LabelNode label_1_ = new LabelNode();
        lambda$null$3.localVariables = new ArrayList<>();
        lambda$null$3.localVariables.add(new LocalVariableNode("sb", "Ljava/lang/StringBuilder;", null, label_0_, label_1_, 0));
        lambda$null$3.localVariables.add(new LocalVariableNode("h", "Lnet/minecraft/client/renderer/texture/Stitcher$Holder;", null, label_0_, label_1_, 1));

        // method body start
        lambda$null$3.instructions.add(label_0_);
        lambda$null$3.instructions.add(new LineNumberNode(63, label_0_));
        lambda$null$3.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        lambda$null$3.instructions.add(new LdcInsnNode("\t"));
        lambda$null$3.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
        lambda$null$3.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        lambda$null$3.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false));
        lambda$null$3.instructions.add(new LdcInsnNode("\n"));
        lambda$null$3.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
        lambda$null$3.instructions.add(new InsnNode(Opcodes.POP));
        lambda$null$3.instructions.add(new InsnNode(Opcodes.RETURN));
        lambda$null$3.instructions.add(label_1_);
        // method body end

        input.methods.add(input.methods.indexOf(ASMUtils.findMethod(input, "lambda$doStitch$3", "(Lnet/minecraft/client/renderer/texture/Stitcher$Holder;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;")) + 1, lambda$null$3);


        return input;
    }
}
