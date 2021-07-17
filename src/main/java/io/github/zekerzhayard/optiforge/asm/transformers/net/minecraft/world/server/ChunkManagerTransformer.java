package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.world.server;

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
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class ChunkManagerTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.world.server.ChunkManager";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/world/server/ChunkManager.java.patch#L15-L16
        //
        //              chunk.func_217318_w();
        //              if (this.field_219254_h.add(chunkpos.func_201841_a())) {
        //                 chunk.func_177417_c(true);
        // +               try {
        // +               p_219200_1_.currentlyLoading = chunk; //Forge - bypass the future chain when getChunk is called, this prevents deadlocks.
        //                 this.field_219255_i.func_147448_a(chunk.func_177434_r().values());
        //                 List<Entity> list = null;
        //                 ClassInheritanceMultiMap<Entity>[] aclassinheritancemultimap = chunk.func_177429_s();
        //

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/world/server/ChunkManager.java.patch#L25-L27
        //
        //                 if (list != null) {
        //                    list.forEach(chunk::func_76622_b);
        //                 }
        // +               net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(chunk));
        // +               } finally {
        // +                   p_219200_1_.currentlyLoading = null;//Forge - Stop bypassing the future chain.
        // +               }
        //              }
        //
        //              return chunk;
        //

        MethodNode lambda$null$25 = Objects.requireNonNull(Bytecode.findMethod(input, "lambda$null$25", "(Lnet/minecraft/world/server/ChunkHolder;Lnet/minecraft/world/chunk/IChunk;)Lnet/minecraft/world/chunk/IChunk;"));

        LabelNode label_0 = new LabelNode();
        LabelNode label_1 = new LabelNode();
        LabelNode label_2 = new LabelNode();
        LabelNode label_3 = new LabelNode();
        lambda$null$25.tryCatchBlocks.add(new TryCatchBlockNode(label_0, label_1, label_2, null));
        lambda$null$25.tryCatchBlocks.add(new TryCatchBlockNode(label_2, label_3, label_2, null));

        for (AbstractInsnNode ain : lambda$null$25.instructions.toArray()) {
            if (ain instanceof MethodInsnNode) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.getOpcode() == Opcodes.INVOKEVIRTUAL && min.owner.equals("net/minecraft/world/chunk/Chunk") && min.name.equals(ASMAPI.mapMethod("func_177417_c")) && min.desc.equals("(Z)V")) {
                    InsnList il = new InsnList();

                    il.add(label_0);
                    il.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    il.add(new VarInsnNode(Opcodes.ALOAD, ASMUtils.findLocalVariableIndex(lambda$null$25, "Lnet/minecraft/world/chunk/Chunk;", 1)));
                    il.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/world/server/ChunkHolder", "currentlyLoading", "Lnet/minecraft/world/chunk/Chunk;"));

                    lambda$null$25.instructions.insert(min, il);
                } else if (min.getOpcode() == Opcodes.INVOKESTATIC && min.owner.equals("net/optifine/reflect/Reflector") && min.name.equals("postForgeBusEvent") && min.desc.equals("(Lnet/optifine/reflect/ReflectorConstructor;[Ljava/lang/Object;)Z")) {
                    LabelNode label_4;
                    AbstractInsnNode ain0 = min;
                    while (!(ain0 instanceof LabelNode)) {
                        ain0 = ain0.getNext();
                    }
                    label_4 = (LabelNode) ain0;

                    AbstractInsnNode ain1 = ain0;
                    while (ain1 != null) {
                        ain1 = ain1.getPrevious();
                        if (ain1 instanceof JumpInsnNode) {
                            JumpInsnNode jin = (JumpInsnNode) ain1;
                            if (jin.label.equals(label_4)) {
                                jin.label = label_1;
                            }
                        }
                    }

                    InsnList il = new InsnList();

                    il.add(label_1);
                    il.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    il.add(new InsnNode(Opcodes.ACONST_NULL));
                    il.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/world/server/ChunkHolder", "currentlyLoading", "Lnet/minecraft/world/chunk/Chunk;"));

                    il.add(new LabelNode());
                    il.add(new JumpInsnNode(Opcodes.GOTO, label_4));

                    il.add(label_2);
                    il.add(new VarInsnNode(Opcodes.ASTORE, ASMUtils.getNextOfMaxLocalVariableIndex(lambda$null$25.localVariables)));

                    il.add(label_3);
                    il.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    il.add(new InsnNode(Opcodes.ACONST_NULL));
                    il.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/world/server/ChunkHolder", "currentlyLoading", "Lnet/minecraft/world/chunk/Chunk;"));

                    il.add(new LabelNode());
                    il.add(new VarInsnNode(Opcodes.ALOAD, ASMUtils.getNextOfMaxLocalVariableIndex(lambda$null$25.localVariables)));
                    il.add(new InsnNode(Opcodes.ATHROW));

                    lambda$null$25.instructions.insertBefore(label_4, il);
                }
            }
        }

        return input;
    }
}
