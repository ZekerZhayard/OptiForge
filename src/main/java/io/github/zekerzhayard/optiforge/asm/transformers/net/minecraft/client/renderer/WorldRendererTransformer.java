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
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/WorldRenderer.java.patch#L7-L11
        //
        //     private void func_228438_a_(LightTexture p_228438_1_, float p_228438_2_, double p_228438_3_, double p_228438_5_, double p_228438_7_) {
        // +      net.minecraftforge.client.IWeatherRenderHandler renderHandler = field_72769_h.func_239132_a_().getWeatherRenderHandler();
        // +      if (renderHandler != null) {
        // +         renderHandler.render(field_72773_u, p_228438_2_, field_72769_h, field_72777_q, p_228438_1_, p_228438_3_, p_228438_5_, p_228438_7_);
        // +         return;
        // +      }
        //        float f = this.field_72777_q.field_71441_e.func_72867_j(p_228438_2_);
        //

        MethodNode renderRainSnow = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_228438_a_"), "(Lnet/minecraft/client/renderer/LightTexture;FDDD)V"));

        AbstractInsnNode[] renderRainSnowAINs = renderRainSnow.instructions.toArray();
        LabelNode renderRainSnow_label_0 = new LabelNode();
        LabelNode renderRainSnow_label_1 = new LabelNode();
        LabelNode renderRainSnow_label_2 = null;
        int renderHandler_renderRainSnow_index = Bytecode.getFirstNonArgLocalIndex(renderRainSnow);

        for (AbstractInsnNode ain : renderRainSnowAINs) {
            if (ain.getPrevious() == null) {
                // First node.
                InsnList il = new InsnList();
                il.add(new LabelNode());
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/WorldRenderer", ASMAPI.mapField("field_72769_h"), "Lnet/minecraft/client/world/ClientWorld;"));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/world/ClientWorld", ASMAPI.mapMethod("func_239132_a_"), "()Lnet/minecraft/client/world/DimensionRenderInfo;", false));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/world/DimensionRenderInfo", "getWeatherRenderHandler", "()Lnet/minecraftforge/client/IWeatherRenderHandler;", false));
                il.add(new VarInsnNode(Opcodes.ASTORE, renderHandler_renderRainSnow_index));

                il.add(renderRainSnow_label_0);
                il.add(new VarInsnNode(Opcodes.ALOAD, renderHandler_renderRainSnow_index));
                il.add(new JumpInsnNode(Opcodes.IFNULL, renderRainSnow_label_1));

                il.add(new LabelNode());
                il.add(new VarInsnNode(Opcodes.ALOAD, renderHandler_renderRainSnow_index));
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/WorldRenderer", ASMAPI.mapField("field_72773_u"), "I"));
                il.add(new VarInsnNode(Opcodes.FLOAD, 2));
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/WorldRenderer", ASMAPI.mapField("field_72769_h"), "Lnet/minecraft/client/world/ClientWorld;"));
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/WorldRenderer", ASMAPI.mapField("field_72777_q"), "Lnet/minecraft/client/Minecraft;"));
                il.add(new VarInsnNode(Opcodes.ALOAD, 1));
                il.add(new VarInsnNode(Opcodes.DLOAD, 3));
                il.add(new VarInsnNode(Opcodes.DLOAD, 5));
                il.add(new VarInsnNode(Opcodes.DLOAD, 7));
                il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/client/IWeatherRenderHandler", "render", "(IFLnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/renderer/LightTexture;DDD)V", true));

                il.add(new LabelNode());
                il.add(new InsnNode(Opcodes.RETURN));
                il.add(renderRainSnow_label_1);

                renderRainSnow.instructions.insertBefore(ain, il);
            } else if (ain instanceof LabelNode) {
                renderRainSnow_label_2 = (LabelNode) ain;
            }
        }

        ASMUtils.insertLocalVariable(renderRainSnow.localVariables, renderRainSnowAINs, new LocalVariableNode("renderHandler", "Lnet/minecraftforge/client/IWeatherRenderHandler;", null, renderRainSnow_label_0, Objects.requireNonNull(renderRainSnow_label_2), renderHandler_renderRainSnow_index));

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
            }
        }

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/WorldRenderer.java.patch#L103-L107
        //
        //     public void func_228424_a_(MatrixStack p_228424_1_, float p_228424_2_) {
        // +      net.minecraftforge.client.ISkyRenderHandler renderHandler = field_72769_h.func_239132_a_().getSkyRenderHandler();
        // +      if (renderHandler != null) {
        // +         renderHandler.render(field_72773_u, p_228424_2_, p_228424_1_, field_72769_h, field_72777_q);
        // +         return;
        // +      }
        //        if (this.field_72777_q.field_71441_e.func_239132_a_().func_241683_c_() == DimensionRenderInfo.FogType.END) {
        //

        MethodNode renderSky = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_228424_a_"), "(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V"));

        AbstractInsnNode[] renderSkyAINs = renderSky.instructions.toArray();
        LabelNode renderSky_label_0 = new LabelNode();
        LabelNode renderSky_label_1 = new LabelNode();
        LabelNode renderSky_label_2 = null;
        int renderHandler_renderSky_index = Bytecode.getFirstNonArgLocalIndex(renderSky);

        for (AbstractInsnNode ain : renderSkyAINs) {
            if (ain.getPrevious() == null) {
                // First node.
                InsnList il = new InsnList();
                il.add(new LabelNode());
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/WorldRenderer", ASMAPI.mapField("field_72769_h"), "Lnet/minecraft/client/world/ClientWorld;"));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/world/ClientWorld", ASMAPI.mapMethod("func_239132_a_"), "()Lnet/minecraft/client/world/DimensionRenderInfo;", false));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/world/DimensionRenderInfo", "getSkyRenderHandler", "()Lnet/minecraftforge/client/ISkyRenderHandler;", false));
                il.add(new VarInsnNode(Opcodes.ASTORE, renderHandler_renderSky_index));

                il.add(renderSky_label_0);
                il.add(new VarInsnNode(Opcodes.ALOAD, renderHandler_renderSky_index));
                il.add(new JumpInsnNode(Opcodes.IFNULL, renderSky_label_1));

                il.add(new LabelNode());
                il.add(new VarInsnNode(Opcodes.ALOAD, renderHandler_renderSky_index));
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/WorldRenderer", ASMAPI.mapField("field_72773_u"), "I"));
                il.add(new VarInsnNode(Opcodes.FLOAD, 2));
                il.add(new VarInsnNode(Opcodes.ALOAD, 1));
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/WorldRenderer", ASMAPI.mapField("field_72769_h"), "Lnet/minecraft/client/world/ClientWorld;"));
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/WorldRenderer", ASMAPI.mapField("field_72777_q"), "Lnet/minecraft/client/Minecraft;"));
                il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/client/ISkyRenderHandler", "render", "(IFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/Minecraft;)V", true));

                il.add(new LabelNode());
                il.add(new InsnNode(Opcodes.RETURN));
                il.add(renderSky_label_1);

                renderSky.instructions.insertBefore(ain, il);
            } else if (ain instanceof LabelNode) {
                renderSky_label_2 = (LabelNode) ain;
            }
        }

        ASMUtils.insertLocalVariable(renderSky.localVariables, renderSkyAINs, new LocalVariableNode("renderHandler", "Lnet/minecraftforge/client/ISkyRenderHandler;", null, renderSky_label_0, Objects.requireNonNull(renderSky_label_2), renderHandler_renderSky_index));

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/WorldRenderer.java.patch#L115-L119
        //
        //     public void func_228425_a_(MatrixStack p_228425_1_, float p_228425_2_, double p_228425_3_, double p_228425_5_, double p_228425_7_) {
        // +      net.minecraftforge.client.ICloudRenderHandler renderHandler = field_72769_h.func_239132_a_().getCloudRenderHandler();
        // +      if (renderHandler != null) {
        // +         renderHandler.render(field_72773_u, p_228425_2_, p_228425_1_, field_72769_h, field_72777_q, p_228425_3_, p_228425_5_, p_228425_7_);
        // +         return;
        // +      }
        //        float f = this.field_72769_h.func_239132_a_().func_239213_a_();
        //

        MethodNode renderClouds = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_228425_a_"), "(Lcom/mojang/blaze3d/matrix/MatrixStack;FDDD)V"));

        AbstractInsnNode[] renderCloudsAINs = renderClouds.instructions.toArray();
        LabelNode renderClouds_label_0 = new LabelNode();
        LabelNode renderClouds_label_1 = new LabelNode();
        LabelNode renderClouds_label_2 = new LabelNode();
        int renderHandler_renderClouds_index = Bytecode.getFirstNonArgLocalIndex(renderClouds);

        for (AbstractInsnNode ain : renderCloudsAINs) {
            if (ain.getPrevious() == null) {
                // First node.
                InsnList il = new InsnList();
                il.add(new LabelNode());
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/WorldRenderer", ASMAPI.mapField("field_72769_h"), "Lnet/minecraft/client/world/ClientWorld;"));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/world/ClientWorld", ASMAPI.mapMethod("func_239132_a_"), "()Lnet/minecraft/client/world/DimensionRenderInfo;", false));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/world/DimensionRenderInfo", "getCloudRenderHandler", "()Lnet/minecraftforge/client/ICloudRenderHandler;", false));
                il.add(new VarInsnNode(Opcodes.ASTORE, renderHandler_renderClouds_index));

                il.add(renderClouds_label_0);
                il.add(new VarInsnNode(Opcodes.ALOAD, renderHandler_renderClouds_index));
                il.add(new JumpInsnNode(Opcodes.IFNULL, renderClouds_label_1));

                il.add(new LabelNode());
                il.add(new VarInsnNode(Opcodes.ALOAD, renderHandler_renderClouds_index));
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/WorldRenderer", ASMAPI.mapField("field_72773_u"), "I"));
                il.add(new VarInsnNode(Opcodes.FLOAD, 2));
                il.add(new VarInsnNode(Opcodes.ALOAD, 1));
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/WorldRenderer", ASMAPI.mapField("field_72769_h"), "Lnet/minecraft/client/world/ClientWorld;"));
                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/WorldRenderer", ASMAPI.mapField("field_72777_q"), "Lnet/minecraft/client/Minecraft;"));
                il.add(new VarInsnNode(Opcodes.DLOAD, 3));
                il.add(new VarInsnNode(Opcodes.DLOAD, 5));
                il.add(new VarInsnNode(Opcodes.DLOAD, 7));
                il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/client/ICloudRenderHandler", "render", "(IFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/Minecraft;DDD)V", true));

                il.add(new LabelNode());
                il.add(new InsnNode(Opcodes.RETURN));
                il.add(renderClouds_label_1);

                renderClouds.instructions.insertBefore(ain, il);
            } else if (ain instanceof LabelNode) {
                renderClouds_label_2 = (LabelNode) ain;
            }
        }

        ASMUtils.insertLocalVariable(renderClouds.localVariables, renderCloudsAINs, new LocalVariableNode("renderHandler", "Lnet/minecraftforge/client/ICloudRenderHandler;", null, renderClouds_label_0, Objects.requireNonNull(renderClouds_label_2), renderHandler_renderClouds_index));

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/WorldRenderer.java.patch#L127-L128
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
