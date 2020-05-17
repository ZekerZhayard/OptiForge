package io.github.zekerzhayard.optiforge.asm.utils.annotations;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public interface IAnnotationAction {
    Class<? extends Annotation> registerAnnotation();

    /**
     *
     * @param targetClass Target class node.
     * @param targetMethod The method with target annotation.
     * @param values The annotation values.
     * @param mixinClassName The mixin class name.
     */
    default void beforePostTransforming(ClassNode targetClass, MethodNode targetMethod, Map<String, ?> values, String mixinClassName) {

    }

    /**
     * See {@link IAnnotationAction#beforePostTransforming}.
     */
    default void afterPostTransforming(ClassNode targetClass, MethodNode targetMethod, Map<String, ?> values, String mixinClassName) {

    }

    interface ActionFunction {
        void action(IAnnotationAction action,ClassNode targetClass, MethodNode targetMethod, Map<String, ?> values, String mixinClassName);
    }
}
