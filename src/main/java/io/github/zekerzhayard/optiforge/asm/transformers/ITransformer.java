package io.github.zekerzhayard.optiforge.asm.transformers;

import org.objectweb.asm.tree.ClassNode;

public interface ITransformer {
    boolean isTargetClass(String className);

    default ClassNode preTransform(ClassNode cn) {
        return cn;
    }

    default ClassNode postTransform(ClassNode cn, String mixinClassName) {
        return cn;
    }
}
