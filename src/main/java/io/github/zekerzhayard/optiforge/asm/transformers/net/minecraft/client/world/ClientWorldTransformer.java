package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.world;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.util.Bytecode;

public class ClientWorldTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.world.ClientWorld";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.15.x/patches/minecraft/net/minecraft/client/world/ClientWorld.java.patch#L76-L77
        //
        //     }
        //
        //     public double func_228331_m_() {
        // -      return this.field_72986_A.func_76067_t() == WorldType.field_77138_c ? 0.0D : 63.0D;
        // +      return this.field_73011_w.getHorizonHeight();
        //     }
        //
        //     public int func_228332_n_() {
        //

        MethodNode getHorizonHeight = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_228331_m_"), "()D"));

        for (AbstractInsnNode ain : getHorizonHeight.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.ALOAD) {
                AbstractInsnNode ain0 = ain.getNext();
                while (ain0.getOpcode() != Opcodes.DRETURN) {
                    ain0 = ain0.getNext();
                    getHorizonHeight.instructions.remove(ain0.getPrevious());
                }

                InsnList il = new InsnList();
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/world/ClientWorld", ASMAPI.mapField("field_73011_w"), "Lnet/minecraft/world/dimension/Dimension;"));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/dimension/Dimension", "getHorizonHeight", "()D", false));

                getHorizonHeight.instructions.insertBefore(ain0, il);
            }
        }

        return input;
    }
}
