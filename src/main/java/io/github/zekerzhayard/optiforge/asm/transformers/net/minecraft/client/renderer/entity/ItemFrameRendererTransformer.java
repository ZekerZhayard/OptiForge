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
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ItemFrameRendererTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.entity.ItemFrameRenderer";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.17.x/patches/minecraft/net/minecraft/client/renderer/entity/ItemFrameRenderer.java.patch#L7-L8
        //
        //        if (!flag) {
        //           BlockRenderDispatcher blockrenderdispatcher = this.f_115046_.m_91289_();
        //           ModelManager modelmanager = blockrenderdispatcher.m_110907_().m_110881_();
        // -         ModelResourceLocation modelresourcelocation = this.m_174212_(p_115076_, itemstack);
        // +         ModelResourceLocation modelresourcelocation = p_115076_.m_31822_().m_41720_() instanceof MapItem ? f_115045_ : f_115044_;
        //           p_115079_.m_85836_();
        //           p_115079_.m_85837_(-0.5D, -0.5D, -0.5D);
        //           blockrenderdispatcher.m_110937_().m_111067_(p_115079_.m_85850_(), p_115080_.m_6299_(Sheets.m_110789_()), (BlockState)null, modelmanager.m_119422_(modelresourcelocation), 1.0F, 1.0F, 1.0F, p_115081_, OverlayTexture.f_118083_);
        //

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.17.x/patches/minecraft/net/minecraft/client/renderer/entity/ItemFrameRenderer.java.patch#L16-L35
        //
        //        }
        //
        //        if (!itemstack.m_41619_()) {
        // -         boolean flag1 = itemstack.m_150930_(Items.f_42573_);
        // +         MapItemSavedData mapitemsaveddata = MapItem.m_42853_(itemstack, p_115076_.f_19853_);
        //           if (flag) {
        //              p_115079_.m_85837_(0.0D, 0.0D, 0.5D);
        //           } else {
        //              p_115079_.m_85837_(0.0D, 0.0D, 0.4375D);
        //           }
        //
        // -         int j = flag1 ? p_115076_.m_31823_() % 4 * 2 : p_115076_.m_31823_();
        // +         int j = mapitemsaveddata != null ? p_115076_.m_31823_() % 4 * 2 : p_115076_.m_31823_();
        //           p_115079_.m_85845_(Vector3f.f_122227_.m_122240_((float)j * 360.0F / 8.0F));
        // -         if (flag1) {
        // +         if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderItemInFrameEvent(p_115076_, this, p_115079_, p_115080_, p_115081_))) {
        // +         if (mapitemsaveddata != null) {
        //              p_115079_.m_85845_(Vector3f.f_122227_.m_122240_(180.0F));
        //              float f = 0.0078125F;
        //              p_115079_.m_85841_(0.0078125F, 0.0078125F, 0.0078125F);
        //              p_115079_.m_85837_(-64.0D, -64.0D, 0.0D);
        //              Integer integer = MapItem.m_151131_(itemstack);
        // -            MapItemSavedData mapitemsaveddata = MapItem.m_151128_(integer, p_115076_.f_19853_);
        //              p_115079_.m_85837_(0.0D, 0.0D, -1.0D);
        //              if (mapitemsaveddata != null) {
        //                 int i = this.m_174208_(p_115076_, 15728850, p_115081_);
        //

        MethodNode render = Objects.requireNonNull(ASMUtils.findMethod(input, ASMAPI.mapMethod("m_7392_"), "(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"));

        LocalVariableNode flag1 = Objects.requireNonNull(ASMUtils.findLocalVariable(render, "Z", 1));
        LocalVariableNode mapitemsaveddata = Objects.requireNonNull(ASMUtils.findLocalVariable(render, "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;", 0));
        render.localVariables.remove(flag1);

        for (AbstractInsnNode ain : render.instructions.toArray()) {
            if (ain instanceof MethodInsnNode min && Objects.equals(min.owner, "net/minecraft/client/renderer/entity/ItemFrameRenderer") && Objects.equals(min.name, ASMAPI.mapMethod("m_174212_")) && Objects.equals(min.desc, "(Lnet/minecraft/world/entity/decoration/ItemFrame;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/ModelResourceLocation;")) {
                render.instructions.remove(min.getPrevious());
                render.instructions.remove(min.getPrevious().getPrevious());

                InsnList il = new InsnList();
                LabelNode label_0 = new LabelNode();
                LabelNode label_1 = new LabelNode();

                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/decoration/ItemFrame", ASMAPI.mapMethod("m_31822_"), "()Lnet/minecraft/world/item/ItemStack;", false));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/item/ItemStack", ASMAPI.mapMethod("m_41720_"), "()Lnet/minecraft/world/item/Item;", false));
                il.add(new TypeInsnNode(Opcodes.INSTANCEOF, "net/minecraft/world/item/MapItem"));
                il.add(new JumpInsnNode(Opcodes.IFEQ, label_0));
                il.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/entity/ItemFrameRenderer", ASMAPI.mapField("f_115045_"), "Lnet/minecraft/client/resources/model/ModelResourceLocation;"));
                il.add(new JumpInsnNode(Opcodes.GOTO, label_1));
                il.add(label_0);
                il.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/entity/ItemFrameRenderer", ASMAPI.mapField("f_115044_"), "Lnet/minecraft/client/resources/model/ModelResourceLocation;"));
                il.add(label_1);

                render.instructions.insert(min, il);
                render.instructions.remove(min);
            } else if (ain instanceof VarInsnNode vin) {
                if (vin.var == flag1.index) {
                    if (vin.getOpcode() == Opcodes.ILOAD) {
                        vin.setOpcode(Opcodes.ALOAD);
                        ((JumpInsnNode) vin.getNext()).setOpcode(Opcodes.IFNULL);
                    } else if (vin.getOpcode() == Opcodes.ISTORE) {
                        vin.setOpcode(Opcodes.ASTORE);
                        while (!(vin.getPrevious() instanceof VarInsnNode)) {
                            render.instructions.remove(vin.getPrevious());
                        }

                        InsnList il = new InsnList();
                        il.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/entity/decoration/ItemFrame", ASMAPI.mapField("f_19853_"), "Lnet/minecraft/world/level/Level;"));
                        il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/world/item/MapItem", ASMAPI.mapMethod("m_42853_"), "(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;", false));
                        render.instructions.insertBefore(vin, il);
                    }
                } else if (vin.var == mapitemsaveddata.index) {
                    if (vin.getOpcode() == Opcodes.ALOAD) {
                        vin.var = flag1.index;
                    } else if (vin.getOpcode() == Opcodes.ASTORE) {
                        while (!(vin.getPrevious().getOpcode() == Opcodes.ASTORE)) {
                            render.instructions.remove(vin.getPrevious());
                        }
                        render.instructions.remove(vin);
                    }
                }
            }
        }

        mapitemsaveddata.start = flag1.start;
        mapitemsaveddata.end = flag1.end;
        mapitemsaveddata.index = flag1.index;

        return input;
    }
}
