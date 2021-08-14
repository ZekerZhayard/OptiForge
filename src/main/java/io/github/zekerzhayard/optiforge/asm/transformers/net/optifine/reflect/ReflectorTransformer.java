package io.github.zekerzhayard.optiforge.asm.transformers.net.optifine.reflect;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ReflectorTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.optifine.reflect.Reflector";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        MethodNode _clinit_ = Objects.requireNonNull(ASMUtils.findMethod(input, "<clinit>", "()V"));

        for (AbstractInsnNode ain : _clinit_.instructions.toArray()) {
            if (ain instanceof LdcInsnNode lin && lin.cst instanceof String cstStr) {
                lin.cst = switch (cstStr) {
                    case "net.minecraftforge.fml.BrandingControl" -> "net.minecraftforge.fmllegacy.BrandingControl";
                    case "net.minecraftforge.fml.client.ClientModLoader" -> "net.minecraftforge.fmlclient.ClientModLoader";
                    case "net.minecraftforge.fml.client.ClientHooks" -> "net.minecraftforge.fmlclient.ClientHooks";
                    case "net.minecraftforge.fml.CrashReportExtender" -> "net.minecraftforge.fmllegacy.CrashReportExtender";
                    case "net.minecraftforge.fml.ForgeI18n" -> "net.minecraftforge.fmllegacy.ForgeI18n";
                    case "net.minecraftforge.common.extensions.IForgeTileEntity" -> "net.minecraftforge.common.extensions.IForgeBlockEntity";
                    case "net.minecraftforge.fml.client.gui.screen.ModListScreen" -> "net.minecraftforge.fmlclient.gui.screen.ModListScreen";
                    case "net.minecraftforge.fml.server.ServerLifecycleHooks" -> "net.minecraftforge.fmllegacy.server.ServerLifecycleHooks";
                    case "getLightValue" -> "getLightEmission";
                    case "forgeLightPipelineEnabled" -> "experimentalForgeLightPipelineEnabled";
                    default -> cstStr;
                };
            }
        }

        return input;
    }
}
