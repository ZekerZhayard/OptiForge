package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.world.server;

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
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class TicketManagerTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.world.server.TicketManager";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/world/server/TicketManager.java.patch#L7
        //
        //     private long field_219389_q;
        //
        // +   private final Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> forcedTickets = new Long2ObjectOpenHashMap<>();
        // +
        //     protected TicketManager(Executor p_i50707_1_, Executor p_i50707_2_) {
        //

        input.fields.add(new FieldNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, "forcedTickets", "Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;", "Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap<Lnet/minecraft/util/SortedArraySet<Lnet/minecraft/world/server/Ticket<*>;>;>;", null));

        MethodNode _init_ = Objects.requireNonNull(Bytecode.findMethod(input, "<init>", "(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)V"));

        for (AbstractInsnNode ain : _init_.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.PUTFIELD) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.owner.equals("net/minecraft/world/server/TicketManager") && fin.name.equals(ASMAPI.mapField("field_219387_o")) && fin.desc.equals("Lit/unimi/dsi/fastutil/longs/LongSet;")) {
                    InsnList il = new InsnList();
                    il.add(new LabelNode());
                    il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    il.add(new TypeInsnNode(Opcodes.NEW, "it/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap"));
                    il.add(new InsnNode(Opcodes.DUP));
                    il.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "it/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap", "<init>", "()V", false));
                    il.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/world/server/TicketManager", "forcedTickets", "Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;"));

                    _init_.instructions.insert(fin, il);
                    break;
                }
            }
        }

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/world/server/TicketManager.java.patch#L16-L19
        //
        //           this.field_219378_f.func_215491_b(p_219347_1_, p_219347_3_.func_219477_b(), true);
        //        }
        //
        // +      if (p_219347_3_.isForceTicks()) {
        // +          SortedArraySet<Ticket<?>> tickets = forcedTickets.computeIfAbsent(p_219347_1_, e -> SortedArraySet.func_226172_a_(4));
        // +          tickets.func_226175_a_(ticket);
        // +      }
        //     }
        //
        //     private void func_219349_b(long p_219349_1_, Ticket<?> p_219349_3_) {
        //

        MethodNode register = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_219347_a"), "(JLnet/minecraft/world/server/Ticket;)V"));

        LabelNode register_label_0 = new LabelNode();
        LabelNode register_label_1 = new LabelNode();
        ASMUtils.insertLocalVariable(register, new LocalVariableNode("tickets", "Lnet/minecraft/util/SortedArraySet;", "Lnet/minecraft/util/SortedArraySet<Lnet/minecraft/world/server/Ticket<*>;>;", register_label_0, register_label_1, ASMUtils.getNextOfMaxLocalVariableIndex(register.localVariables)), 0);

        for (AbstractInsnNode ain : register.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.RETURN) {
                InsnList il = new InsnList();
                il.add(new VarInsnNode(Opcodes.ALOAD, 3));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/server/Ticket", "isForceTicks", "()Z", false));
                il.add(new JumpInsnNode(Opcodes.IFEQ, register_label_1));
                il.add(new LabelNode());

                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/server/TicketManager", "forcedTickets", "Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;"));
                il.add(new VarInsnNode(Opcodes.LLOAD, 1));
                il.add(new InvokeDynamicInsnNode("apply", "()Ljava/util/function/LongFunction;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(J)Ljava/lang/Object;"), new Handle(Opcodes.H_INVOKESTATIC, "net/minecraft/world/server/TicketManager", "lambda$register$6", "(J)Lnet/minecraft/util/SortedArraySet;", false), Type.getType("(J)Lnet/minecraft/util/SortedArraySet;")));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "it/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap", "computeIfAbsent", "(JLjava/util/function/LongFunction;)Ljava/lang/Object;", false));
                il.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/util/SortedArraySet"));
                il.add(new VarInsnNode(Opcodes.ASTORE, ASMUtils.findLocalVariableIndex(register, "Lnet/minecraft/util/SortedArraySet;", 1)));
                il.add(register_label_0);

                il.add(new VarInsnNode(Opcodes.ALOAD, ASMUtils.findLocalVariableIndex(register, "Lnet/minecraft/util/SortedArraySet;", 1)));
                il.add(new VarInsnNode(Opcodes.ALOAD, ASMUtils.findLocalVariableIndex(register, "Lnet/minecraft/world/server/Ticket;", 1)));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/SortedArraySet", ASMAPI.mapMethod("func_226175_a_"), "(Ljava/lang/Object;)Ljava/lang/Object;", false));
                il.add(new InsnNode(Opcodes.POP));
                il.add(register_label_1);

                register.instructions.insertBefore(ain, il);
                break;
            }
        }

        MethodNode lambda$register$6 = new MethodNode();
        lambda$register$6.access = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC;
        lambda$register$6.name = "lambda$register$6";
        lambda$register$6.desc = "(J)Lnet/minecraft/util/SortedArraySet;";

        LabelNode lambda$register$6_label_0 = new LabelNode();
        LabelNode lambda$register$6_label_1 = new LabelNode();
        lambda$register$6.localVariables = new ArrayList<>();
        lambda$register$6.localVariables.add(new LocalVariableNode("e", "J", null, lambda$register$6_label_0, lambda$register$6_label_1, 0));

        // method body start
        lambda$register$6.instructions.add(lambda$register$6_label_0);
        lambda$register$6.instructions.add(new LineNumberNode(149, lambda$register$6_label_0));
        lambda$register$6.instructions.add(new InsnNode(Opcodes.ICONST_4));
        lambda$register$6.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/util/SortedArraySet", ASMAPI.mapMethod("func_226172_a_"), "(I)Lnet/minecraft/util/SortedArraySet;", false));
        lambda$register$6.instructions.add(new InsnNode(Opcodes.ARETURN));
        lambda$register$6.instructions.add(lambda$register$6_label_1);
        // method body end

        input.methods.add(input.methods.indexOf(Bytecode.findMethod(input, "lambda$processUpdates$5", "(JLcom/mojang/datafixers/util/Either;)V")), lambda$register$6);

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/world/server/TicketManager.java.patch#L28-L33
        //
        //        this.field_219378_f.func_215491_b(p_219349_1_, func_229844_a_(sortedarrayset), false);
        // +
        // +      if (p_219349_3_.isForceTicks()) {
        // +          SortedArraySet<Ticket<?>> tickets = forcedTickets.get(p_219349_1_);
        // +          if (tickets != null) {
        // +              tickets.remove(p_219349_3_);
        // +          }
        // +      }
        //     }
        //
        //     public <T> void func_219356_a(TicketType<T> p_219356_1_, ChunkPos p_219356_2_, int p_219356_3_, T p_219356_4_) {
        //

        MethodNode release = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_219349_b"), "(JLnet/minecraft/world/server/Ticket;)V"));

        LabelNode release_label_0 = new LabelNode();
        LabelNode release_label_1 = new LabelNode();
        ASMUtils.insertLocalVariable(release, new LocalVariableNode("tickets", "Lnet/minecraft/util/SortedArraySet;", "Lnet/minecraft/util/SortedArraySet<Lnet/minecraft/world/server/Ticket<*>;>;", release_label_0, release_label_1, ASMUtils.getNextOfMaxLocalVariableIndex(release.localVariables)), 0);

        for (AbstractInsnNode ain : release.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.RETURN) {
                InsnList il = new InsnList();
                il.add(new VarInsnNode(Opcodes.ALOAD, 3));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/server/Ticket", "isForceTicks", "()Z", false));
                il.add(new JumpInsnNode(Opcodes.IFEQ, release_label_1));
                il.add(new LabelNode());

                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/server/TicketManager", "forcedTickets", "Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;"));
                il.add(new VarInsnNode(Opcodes.LLOAD, 1));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "it/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap", "get", "(J)Ljava/lang/Object;", false));
                il.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/util/SortedArraySet"));
                il.add(new VarInsnNode(Opcodes.ASTORE, ASMUtils.findLocalVariableIndex(release, "Lnet/minecraft/util/SortedArraySet;", 1)));
                il.add(release_label_0);

                il.add(new VarInsnNode(Opcodes.ALOAD, ASMUtils.findLocalVariableIndex(release, "Lnet/minecraft/util/SortedArraySet;", 1)));
                il.add(new JumpInsnNode(Opcodes.IFNULL, release_label_1));
                il.add(new LabelNode());

                il.add(new VarInsnNode(Opcodes.ALOAD, ASMUtils.findLocalVariableIndex(release, "Lnet/minecraft/util/SortedArraySet;", 1)));
                il.add(new VarInsnNode(Opcodes.ALOAD, 3));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/SortedArraySet", "remove", "(Ljava/lang/Object;)Z", false));
                il.add(new InsnNode(Opcodes.POP));
                il.add(release_label_1);

                release.instructions.insertBefore(ain, il);
            }
        }

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/world/server/TicketManager.java.patch#L41-L44
        //
        //        return this.field_219384_l.func_225396_a();
        //     }
        //
        // +   public boolean shouldForceTicks(long chunkPos) {
        // +       SortedArraySet<Ticket<?>> tickets = forcedTickets.get(chunkPos);
        // +       return tickets != null && !tickets.isEmpty();
        // +   }
        // +
        //     class ChunkTicketTracker extends ChunkDistanceGraph {
        //

        MethodNode shouldForceTicks = new MethodNode();
        shouldForceTicks.access = Opcodes.ACC_PUBLIC;
        shouldForceTicks.name = "shouldForceTicks";
        shouldForceTicks.desc = "(J)Z";

        LabelNode shouldForceTicks_label_0 = new LabelNode();
        LabelNode shouldForceTicks_label_1 = new LabelNode();
        LabelNode shouldForceTicks_label_2 = new LabelNode();
        LabelNode shouldForceTicks_label_3 = new LabelNode();
        LabelNode shouldForceTicks_label_4 = new LabelNode();
        shouldForceTicks.localVariables = new ArrayList<>();
        shouldForceTicks.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/world/server/TicketManager;", null, shouldForceTicks_label_0, shouldForceTicks_label_4, 0));
        shouldForceTicks.localVariables.add(new LocalVariableNode("chunkPos", "J", null, shouldForceTicks_label_0, shouldForceTicks_label_4, 1));
        shouldForceTicks.localVariables.add(new LocalVariableNode("tickets", "Lnet/minecraft/util/SortedArraySet;", "Lnet/minecraft/util/SortedArraySet<Lnet/minecraft/world/server/Ticket<*>;>;", shouldForceTicks_label_0, shouldForceTicks_label_4, 3));

        // method body start
        shouldForceTicks.instructions.add(shouldForceTicks_label_0);
        shouldForceTicks.instructions.add(new LineNumberNode(259, shouldForceTicks_label_0));
        shouldForceTicks.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        shouldForceTicks.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/server/TicketManager", "forcedTickets", "Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;"));
        shouldForceTicks.instructions.add(new VarInsnNode(Opcodes.LLOAD, 1));
        shouldForceTicks.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "it/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap", "get", "(J)Ljava/lang/Object;", false));
        shouldForceTicks.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/util/SortedArraySet"));
        shouldForceTicks.instructions.add(new VarInsnNode(Opcodes.ASTORE, 3));

        shouldForceTicks.instructions.add(shouldForceTicks_label_1);
        shouldForceTicks.instructions.add(new LineNumberNode(260, shouldForceTicks_label_1));
        shouldForceTicks.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
        shouldForceTicks.instructions.add(new JumpInsnNode(Opcodes.IFNULL, shouldForceTicks_label_2));
        shouldForceTicks.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
        shouldForceTicks.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/SortedArraySet", "isEmpty", "()Z", false));
        shouldForceTicks.instructions.add(new JumpInsnNode(Opcodes.IFNE, shouldForceTicks_label_2));
        shouldForceTicks.instructions.add(new InsnNode(Opcodes.ICONST_1));
        shouldForceTicks.instructions.add(new JumpInsnNode(Opcodes.GOTO, shouldForceTicks_label_3));

        shouldForceTicks.instructions.add(shouldForceTicks_label_2);
        shouldForceTicks.instructions.add(new InsnNode(Opcodes.ICONST_0));

        shouldForceTicks.instructions.add(shouldForceTicks_label_3);
        shouldForceTicks.instructions.add(new InsnNode(Opcodes.IRETURN));
        shouldForceTicks.instructions.add(shouldForceTicks_label_4);
        // method body end

        input.methods.add(input.methods.indexOf(Bytecode.findMethod(input, "lambda$updatePlayerPosition$7", "(J)Lit/unimi/dsi/fastutil/objects/ObjectSet;")), shouldForceTicks);

        return input;
    }
}
