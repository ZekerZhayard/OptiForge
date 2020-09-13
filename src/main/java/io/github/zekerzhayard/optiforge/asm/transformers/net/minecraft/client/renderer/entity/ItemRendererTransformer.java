package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.entity;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class ItemRendererTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.entity.ItemRenderer";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/ItemRenderer.java.patch#L12-L14
        //
        //        p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229193_c_(f3));
        // -      float f4 = ibakedmodel.func_177552_f().field_181699_o.field_178363_d.func_195899_a();
        // -      float f5 = ibakedmodel.func_177552_f().field_181699_o.field_178363_d.func_195900_b();
        // -      float f6 = ibakedmodel.func_177552_f().field_181699_o.field_178363_d.func_195902_c();
        //        if (!flag) {
        //

        MethodNode render = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_225623_a_"), "(Lnet/minecraft/entity/item/ItemEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V"));
        LocalVariableNode f4 = Objects.requireNonNull(ASMUtils.findLocalVariable(render, "F", 6));
        LocalVariableNode f5 = Objects.requireNonNull(ASMUtils.findLocalVariable(render, "F", 7));
        LocalVariableNode f6 = Objects.requireNonNull(ASMUtils.findLocalVariable(render, "F", 8));

        render.localVariables.remove(f4);
        render.localVariables.remove(f5);
        render.localVariables.remove(f6);

        for (AbstractInsnNode ain : render.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.FSTORE) {
                VarInsnNode vin = (VarInsnNode) ain;
                if (vin.var == f6.index) {

                    // invokevirtual void MatrixStack.func_227863_a_(Quaternion)
                    // remove -> label 17
                    // remove -> line 74
                    // remove -> aload 9
                    // remove -> invokeinterface ItemCameraTransforms IBakedModel.func_177552_f()
                    // remove -> getfield ItemTransformVec3f ItemCameraTransforms.field_181699_o
                    // remove -> getfield Vector3f ItemTransformVec3f.field_178363_d
                    // remove -> invokevirtual float Vector3f.func_195899_a()
                    // remove -> fstore 16
                    // remove -> label 18
                    // remove -> line 75
                    // remove -> aload 9
                    // remove -> invokeinterface ItemCameraTransforms IBakedModel.func_177552_f()
                    // remove -> getfield ItemTransformVec3f ItemCameraTransforms.field_181699_o
                    // remove -> getfield Vector3f ItemTransformVec3f.field_178363_d
                    // remove -> invokevirtual float Vector3f.func_195900_b()
                    // remove -> fstore 17
                    // remove -> label 19
                    // remove -> line 76
                    // remove -> aload 9
                    // remove -> invokeinterface ItemCameraTransforms IBakedModel.func_177552_f()
                    // remove -> getfield ItemTransformVec3f ItemCameraTransforms.field_181699_o
                    // remove -> getfield Vector3f ItemTransformVec3f.field_178363_d
                    // remove -> invokevirtual float Vector3f.func_195902_c()
                    // remove -> fstore 18
                    // label 20
                    AbstractInsnNode ain0 = vin.getPrevious();
                    MethodInsnNode min;
                    do {
                        render.instructions.remove(ain0.getNext());
                        ain0 = ain0.getPrevious();
                    } while (!(ain0.getOpcode() == Opcodes.INVOKEVIRTUAL && ((min = (MethodInsnNode) ain0).owner.equals("com/mojang/blaze3d/matrix/MatrixStack")) && min.name.equals(ASMAPI.mapMethod("func_227863_a_")) && min.desc.equals("(Lnet/minecraft/util/math/vector/Quaternion;)V")));
                }
            } else if (ain.getOpcode() == Opcodes.FLOAD) {
                // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/ItemRenderer.java.patch#L16-L21
                //
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
                // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/ItemRenderer.java.patch#L42-L43
                //
                //           if (!flag) {
                // -            p_225623_4_.func_227861_a_((double)(0.0F * f4), (double)(0.0F * f5), (double)(0.09375F * f6));
                // +            p_225623_4_.func_227861_a_(0.0, 0.0, 0.09375F);
                //           }
                //

                VarInsnNode vin = (VarInsnNode) ain;
                if (vin.var == f4.index || vin.var == f5.index || vin.var == f6.index) {

                    // remove -> fload 16/17/18
                    // remove -> fmul
                    render.instructions.remove(vin.getNext());
                    render.instructions.remove(vin);
                }
            }
        }

        return input;
    }
}
