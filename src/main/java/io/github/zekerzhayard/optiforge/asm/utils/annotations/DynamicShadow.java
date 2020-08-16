package io.github.zekerzhayard.optiforge.asm.utils.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Shadow the method which only exists after transforming at runtime.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicShadow {
    class AnnotationAction implements IAnnotationAction {
        @Override
        public Class<? extends Annotation> registerAnnotation() {
            return DynamicShadow.class;
        }

        @Override
        public void beforePostTransforming(ClassNode targetClass, MethodNode targetMethod, Map<String, ?> values, String mixinClassName) {
            targetClass.methods.remove(targetMethod);
        }
    }
}
