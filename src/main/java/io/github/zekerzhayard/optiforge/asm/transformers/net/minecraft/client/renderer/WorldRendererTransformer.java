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
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/WorldRenderer.java.patch#L7-L8
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

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/WorldRenderer.java.patch#L32-L34
        //
        //        this.func_228441_a_(RenderType.func_228639_c_(), p_228426_1_, d0, d1, d2);
        // +      this.field_72777_q.func_209506_al().func_229356_a_(AtlasTexture.field_110575_b).setBlurMipmap(false, this.field_72777_q.field_71474_y.field_151442_I > 0); // FORGE: fix flickering leaves when mods mess up the blurMipmap settings
        //        this.func_228441_a_(RenderType.func_228641_d_(), p_228426_1_, d0, d1, d2);
        // +      this.field_72777_q.func_209506_al().func_229356_a_(AtlasTexture.field_110575_b).restoreLastBlurMipmap();
        //        this.func_228441_a_(RenderType.func_228643_e_(), p_228426_1_, d0, d1, d2);
        //

        MethodNode updateCameraAndRender = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_228426_a_"), "(Lcom/mojang/blaze3d/matrix/MatrixStack;FJZLnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/util/math/vector/Matrix4f;)V"));

        int getModelManagerCount = 0;
        int objectMouseOverCount = 0;
        int raytraceresultIndex = ASMUtils.findLocalVariableIndex(updateCameraAndRender, "Lnet/minecraft/util/math/RayTraceResult;", 0);
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
                } else if (min.owner.equals("net/minecraft/client/particle/ParticleManager") && min.name.equals(ASMAPI.mapMethod("func_228345_a_")) && min.desc.equals("(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;F)V")) {
                    // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/WorldRenderer.java.patch#L73-L74
                    //
                    //           iprofiler.func_219895_b("particles");
                    // -         this.field_72777_q.field_71452_i.func_228345_a_(p_228426_1_, irendertypebuffer$impl, p_228426_8_, p_228426_6_, p_228426_2_);
                    // +         this.field_72777_q.field_71452_i.renderParticles(p_228426_1_, irendertypebuffer$impl, p_228426_8_, p_228426_6_, p_228426_2_, clippinghelper);
                    //           RenderState.field_239237_T_.func_228549_b_();
                    //
                    // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/WorldRenderer.java.patch#L82-L83
                    //
                    //           iprofiler.func_219895_b("particles");
                    // -         this.field_72777_q.field_71452_i.func_228345_a_(p_228426_1_, irendertypebuffer$impl, p_228426_8_, p_228426_6_, p_228426_2_);
                    // +         this.field_72777_q.field_71452_i.renderParticles(p_228426_1_, irendertypebuffer$impl, p_228426_8_, p_228426_6_, p_228426_2_, clippinghelper);
                    //        }
                    //

                    updateCameraAndRender.instructions.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, ASMUtils.findLocalVariableIndex(updateCameraAndRender, "Lnet/minecraft/client/renderer/culling/ClippingHelper;", 0)));
                    min.name = "renderParticles";
                    min.desc = "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;FLnet/minecraft/client/renderer/culling/ClippingHelper;)V";
                } else if (min.owner.equals("net/minecraft/client/renderer/WorldRenderer") && min.name.equals(ASMAPI.mapMethod("func_228429_a_")) && min.desc.equals("(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V")) {
                    // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/WorldRenderer.java.patch#L64-L65
                    //
                    //              this.func_228429_a_(p_228426_1_, ivertexbuilder2, p_228426_6_.func_216773_g(), d0, d1, d2, blockpos, blockstate);
                    //           }
                    // +      } else if (raytraceresult != null && raytraceresult.func_216346_c() == RayTraceResult.Type.ENTITY) {
                    // +         net.minecraftforge.client.ForgeHooksClient.onDrawBlockHighlight(this, p_228426_6_, raytraceresult, p_228426_2_, p_228426_1_, irendertypebuffer$impl);
                    //        }
                    //
                    //        RenderSystem.pushMatrix();
                    //

                    AbstractInsnNode ain0 = min;
                    while (ain0.getOpcode() != Opcodes.IFEQ) {
                        ain0 = ain0.getNext();
                    }

                    JumpInsnNode jin = (JumpInsnNode) ain0;
                    LabelNode label_0 = new LabelNode();
                    LabelNode label_1 = new LabelNode();
                    LabelNode ln = jin.label;
                    jin.label = label_0;

                    while (!ain0.equals(ln)) {
                        ain0 = ain0.getNext();
                    }

                    InsnList ilPrevious = new InsnList();
                    ilPrevious.add(label_0);
                    ilPrevious.add(new JumpInsnNode(Opcodes.GOTO, label_1));
                    updateCameraAndRender.instructions.insertBefore(ain0, ilPrevious);

                    InsnList ilNext = new InsnList();
                    ilNext.add(new VarInsnNode(Opcodes.ALOAD, raytraceresultIndex));
                    ilNext.add(new JumpInsnNode(Opcodes.IFNULL, label_1));
                    ilNext.add(new VarInsnNode(Opcodes.ALOAD, raytraceresultIndex));
                    ilNext.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/math/RayTraceResult", ASMAPI.mapMethod("func_216346_c"), "()Lnet/minecraft/util/math/RayTraceResult$Type;", false));
                    ilNext.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/util/math/RayTraceResult$Type", ASMAPI.mapField("ENTITY"), "Lnet/minecraft/util/math/RayTraceResult$Type;"));
                    ilNext.add(new JumpInsnNode(Opcodes.IF_ACMPNE, label_1));

                    ilNext.add(new LabelNode());
                    ilNext.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    ilNext.add(new VarInsnNode(Opcodes.ALOAD, 6));
                    ilNext.add(new VarInsnNode(Opcodes.ALOAD, raytraceresultIndex));
                    ilNext.add(new VarInsnNode(Opcodes.FLOAD, 2));
                    ilNext.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    ilNext.add(new VarInsnNode(Opcodes.ALOAD, ASMUtils.findLocalVariableIndex(updateCameraAndRender, "Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;", 0)));
                    ilNext.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/client/ForgeHooksClient", "onDrawBlockHighlight", "(Lnet/minecraft/client/renderer/WorldRenderer;Lnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/util/math/RayTraceResult;FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;)Z", false));
                    ilNext.add(new InsnNode(Opcodes.POP));

                    ilNext.add(label_1);

                    updateCameraAndRender.instructions.insert(ain0, ilNext);
                }
            } else if (ain.getOpcode() == Opcodes.GETFIELD) {
                // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/WorldRenderer.java.patch#L59
                //
                //           BlockState blockstate = this.field_72769_h.func_180495_p(blockpos);
                // -         if (!blockstate.func_196958_f() && this.field_72769_h.func_175723_af().func_177746_a(blockpos)) {
                // +         if (!net.minecraftforge.client.ForgeHooksClient.onDrawBlockHighlight(this, p_228426_6_, raytraceresult, p_228426_2_, p_228426_1_, irendertypebuffer$impl))
                // +         if (!blockstate.isAir(this.field_72769_h, blockpos) && this.field_72769_h.func_175723_af().func_177746_a(blockpos)) {
                //              IVertexBuilder ivertexbuilder2 = irendertypebuffer$impl.getBuffer(RenderType.func_228659_m_());
                //

                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.owner.equals("net/minecraft/client/Minecraft") && fin.name.equals(ASMAPI.mapField("field_71476_x")) && fin.desc.equals("Lnet/minecraft/util/math/RayTraceResult;")) {
                    objectMouseOverCount++;
                    if (objectMouseOverCount == 3) {

                        // iconst_2
                        // modify -> aload 0 -> 43
                        // remove -> getfield Minecraft WorldRenderer.field_72777_q
                        // remove -> getfield RayTraceResult Minecraft.field_71476_x
                        // aastore
                        AbstractInsnNode ain0 = fin;
                        while (!(ain0.getOpcode() == Opcodes.ALOAD)) {
                            ain0 = ain0.getPrevious();
                            updateCameraAndRender.instructions.remove(ain0.getNext());
                        }
                        ((VarInsnNode) ain0).var = raytraceresultIndex;
                    }
                }
            }
        }

        return input;
    }
}
