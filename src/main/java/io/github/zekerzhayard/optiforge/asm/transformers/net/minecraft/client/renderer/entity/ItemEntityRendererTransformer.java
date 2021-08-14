package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.entity;

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
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ItemEntityRendererTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.entity.ItemEntityRenderer";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/ItemRenderer.java.patch#L7-L21
        //
        //        int j = this.func_177078_a(itemstack);
        //        float f = 0.25F;
        //        float f1 = MathHelper.func_76126_a(((float)p_225623_1_.func_174872_o() + p_225623_3_) / 10.0F + p_225623_1_.field_70290_d) * 0.1F + 0.1F;
        // -      float f2 = ibakedmodel.func_177552_f().func_181688_b(ItemCameraTransforms.TransformType.GROUND).field_178363_d.func_195900_b();
        // +      float f2 = shouldBob() ? ibakedmodel.func_177552_f().func_181688_b(ItemCameraTransforms.TransformType.GROUND).field_178363_d.func_195900_b() : 0;
        //        p_225623_4_.func_227861_a_(0.0D, (double)(f1 + 0.25F * f2), 0.0D);
        //        float f3 = p_225623_1_.func_234272_a_(p_225623_3_);
        //        p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229193_c_(f3));
        // -      float f4 = ibakedmodel.func_177552_f().field_181699_o.field_178363_d.func_195899_a();
        // -      float f5 = ibakedmodel.func_177552_f().field_181699_o.field_178363_d.func_195900_b();
        // -      float f6 = ibakedmodel.func_177552_f().field_181699_o.field_178363_d.func_195902_c();
        //        if (!flag) {
        // -         float f7 = -0.0F * (float)(j - 1) * 0.5F * f4;
        // -         float f8 = -0.0F * (float)(j - 1) * 0.5F * f5;
        // -         float f9 = -0.09375F * (float)(j - 1) * 0.5F * f6;
        // +         float f7 = -0.0F * (float)(j - 1) * 0.5F;
        // +         float f8 = -0.0F * (float)(j - 1) * 0.5F;
        // +         float f9 = -0.09375F * (float)(j - 1) * 0.5F;
        //           p_225623_4_.func_227861_a_((double)f7, (double)f8, (double)f9);
        //        }
        //
        //

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/ItemRenderer.java.patch#L42-L43
        //
        //                 float f11 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
        //                 float f13 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
        //                 float f10 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
        // -               p_225623_4_.func_227861_a_((double)f11, (double)f13, (double)f10);
        // +               p_225623_4_.func_227861_a_(shouldSpreadItems() ? f11 : 0, shouldSpreadItems() ? f13 : 0, shouldSpreadItems() ? f10 : 0);
        //              } else {
        //                 float f12 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
        //                 float f14 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
        // -               p_225623_4_.func_227861_a_((double)f12, (double)f14, 0.0D);
        // +               p_225623_4_.func_227861_a_(shouldSpreadItems() ? f12 : 0, shouldSpreadItems() ? f14 : 0, 0.0D);
        //              }
        //           }
        //
        //           this.field_177080_a.func_229111_a_(itemstack, ItemCameraTransforms.TransformType.GROUND, false, p_225623_4_, p_225623_5_, p_225623_6_, OverlayTexture.field_229196_a_, ibakedmodel);
        //           p_225623_4_.func_227865_b_();
        //           if (!flag) {
        // -            p_225623_4_.func_227861_a_((double)(0.0F * f4), (double)(0.0F * f5), (double)(0.09375F * f6));
        // +            p_225623_4_.func_227861_a_(0.0, 0.0, 0.09375F);
        //           }
        //        }
        //
        //

        MethodNode render = Objects.requireNonNull(ASMUtils.findMethod(input, ASMAPI.mapMethod("m_7392_"), "(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"));

        LocalVariableNode f4 = Objects.requireNonNull(ASMUtils.findLocalVariable(render, "F", 6));
        LocalVariableNode f5 = Objects.requireNonNull(ASMUtils.findLocalVariable(render, "F", 7));
        LocalVariableNode f6 = Objects.requireNonNull(ASMUtils.findLocalVariable(render, "F", 8));

        render.localVariables.remove(f4);
        render.localVariables.remove(f5);
        render.localVariables.remove(f6);

        for (AbstractInsnNode ain : render.instructions.toArray()) {
            if (ain instanceof MethodInsnNode min && min.getOpcode() == Opcodes.INVOKEVIRTUAL && Objects.equals(min.owner, "net/minecraft/client/renderer/entity/ItemEntityRenderer") && Objects.equals(min.name, "shouldBob") && Objects.equals(min.desc, "()Z")) {
                JumpInsnNode jin0 = (JumpInsnNode) min.getNext();
                while (!(jin0.getNext() instanceof LabelNode ln0 && Objects.equals(ln0, jin0.label))) {
                    render.instructions.remove(jin0.getNext());
                }
                AbstractInsnNode ain0 = jin0.getNext();
                while (!(ain0 instanceof MethodInsnNode min0 && min0.getOpcode() == Opcodes.INVOKEVIRTUAL && Objects.equals(min0.owner, "com/mojang/math/Vector3f") && Objects.equals(min0.name, ASMAPI.mapMethod("m_122260_")) && Objects.equals(min0.desc, "()F"))) {
                    ain0 = ain0.getNext();
                }

                LabelNode label_0 = new LabelNode();
                LabelNode label_1 = new LabelNode();
                render.instructions.set(jin0, new JumpInsnNode(Opcodes.IFEQ, label_0));

                InsnList il = new InsnList();
                il.add(new JumpInsnNode(Opcodes.GOTO, label_1));
                il.add(label_0);
                il.add(new InsnNode(Opcodes.FCONST_0));
                il.add(label_1);
                render.instructions.insert(ain0, il);
            } else if (ain instanceof VarInsnNode vin) {
                if (vin.getOpcode() == Opcodes.FSTORE && vin.var == f6.index) {
                    AbstractInsnNode ain0 = vin.getPrevious();
                    do {
                        render.instructions.remove(ain0.getNext());
                        ain0 = ain0.getPrevious();
                    } while (!(ain0 instanceof MethodInsnNode min0 && min0.getOpcode() == Opcodes.INVOKEVIRTUAL && Objects.equals(min0.owner, "com/mojang/blaze3d/vertex/PoseStack") && Objects.equals(min0.name, ASMAPI.mapMethod("m_85845_")) && Objects.equals(min0.desc, "(Lcom/mojang/math/Quaternion;)V")));
                } else if (vin.getOpcode() == Opcodes.FLOAD && (vin.var == f4.index || vin.var == f5.index || vin.var == f6.index)) {
                    AbstractInsnNode ainP = vin.getPrevious();
                    AbstractInsnNode ainN = vin.getNext();
                    if (ainP.getOpcode() == Opcodes.FCONST_0) {
                        render.instructions.set(ainP, new InsnNode(Opcodes.DCONST_0));
                    } else if (ainP instanceof LdcInsnNode lin0) {
                        lin0.cst = 0.09375D;
                    }
                    if (ainN.getNext().getOpcode() == Opcodes.F2D) {
                        render.instructions.remove(ainN.getNext());
                    }
                    render.instructions.remove(vin.getNext());
                    render.instructions.remove(vin);
                }
            }
        }

        return input;
    }
}
