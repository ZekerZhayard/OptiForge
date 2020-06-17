package io.github.zekerzhayard.optiforge.asm.utils.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.util.Annotations;

/**
 * Mark owners of static methods which exist at runtime but not exist at compile time.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MarkStatic {
    Marker[] markers();

    @interface Marker {
        Class<?> before();

        Class<?> after();

        /**
         * @return True represents the target method is in an interface.
         */
        boolean isInterface() default false;
    }

    class AnnotationAction implements IAnnotationAction {
        @Override
        public Class<? extends Annotation> registerAnnotation() {
            return MarkStatic.class;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void afterPostTransforming(ClassNode targetClass, MethodNode targetMethod, Map<String, ?> values, String mixinClassName) {
            Map<String, String> map = new HashMap<>();
            Map<String, Boolean> isInterfaceMap = new HashMap<>();
            for (AnnotationNode an : (List<AnnotationNode>) values.get("markers")) {
                String before = ((Type) Annotations.getValue(an, "before")).getInternalName();
                map.put(before, ((Type) Annotations.getValue(an, "after")).getInternalName());
                isInterfaceMap.put(before, Boolean.TRUE.equals(Annotations.getValue(an, "isInterface")));
            }

            for (AbstractInsnNode ain : targetMethod.instructions.toArray()) {
                if (ain.getOpcode() == Opcodes.INVOKESTATIC) {
                    MethodInsnNode min = (MethodInsnNode) ain;
                    if (map.containsKey(min.owner)) {
                        min.itf = isInterfaceMap.get(min.owner);
                        min.owner = map.get(min.owner);
                    }
                }
            }
        }
    }
}
