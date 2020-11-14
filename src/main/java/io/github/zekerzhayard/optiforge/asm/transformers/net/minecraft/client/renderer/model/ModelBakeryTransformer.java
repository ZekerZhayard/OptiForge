package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.model;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class ModelBakeryTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.model.ModelBakery";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/model/ModelBakery.java.patch#L17
        //
        // +   protected void processLoading(IProfiler p_i226056_3_, int p_i226056_4_) {
        // +      net.minecraftforge.client.model.ModelLoaderRegistry.onModelLoadingStart();
        //        p_i226056_3_.func_76320_a("missing_model");
        //

        MethodNode processLoading = Objects.requireNonNull(Bytecode.findMethod(input, "processLoading", "(Lnet/minecraft/profiler/IProfiler;I)V"));

        InsnList processLoading_il = new InsnList();
        processLoading_il.add(new LabelNode());
        processLoading_il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/client/model/ModelLoaderRegistry", "onModelLoadingStart", "()V", false));
        processLoading.instructions.insert(processLoading_il);

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/model/ModelBakery.java.patch#L83-L84
        //
        // -         IBakedModel ibakedmodel = iunbakedmodel.func_225613_a_(this, this.field_229322_z_::func_229151_a_, p_217845_2_, p_217845_1_);
        // +         IBakedModel ibakedmodel = iunbakedmodel.func_225613_a_(this, textureGetter, p_217845_2_, p_217845_1_);
        //           this.field_217850_G.put(triple, ibakedmodel);
        //

        MethodNode getBakedModel = Objects.requireNonNull(Bytecode.findMethod(input, "getBakedModel", "(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/model/IModelTransform;Ljava/util/function/Function;)Lnet/minecraft/client/renderer/model/IBakedModel;"));

        // logic:
        //   ...
        //   GOTO label_1                             <- add
        //   label_0                                  <- add
        //     [Vanilla Logic]
        //   GOTO label_2                             <- add
        //   label_1
        //   IF ([Forge Exist] == false) GOTO label_0 <- modify (label_2)
        //     [Forge Logic]
        //   label_2
        //   ...
        AbstractInsnNode[] ains = getBakedModel.instructions.toArray();
        int ifeqCount = 0;
        LabelNode label_0 = new LabelNode();
        LabelNode label_1 = null;
        LabelNode label_2 = null;
        for (int i = ains.length - 1; i >= 0; i--) {
            AbstractInsnNode ain = ains[i];
            if (ain.getOpcode() == Opcodes.IFEQ) {
                JumpInsnNode jin = (JumpInsnNode) ain;
                ifeqCount++;
                if (ifeqCount == 1) {
                    label_2 = jin.label;
                    jin.label = label_0;
                }
            } else if (label_1 == null && label_2 != null && ain instanceof LabelNode) {
                label_1 = (LabelNode) ain;
                getBakedModel.instructions.insertBefore(ain, new JumpInsnNode(Opcodes.GOTO, label_2));
            } else if (label_1 != null && label_2 != null && ain.getOpcode() == Opcodes.ALOAD) {
                VarInsnNode vin = (VarInsnNode) ain;
                int iUnbakedModelIndex = ASMUtils.findLocalVariableIndex(getBakedModel, "Lnet/minecraft/client/renderer/model/IUnbakedModel;", 0);
                if (vin.var == iUnbakedModelIndex) {
                    getBakedModel.instructions.insertBefore(vin, new JumpInsnNode(Opcodes.GOTO, label_1));
                    getBakedModel.instructions.insertBefore(vin, label_0);
                    break;
                }
            }
        }

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/model/ModelBakery.java.patch#L42
        //
        //           } catch (Exception exception) {
        // +            exception.printStackTrace();
        //              field_177603_c.warn("Unable to bake model: '{}': {}", p_229350_1_, exception);
        //           }
        //

        MethodNode lambda$uploadTextures$12 = Objects.requireNonNull(Bytecode.findMethod(input, "lambda$uploadTextures$12", "(Lnet/minecraft/util/ResourceLocation;)V"));
        for (AbstractInsnNode ain : lambda$uploadTextures$12.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.GETSTATIC) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.owner.equals("net/minecraft/client/renderer/model/ModelBakery") && fin.name.equals(ASMAPI.mapField("field_177603_c")) && fin.desc.equals("Lorg/apache/logging/log4j/Logger;")) {
                    InsnList il = new InsnList();
                    il.add(new VarInsnNode(Opcodes.ALOAD, ASMUtils.findLocalVariableIndex(lambda$uploadTextures$12, "Ljava/lang/Exception;", 0)));
                    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false));
                    lambda$uploadTextures$12.instructions.insertBefore(fin, il);
                    break;
                }
            }
        }

        return input;
    }
}
