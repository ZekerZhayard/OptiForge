package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.gui.screen;

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
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class MainMenuScreenTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.gui.screen.MainMenuScreen";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/client/gui/screen/MainMenuScreen.java.patch#L41
        //
        //
        //           this.field_230706_i_.func_110434_K().func_110577_a(field_194400_H);
        //           func_238463_a_(p_230430_1_, j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
        // +         net.minecraftforge.client.ForgeHooksClient.renderMainMenu(this, p_230430_1_, this.field_230712_o_, this.field_230708_k_, this.field_230709_l_, l);
        //           if (this.field_73975_c != null) {
        //              RenderSystem.pushMatrix();
        //              RenderSystem.translatef((float)(this.field_230708_k_ / 2 + 90), 70.0F, 0.0F);
        //

        MethodNode render = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_230430_a_"), "(Lcom/mojang/blaze3d/matrix/MatrixStack;IIF)V"));

        for (AbstractInsnNode ain : render.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.GETSTATIC) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.getNext().getOpcode() == Opcodes.ICONST_5 && fin.owner.equals("net/optifine/reflect/Reflector") && fin.name.equals("ForgeHooksClient_renderMainMenu") && fin.desc.equals("Lnet/optifine/reflect/ReflectorMethod;")) {
                    render.instructions.set(fin.getNext(), new IntInsnNode(Opcodes.BIPUSH, 6));
                }
            } else if (ain.getOpcode() == Opcodes.INVOKESTATIC) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("net/optifine/reflect/Reflector") && min.name.equals("callVoid") && min.desc.equals("(Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)V")) {
                    InsnList il = new InsnList();
                    il.add(new InsnNode(Opcodes.DUP));
                    il.add(new InsnNode(Opcodes.ICONST_5));
                    il.add(new VarInsnNode(Opcodes.ILOAD, ASMUtils.findLocalVariableIndex(render, "I", 5)));
                    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));
                    il.add(new InsnNode(Opcodes.AASTORE));
                    render.instructions.insertBefore(min, il);
                }
            }
        }

        return input;
    }
}
