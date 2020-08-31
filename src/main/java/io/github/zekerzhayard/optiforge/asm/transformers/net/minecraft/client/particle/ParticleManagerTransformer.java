package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.particle;

import java.util.Objects;

import io.github.zekerzhayard.optiforge.asm.transformers.ITransformer;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.util.Bytecode;

public class ParticleManagerTransformer implements ITransformer {
    @Override
    public boolean isTargetClass(String className) {
        return className.equals("net.minecraft.client.particle.ParticleManager");
    }

    @Override
    public ClassNode postTransform(ClassNode cn, String mixinClassName) {
        MethodNode mn = Objects.requireNonNull(Bytecode.findMethod(cn, ASMAPI.mapMethod("func_228345_a_"), "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;F)V"));
        LabelNode start = null, end = null, jump = null;
        int jumpCount = 0;
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain instanceof LabelNode) {
                LabelNode ln = (LabelNode) ain;
                if (start == null) {
                    start = ln;
                } else {
                    end = ln;
                }
            } else if (ain.getOpcode() == Opcodes.INVOKEINTERFACE) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("java/util/Iterator") && min.name.equals("hasNext") && min.desc.equals("()Z")) {
                    jumpCount++;
                    if (jumpCount == 2) {
                        AbstractInsnNode ain0 = ain;
                        do {
                            ain0 = ain0.getPrevious();
                        } while (!(ain0 instanceof LabelNode));
                        jump = (LabelNode) ain0;
                    }
                }
            } else if (ain.getOpcode() == Opcodes.INVOKESPECIAL) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals(cn.name) && min.name.endsWith("inject$renderParticles$0")) {
                    // Check if this method is OptiForge method.
                    boolean isTargetMethod = false;
                    for (MethodNode mn0 : cn.methods) {
                        if (mn0.name.equals(min.name) && mn0.desc.equals(min.desc) && ASMUtils.isMixinMethod(mn0, mixinClassName)) {
                            isTargetMethod = true;
                            break;
                        }
                    }
                    if (isTargetMethod) {
                        mn.instructions.insert(min, new JumpInsnNode(Opcodes.IFNE, Objects.requireNonNull(jump)));
                    }
                }
            }
        }
        ASMUtils.insertLocalVariable(mn, new LocalVariableNode("clippingHelper", "Lnet/minecraft/client/renderer/culling/ClippingHelper;", null, Objects.requireNonNull(start), Objects.requireNonNull(end), Bytecode.getFirstNonArgLocalIndex(mn)));
        mn.name = "renderParticles";
        mn.desc = "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;FLnet/minecraft/client/renderer/culling/ClippingHelper;)V";
        return cn;
    }
}
