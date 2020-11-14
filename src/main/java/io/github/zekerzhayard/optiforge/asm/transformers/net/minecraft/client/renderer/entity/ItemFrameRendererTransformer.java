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
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class ItemFrameRendererTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.entity.ItemFrameRenderer";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/entity/ItemFrameRenderer.java.patch#L16-L34
        //
        //        ItemStack itemstack = p_225623_1_.func_82335_i();
        //        if (!itemstack.func_190926_b()) {
        // -         boolean flag1 = itemstack.func_77973_b() == Items.field_151098_aY;
        // +         MapData mapdata = FilledMapItem.func_195950_a(itemstack, p_225623_1_.field_70170_p);
        //           if (flag) {
        //              p_225623_4_.func_227861_a_(0.0D, 0.0D, 0.5D);
        //           } else {
        //              p_225623_4_.func_227861_a_(0.0D, 0.0D, 0.4375D);
        //           }
        //
        // -         int i = flag1 ? p_225623_1_.func_82333_j() % 4 * 2 : p_225623_1_.func_82333_j();
        // +         int i = mapdata != null ? p_225623_1_.func_82333_j() % 4 * 2 : p_225623_1_.func_82333_j();
        //           p_225623_4_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_((float)i * 360.0F / 8.0F));
        // -         if (flag1) {
        // +         if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderItemInFrameEvent(p_225623_1_, this, p_225623_4_, p_225623_5_, p_225623_6_))) {
        // +         if (mapdata != null) {
        //              p_225623_4_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
        //              float f = 0.0078125F;
        //              p_225623_4_.func_227862_a_(0.0078125F, 0.0078125F, 0.0078125F);
        //              p_225623_4_.func_227861_a_(-64.0D, -64.0D, 0.0D);
        // -            MapData mapdata = FilledMapItem.func_195950_a(itemstack, p_225623_1_.field_70170_p);
        //              p_225623_4_.func_227861_a_(0.0D, 0.0D, -1.0D);
        //              if (mapdata != null) {
        //

        MethodNode render = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_225623_a_"), "(Lnet/minecraft/entity/item/ItemFrameEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V"));
        // flag1 -> mapdata
        LocalVariableNode flag1 = Objects.requireNonNull(ASMUtils.findLocalVariable(render, "Z", 1));
        LocalVariableNode mapdata = Objects.requireNonNull(ASMUtils.findLocalVariable(render, "Lnet/minecraft/world/storage/MapData;", 0));

        for (AbstractInsnNode ain : render.instructions.toArray()) {
            if (ain instanceof VarInsnNode) {
                VarInsnNode vin = (VarInsnNode) ain;
                if (vin.var == flag1.index) {
                    if (vin.getOpcode() == Opcodes.ILOAD) {
                        vin.setOpcode(Opcodes.ALOAD);
                        ((JumpInsnNode) vin.getNext()).setOpcode(Opcodes.IFNULL); // IFEQ -> IFNULL
                    } else if (vin.getOpcode() == Opcodes.ISTORE) {
                        vin.setOpcode(Opcodes.ASTORE);

                        // ALOAD 12
                        // INVOKEVITRUAL net/minecraft/item/ItemStack func_77973_b ()Lnet/minecraft/item/Item; <- remove
                        // INSTANCEOF net/minecraft/item/FilledMapItem                                         <- remove
                        // ISTORE 13 (ASTORE)
                        while (!(vin.getPrevious() instanceof VarInsnNode)) {
                            render.instructions.remove(vin.getPrevious());
                        }

                        InsnList il = new InsnList();
                        il.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/item/ItemFrameEntity", ASMAPI.mapField("field_70170_p"), "Lnet/minecraft/world/World;"));
                        il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/item/FilledMapItem", ASMAPI.mapMethod("func_195950_a"), "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;)Lnet/minecraft/world/storage/MapData;", false));
                        render.instructions.insertBefore(vin, il);
                    }
                } else if (vin.var == mapdata.index) {
                    if (vin.getOpcode() == Opcodes.ALOAD) {
                        vin.var = flag1.index;
                    } else if (vin.getOpcode() == Opcodes.ASTORE) {

                        // INVOKEVITRUAL com/mojang/blaze3d/matrix/MatrixStack func_227861_a_ (DDD)V
                        // LABEL 36                                                                                                                                                     <- remove
                        // LINENUMBER 102                                                                                                                                               <- remove
                        // ALOAD 12                                                                                                                                                     <- remove
                        // ALOAD 1                                                                                                                                                      <- remove
                        // GETFIELD net/minecraft/entity/item/ItemFrameEntity field_70170_p Lnet/minecraft/world/World;                                                                 <- remove
                        // INVOKESTATIC net/optifine/reflect/ReflectorForge getMapData (Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;)Lnet/minecraft/world/storage/MapData; <- remove
                        // ASTORE 16                                                                                                                                                    <- remove
                        // LABEL 37
                        while (!(vin.getPrevious().getOpcode() == Opcodes.INVOKEVIRTUAL)) {
                            render.instructions.remove(vin.getPrevious());
                        }
                        render.instructions.remove(vin);
                    }
                }
            }
        }

        render.localVariables.remove(flag1);
        mapdata.start = flag1.start;
        mapdata.end = flag1.end;
        mapdata.index = flag1.index;

        return input;
    }
}
