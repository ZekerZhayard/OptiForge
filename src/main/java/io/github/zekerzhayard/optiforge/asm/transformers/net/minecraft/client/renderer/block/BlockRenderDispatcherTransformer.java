package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.block;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class BlockRenderDispatcherTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.block.BlockRenderDispatcher";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.17.x/patches/minecraft/net/minecraft/client/renderer/block/BlockRenderDispatcher.java.patch#L61-L63
        //
        //              float f = (float)(i >> 16 & 255) / 255.0F;
        //              float f1 = (float)(i >> 8 & 255) / 255.0F;
        //              float f2 = (float)(i & 255) / 255.0F;
        // -            this.f_110900_.m_111067_(p_110914_.m_85850_(), p_110915_.m_6299_(ItemBlockRenderTypes.m_109284_(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_);
        // +            this.f_110900_.renderModel(p_110914_.m_85850_(), p_110915_.m_6299_(ItemBlockRenderTypes.m_109284_(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData);
        //              break;
        //           case ENTITYBLOCK_ANIMATED:
        // -            this.f_173397_.m_108829_(new ItemStack(p_110913_.m_60734_()), ItemTransforms.TransformType.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
        // +            ItemStack stack = new ItemStack(p_110913_.m_60734_());
        // +            net.minecraftforge.client.RenderProperties.get(stack).getItemStackRenderer().m_108829_(stack, ItemTransforms.TransformType.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
        //           }
        //
        //        }
        //

        MethodNode renderSingleBlock = Objects.requireNonNull(ASMUtils.findMethod(input, "renderSingleBlock", "(Lnet/minecraft/world/level/block/state/BlockState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraftforge/client/model/data/IModelData;)V"));

        for (AbstractInsnNode ain : renderSingleBlock.instructions.toArray()) {
            if (ain instanceof MethodInsnNode min && min.getOpcode() == Opcodes.INVOKEVIRTUAL && Objects.equals(min.owner, "net/optifine/reflect/ReflectorMethod") && Objects.equals(min.name, "exists") && Objects.equals(min.desc, "()Z")) {
                renderSingleBlock.instructions.remove(min.getPrevious());
                renderSingleBlock.instructions.set(min, new InsnNode(Opcodes.ICONST_1));
            } else if (ain instanceof TypeInsnNode tin && tin.getOpcode() == Opcodes.CHECKCAST && Objects.equals(tin.desc, "net/minecraft/client/renderer/BlockEntityWithoutLevelRenderer")) {
                while (tin.getPrevious().getOpcode() != Opcodes.ALOAD) {
                    renderSingleBlock.instructions.remove(tin.getPrevious());
                }
                renderSingleBlock.instructions.insertBefore(tin, new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/client/RenderProperties", "get", "(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraftforge/client/IItemRenderProperties;", false));
                renderSingleBlock.instructions.set(tin, new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/client/IItemRenderProperties", "getItemStackRenderer", "()Lnet/minecraft/client/renderer/BlockEntityWithoutLevelRenderer;", true));
            }
        }

        return input;
    }
}
