package io.github.zekerzhayard.optiforge.asm;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import cpw.mods.modlauncher.Launcher;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformer;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import io.github.zekerzhayard.optiforge.asm.utils.annotations.IAnnotationAction;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.util.Annotations;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    private final static Logger LOGGER = LogManager.getLogger("OptiForge");

    private boolean checked = false;
    private List<ITransformer> transformers = new ArrayList<>();
    private HashMap<String, IAnnotationAction> annotationActions = new HashMap<>();

    @Override
    public void onLoad(String mixinPackage) {
        try {
            // see https://github.com/cpw/modlauncher/issues/39
            // assert this.getClass().getProtectionDomain().getCodeSource().getLocation() == null;
            // The SPI system is broken by this issue, so we must find a hacky way.
            URLClassLoader ucl = (URLClassLoader) FieldUtils.readDeclaredField(this.getClass().getClassLoader(), "delegatedClassLoader", true);
            FMLLoader.getLoadingModList().getModFiles().stream().filter(mfi -> mfi.getMods().stream().anyMatch(mi -> mi.getModId().equals("optiforge"))).map(mfi -> {
                try {
                    return mfi.getFile().getFilePath().toUri().toURL();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).forEach(url -> {
                try {
                    MethodUtils.invokeMethod(ucl, true, "addURL", url);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            ServiceLoader.load(ITransformer.class, ucl).forEach(t -> {
                this.transformers.add(t);
                LOGGER.info(" - Add a transformer: {}", t.getClass().getName());
            });
            LOGGER.info("Add {} transformer(s) totally.", this.transformers.size());

            ServiceLoader.load(IAnnotationAction.class, ucl).forEach(a -> {
                this.annotationActions.put(Type.getDescriptor(a.registerAnnotation()), a);
                LOGGER.info(" - Add an annotation action: {}", a.getClass().getName());
            });
            LOGGER.info("Add {} annotation action(s) totally.", this.annotationActions.size());

            this.checked = true;
        } catch (Exception e) {
            LOGGER.error("An unexpected issue occurred when loading transformers and all mixin classes will not apply: ", e);
            this.checked = false;
        }
    }

    @Override
    public String getRefMapperConfig() {
        // If we are under development environment, we needn't reference mappings.
        return Launcher.INSTANCE.environment().findNameMapping("srg").isPresent() ? null : "mixins.optiforge.refmap.json";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return this.checked;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (this.checked) {
            this.transformers.stream().filter(t -> t.isTargetClass(targetClassName)).peek(t -> LOGGER.info("[PRE] Found a transformer \"{}\" for class \"{}\"", t.getClass().getName(), targetClassName)).forEach(t -> t.preTransform(targetClass));
        }
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (this.checked) {
            this.applyAnnotationAction(targetClass, mixinClassName, IAnnotationAction::beforePostTransforming);
            this.transformers.stream().filter(t -> t.isTargetClass(targetClassName)).peek(t -> LOGGER.info("[POST] Found a transformer \"{}\" for class \"{}\"", t.getClass().getName(), targetClassName)).forEach(t -> t.postTransform(targetClass, mixinClassName));
            this.applyAnnotationAction(targetClass, mixinClassName, IAnnotationAction::afterPostTransforming);
        }
    }

    private void applyAnnotationAction(ClassNode targetClass, String mixinClassName, IAnnotationAction.ActionFunction function) {
        List<MethodNode> methods = new ArrayList<>(targetClass.methods);
        for (MethodNode mn : methods) {
            if (ASMUtils.isMixinMethod(mn, mixinClassName)) {
                this.annotationActions.forEach((k, v) -> {
                    AnnotationNode annotation = Annotations.get(mn.visibleAnnotations, k);
                    if (annotation != null) {
                        HashMap<String, Object> values = new HashMap<>();
                        if (annotation.values != null) {
                            for (int i = 0; i < annotation.values.size(); i += 2) {
                                values.put((String) annotation.values.get(i), annotation.values.get(i + 1));
                            }
                        }
                        function.action(v, targetClass, mn, values, mixinClassName);
                    }
                });
            }
        }
    }
}
