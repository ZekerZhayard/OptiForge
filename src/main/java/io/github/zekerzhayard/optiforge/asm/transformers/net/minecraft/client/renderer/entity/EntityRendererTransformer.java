package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.entity;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class EntityRendererTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.entity.EntityRenderer";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/EntityRenderer.java.patch#L20-L21
        //
        //     protected void func_225629_a_(T p_225629_1_, ITextComponent p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
        //        double d0 = this.field_76990_c.func_229099_b_(p_225629_1_);
        // -      if (!(d0 > 4096.0D)) {
        // +      if (net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(p_225629_1_, d0)) {
        //           boolean flag = !p_225629_1_.func_226273_bm_();
        //

        MethodNode renderName = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_225629_a_"), "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/text/ITextComponent;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V"));

        for (AbstractInsnNode ain : renderName.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.IFGT) {

                // label 1
                // line 136
                // add -> aload 1
                // dload 6
                // remove -> ldc Double 4096.0
                // remove -> dcmpl
                // remove -> ifgt 22
                // add -> invokestatic boolean ForgeHooksClient.isNameplateInRenderDistance(Entity, double)
                // add -> ifeq 22
                // label 2
                // line 138
                AbstractInsnNode ain0 = ain.getPrevious();
                while (ain0.getOpcode() != Opcodes.DLOAD) {
                    ain0 = ain0.getPrevious();
                    renderName.instructions.remove(ain0.getNext());
                }
                renderName.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.ALOAD, 1));
                renderName.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/client/ForgeHooksClient", "isNameplateInRenderDistance", "(Lnet/minecraft/entity/Entity;D)Z", false));
                ((JumpInsnNode) ain).setOpcode(Opcodes.IFEQ);

                break;
            }
        }

        return input;
    }
}
