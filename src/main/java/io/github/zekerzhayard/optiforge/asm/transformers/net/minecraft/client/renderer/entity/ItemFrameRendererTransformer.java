package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.entity;

import java.util.Objects;

import io.github.zekerzhayard.optiforge.asm.transformers.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class ItemFrameRendererTransformer implements ITransformer {
    @Override
    public boolean isTargetClass(String className) {
        return className.equals("net.minecraft.client.renderer.entity.ItemFrameRenderer");
    }

    @Override
    public ClassNode preTransform(ClassNode cn) {
        MethodNode mn = Objects.requireNonNull(Bytecode.findMethod(cn, "func_225623_a_", "(Lnet/minecraft/entity/item/ItemFrameEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V"));
        LocalVariableNode flag = null, mapdata = null;
        for (LocalVariableNode lvn : mn.localVariables) {
            if (lvn.name.equals("flag") && lvn.desc.equals("Z")) {
                lvn.desc = "Lnet/minecraft/world/storage/MapData;";
                flag = lvn;
            } else if (lvn.name.equals("mapdata") && lvn.desc.equals("Lnet/minecraft/world/storage/MapData;")) {
                mapdata = lvn;
            }
        }
        Objects.requireNonNull(flag);
        Objects.requireNonNull(mapdata);
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.ILOAD) {
                VarInsnNode vin = (VarInsnNode) ain;
                AbstractInsnNode jin = vin.getNext();
                if (vin.var == flag.index && jin.getOpcode() == Opcodes.IFEQ) {
                    vin.setOpcode(Opcodes.ALOAD);
                    ((JumpInsnNode) jin).setOpcode(Opcodes.IFNULL);
                }
            } else if (ain.getOpcode() == Opcodes.ISTORE) {
                VarInsnNode vin = (VarInsnNode) ain;
                if (vin.var == flag.index) {
                    vin.setOpcode(Opcodes.ASTORE);
                }
            }

            if (ain.getOpcode() == Opcodes.ASTORE) {
                VarInsnNode vin = (VarInsnNode) ain;
                if (vin.var == mapdata.index) {
                    mn.instructions.insertBefore(vin, new InsnNode(Opcodes.POP));
                    mn.instructions.insertBefore(vin, new VarInsnNode(Opcodes.ALOAD, flag.index));
                }
            }
        }
        return cn;
    }
}
