package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.particle;

import java.util.ArrayList;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

public class ParticleTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.particle.Particle";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/particle/Particle.java.patch#L12-L14
        //
        // +   public boolean shouldCull() {
        // +      return true;
        // +   }
        //

        MethodNode shouldCull = new MethodNode();
        shouldCull.name = "shouldCull";
        shouldCull.desc = "()Z";
        shouldCull.access = Opcodes.ACC_PUBLIC;

        LabelNode label_0 = new LabelNode();
        LabelNode label_1 = new LabelNode();
        shouldCull.localVariables = new ArrayList<>();
        shouldCull.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/client/particle/Particle;", null, label_0, label_1, 0));

        shouldCull.instructions.add(label_0);
        shouldCull.instructions.add(new LineNumberNode(217, label_0));
        shouldCull.instructions.add(new InsnNode(Opcodes.ICONST_1));
        shouldCull.instructions.add(new InsnNode(Opcodes.IRETURN));
        shouldCull.instructions.add(label_1);

        input.methods.add(shouldCull);
        return input;
    }
}
