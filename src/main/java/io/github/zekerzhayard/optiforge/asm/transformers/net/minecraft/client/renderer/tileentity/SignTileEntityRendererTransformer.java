package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.tileentity;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class SignTileEntityRendererTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.tileentity.SignTileEntityRenderer";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/tileentity/SignTileEntityRenderer.java.patch#L7-L8
        //
        //           woodtype = WoodType.field_227038_a_;
        //        }
        //
        // -      return Atlases.func_228773_a_(woodtype);
        // +      return Atlases.field_228750_i_.get(woodtype);
        //     }
        //
        //     @OnlyIn(Dist.CLIENT)
        //

        MethodNode getMaterial = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_228877_a_"), "(Lnet/minecraft/block/Block;)Lnet/minecraft/client/renderer/model/RenderMaterial;"));

        for (AbstractInsnNode ain : getMaterial.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.INVOKESTATIC) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("net/minecraft/client/renderer/Atlases") && min.name.equals(ASMAPI.mapMethod("func_228773_a_")) && min.desc.equals("(Lnet/minecraft/block/WoodType;)Lnet/minecraft/client/renderer/model/RenderMaterial;")) {
                    getMaterial.instructions.insertBefore(min.getPrevious(), new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/Atlases", ASMAPI.mapField("field_228750_i_"), "Ljava/util/Map;"));

                    getMaterial.instructions.insert(min, new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/client/renderer/model/RenderMaterial"));
                    getMaterial.instructions.set(min, new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true));
                }
            }
        }

        return input;
    }
}
