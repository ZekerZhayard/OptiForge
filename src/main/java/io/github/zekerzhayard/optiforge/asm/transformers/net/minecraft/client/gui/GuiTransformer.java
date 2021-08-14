package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.gui;

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
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class GuiTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.gui.Gui";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.17.x/patches/minecraft/net/minecraft/client/gui/Gui.java.patch#L7-L10
        //
        //
        //           for(MobEffectInstance mobeffectinstance : Ordering.natural().reverse().sortedCopy(collection)) {
        //              MobEffect mobeffect = mobeffectinstance.m_19544_();
        // +            net.minecraftforge.client.EffectRenderer renderer = net.minecraftforge.client.RenderProperties.getEffectRenderer(mobeffectinstance);
        // +            if (!renderer.shouldRenderHUD(mobeffectinstance)) continue;
        // +            // Rebind in case previous renderHUDEffect changed texture
        // +            RenderSystem.m_157456_(0, AbstractContainerScreen.f_97725_);
        //              if (mobeffectinstance.m_19575_()) {
        //                 int k = this.f_92977_;
        //                 int l = 1;
        //

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.17.x/patches/minecraft/net/minecraft/client/gui/Gui.java.patch#L18
        //
        //                    RenderSystem.m_157429_(1.0F, 1.0F, 1.0F, f1);
        //                    m_93200_(p_93029_, j1 + 3, k1 + 3, this.m_93252_(), 18, 18, textureatlassprite);
        //                 });
        // +               renderer.renderHUDEffect(mobeffectinstance,this, p_93029_, k, l, this.m_93252_(), f);
        //              }
        //           }
        //
        //

        MethodNode renderEffects = Objects.requireNonNull(ASMUtils.findMethod(input, ASMAPI.mapMethod("m_93028_"), "(Lcom/mojang/blaze3d/vertex/PoseStack;)V"));

        LabelNode label_0 = new LabelNode();
        LocalVariableNode mobeffectinstance = Objects.requireNonNull(ASMUtils.findLocalVariable(renderEffects, "Lnet/minecraft/world/effect/MobEffectInstance;", 0));
        LocalVariableNode mobeffect = Objects.requireNonNull(ASMUtils.findLocalVariable(renderEffects, "Lnet/minecraft/world/effect/MobEffect;", 0));
        LocalVariableNode render = new LocalVariableNode("render", "Lnet/minecraftforge/client/EffectRenderer;", null, label_0, mobeffect.end, mobeffect.index + 1);
        ASMUtils.insertLocalVariable(renderEffects, render, renderEffects.localVariables.indexOf(mobeffect));

        int count_renderEffects = 0;
        for (AbstractInsnNode ain : renderEffects.instructions.toArray()) {
            if (ain instanceof FieldInsnNode fin && fin.getOpcode() == Opcodes.GETSTATIC && Objects.equals(fin.owner, "net/optifine/reflect/Reflector") && Objects.equals(fin.desc, "Lnet/optifine/reflect/ReflectorMethod;")) {
                if (count_renderEffects == 0 && Objects.equals(fin.name, "IForgeEffectInstance_shouldRenderHUD")) {
                    InsnList il = new InsnList();

                    il.add(new InsnNode(Opcodes.ACONST_NULL));
                    il.add(new VarInsnNode(Opcodes.ASTORE, render.index));
                    il.add(label_0);
                    il.add(new InsnNode(Opcodes.ICONST_1));

                    renderEffects.instructions.insertBefore(fin, il);
                    renderEffects.instructions.remove(fin.getNext());
                    renderEffects.instructions.remove(fin);
                } else if (count_renderEffects == 1 && Objects.equals(fin.name, "IForgeEffectInstance_shouldRenderHUD")) {
                    InsnList il = new InsnList();

                    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/client/RenderProperties", "getEffectRenderer", "(Lnet/minecraft/world/effect/MobEffectInstance;)Lnet/minecraftforge/client/EffectRenderer;", false));
                    il.add(new VarInsnNode(Opcodes.ASTORE, render.index));
                    il.add(new LabelNode());

                    il.add(new VarInsnNode(Opcodes.ALOAD, render.index));
                    il.add(new VarInsnNode(Opcodes.ALOAD, mobeffectinstance.index));
                    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/EffectRenderer", "shouldRenderHUD", "(Lnet/minecraft/world/effect/MobEffectInstance;)Z", false));

                    renderEffects.instructions.insertBefore(fin, il);
                    for (int i = 0; i < 3; i++) {
                        renderEffects.instructions.remove(fin.getNext());
                    }
                    renderEffects.instructions.remove(fin);
                } else if (count_renderEffects == 2 && Objects.equals(fin.name, "IForgeEffectInstance_renderHUDEffect")) {
                    renderEffects.instructions.remove(fin.getNext());
                    renderEffects.instructions.set(fin, new InsnNode(Opcodes.ICONST_1));
                } else if (count_renderEffects == 3 && Objects.equals(fin.name, "IForgeEffectInstance_renderHUDEffect")) {
                    renderEffects.instructions.insertBefore(fin.getPrevious(), new VarInsnNode(Opcodes.ALOAD, render.index));
                    AbstractInsnNode ain0 = fin;
                    do {
                        ain0 = ain0.getNext();
                        AbstractInsnNode ainP = ain0.getPrevious();
                        if (!(ainP instanceof VarInsnNode) && (!(ainP instanceof MethodInsnNode minP) || Objects.equals(minP.name, "valueOf"))) {
                            renderEffects.instructions.remove(ainP);
                        }
                    } while (!(ain0 instanceof MethodInsnNode min0 && min0.getOpcode() == Opcodes.INVOKESTATIC && Objects.equals(min0.owner, "net/optifine/reflect/Reflector") && Objects.equals(min0.name, "call") && Objects.equals(min0.desc, "(Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Ljava/lang/Object;")));
                    renderEffects.instructions.remove(ain0.getNext());
                    renderEffects.instructions.insertBefore(ain0.getPrevious(), new InsnNode(Opcodes.I2F));
                    renderEffects.instructions.set(ain0, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/EffectRenderer", "renderHUDEffect", "(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/client/gui/GuiComponent;Lcom/mojang/blaze3d/vertex/PoseStack;IIFF)V", false));
                }
                count_renderEffects++;
            } else if (ain instanceof MethodInsnNode min) {
                if (min.getOpcode() == Opcodes.INVOKEVIRTUAL && Objects.equals(min.owner, "net/minecraft/client/renderer/texture/TextureManager") && Objects.equals(min.name, ASMAPI.mapMethod("m_174784_")) && Objects.equals(min.desc, "(Lnet/minecraft/resources/ResourceLocation;)V")) {
                    AbstractInsnNode ain0 = ain.getPrevious();
                    for (int i = 0; i < 3; i++) {
                        renderEffects.instructions.remove(ain0.getPrevious());
                    }
                    renderEffects.instructions.insertBefore(ain0, new InsnNode(Opcodes.ICONST_0));
                    renderEffects.instructions.set(min, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mojang/blaze3d/systems/RenderSystem", ASMAPI.mapMethod("m_157456_"), "(ILnet/minecraft/resources/ResourceLocation;)V", false));
                }
            }
        }

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.17.x/patches/minecraft/net/minecraft/client/gui/Gui.java.patch#L36-L43
        //
        //              RenderSystem.m_69478_();
        //              RenderSystem.m_69453_();
        //              m_93172_(p_93070_, j - 2, k - 2, j + i + 2, k + 9 + 2, this.f_92986_.f_91066_.m_92143_(0));
        // -            this.m_93082_().m_92763_(p_93070_, mutablecomponent, (float)j, (float)k, 16777215 + (l << 24));
        // +            Font font = net.minecraftforge.client.RenderProperties.get(f_92994_).getFont(f_92994_);
        // +            if (font == null) {
        // +               this.m_93082_().m_92763_(p_93070_, highlightTip, (float)j, (float)k, 16777215 + (l << 24));
        // +            } else {
        // +               j = (this.f_92977_ - font.m_92852_(highlightTip)) / 2;
        // +               font.m_92763_(p_93070_, highlightTip, (float)j, (float)k, 16777215 + (l << 24));
        // +            }
        //              RenderSystem.m_69461_();
        //           }
        //        }
        //

        MethodNode renderSelectedItemName = Objects.requireNonNull(ASMUtils.findMethod(input, ASMAPI.mapMethod("m_93069_"), "(Lcom/mojang/blaze3d/vertex/PoseStack;)V"));

        int count_renderSelectedItemName = 0;
        for (AbstractInsnNode ain : renderSelectedItemName.instructions.toArray()) {
            if (ain instanceof FieldInsnNode fin && Objects.equals(fin.owner, "net/optifine/reflect/Reflector") && Objects.equals(fin.name, "IForgeItem_getFontRenderer") && Objects.equals(fin.desc, "Lnet/optifine/reflect/ReflectorMethod;")) {
                if (count_renderSelectedItemName == 0) {
                    renderSelectedItemName.instructions.remove(fin.getNext());
                    renderSelectedItemName.instructions.set(fin, new InsnNode(Opcodes.ICONST_1));
                } else if (count_renderSelectedItemName == 1) {
                    renderSelectedItemName.instructions.set(fin.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/client/RenderProperties", "get", "(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraftforge/client/IItemRenderProperties;", false));
                    AbstractInsnNode ain0 = fin;
                    do {
                        ain0 = ain0.getNext();
                        AbstractInsnNode ainP = ain0.getPrevious();
                        if (ainP.getOpcode() != Opcodes.ALOAD && ainP.getOpcode() != Opcodes.GETFIELD) {
                            renderEffects.instructions.remove(ainP);
                        }
                    } while (ain0.getOpcode() != Opcodes.ASTORE);
                    renderSelectedItemName.instructions.insertBefore(ain0, new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/client/IItemRenderProperties", "getFont", "(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/gui/Font;", true));
                }
                count_renderSelectedItemName++;
            }
        }

        return input;
    }
}
