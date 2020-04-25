package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.entity;

import java.util.Objects;

import io.github.zekerzhayard.optiforge.asm.transformers.ITransformer;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.util.Bytecode;

public class EntityRendererManagerTransformer implements ITransformer {
    @Override
    public boolean isTargetClass(String className) {
        return className.equals("net.minecraft.client.renderer.entity.EntityRendererManager");
    }

    @Override
    public ClassNode preTransform(ClassNode cn) {
        MethodNode mn = Objects.requireNonNull(Bytecode.findMethod(cn, "<init>", "(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/renderer/ItemRenderer;Lnet/minecraft/resources/IReloadableResourceManager;Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/GameSettings;)V"));
        FieldInsnNode fin = Objects.requireNonNull(ASMUtils.findFirstFieldInsnNode(mn, Opcodes.GETSTATIC, "net/minecraft/util/registry/Registry", ASMAPI.mapField("field_212629_r"), "Lnet/minecraft/util/registry/DefaultedRegistry;"));
        mn.instructions.insertBefore(fin, new InsnNode(Opcodes.RETURN));
        return cn;
    }
}
