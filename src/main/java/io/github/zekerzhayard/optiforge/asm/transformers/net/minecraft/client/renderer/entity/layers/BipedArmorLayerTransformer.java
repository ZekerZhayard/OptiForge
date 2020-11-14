package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.entity.layers;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.util.Bytecode;

public class BipedArmorLayerTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.entity.layers.BipedArmorLayer";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/layers/BipedArmorLayer.java.patch#L19-L25
        //
        //                 float f2 = (float)(i & 255) / 255.0F;
        // -               this.func_241738_a_(p_241739_1_, p_241739_2_, p_241739_5_, armoritem, flag1, p_241739_6_, flag, f, f1, f2, (String)null);
        // -               this.func_241738_a_(p_241739_1_, p_241739_2_, p_241739_5_, armoritem, flag1, p_241739_6_, flag, 1.0F, 1.0F, 1.0F, "overlay");
        // +               this.func_241738_a_(p_241739_1_, p_241739_2_, p_241739_5_, flag1, p_241739_6_, f, f1, f2, this.getArmorResource(p_241739_3_, itemstack, p_241739_4_, null));
        // +               this.func_241738_a_(p_241739_1_, p_241739_2_, p_241739_5_, flag1, p_241739_6_, 1.0F, 1.0F, 1.0F, this.getArmorResource(p_241739_3_, itemstack, p_241739_4_, "overlay"));
        //              } else {
        // -               this.func_241738_a_(p_241739_1_, p_241739_2_, p_241739_5_, armoritem, flag1, p_241739_6_, flag, 1.0F, 1.0F, 1.0F, (String)null);
        // +               this.func_241738_a_(p_241739_1_, p_241739_2_, p_241739_5_, flag1, p_241739_6_, 1.0F, 1.0F, 1.0F, this.getArmorResource(p_241739_3_, itemstack, p_241739_4_, null));
        //              }
        //

        MethodNode renderArmorPiece = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_241739_a_"), "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/inventory/EquipmentSlotType;ILnet/minecraft/client/renderer/entity/model/BipedModel;)V"));
        for (AbstractInsnNode ain : renderArmorPiece.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.INVOKESPECIAL) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("net/minecraft/client/renderer/entity/layers/BipedArmorLayer") && min.name.equals("renderModel") && min.desc.equals("(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IZLnet/minecraft/client/renderer/entity/model/BipedModel;FFFLnet/minecraft/util/ResourceLocation;)V")) {
                    min.name = "func_241738_a_"; // This method name shouldn't be mapped because it is added by forge.
                }
            }
        }

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/layers/BipedArmorLayer.java.patch#L33-L37
        //
        //     private void func_241738_a_(MatrixStack p_241738_1_, IRenderTypeBuffer p_241738_2_, int p_241738_3_, ArmorItem p_241738_4_, boolean p_241738_5_, A p_241738_6_, boolean p_241738_7_, float p_241738_8_, float p_241738_9_, float p_241738_10_, @Nullable String p_241738_11_) {
        // -      IVertexBuilder ivertexbuilder = ItemRenderer.func_239386_a_(p_241738_2_, RenderType.func_239263_a_(this.func_241737_a_(p_241738_4_, p_241738_7_, p_241738_11_)), false, p_241738_5_);
        // +       func_241738_a_(p_241738_1_, p_241738_2_, p_241738_3_, p_241738_5_, p_241738_6_, p_241738_8_, p_241738_9_, p_241738_10_, this.func_241737_a_(p_241738_4_, p_241738_7_, p_241738_11_));
        // +   }
        // +   private void func_241738_a_(MatrixStack p_241738_1_, IRenderTypeBuffer p_241738_2_, int p_241738_3_, boolean p_241738_5_, A p_241738_6_, float p_241738_8_, float p_241738_9_, float p_241738_10_, ResourceLocation armorResource) {
        // +      IVertexBuilder ivertexbuilder = ItemRenderer.func_239386_a_(p_241738_2_, RenderType.func_239263_a_(armorResource), false, p_241738_5_);
        //        p_241738_6_.func_225598_a_(p_241738_1_, ivertexbuilder, p_241738_3_, OverlayTexture.field_229196_a_, p_241738_8_, p_241738_9_, p_241738_10_, 1.0F);
        //

        MethodNode renderModel = Objects.requireNonNull(Bytecode.findMethod(input, "renderModel", "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IZLnet/minecraft/client/renderer/entity/model/BipedModel;FFFLnet/minecraft/util/ResourceLocation;)V"));
        renderModel.name = "func_241738_a_"; // This method name shouldn't be mapped because it is added by forge.

        return input;
    }
}
