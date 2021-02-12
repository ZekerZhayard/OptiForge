package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.util;

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

public class UtilTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.util.Util";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/util/Util.java.patch#L7-L8
        //
        //        try {
        //           type = DataFixesManager.func_210901_a().getSchema(DataFixUtils.makeKey(SharedConstants.func_215069_a().getWorldVersion())).getChoiceType(p_240990_0_, p_240990_1_);
        //        } catch (IllegalArgumentException illegalargumentexception) {
        // -         field_195650_a.error("No data fixer registered for {}", (Object)p_240990_1_);
        // +         field_195650_a.debug("No data fixer registered for {}", (Object)p_240990_1_);
        //           if (SharedConstants.field_206244_b) {
        //              throw illegalargumentexception;
        //           }
        //

        MethodNode attemptDataFixInternal = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_240990_b_"), "(Lcom/mojang/datafixers/DSL$TypeReference;Ljava/lang/String;)Lcom/mojang/datafixers/types/Type;"));

        for (AbstractInsnNode ain : attemptDataFixInternal.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.INVOKEINTERFACE) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("org/apache/logging/log4j/Logger") && min.name.equals("error") && min.desc.equals("(Ljava/lang/String;Ljava/lang/Object;)V")) {
                    min.name = "debug";
                }
            }
        }

        return input;
    }
}
