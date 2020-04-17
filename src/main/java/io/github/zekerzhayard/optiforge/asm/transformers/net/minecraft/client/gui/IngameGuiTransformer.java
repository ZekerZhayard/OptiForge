package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.gui;

import io.github.zekerzhayard.optiforge.asm.transformers.ITransformer;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import org.objectweb.asm.tree.ClassNode;

public class IngameGuiTransformer implements ITransformer {
    @Override
    public boolean isTargetClass(String className) {
        return className.equals("net.minecraft.client.gui.IngameGui");
    }

    @Override
    public ClassNode postTransform(ClassNode cn, String mixinClassName) {
        ASMUtils.replaceRedirectSurrogateMethod(cn, mixinClassName);
        return cn;
    }
}
