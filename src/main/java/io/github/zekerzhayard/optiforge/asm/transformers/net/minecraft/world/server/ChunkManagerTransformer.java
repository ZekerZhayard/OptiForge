package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.world.server;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class ChunkManagerTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.world.server.ChunkManager";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        //
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/world/server/ChunkManager.java.patch#L39-L40
        //
        //     }
        //
        //     protected void func_219210_a(Entity p_219210_1_) {
        // -      if (!(p_219210_1_ instanceof EnderDragonPartEntity)) {
        // +      if (!(p_219210_1_ instanceof net.minecraftforge.entity.PartEntity)) {
        //           EntityType<?> entitytype = p_219210_1_.func_200600_R();
        //           int i = entitytype.func_233602_m_() * 16;
        //           int j = entitytype.func_220332_l();
        //

        MethodNode track = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_219210_a"), "(Lnet/minecraft/entity/Entity;)V"));

        for (AbstractInsnNode ain : track.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.INSTANCEOF) {
                TypeInsnNode tin = (TypeInsnNode) ain;
                if (tin.desc.equals("net/minecraft/entity/boss/dragon/EnderDragonPartEntity")) {
                    tin.desc = "net/minecraftforge/entity/PartEntity";
                }
            }
        }

        return input;
    }
}
