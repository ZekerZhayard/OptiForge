package io.github.zekerzhayard.optiforge.asm.utils.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedirectSurrogate {
    int[] loacalVariableOrdinals();

    class AnnotationAction implements IAnnotationAction {
        @Override
        public Class<? extends Annotation> registerAnnotation() {
            return RedirectSurrogate.class;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void afterPostTransforming(ClassNode targetClass, MethodNode targetMethod, Map<String, ?> values, String mixinClassName) {
            MethodNode mixinMethod = null;
            for (MethodNode mn : targetClass.methods) {
                if (ASMUtils.isMixinMethod(mn, mixinClassName)) {
                    String[] names = mn.name.split("\\$", 3);
                    if (names.length == 3 && targetMethod.name.equals(names[2])) {
                        mixinMethod = mn;
                        break;
                    }
                }
            }
            Objects.requireNonNull(mixinMethod, "You must specify a @RedirectSurrogate method to overload another mixin method!");
            for (MethodNode mn : targetClass.methods) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain instanceof MethodInsnNode) {
                        MethodInsnNode min = (MethodInsnNode) ain;
                        if (min.owner.equals(targetClass.name) && min.name.equals(mixinMethod.name) && min.desc.equals(mixinMethod.desc)) {
                            List<Integer> ordinals = (List<Integer>) values.get("loacalVariableOrdinals");
                            if (ordinals != null) {
                                Type[] targetMethodParams = Type.getArgumentTypes(targetMethod.desc);
                                for (int len = Type.getArgumentTypes(mixinMethod.desc).length, i = len; i < targetMethodParams.length; i++) {
                                    mn.instructions.insertBefore(min, new VarInsnNode(targetMethodParams[i].getOpcode(Opcodes.ILOAD), ASMUtils.findLocalVariableIndex(mn, targetMethodParams[i].getDescriptor(), ordinals.get(i - len))));
                                }
                                min.desc = targetMethod.desc;
                            }
                        }
                    }
                }
            }
            targetMethod.name = mixinMethod.name;
            targetClass.methods.remove(mixinMethod);
        }
    }
}
