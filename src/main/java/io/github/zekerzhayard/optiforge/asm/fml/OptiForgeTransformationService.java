package io.github.zekerzhayard.optiforge.asm.fml;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import javax.annotation.Nonnull;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.ServiceLoaderStreamUtils;
import cpw.mods.modlauncher.TransformList;
import cpw.mods.modlauncher.TransformStore;
import cpw.mods.modlauncher.TransformTargetLabel;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import net.minecraftforge.fml.loading.FMLServiceProvider;
import net.minecraftforge.fml.loading.ModDirTransformerDiscoverer;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class OptiForgeTransformationService implements ITransformationService {
    private final static Logger LOGGER = LogManager.getLogger();
    private static String name = "optiforge";

    private static boolean checked;

    static {
        try {
            // This can make sure this class is always ordered after FMLServiceProvider.
            FieldUtils.writeDeclaredField(name, "hash", "fml".hashCode() + 1, true);
        } catch (IllegalAccessException e) {
            LOGGER.error("", e);
        }
    }

    @Nonnull
    @Override
    public String name() {
        return name;
    }

    // This method is called after FMLServiceProvider#argumentValues, but before FMLServiceProvider#runScan,
    // so we can check Forge version and add custom mod locator.
    @Override
    public void argumentValues(@Nonnull OptionResult option) {
        String currentFMLVersion = null;
        try {
            Object transformationServicesHandler = FieldUtils.readDeclaredField(Launcher.INSTANCE, "transformationServicesHandler", true);
            @SuppressWarnings("unchecked")
            ServiceLoader<ITransformationService> transformationServices = (ServiceLoader<ITransformationService>) FieldUtils.readDeclaredField(transformationServicesHandler, "transformationServices", true);
            Optional<ITransformationService> transformationService = ServiceLoaderStreamUtils.map(transformationServices, ts -> ts).filter(ts -> ts.getClass().equals(FMLServiceProvider.class)).findFirst();
            if (transformationService.isPresent()) {
                currentFMLVersion = (String) FieldUtils.readDeclaredField(transformationService.get(), "targetForgeVersion", true);
            }
        } catch (Exception e) {
            LOGGER.error("An unexpected issue occurred when detect Forge version: ", e);
        }

        if (checked = (VersionChecker.checkOptiFineVersion(VersionChecker.DEFAULT_FUNCTION) && VersionChecker.checkMixinVersion(VersionChecker.DEFAULT_FUNCTION) && VersionChecker.checkForgeVersion(VersionChecker.DEFAULT_FUNCTION, currentFMLVersion))) {
            try {
                // FML can't detect IModLocator when ITransformationService exists in the same jar, so we must add it manually.
                ModDirTransformerDiscoverer.getExtraLocators().add(Paths.get(OptiForgeTransformationService.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
            } catch (Exception e) {
                LOGGER.error("An unexpected issue occurred when adding custom mod locators: ", e);
            }
        }
    }

    @Override
    public void initialize(@Nonnull IEnvironment environment) {

    }

    @Override
    public void beginScanning(@Nonnull IEnvironment environment) {

    }

    @Override
    public void onLoad(@Nonnull IEnvironment env, @Nonnull Set<String> otherServices) {

    }

    @Nonnull
    @Override
    @SuppressWarnings({
        "rawtypes",
        "unchecked"
    })
    public List<ITransformer> transformers() {
        List<ITransformer> list = new ArrayList<>();
        // see https://github.com/cpw/modlauncher/issues/37
        // OptiFine transformer overwrite all JavaScript field and method transformers.
        // We are now called after FMLServiceProvider#transformers so that we can wrap JavaScript core mod system.
        // But we still have no idea to wrap all transformers which added by other transformation service systems.
        try {
            Object transformationServicesHandler = FieldUtils.readDeclaredField(Launcher.INSTANCE, "transformationServicesHandler", true);
            TransformStore transformStore = (TransformStore) FieldUtils.readDeclaredField(transformationServicesHandler, "transformStore", true);
            EnumMap<TransformTargetLabel.LabelType, TransformList<?>> transformers = (EnumMap<TransformTargetLabel.LabelType, TransformList<?>>) FieldUtils.readDeclaredField(transformStore, "transformers", true);

            // If OptiFine exists but Mixin doesn't, remove OptiFine transformer to avoid crash.
            // Else we must wrap all JavaScript transformers.
            if (!checked) {
                TransformList<ClassNode> classList = (TransformList<ClassNode>) transformers.get(TransformTargetLabel.LabelType.CLASS);
                Map<TransformTargetLabel, List<ITransformer<ClassNode>>> classTransformers = (Map<TransformTargetLabel, List<ITransformer<ClassNode>>>) FieldUtils.readDeclaredField(classList, "transformers", true);
                for (List<ITransformer<ClassNode>> value : classTransformers.values()) {
                    value.removeIf(t -> {
                        try {
                            // assert t.getClass().getName().equals("cpw.mods.modlauncher.TransformerHolder");
                            return FieldUtils.readDeclaredField(t, "wrapped", true).getClass().getName().equals("optifine.OptiFineTransformer");
                        } catch (Exception e) {
                            LOGGER.error("", e);
                            return false;
                        }
                    });
                }
            } else {
                // Wrap all method transformers.
                TransformList<MethodNode> methodList = (TransformList<MethodNode>) transformers.get(TransformTargetLabel.LabelType.METHOD);
                Map<TransformTargetLabel, List<ITransformer<MethodNode>>> methodTransformers = (Map<TransformTargetLabel, List<ITransformer<MethodNode>>>) FieldUtils.readDeclaredField(methodList, "transformers", true);
                for (Map.Entry<TransformTargetLabel, List<ITransformer<MethodNode>>> entry : methodTransformers.entrySet()) {
                    for (ITransformer<MethodNode> transformer : entry.getValue()) {
                        list.add(new OptiForgeTransformer(transformer, ITransformer.Target.targetMethod(((Type) MethodUtils.invokeMethod(entry.getKey(), true, "getClassName")).getInternalName(), entry.getKey().getElementName(), entry.getKey().getElementDescriptor().getInternalName())));
                    }
                }
                methodTransformers.clear();

                // Wrap all field transformers.
                TransformList<FieldNode> fieldList = (TransformList<FieldNode>) transformers.get(TransformTargetLabel.LabelType.FIELD);
                Map<TransformTargetLabel, List<ITransformer<FieldNode>>> fieldTransformers = (Map<TransformTargetLabel, List<ITransformer<FieldNode>>>) FieldUtils.readDeclaredField(fieldList, "transformers", true);
                for (Map.Entry<TransformTargetLabel, List<ITransformer<FieldNode>>> entry : fieldTransformers.entrySet()) {
                    for (ITransformer<FieldNode> transformer : entry.getValue()) {
                        list.add(new OptiForgeTransformer(transformer, ITransformer.Target.targetField(((Type) MethodUtils.invokeMethod(entry.getKey(), true, "getClassName")).getInternalName(), entry.getKey().getElementName())));
                    }
                }
                fieldTransformers.clear();
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return list;
    }
}
