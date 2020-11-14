package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer;

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
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class WorldRendererTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.WorldRenderer";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/WorldRenderer.java.patch#L19-L20
        //
        //              boolean flag1 = blockpos2.func_177951_i(blockpos) < 768.0D;
        // -            if (!chunkrenderdispatcher$chunkrender4.func_188281_o() && !flag1) {
        // +            if (net.minecraftforge.common.ForgeConfig.CLIENT.alwaysSetupTerrainOffThread.get() || !chunkrenderdispatcher$chunkrender4.func_188281_o() && !flag1) {
        //                 this.field_175009_l.add(chunkrenderdispatcher$chunkrender4);
        //

        MethodNode setupTerrain = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_228437_a_"), "(Lnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/client/renderer/culling/ClippingHelper;ZIZ)V"));

        for (AbstractInsnNode ain : setupTerrain.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("net/minecraft/client/renderer/chunk/ChunkRenderDispatcher$ChunkRender") && min.name.equals(ASMAPI.mapMethod("func_188281_o")) && min.desc.equals("()Z")) {

                    // label 185
                    // line 1374
                    // add -> getstatic ForgeConfig$Client ForgeConfig.CLIENT
                    // add -> getfield ForgeConfigSpec$BooleanValue ForgeConfig$Client.alwaysSetupTerrainOffThread
                    // add -> invokevirtual Object ForgeConfigSpec$BooleanValue.get()
                    // add -> checkcast Boolean
                    // add -> invokevirtual boolean Boolean.booleanValue()
                    // add -> ifne 186
                    // aload 26
                    // invokevirtual boolean ChunkRenderDispatcher$ChunkRender.func_188281_o()
                    // ifne 187
                    // iload 28
                    // ifne 187
                    // label 186
                    // line 1378
                    AbstractInsnNode ain0 = min;
                    while (!(ain0 instanceof LabelNode)) {
                        ain0 = ain0.getNext();
                    }
                    LabelNode ln = (LabelNode) ain0;

                    InsnList il = new InsnList();
                    il.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/ForgeConfig", "CLIENT", "Lnet/minecraftforge/common/ForgeConfig$Client;"));
                    il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/common/ForgeConfig$Client", "alwaysSetupTerrainOffThread", "Lnet/minecraftforge/common/ForgeConfigSpec$BooleanValue;"));
                    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/common/ForgeConfigSpec$BooleanValue", "get", "()Ljava/lang/Object;", false));
                    il.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Boolean"));
                    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
                    il.add(new JumpInsnNode(Opcodes.IFNE, ln));

                    setupTerrain.instructions.insertBefore(min.getPrevious(), il);
                    break;
                }
            }
        }

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/WorldRenderer.java.patch#L44-L46
        //
        //        this.func_228441_a_(RenderType.func_228639_c_(), p_228426_1_, d0, d1, d2);
        // +      this.field_72777_q.func_209506_al().func_229356_a_(AtlasTexture.field_110575_b).setBlurMipmap(false, this.field_72777_q.field_71474_y.field_151442_I > 0); // FORGE: fix flickering leaves when mods mess up the blurMipmap settings
        //        this.func_228441_a_(RenderType.func_228641_d_(), p_228426_1_, d0, d1, d2);
        // +      this.field_72777_q.func_209506_al().func_229356_a_(AtlasTexture.field_110575_b).restoreLastBlurMipmap();
        //        this.func_228441_a_(RenderType.func_228643_e_(), p_228426_1_, d0, d1, d2);
        //

        MethodNode updateCameraAndRender = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_228426_a_"), "(Lcom/mojang/blaze3d/matrix/MatrixStack;FJZLnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/util/math/vector/Matrix4f;)V"));

        int getModelManagerCount = 0;
        for (AbstractInsnNode ain : updateCameraAndRender.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL || ain.getOpcode() == Opcodes.INVOKESPECIAL) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("net/minecraft/client/Minecraft") && min.name.equals(ASMAPI.mapMethod("func_110434_K")) && min.desc.equals("()Lnet/minecraft/client/renderer/texture/TextureManager;")) {
                    getModelManagerCount++;
                    if (getModelManagerCount > 1) {
                        min.name = ASMAPI.mapMethod("func_209506_al");
                        min.desc = "()Lnet/minecraft/client/renderer/model/ModelManager;";
                    }
                } else if (min.owner.equals("net/minecraft/client/renderer/texture/TextureManager") && min.name.equals(ASMAPI.mapMethod("func_229267_b_")) && min.desc.equals("(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/renderer/texture/Texture;")) {
                    min.owner = "net/minecraft/client/renderer/model/ModelManager";
                    min.name = ASMAPI.mapMethod("func_229356_a_");
                    min.desc = "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/renderer/texture/AtlasTexture;";
                } else if (min.owner.equals("net/minecraft/client/renderer/texture/Texture") && min.name.equals(ASMAPI.mapMethod("func_174937_a")) && min.desc.equals("(ZZ)V")) {
                    min.owner = "net/minecraft/client/renderer/texture/AtlasTexture";
                    min.name = "setBlurMipmap";
                }
            } else if (ain.getOpcode() == Opcodes.INSTANCEOF) {

                // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/WorldRenderer.java.patch#L54-L55
                //
                //        for(Entity entity : this.field_72769_h.func_217416_b()) {
                // -         if ((this.field_175010_j.func_229086_a_(entity, clippinghelper, d0, d1, d2) || entity.func_184215_y(this.field_72777_q.field_71439_g)) && (entity != p_228426_6_.func_216773_g() || p_228426_6_.func_216770_i() || p_228426_6_.func_216773_g() instanceof LivingEntity && ((LivingEntity)p_228426_6_.func_216773_g()).func_70608_bn()) && (!(entity instanceof ClientPlayerEntity) || p_228426_6_.func_216773_g() == entity)) {
                // +         if ((this.field_175010_j.func_229086_a_(entity, clippinghelper, d0, d1, d2) || entity.func_184215_y(this.field_72777_q.field_71439_g)) && (entity != p_228426_6_.func_216773_g() || p_228426_6_.func_216770_i() || p_228426_6_.func_216773_g() instanceof LivingEntity && ((LivingEntity)p_228426_6_.func_216773_g()).func_70608_bn()) && (!(entity instanceof ClientPlayerEntity) || p_228426_6_.func_216773_g() == entity || (entity == field_72777_q.field_71439_g && !field_72777_q.field_71439_g.func_175149_v()))) { //FORGE: render local player entity when it is not the renderViewEntity
                //              ++this.field_72749_I;
                //

                TypeInsnNode tin = (TypeInsnNode) ain;
                if (tin.desc.equals("net/minecraft/client/entity/player/ClientPlayerEntity")) {
                    int entityIndex = ((VarInsnNode) tin.getPrevious()).var;
                    LabelNode label_0 = ((JumpInsnNode) tin.getNext()).label;

                    AbstractInsnNode ain0 = ain;
                    while (ain0.getOpcode() != Opcodes.IF_ACMPNE) {
                        ain0 = ain0.getNext();
                    }
                    LabelNode label_1 = ((JumpInsnNode) ain0).label;

                    InsnList il = new InsnList();
                    il.add(new JumpInsnNode(Opcodes.IF_ACMPEQ, label_0));
                    il.add(new VarInsnNode(Opcodes.ALOAD, entityIndex));
                    il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/WorldRenderer", ASMAPI.mapField("field_72777_q"), "Lnet/minecraft/client/Minecraft;"));
                    il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", ASMAPI.mapField("field_71439_g"), "Lnet/minecraft/client/entity/player/ClientPlayerEntity;"));
                    il.add(new JumpInsnNode(Opcodes.IF_ACMPNE, label_1));
                    il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/WorldRenderer", ASMAPI.mapField("field_72777_q"), "Lnet/minecraft/client/Minecraft;"));
                    il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", ASMAPI.mapField("field_71439_g"), "Lnet/minecraft/client/entity/player/ClientPlayerEntity;"));
                    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/entity/player/ClientPlayerEntity", ASMAPI.mapMethod("func_175149_v"), "()Z"));
                    il.add(new JumpInsnNode(Opcodes.IFNE, label_1));
                    updateCameraAndRender.instructions.insertBefore(ain0, il);
                    updateCameraAndRender.instructions.remove(ain0);
                }
            }
        }

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/WorldRenderer.java.patch#L136-L137
        //
        //              ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = iterator.next();
        // -            if (chunkrenderdispatcher$chunkrender.func_188281_o()) {
        // +            if (!net.minecraftforge.common.ForgeConfig.CLIENT.alwaysSetupTerrainOffThread.get() && chunkrenderdispatcher$chunkrender.func_188281_o()) {
        //                 this.field_174995_M.func_228902_a_(chunkrenderdispatcher$chunkrender);
        //

        MethodNode updateChunks = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_174967_a"), "(J)V"));

        for (AbstractInsnNode ain : updateChunks.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("net/minecraft/client/renderer/chunk/ChunkRenderDispatcher$ChunkRender") && min.name.equals(ASMAPI.mapMethod("func_188281_o")) && min.desc.equals("()Z")) {

                    // add -> getstatic ForgeConfig$Client ForgeConfig.CLIENT
                    // add -> getfield ForgeConfigSpec$BooleanValue ForgeConfig$Client.alwaysSetupTerrainOffThread
                    // add -> invokevirtual Object ForgeConfigSpec$BooleanValue.get()
                    // add -> checkcast Boolean
                    // add -> invokevirtual boolean Boolean.booleanValue()
                    // add -> ifne 33
                    // aload 10
                    // invokevirtual boolean ChunkRenderDispatcher$ChunkRender.func_188281_o()
                    // ifne 31
                    // iload 12
                    // ifeq 33
                    AbstractInsnNode ain0 = min;
                    while (ain0.getOpcode() != Opcodes.IFEQ) {
                        ain0 = ain0.getNext();
                    }
                    LabelNode ln = ((JumpInsnNode) ain0).label;

                    InsnList il = new InsnList();
                    il.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/ForgeConfig", "CLIENT", "Lnet/minecraftforge/common/ForgeConfig$Client;"));
                    il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/common/ForgeConfig$Client", "alwaysSetupTerrainOffThread", "Lnet/minecraftforge/common/ForgeConfigSpec$BooleanValue;"));
                    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/common/ForgeConfigSpec$BooleanValue", "get", "()Ljava/lang/Object;", false));
                    il.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Boolean"));
                    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
                    il.add(new JumpInsnNode(Opcodes.IFNE, ln));

                    updateChunks.instructions.insertBefore(min.getPrevious(), il);
                    break;
                }
            }
        }

        return input;
    }
}
