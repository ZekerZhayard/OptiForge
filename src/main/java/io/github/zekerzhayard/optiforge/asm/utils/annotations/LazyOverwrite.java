package io.github.zekerzhayard.optiforge.asm.utils.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import net.minecraftforge.coremod.api.ASMAPI;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * For legacy Mixin, {@link Overwrite} will overwrite the method before {@link IMixinConfigPlugin#preApply(String, ClassNode, String, IMixinInfo)} called.
 * Use this annotation can apply overwrite after it.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LazyOverwrite {
    String prefix();

    /**
     * if true, it will remap member from srg name to mcp name at development environment.
     */
    boolean remap() default true;

    class AnnotationAction implements IAnnotationAction {
        @Override
        public Class<? extends Annotation> registerAnnotation() {
            return LazyOverwrite.class;
        }

        @Override
        public void afterPostTransforming(ClassNode targetClass, MethodNode targetMethod, Map<String, ?> values, String mixinClassName) {
            Object prefix = values.get("prefix");
            if (prefix instanceof String) {
                targetMethod.name = StringUtils.removeStart(targetMethod.name, (String) prefix);
            }
            Object remap = values.get("remap");
            if (!Boolean.FALSE.equals(remap)) {
                targetMethod.name = ASMAPI.mapMethod(targetMethod.name);
            }
        }
    }
}
