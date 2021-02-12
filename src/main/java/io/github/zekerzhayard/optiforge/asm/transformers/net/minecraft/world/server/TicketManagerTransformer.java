package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.world.server;

import java.util.ArrayList;
import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
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

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/world/server/TicketManager.java.patch#L41-L48
        //
        //        this.func_219349_b(p_219362_2_.func_201841_a(), ticket);
        //     }
        //
        // +   public <T> void registerTicking(TicketType<T> type, ChunkPos pos, int distance, T value) {
        // +      this.func_219347_a(pos.func_201841_a(), new Ticket<>(type, 33 - distance, value, true));
        // +   }
        // +
        // +   public <T> void releaseTicking(TicketType<T> type, ChunkPos pos, int distance, T value) {
        // +      this.func_219349_b(pos.func_201841_a(), new Ticket<>(type, 33 - distance, value, true));
        // +   }
        // +
        //     private SortedArraySet<Ticket<?>> func_229848_e_(long p_229848_1_) {
        //        return this.field_219377_e.computeIfAbsent(p_229848_1_, (p_229851_0_) -> {
        //           return SortedArraySet.func_226172_a_(4);
        //

        MethodNode registerTicking = new MethodNode();
        registerTicking.access = Opcodes.ACC_PUBLIC;
        registerTicking.name = "registerTicking";
        registerTicking.desc = "(Lnet/minecraft/world/server/TicketType;Lnet/minecraft/util/math/ChunkPos;ILjava/lang/Object;)V";
        registerTicking.signature = "<T:Ljava/lang/Object;>(Lnet/minecraft/world/server/TicketType<TT;>;Lnet/minecraft/util/math/ChunkPos;ITT;)V";

        LabelNode registerTicking_label_0 = new LabelNode();
        LabelNode registerTicking_label_1 = new LabelNode();
        LabelNode registerTicking_label_2 = new LabelNode();
        registerTicking.localVariables = new ArrayList<>();
        registerTicking.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/world/server/TicketManager;", null, registerTicking_label_0, registerTicking_label_2, 0));
        registerTicking.localVariables.add(new LocalVariableNode("type", "Lnet/minecraft/world/server/TicketType;", "Lnet/minecraft/world/server/TicketType<TT;>;", registerTicking_label_0, registerTicking_label_2, 1));
        registerTicking.localVariables.add(new LocalVariableNode("pos", "Lnet/minecraft/util/math/ChunkPos;", null, registerTicking_label_0, registerTicking_label_2, 2));
        registerTicking.localVariables.add(new LocalVariableNode("distance", "I", null, registerTicking_label_0, registerTicking_label_2, 3));
        registerTicking.localVariables.add(new LocalVariableNode("value", "Ljava/lang/Object;", "TT;", registerTicking_label_0, registerTicking_label_2, 4));

        // method body start
        registerTicking.instructions.add(registerTicking_label_0);
        registerTicking.instructions.add(new LineNumberNode(192, registerTicking_label_0));
        registerTicking.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        registerTicking.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        registerTicking.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/math/ChunkPos", ASMAPI.mapMethod("func_201841_a"), "()J", false));
        registerTicking.instructions.add(new TypeInsnNode(Opcodes.NEW, "net/minecraft/world/server/Ticket"));
        registerTicking.instructions.add(new InsnNode(Opcodes.DUP));
        registerTicking.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        registerTicking.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 33));
        registerTicking.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
        registerTicking.instructions.add(new InsnNode(Opcodes.ISUB));
        registerTicking.instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
        registerTicking.instructions.add(new InsnNode(Opcodes.ICONST_1));
        registerTicking.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/world/server/Ticket", "<init>", "(Lnet/minecraft/world/server/TicketType;ILjava/lang/Object;Z)V", false));
        registerTicking.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/world/server/TicketManager", ASMAPI.mapMethod("func_219347_a"), "(JLnet/minecraft/world/server/Ticket;)V", false));

        registerTicking.instructions.add(registerTicking_label_1);
        registerTicking.instructions.add(new LineNumberNode(193, registerTicking_label_1));
        registerTicking.instructions.add(new InsnNode(Opcodes.RETURN));
        registerTicking.instructions.add(registerTicking_label_2);
        // method body end

        input.methods.add(input.methods.indexOf(Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_229848_e_"), "(J)Lnet/minecraft/util/SortedArraySet;"))), registerTicking);


        MethodNode releaseTicking = new MethodNode();
        releaseTicking.access = Opcodes.ACC_PUBLIC;
        releaseTicking.name = "releaseTicking";
        releaseTicking.desc = "(Lnet/minecraft/world/server/TicketType;Lnet/minecraft/util/math/ChunkPos;ILjava/lang/Object;)V";
        releaseTicking.signature = "<T:Ljava/lang/Object;>(Lnet/minecraft/world/server/TicketType<TT;>;Lnet/minecraft/util/math/ChunkPos;ITT;)V";

        LabelNode releaseTicking_label_0 = new LabelNode();
        LabelNode releaseTicking_label_1 = new LabelNode();
        LabelNode releaseTicking_label_2 = new LabelNode();
        releaseTicking.localVariables = new ArrayList<>();
        releaseTicking.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/world/server/TicketManager;", null, releaseTicking_label_0, releaseTicking_label_2, 0));
        releaseTicking.localVariables.add(new LocalVariableNode("type", "Lnet/minecraft/world/server/TicketType;", "Lnet/minecraft/world/server/TicketType<TT;>;", releaseTicking_label_0, releaseTicking_label_2, 1));
        releaseTicking.localVariables.add(new LocalVariableNode("pos", "Lnet/minecraft/util/math/ChunkPos;", null, releaseTicking_label_0, releaseTicking_label_2, 2));
        releaseTicking.localVariables.add(new LocalVariableNode("distance", "I", null, releaseTicking_label_0, releaseTicking_label_2, 3));
        releaseTicking.localVariables.add(new LocalVariableNode("value", "Ljava/lang/Object;", "TT;", releaseTicking_label_0, releaseTicking_label_2, 4));

        // method body start
        releaseTicking.instructions.add(releaseTicking_label_0);
        releaseTicking.instructions.add(new LineNumberNode(196, releaseTicking_label_0));
        releaseTicking.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        releaseTicking.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        releaseTicking.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/math/ChunkPos", ASMAPI.mapMethod("func_201841_a"), "()J", false));
        releaseTicking.instructions.add(new TypeInsnNode(Opcodes.NEW, "net/minecraft/world/server/Ticket"));
        releaseTicking.instructions.add(new InsnNode(Opcodes.DUP));
        releaseTicking.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        releaseTicking.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 33));
        releaseTicking.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
        releaseTicking.instructions.add(new InsnNode(Opcodes.ISUB));
        releaseTicking.instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
        releaseTicking.instructions.add(new InsnNode(Opcodes.ICONST_1));
        releaseTicking.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/world/server/Ticket", "<init>", "(Lnet/minecraft/world/server/TicketType;ILjava/lang/Object;Z)V", false));
        releaseTicking.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/world/server/TicketManager", ASMAPI.mapMethod("func_219349_b"), "(JLnet/minecraft/world/server/Ticket;)V", false));

        releaseTicking.instructions.add(releaseTicking_label_1);
        releaseTicking.instructions.add(new LineNumberNode(197, releaseTicking_label_1));
        releaseTicking.instructions.add(new InsnNode(Opcodes.RETURN));
        releaseTicking.instructions.add(releaseTicking_label_2);
        // method body end

        input.methods.add(input.methods.indexOf(Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_229848_e_"), "(J)Lnet/minecraft/util/SortedArraySet;"))), releaseTicking);

        return input;
    }
}
