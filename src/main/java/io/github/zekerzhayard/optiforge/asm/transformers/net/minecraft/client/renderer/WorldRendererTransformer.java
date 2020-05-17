package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer;

import java.util.Objects;

import io.github.zekerzhayard.optiforge.asm.transformers.ITransformer;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.util.Bytecode;

public class WorldRendererTransformer implements ITransformer {
    @Override
    public boolean isTargetClass(String className) {
        return className.equals("net.minecraft.client.renderer.WorldRenderer");
    }

    @Override
    public ClassNode postTransform(ClassNode cn, String mixinClassName) {
        MethodNode mn = Objects.requireNonNull(Bytecode.findMethod(cn, ASMAPI.mapMethod("func_184377_a"), "(Lnet/minecraft/util/SoundEvent;Lnet/minecraft/util/math/BlockPos;)V"));
        LabelNode start = null, end = null;
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain instanceof LabelNode) {
                LabelNode ln = (LabelNode) ain;
                if (start == null) {
                    start = ln;
                } else {
                    end = ln;
                }
            }
        }
        ASMUtils.insertLocalVariable(mn, new LocalVariableNode("musicDiscItem", "Lnet/minecraft/item/MusicDiscItem;", null, Objects.requireNonNull(start), Objects.requireNonNull(end), Bytecode.getFirstNonArgLocalIndex(mn)));
        mn.name = "playRecord";
        mn.desc = "(Lnet/minecraft/util/SoundEvent;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/MusicDiscItem;)V";
        return cn;
    }
}
