package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.entity;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class EntityRendererManagerTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.entity.EntityRendererManager";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/EntityRendererManager.java.patch#L18-L19
        //
        //     private void func_229093_a_(MatrixStack p_229093_1_, IVertexBuilder p_229093_2_, Entity p_229093_3_, float p_229093_4_) {
        //        float f = p_229093_3_.func_213311_cf() / 2.0F;
        //        this.func_229094_a_(p_229093_1_, p_229093_2_, p_229093_3_, 1.0F, 1.0F, 1.0F);
        // -      if (p_229093_3_ instanceof EnderDragonEntity) {
        // +      if (p_229093_3_.isMultipartEntity()) {
        //           double d0 = -MathHelper.func_219803_d((double)p_229093_4_, p_229093_3_.field_70142_S, p_229093_3_.func_226277_ct_());
        //           double d1 = -MathHelper.func_219803_d((double)p_229093_4_, p_229093_3_.field_70137_T, p_229093_3_.func_226278_cu_());
        //           double d2 = -MathHelper.func_219803_d((double)p_229093_4_, p_229093_3_.field_70136_U, p_229093_3_.func_226281_cx_());
        //

        MethodNode renderDebugBoundingBox = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_229093_a_"), "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lnet/minecraft/entity/Entity;F)V"));

        for (AbstractInsnNode ain : renderDebugBoundingBox.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.INSTANCEOF) {
                TypeInsnNode tin = (TypeInsnNode) ain;
                if (tin.desc.equals("net/minecraft/entity/boss/dragon/EnderDragonEntity")) {
                    renderDebugBoundingBox.instructions.set(tin, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/Entity", "isMultipartEntity", "()Z", false));
                }
            } else if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("net/minecraft/entity/boss/dragon/EnderDragonEntity") && min.name.equals(ASMAPI.mapMethod("func_213404_dT")) && min.desc.equals("()[Lnet/minecraft/entity/boss/dragon/EnderDragonPartEntity;")) {

                    //
                    // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/EntityRendererManager.java.patch#L24-L25
                    //
                    //           double d1 = -MathHelper.func_219803_d((double)p_229093_4_, p_229093_3_.field_70137_T, p_229093_3_.func_226278_cu_());
                    //           double d2 = -MathHelper.func_219803_d((double)p_229093_4_, p_229093_3_.field_70136_U, p_229093_3_.func_226281_cx_());
                    //
                    // -         for(EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity)p_229093_3_).func_213404_dT()) {
                    // +         for(net.minecraftforge.entity.PartEntity<?> enderdragonpartentity : p_229093_3_.getParts()) {
                    //              p_229093_1_.func_227860_a_();
                    //              double d3 = d0 + MathHelper.func_219803_d((double)p_229093_4_, enderdragonpartentity.field_70142_S, enderdragonpartentity.func_226277_ct_());
                    //              double d4 = d1 + MathHelper.func_219803_d((double)p_229093_4_, enderdragonpartentity.field_70137_T, enderdragonpartentity.func_226278_cu_());
                    //

                    renderDebugBoundingBox.instructions.remove(min.getPrevious());
                    renderDebugBoundingBox.instructions.set(min, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/Entity", "getParts", "()[Lnet/minecraftforge/entity/PartEntity;"));
                } else if (min.owner.equals("net/minecraft/entity/boss/dragon/EnderDragonPartEntity") && (min.name.equals(ASMAPI.mapMethod("func_226277_ct_")) || min.name.equals(ASMAPI.mapMethod("func_226278_cu_")) || min.name.equals(ASMAPI.mapMethod("func_226281_cx_"))) && min.desc.equals("()D")) {
                    min.owner = "net/minecraftforge/entity/PartEntity";
                }
            } else if (ain.getOpcode() == Opcodes.GETFIELD) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.owner.equals("net/minecraft/entity/boss/dragon/EnderDragonPartEntity") && (fin.name.equals(ASMAPI.mapField("field_70142_S")) || fin.name.equals(ASMAPI.mapField("field_70137_T")) || fin.name.equals(ASMAPI.mapField("field_70136_U"))) && fin.desc.equals("D")) {
                    fin.owner = "net/minecraftforge/entity/PartEntity";
                }
            }
        }

        LocalVariableNode enderdragonpartentity = Objects.requireNonNull(ASMUtils.findLocalVariable(renderDebugBoundingBox, "Lnet/minecraft/entity/boss/dragon/EnderDragonPartEntity;", 0));
        enderdragonpartentity.desc = "Lnet/minecraftforge/entity/PartEntity;";
        enderdragonpartentity.signature = "Lnet/minecraftforge/entity/PartEntity<*>;";

        return input;
    }
}
