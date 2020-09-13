package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.entity;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
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
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/EntityRenderer.java.patch#L9
        //
        //     public void func_225623_a_(T p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
        // -      if (this.func_177070_b(p_225623_1_)) {
        // -         this.func_225629_a_(p_225623_1_, p_225623_1_.func_145748_c_(), p_225623_4_, p_225623_5_, p_225623_6_);
        // +      net.minecraftforge.client.event.RenderNameplateEvent renderNameplateEvent = new net.minecraftforge.client.event.RenderNameplateEvent(p_225623_1_, p_225623_1_.func_145748_c_(), this, p_225623_4_, p_225623_5_, p_225623_6_, p_225623_3_);
        // +      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
        // +      if (renderNameplateEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameplateEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || this.func_177070_b(p_225623_1_))) {
        // +         this.func_225629_a_(p_225623_1_, renderNameplateEvent.getContent(), p_225623_4_, p_225623_5_, p_225623_6_);
        //        }
        //     }
        //

        MethodNode render = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_225623_a_"), "(Lnet/minecraft/entity/Entity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V"));

        IntInsnNode iin = null;
        for (AbstractInsnNode ain : render.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.BIPUSH && iin == null) {
                iin = (IntInsnNode) ain;
                iin.operand++;
            } else if (ain.getOpcode() == Opcodes.INVOKESTATIC && iin != null) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("net/optifine/reflect/Reflector") && min.name.equals("newInstance") && min.desc.equals("(Lnet/optifine/reflect/ReflectorConstructor;[Ljava/lang/Object;)Ljava/lang/Object;")) {
                    InsnList il = new InsnList();
                    il.add(new InsnNode(Opcodes.DUP));
                    il.add(new IntInsnNode(Opcodes.BIPUSH, iin.operand - 1));
                    il.add(new VarInsnNode(Opcodes.FLOAD, 3));
                    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;"));
                    il.add(new InsnNode(Opcodes.AASTORE));

                    render.instructions.insertBefore(min, il);

                    break;
                }
            }
        }

        return input;
    }
}
