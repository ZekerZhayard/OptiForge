package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer.texture;

import java.util.HashMap;
import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class AtlasTextureTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.texture.AtlasTexture";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/texture/AtlasTexture.java.patch#L23-L25
        //
        //        int j1 = MathHelper.func_151239_c(i1);
        // -      int k1;
        // +      int k1 = p_229220_4_;
        // +      if (false) // FORGE: do not lower the mipmap level
        //        if (j1 < p_229220_4_) {
        //

        MethodNode stitch = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_229220_a_"), "(Lnet/minecraft/resources/IResourceManager;Ljava/util/stream/Stream;Lnet/minecraft/profiler/IProfiler;I)Lnet/minecraft/client/renderer/texture/AtlasTexture$SheetData;"));

        int warnCount = 0;
        for (AbstractInsnNode ain : stitch.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.GETSTATIC) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.owner.equals("net/minecraft/client/renderer/texture/AtlasTexture") && fin.name.equals(ASMAPI.mapField("field_147635_d")) && fin.desc.equals("Lorg/apache/logging/log4j/Logger;")) {
                    warnCount++;
                    if (warnCount == 2) {

                        // label 57
                        // line 343
                        // f_new 15 0
                        // remove -> iload 14
                        // remove -> iload 4
                        // remove -> if_icmpge 61
                        // remove -> label 58
                        // remove -> line 345
                        // remove -> getstatic Logger AtlasTexture.field_147635_d
                        // remove -> ldc String "{}: dropping miplevel from {} to {}, because of minimum power of two: {}"
                        // remove -> aload 0
                        // remove -> getfield ResourceLocation AtlasTexture.field_229214_j_
                        // remove -> iload 4
                        // remove -> invokestatic Integer Integer.valueOf(int)
                        // remove -> iload 14
                        // remove -> invokestatic Integer Integer.valueOf(int)
                        // remove -> iload 13
                        // remove -> invokestatic Integer Integer.valueOf(int)
                        // remove -> invokeinterface void Logger.warn(String, Object, Object, Object, Object)
                        // remove -> label 59
                        // remove -> line 346
                        // remove -> iload 14
                        // remove -> istore 15
                        // remove -> label 60
                        // remove -> goto 62
                        // remove -> label 61
                        // remove -> line 350
                        // remove -> f_new 15 0
                        // iload 4
                        // istore 15
                        AbstractInsnNode ain0 = fin;
                        while (!(ain0 instanceof FrameNode)) {
                            ain0 = ain0.getPrevious();
                        }
                        while (!(ain0.getNext() instanceof FrameNode)) {
                            stitch.instructions.remove(ain0.getNext());
                        }
                        stitch.instructions.remove(ain0.getNext());
                    }
                }
            }
        }

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/renderer/texture/AtlasTexture.java.patch#L33-L34
        //
        //        try (IResource iresource = p_229218_1_.func_199002_a(resourcelocation)) {
        //           NativeImage nativeimage = NativeImage.func_195713_a(iresource.func_199027_b());
        // +         TextureAtlasSprite customSprite = net.minecraftforge.client.ForgeHooksClient.loadTextureAtlasSprite(this, p_229218_1_, p_229218_2_, iresource, p_229218_3_, p_229218_4_, p_229218_6_, p_229218_7_, p_229218_5_, nativeimage);
        // +         if (customSprite != null) return customSprite;
        //           return new TextureAtlasSprite(this, p_229218_2_, p_229218_5_, p_229218_3_, p_229218_4_, p_229218_6_, p_229218_7_, nativeimage);
        //        } catch (RuntimeException runtimeexception) {
        //           field_147635_d.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
        //

        MethodNode loadSprite = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_229218_a_"), "(Lnet/minecraft/resources/IResourceManager;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;IIIII)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"));

        LabelNode label_0 = new LabelNode();
        LabelNode label_1 = new LabelNode();
        LabelNode label_2 = new LabelNode();
        LabelNode label_3 = new LabelNode();
        LabelNode label_4 = new LabelNode();
        LabelNode label_5 = new LabelNode();
        LabelNode label_6 = new LabelNode();
        LabelNode label_7 = new LabelNode();
        LocalVariableNode iresource = Objects.requireNonNull(ASMUtils.findLocalVariable(loadSprite, "Lnet/minecraft/resources/IResource;", 0));
        LocalVariableNode textureatlassprite = Objects.requireNonNull(ASMUtils.findLocalVariable(loadSprite, "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", 0));
        LocalVariableNode runtimeexception = Objects.requireNonNull(ASMUtils.findLocalVariable(loadSprite, "Ljava/lang/RuntimeException;", 0));
        LocalVariableNode customSprite = new LocalVariableNode("customSprite", "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", null, label_0, textureatlassprite.end, textureatlassprite.index);
        ASMUtils.insertLocalVariable(loadSprite, customSprite, loadSprite.localVariables.size());

        HashMap<Integer, TryCatchBlockNode> tryCatchBlocks = new HashMap<>();
        tryCatchBlocks.put(0, new TryCatchBlockNode(label_2, label_3, label_4, "java/lang/Throwable"));
        TryCatchBlockNode tcbn0 = null;
        for (int i = 0, len = loadSprite.tryCatchBlocks.size(); i < len; i++) {
            TryCatchBlockNode tcbn = loadSprite.tryCatchBlocks.get(i);
            if (tcbn.handler.equals(textureatlassprite.end) && tcbn.type != null && tcbn.type.equals("java/lang/Throwable")) {
                tryCatchBlocks.put(i, new TryCatchBlockNode(tcbn.start, label_1, tcbn.handler, tcbn.type));
                tcbn.start = label_7;
                tcbn0 = tcbn;
            } else if (tcbn0 != null && tcbn.end.equals(tcbn0.end) && tcbn.type == null) {
                tryCatchBlocks.put(i, new TryCatchBlockNode(tcbn.start, label_1, tcbn.handler, null));
                tcbn.start = label_7;
            } else if (!tcbn.start.equals(textureatlassprite.end) && tcbn.handler.equals(iresource.end) && tcbn.type != null && tcbn.type.equals("java/lang/RuntimeException")) {
                tryCatchBlocks.put(i, new TryCatchBlockNode(tcbn.start, label_6, tcbn.handler, tcbn.type));
                tcbn.start = label_7;
            } else if (!tcbn.start.equals(textureatlassprite.end) && tcbn.handler.equals(runtimeexception.end) && tcbn.type != null && tcbn.type.equals("java/io/IOException")) {
                tryCatchBlocks.put(i, new TryCatchBlockNode(tcbn.start, label_6, tcbn.handler, tcbn.type));
                tcbn.start = label_7;
            }
        }
        for (int i = loadSprite.tryCatchBlocks.size(); i >= 0; i--) {
            TryCatchBlockNode tcbn = tryCatchBlocks.get(i);
            if (tcbn != null) {
                loadSprite.tryCatchBlocks.add(i, tcbn);
            }
        }

        for (AbstractInsnNode ain : loadSprite.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.NEW) {
                TypeInsnNode tin = (TypeInsnNode) ain;
                if (tin.desc.equals("net/minecraft/client/renderer/texture/TextureAtlasSprite")) {
                    InsnList il = new InsnList();
                    il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    il.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    il.add(new VarInsnNode(Opcodes.ALOAD, 2));
                    il.add(new VarInsnNode(Opcodes.ALOAD, iresource.index));
                    il.add(new VarInsnNode(Opcodes.ILOAD, 3));
                    il.add(new VarInsnNode(Opcodes.ILOAD, 4));
                    il.add(new VarInsnNode(Opcodes.ILOAD, 6));
                    il.add(new VarInsnNode(Opcodes.ILOAD, 7));
                    il.add(new VarInsnNode(Opcodes.ILOAD, 5));
                    il.add(new VarInsnNode(Opcodes.ALOAD, ASMUtils.findLocalVariableIndex(loadSprite, "Lnet/minecraft/client/renderer/texture/NativeImage;", 0)));
                    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/client/ForgeHooksClient", "loadTextureAtlasSprite", "(Lnet/minecraft/client/renderer/texture/AtlasTexture;Lnet/minecraft/resources/IResourceManager;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;Lnet/minecraft/resources/IResource;IIIIILnet/minecraft/client/renderer/texture/NativeImage;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", false));
                    il.add(new VarInsnNode(Opcodes.ASTORE, customSprite.index));

                    il.add(label_0);
                    il.add(new VarInsnNode(Opcodes.ALOAD, customSprite.index));
                    il.add(new JumpInsnNode(Opcodes.IFNULL, label_7));
                    il.add(new VarInsnNode(Opcodes.ALOAD, customSprite.index));
                    il.add(new VarInsnNode(Opcodes.ASTORE, customSprite.index + 1));

                    il.add(label_1);
                    il.add(new VarInsnNode(Opcodes.ALOAD, iresource.index));
                    il.add(new JumpInsnNode(Opcodes.IFNULL, label_6));
                    il.add(new VarInsnNode(Opcodes.ALOAD, iresource.index + 1));
                    il.add(new JumpInsnNode(Opcodes.IFNULL, label_5));

                    il.add(label_2);
                    il.add(new VarInsnNode(Opcodes.ALOAD, iresource.index));
                    il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraft/resources/IResource", "close", "()V", true));

                    il.add(label_3);
                    il.add(new JumpInsnNode(Opcodes.GOTO, label_6));

                    il.add(label_4);
                    il.add(new VarInsnNode(Opcodes.ASTORE, customSprite.index + 2));
                    il.add(new VarInsnNode(Opcodes.ALOAD, iresource.index + 1));
                    il.add(new VarInsnNode(Opcodes.ALOAD, customSprite.index + 2));
                    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Throwable", "addSuppressed", "(Ljava/lang/Throwable;)V", false));
                    il.add(new JumpInsnNode(Opcodes.GOTO, label_6));

                    il.add(label_5);
                    il.add(new VarInsnNode(Opcodes.ALOAD, iresource.index));
                    il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraft/resources/IResource", "close", "()V", true));

                    il.add(label_6);
                    il.add(new VarInsnNode(Opcodes.ALOAD, customSprite.index + 1));
                    il.add(new InsnNode(Opcodes.ARETURN));
                    il.add(label_7);

                    loadSprite.instructions.insertBefore(tin, il);
                }
            }
        }

        return input;
    }
}
