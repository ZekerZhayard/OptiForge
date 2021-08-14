package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.block.model;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ItemOverridesTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.block.model.ItemOverrides";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.17.x/patches/minecraft/net/minecraft/client/renderer/block/model/ItemOverrides.java.patch#L40-L43
        //
        //        }
        //
        //        return p_173465_;
        // +   }
        // +
        // +   public com.google.common.collect.ImmutableList<BakedOverride> getOverrides() {
        // +      return com.google.common.collect.ImmutableList.copyOf(f_111735_);
        //     }
        //
        //     @OnlyIn(Dist.CLIENT)
        //

        MethodNode getOverrides = new MethodNode(Opcodes.ACC_PUBLIC, "getOverrides", "()Lcom/google/common/collect/ImmutableList;", "()Lcom/google/common/collect/ImmutableList<Lnet/minecraft/client/renderer/block/model/ItemOverrides$BakedOverride;>;", null);

        LabelNode label_0_getOverrides = new LabelNode();
        LabelNode label_1_getOverrides = new LabelNode();
        getOverrides.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/client/renderer/block/model/ItemOverrides;", null, label_0_getOverrides, label_1_getOverrides, 0));

        getOverrides.instructions.add(label_0_getOverrides);
        getOverrides.instructions.add(new LineNumberNode(105, label_0_getOverrides));
        getOverrides.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        getOverrides.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/block/model/ItemOverrides", ASMAPI.mapField("f_111735_"), "[Lnet/minecraft/client/renderer/block/model/ItemOverrides$BakedOverride;"));
        getOverrides.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/google/common/collect/ImmutableList", "copyOf", "([Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;", false));
        getOverrides.instructions.add(new InsnNode(Opcodes.ARETURN));
        getOverrides.instructions.add(label_1_getOverrides);

        input.methods.add(input.methods.indexOf(Objects.requireNonNull(ASMUtils.findMethod(input, ASMAPI.mapMethod("m_173464_"), "(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/entity/LivingEntity;I)Lnet/minecraft/client/resources/model/BakedModel;"))) + 1, getOverrides);

        return input;
    }
}
