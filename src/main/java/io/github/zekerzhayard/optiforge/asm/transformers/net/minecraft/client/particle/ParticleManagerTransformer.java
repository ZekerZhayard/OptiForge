package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.particle;

import java.util.ArrayList;
import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class ParticleManagerTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.particle.ParticleManager";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/particle/ParticleManager.java.patch#L44-L47
        //
        // +   /**@deprecated Forge: use {@link #renderParticles(MatrixStack, IRenderTypeBuffer.Impl, LightTexture, ActiveRenderInfo, float, net.minecraft.client.renderer.culling.ClippingHelper)} with ClippingHelper as additional parameter*/
        // +   @Deprecated
        //     public void func_228345_a_(MatrixStack p_228345_1_, IRenderTypeBuffer.Impl p_228345_2_, LightTexture p_228345_3_, ActiveRenderInfo p_228345_4_, float p_228345_5_) {
        // +      renderParticles(p_228345_1_, p_228345_2_, p_228345_3_, p_228345_4_, p_228345_5_, null);
        // +   }
        // +
        // +   public void renderParticles(MatrixStack p_228345_1_, IRenderTypeBuffer.Impl p_228345_2_, LightTexture p_228345_3_, ActiveRenderInfo p_228345_4_, float p_228345_5_, @Nullable net.minecraft.client.renderer.culling.ClippingHelper clippingHelper) {
        //        p_228345_3_.func_205109_c();
        //

        MethodNode renderParticles_0 = new MethodNode();
        renderParticles_0.name = ASMAPI.mapMethod("func_228345_a_");
        renderParticles_0.desc = "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;F)V";
        renderParticles_0.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_DEPRECATED;

        LabelNode label_0 = new LabelNode();
        LabelNode label_1 = new LabelNode();
        LabelNode label_2 = new LabelNode();
        renderParticles_0.localVariables = new ArrayList<>();
        renderParticles_0.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/client/particle/ParticleManager;", null, label_0, label_2, 0));
        renderParticles_0.localVariables.add(new LocalVariableNode("matrixStackIn", "Lcom/mojang/blaze3d/matrix/MatrixStack;", null, label_0, label_2, 1));
        renderParticles_0.localVariables.add(new LocalVariableNode("bufferIn", "Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;", null, label_0, label_2, 2));
        renderParticles_0.localVariables.add(new LocalVariableNode("lightTextureIn", "Lnet/minecraft/client/renderer/LightTexture;", null, label_0, label_2, 3));
        renderParticles_0.localVariables.add(new LocalVariableNode("activeRenderInfoIn", "Lnet/minecraft/client/renderer/ActiveRenderInfo;", null, label_0, label_2, 4));
        renderParticles_0.localVariables.add(new LocalVariableNode("partialTicks", "F", null, label_0, label_2, 5));

        renderParticles_0.instructions.add(label_0);
        renderParticles_0.instructions.add(new LineNumberNode(319, label_0));
        renderParticles_0.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        renderParticles_0.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        renderParticles_0.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        renderParticles_0.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
        renderParticles_0.instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
        renderParticles_0.instructions.add(new VarInsnNode(Opcodes.FLOAD, 5));
        renderParticles_0.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
        renderParticles_0.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/particle/ParticleManager", "renderParticles", "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;FLnet/minecraft/client/renderer/culling/ClippingHelper;)V", false));
        renderParticles_0.instructions.add(label_1);
        renderParticles_0.instructions.add(new LineNumberNode(320, label_1));
        renderParticles_0.instructions.add(new InsnNode(Opcodes.RETURN));
        renderParticles_0.instructions.add(label_2);

        MethodNode renderParticles_1 = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_228345_a_"), "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;F)V"));
        AbstractInsnNode[] ains = renderParticles_1.instructions.toArray();
        LabelNode start = null;
        LabelNode end = null;
        for (int i = 0; i < ains.length; i++) {
            if (ains[i] instanceof LabelNode) {
                start = (LabelNode) ains[i];
                break;
            }
        }
        for (int i = ains.length - 1; i >= 0; i--) {
            if (ains[i] instanceof LabelNode) {
                end = (LabelNode) ains[i];
                break;
            }
        }
        ASMUtils.insertLocalVariable(renderParticles_1, new LocalVariableNode("clippingHelper", "Lnet/minecraft/client/renderer/culling/ClippingHelper;", null, Objects.requireNonNull(start), Objects.requireNonNull(end), Bytecode.getFirstNonArgLocalIndex(renderParticles_1)));

        renderParticles_1.name = "renderParticles";
        renderParticles_1.desc = "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;FLnet/minecraft/client/renderer/culling/ClippingHelper;)V";
        input.methods.add(input.methods.indexOf(renderParticles_1), renderParticles_0);

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/particle/ParticleManager.java.patch#L72
        //
        //              iparticlerendertype.func_217600_a(bufferbuilder, this.field_78877_c);
        //
        //              for(Particle particle : iterable) {
        // +               if (clippingHelper != null && particle.shouldCull() && !clippingHelper.func_228957_a_(particle.func_187116_l())) continue;
        //                 try {
        //                    particle.func_225606_a_(bufferbuilder, p_228345_4_, p_228345_5_);
        //                 } catch (Throwable throwable) {
        //

        LabelNode jump_0 = null; // before the for loop
        LabelNode jump_1 = new LabelNode(); // after "particle" being assigned
        LabelNode jump_2 = null; // before try block
        int jumpCount = 0;
        for (AbstractInsnNode ain : renderParticles_1.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.INVOKEINTERFACE) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("java/util/Iterator") && min.name.equals("hasNext") && min.desc.equals("()Z")) {
                    jumpCount++;
                    if (jumpCount == 2) {
                        AbstractInsnNode ain0 = ain;
                        do {
                            ain0 = ain0.getPrevious();
                        } while (!(ain0 instanceof LabelNode));
                        jump_0 = (LabelNode) ain0;

                        ain0 = ain;
                        do {
                            ain0 = ain0.getNext();
                        } while (!(ain0 instanceof LabelNode));
                        jump_2 = (LabelNode) ain0;
                        break;
                    }
                }
            }
        }
        Objects.requireNonNull(jump_0);
        Objects.requireNonNull(jump_2);

        int clippingHelperIndex = ASMUtils.findLocalVariableIndex(renderParticles_1, "Lnet/minecraft/client/renderer/culling/ClippingHelper;", 0);
        int particleIndex = ASMUtils.findLocalVariableIndex(renderParticles_1, "Lnet/minecraft/client/particle/Particle;", 0);
        InsnList il = new InsnList();
        il.add(jump_1);
        il.add(new VarInsnNode(Opcodes.ALOAD, clippingHelperIndex));
        il.add(new JumpInsnNode(Opcodes.IFNULL, jump_2));
        il.add(new VarInsnNode(Opcodes.ALOAD, particleIndex));
        il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/particle/Particle", "shouldCull", "()Z", false));
        il.add(new JumpInsnNode(Opcodes.IFEQ, jump_2));
        il.add(new VarInsnNode(Opcodes.ALOAD, clippingHelperIndex));
        il.add(new VarInsnNode(Opcodes.ALOAD, particleIndex));
        il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/particle/Particle", ASMAPI.mapMethod("func_187116_l"), "()Lnet/minecraft/util/math/AxisAlignedBB;", false));
        il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/culling/ClippingHelper", ASMAPI.mapMethod("func_228957_a_"), "(Lnet/minecraft/util/math/AxisAlignedBB;)Z", false));
        il.add(new JumpInsnNode(Opcodes.IFNE, jump_2));
        il.add(new JumpInsnNode(Opcodes.GOTO, jump_0));
        renderParticles_1.instructions.insertBefore(jump_2, il);

        return input;
    }
}
