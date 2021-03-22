package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.entity;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class LivingRendererTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.entity.LivingRenderer";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.15.x/patches/minecraft/net/minecraft/client/renderer/entity/LivingRenderer.java.patch#L10-L13
        //
        //        this.field_77045_g.field_217112_c = this.func_77040_d(p_225623_1_, p_225623_3_);
        // -      this.field_77045_g.field_217113_d = p_225623_1_.func_184218_aH();
        // +
        // +      boolean shouldSit = p_225623_1_.func_184218_aH() && (p_225623_1_.func_184187_bx() != null && p_225623_1_.func_184187_bx().shouldRiderSit());
        // +      this.field_77045_g.field_217113_d = shouldSit;
        //        this.field_77045_g.field_217114_e = p_225623_1_.func_70631_g_();
        //

        MethodNode render = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_225623_a_"), "(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V"));
        int maxIndex = 0;
        boolean isLongOrDouble = false;
        for (LocalVariableNode lvn : render.localVariables) {
            if (maxIndex < lvn.index) {
                maxIndex = lvn.index;
                isLongOrDouble = lvn.desc.equals("J") || lvn.desc.equals("D");
            }
        }
        maxIndex = maxIndex + (isLongOrDouble ? 2 : 1); // the index of shouldSit
        LabelNode start = null;
        LabelNode end = null;

        int isPassengerCount = 0;
        for (AbstractInsnNode ain : render.instructions.toArray()) {
            if (ain instanceof LabelNode) {
                if (start == null) {
                    start = (LabelNode) ain;
                } else {
                    end = (LabelNode) ain;
                }
            } else if (ain instanceof FieldInsnNode) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.owner.equals("net/minecraft/client/renderer/entity/model/EntityModel") && fin.name.equals(ASMAPI.mapField("field_217113_d")) && fin.desc.equals("Z")) {
                    if (fin.getOpcode() == Opcodes.PUTFIELD) {
                        render.instructions.insertBefore(fin, new VarInsnNode(Opcodes.ISTORE, maxIndex));
                        render.instructions.insertBefore(fin, new VarInsnNode(Opcodes.ILOAD, maxIndex));
                    } else if (fin.getOpcode() == Opcodes.GETFIELD) {

                        // https://github.com/MinecraftForge/MinecraftForge/blob/1.15.x/patches/minecraft/net/minecraft/client/renderer/entity/LivingRenderer.java.patch#L18-L19
                        //
                        //        float f2 = f1 - f;
                        // -      if (p_225623_1_.func_184218_aH() && p_225623_1_.func_184187_bx() instanceof LivingEntity) {
                        // +      if (shouldSit && p_225623_1_.func_184187_bx() instanceof LivingEntity) {
                        //           LivingEntity livingentity = (LivingEntity)p_225623_1_.func_184187_bx();
                        //

                        // label 16
                        // line 94
                        // remove -> aload 0
                        // remove -> getfield EntityModel LivingRenderer.field_77045_g
                        // remove -> getfield boolean EntityModel.field_217113_d
                        // ifeq 29
                        AbstractInsnNode ain0 = fin.getPrevious();
                        do {
                            render.instructions.remove(ain0.getNext());
                            ain0 = ain0.getPrevious();
                        } while (!(ain0.getNext() instanceof VarInsnNode));
                        render.instructions.remove(ain0.getNext());

                        render.instructions.insert(ain0, new VarInsnNode(Opcodes.ILOAD, maxIndex));
                    }
                }
            } else if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {

                // https://github.com/MinecraftForge/MinecraftForge/blob/1.15.x/patches/minecraft/net/minecraft/client/renderer/entity/LivingRenderer.java.patch#L27-L28
                //
                //        float f5 = 0.0F;
                // -      if (!p_225623_1_.func_184218_aH() && p_225623_1_.func_70089_S()) {
                // +      if (!shouldSit && p_225623_1_.func_70089_S()) {
                //           f8 = MathHelper.func_219799_g(p_225623_3_, p_225623_1_.field_184618_aE, p_225623_1_.field_70721_aZ);
                //

                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("net/minecraft/entity/LivingEntity") && min.name.equals(ASMAPI.mapMethod("func_184218_aH")) && min.desc.equals("()Z")) {
                    isPassengerCount++;
                    if (isPassengerCount > 2) {
                        render.instructions.insert(min, new VarInsnNode(Opcodes.ILOAD, maxIndex));

                        // label 42
                        // line 142
                        // remove -> aload 1
                        // remove -> invokevirtual boolean LivingEntity.func_184218_aH()
                        // ifne 49
                        render.instructions.remove(min.getPrevious());
                        render.instructions.remove(min);
                    }
                }
            }
        }

        LocalVariableNode shouldSit = new LocalVariableNode("shouldSit", "Z", null, start, end, maxIndex);
        render.localVariables.add(shouldSit);

        return input;
    }
}
