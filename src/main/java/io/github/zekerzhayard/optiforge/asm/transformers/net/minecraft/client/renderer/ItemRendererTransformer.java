package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer;

import java.util.Objects;

import io.github.zekerzhayard.optiforge.asm.transformers.ITransformer;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.util.Bytecode;

public class ItemRendererTransformer implements ITransformer {
    @Override
    public boolean isTargetClass(String className) {
        return className.equals("net.minecraft.client.renderer.ItemRenderer");
    }

    @Override
    public ClassNode postTransform(ClassNode cn, String mixinClassName) {
        MethodNode mn = Objects.requireNonNull(Bytecode.findMethod(cn, ASMAPI.mapMethod("func_229111_a_"), "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V"));
        int isTarget = 0;
        LabelNode ln = new LabelNode();
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.INVOKESPECIAL) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.name.contains("inject$renderItem$0")) {
                    for (MethodNode method : cn.methods) {
                        if (min.name.equals(method.name) && min.desc.equals(method.desc) && ASMUtils.isMixinMethod(method, mixinClassName)) {
                            mn.instructions.insert(ain, new JumpInsnNode(Opcodes.IFNE, ln));
                            break;
                        }
                    }
                }
            } else if (isTarget == 0 && ain.getOpcode() == Opcodes.GETSTATIC) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.owner.equals("net/optifine/reflect/Reflector") && fin.name.equals("IForgeItem_getItemStackTileEntityRenderer") && fin.desc.equals("Lnet/optifine/reflect/ReflectorMethod;")) {
                    isTarget = 1;
                    AbstractInsnNode ain2 = ain;
                    do {
                        ain2 = ain2.getPrevious();
                    } while (ain2.getOpcode() != Opcodes.GOTO);
                    LabelNode ln2 = ((JumpInsnNode) ain2).label;
                    mn.instructions.insert(ain2, ln);
                    mn.instructions.insert(ln, new JumpInsnNode(Opcodes.GOTO, ln2));
                }
            }
        }
        return cn;
    }
}
