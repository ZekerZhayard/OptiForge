package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.gui;

import java.util.Objects;

import io.github.zekerzhayard.optiforge.asm.transformers.ITransformer;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class IngameGuiTransformer implements ITransformer {
    @Override
    public boolean isTargetClass(String className) {
        return className.equals("net.minecraft.client.gui.IngameGui");
    }

    @Override
    public ClassNode preTransform(ClassNode cn) {
        MethodNode mn0 = Objects.requireNonNull(Bytecode.findMethod(cn, ASMAPI.mapMethod("func_238453_b_"), "(Lcom/mojang/blaze3d/matrix/MatrixStack;)V"));
        int count = 0;
        for (AbstractInsnNode ain : mn0.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.ISTORE) {
                VarInsnNode vin = (VarInsnNode) ain;
                if (vin.var == 4) {
                    count++;
                    if (count == 2) {
                        vin.var = 5;
                    }
                }
            }
        }

        MethodNode mn1 = Objects.requireNonNull(Bytecode.findMethod(cn, ASMAPI.mapMethod("func_73831_a"), "()V"));
        for (LocalVariableNode lvn : mn1.localVariables) {
            if ((lvn.name.equals("stackTip") || lvn.name.equals("highlightTip")) && lvn.desc.equals("Ljava/lang/String;")) {
                lvn.desc = "Lnet/minecraft/util/text/ITextComponent;";
            }
        }
        return cn;
    }
}
