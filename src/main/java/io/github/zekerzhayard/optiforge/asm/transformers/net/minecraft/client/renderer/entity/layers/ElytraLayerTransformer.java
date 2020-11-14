package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.entity.layers;

import java.util.ArrayList;
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
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class ElytraLayerTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.entity.layers.ElytraLayer";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/layers/ElytraLayer.java.patch#L7-L8
        //
        //        ItemStack itemstack = p_225628_4_.func_184582_a(EquipmentSlotType.CHEST);
        // -      if (itemstack.func_77973_b() == Items.field_185160_cR) {
        // +      if (shouldRender(itemstack, p_225628_4_)) {
        //           ResourceLocation resourcelocation;
        //

        MethodNode render = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_225628_a_"), "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFF)V"));

        for (AbstractInsnNode ain : render.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.IF_ACMPNE) {

                // add -> aload 0
                // aload 11
                // remove -> invokevirtual Item ItemStack.func_77973_b()
                // remove -> getstatic Item Items.field_185160_cR
                // remove -> if_acmpne 24
                // add -> aload 4
                // add -> invokevirtual boolean ElytraLayer.shouldRender(ItemStack, LivingEntity)
                // add -> ifeq 24
                while (ain.getPrevious().getOpcode() != Opcodes.ALOAD) {
                    render.instructions.remove(ain.getPrevious());
                }
                render.instructions.insertBefore(ain.getPrevious(), new VarInsnNode(Opcodes.ALOAD, 0));
                render.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 4));
                render.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/entity/layers/ElytraLayer", "shouldRender", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Z", false));
                ((JumpInsnNode) ain).setOpcode(Opcodes.IFEQ);
            } else if (ain.getOpcode() == Opcodes.GETSTATIC) {

                // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/layers/ElytraLayer.java.patch#L15-L22
                //
                //              } else {
                // -               resourcelocation = field_188355_a;
                // +               resourcelocation = getElytraTexture(itemstack, p_225628_4_);
                //              }
                //           } else {
                // -            resourcelocation = field_188355_a;
                // +            resourcelocation = getElytraTexture(itemstack, p_225628_4_);
                //           }
                //

                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.owner.equals("net/minecraft/client/renderer/entity/layers/ElytraLayer") && fin.name.equals(ASMAPI.mapField("field_188355_a")) && fin.desc.equals("Lnet/minecraft/util/ResourceLocation;")) {
                    InsnList il = new InsnList();
                    il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    il.add(new VarInsnNode(Opcodes.ALOAD, ASMUtils.findLocalVariableIndex(render, "Lnet/minecraft/item/ItemStack;", 0)));
                    il.add(new VarInsnNode(Opcodes.ALOAD, 4));
                    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/entity/layers/ElytraLayer", "getElytraTexture", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/util/ResourceLocation;", false));
                    render.instructions.insertBefore(fin, il);
                    render.instructions.remove(fin);
                }
            }
        }

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/layers/ElytraLayer.java.patch#L39-L41
        //
        // +   public boolean shouldRender(ItemStack stack, T entity) {
        // +      return stack.func_77973_b() == Items.field_185160_cR;
        // +   }
        //

        MethodNode shouldRender = new MethodNode();

        shouldRender.access = Opcodes.ACC_PUBLIC;
        shouldRender.name = "shouldRender";
        shouldRender.desc = "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Z";
        shouldRender.signature = "(Lnet/minecraft/item/ItemStack;TT;)Z";

        LabelNode shouldRender_label_0 = new LabelNode();
        LabelNode shouldRender_label_1 = new LabelNode();
        LabelNode shouldRender_label_2 = new LabelNode();
        LabelNode shouldRender_label_3 = new LabelNode();
        shouldRender.localVariables = new ArrayList<>();
        shouldRender.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/client/renderer/entity/layers/ElytraLayer;", "Lnet/minecraft/client/renderer/entity/layers/ElytraLayer<TT;TM;>;", shouldRender_label_0, shouldRender_label_3, 0));
        shouldRender.localVariables.add(new LocalVariableNode("stack", "Lnet/minecraft/item/ItemStack;", null, shouldRender_label_0, shouldRender_label_3, 1));
        shouldRender.localVariables.add(new LocalVariableNode("entity", "Lnet/minecraft/entity/LivingEntity;", "TT;", shouldRender_label_0, shouldRender_label_3, 2));

        shouldRender.instructions.add(shouldRender_label_0);
        shouldRender.instructions.add(new LineNumberNode(67, shouldRender_label_0));
        shouldRender.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        shouldRender.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemStack", ASMAPI.mapMethod("func_77973_b"), "()Lnet/minecraft/item/Item;", false));
        shouldRender.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/item/Items", ASMAPI.mapField("field_185160_cR"), "Lnet/minecraft/item/Item;"));
        shouldRender.instructions.add(new JumpInsnNode(Opcodes.IF_ACMPNE, shouldRender_label_1));
        shouldRender.instructions.add(new InsnNode(Opcodes.ICONST_1));
        shouldRender.instructions.add(new JumpInsnNode(Opcodes.GOTO, shouldRender_label_2));

        shouldRender.instructions.add(shouldRender_label_1);
        shouldRender.instructions.add(new InsnNode(Opcodes.ICONST_0));

        shouldRender.instructions.add(shouldRender_label_2);
        shouldRender.instructions.add(new InsnNode(Opcodes.IRETURN));
        shouldRender.instructions.add(shouldRender_label_3);

        input.methods.add(input.methods.indexOf(render) + 1, shouldRender);

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/layers/ElytraLayer.java.patch#L51-L53
        //
        // +   public ResourceLocation getElytraTexture(ItemStack stack, T entity) {
        // +      return field_188355_a;
        // +   }
        //

        MethodNode getElytraTexture = new MethodNode();

        getElytraTexture.access = Opcodes.ACC_PUBLIC;
        getElytraTexture.name = "getElytraTexture";
        getElytraTexture.desc = "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/util/ResourceLocation;";
        getElytraTexture.signature = "(Lnet/minecraft/item/ItemStack;TT;)Lnet/minecraft/util/ResourceLocation;";

        LabelNode getElytraTexture_label_0 = new LabelNode();
        LabelNode getElytraTexture_label_1 = new LabelNode();
        getElytraTexture.localVariables = new ArrayList<>();
        getElytraTexture.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/client/renderer/entity/layers/ElytraLayer;", "Lnet/minecraft/client/renderer/entity/layers/ElytraLayer<TT;TM;>;", getElytraTexture_label_0, getElytraTexture_label_1, 0));
        getElytraTexture.localVariables.add(new LocalVariableNode("stack", "Lnet/minecraft/item/ItemStack;", null, getElytraTexture_label_0, getElytraTexture_label_1, 1));
        getElytraTexture.localVariables.add(new LocalVariableNode("entity", "Lnet/minecraft/entity/LivingEntity;", "TT;", getElytraTexture_label_0, getElytraTexture_label_1, 2));

        getElytraTexture.instructions.add(getElytraTexture_label_0);
        getElytraTexture.instructions.add(new LineNumberNode(79, getElytraTexture_label_0));
        getElytraTexture.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/entity/layers/ElytraLayer", ASMAPI.mapField("field_188355_a"), "Lnet/minecraft/util/ResourceLocation;"));
        getElytraTexture.instructions.add(new InsnNode(Opcodes.ARETURN));
        getElytraTexture.instructions.add(getElytraTexture_label_1);

        input.methods.add(input.methods.indexOf(shouldRender) + 1, getElytraTexture);

        return input;
    }
}
