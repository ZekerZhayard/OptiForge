package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer;

import java.util.Objects;

import io.github.zekerzhayard.optiforge.asm.transformers.ITransformer;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.util.Bytecode;

public class OverlayRendererTransformer implements ITransformer {
    @Override
    public boolean isTargetClass(String className) {
        return className.equals("net.minecraft.client.renderer.OverlayRenderer");
    }

    @Override
    public ClassNode preTransform(ClassNode cn) {
        MethodNode mn = Objects.requireNonNull(Bytecode.findMethod(cn, ASMAPI.mapMethod("func_228734_a_"), "(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/matrix/MatrixStack;)V"));
        int isTarget = 0;
        LabelNode ln = null;
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (isTarget == 0 && ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("net/optifine/reflect/ReflectorMethod") && min.name.equals("callBoolean") && min.desc.equals("([Ljava/lang/Object;)Z")) {
                    isTarget = 1;
                }
            } else if (isTarget == 1 && ain instanceof LabelNode) {
                isTarget = 2;
                ln = (LabelNode) ain;
                mn.instructions.remove(ain);
            } else if (isTarget == 2 && ln != null && ain.getOpcode() == Opcodes.INVOKESTATIC) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("net/minecraft/client/renderer/OverlayRenderer") && min.name.equals(ASMAPI.mapMethod("func_228735_a_")) && min.desc.equals("(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lcom/mojang/blaze3d/matrix/MatrixStack;)V")) {
                    isTarget = 3;
                    mn.instructions.insert(ain, ln);
                }
            }
        }
        return cn;
    }
}
