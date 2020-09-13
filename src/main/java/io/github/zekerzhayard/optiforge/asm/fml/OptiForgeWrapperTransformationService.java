package io.github.zekerzhayard.optiforge.asm.fml;

import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import javax.annotation.Nonnull;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.ServiceLoaderStreamUtils;
import cpw.mods.modlauncher.TransformList;
import cpw.mods.modlauncher.TransformStore;
import cpw.mods.modlauncher.TransformTargetLabel;
import cpw.mods.modlauncher.TransformationServiceDecorator;
import cpw.mods.modlauncher.TransformerHolder;
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

// Method invoker orderer:
//   <clinit>
//   <init>
//   name (more than once)
//   onLoad
//   arguments (parallel)
//   argumentValues (parallel)
//   initialize
//   runScan
//   (beginScanning)
//   transformers
//   additionalClassesLocator
//   additionalResourcesLocator
public class OptiForgeWrapperTransformationService implements ITransformationService {
    private final static Logger LOGGER = LogManager.getLogger();
    private final static String NAME;

    private static boolean checked;

    static {
        NAME = "optiforgewrapper"; // Avoid compiler to replace strings automatically.
        try {
            // This can make sure this class is always ordered after all other services.
            FieldUtils.writeDeclaredField(NAME, "hash", -65536, true);
        } catch (IllegalAccessException e) {
            LOGGER.error("", e);
        }
    }

    @Nonnull
    @Override
    public String name() {
        return NAME;
    }

    // This method is called before FMLServiceProvider#runScan, so we can check Forge version and add custom mod locator.
    @Override
    @SuppressWarnings("unchecked")
    public void initialize(@Nonnull IEnvironment environment) {
        StringBuilder currentFMLVersion = new StringBuilder();
        Object transformationServicesHandler = new Object();
        try {
            transformationServicesHandler = FieldUtils.readDeclaredField(Launcher.INSTANCE, "transformationServicesHandler", true);
            ServiceLoader<ITransformationService> transformationServices = (ServiceLoader<ITransformationService>) FieldUtils.readDeclaredField(transformationServicesHandler, "transformationServices", true);
            ServiceLoaderStreamUtils.map(transformationServices, ts -> ts).filter(ts -> ts.getClass().equals(FMLServiceProvider.class)).findFirst().ifPresent(ts -> {
                try {
                    // The method "initialize" is called after "argumentValues", so we can read the field "targetForgeVersion" directly.
                    currentFMLVersion.append(FieldUtils.readDeclaredField(ts, "targetForgeVersion", true));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            LOGGER.error("An unexpected issue occurred when detecting Forge version, ignore this if you are under development environment: ", e);
        }

        try {
            Path path = Paths.get(OptiForgeWrapperTransformationService.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            // We should check if it is under development environments or loaded required versions successfully.
            if (checked = Files.isDirectory(path) || currentFMLVersion.length() == 0) {
                // Nothing to do.
            } else if (checked = VersionChecker.IS_LOADED && VersionChecker.checkOptiFineVersion(VersionChecker.DEFAULT_FUNCTION, true) && VersionChecker.checkForgeVersion(VersionChecker.DEFAULT_FUNCTION, currentFMLVersion.toString())) {
                // FML can't detect IModLocator when ITransformationService exists in the same jar, so we must add it manually.
                ModDirTransformerDiscoverer.getExtraLocators().add(path);
            } else {
                // If required mods don't exist, we should remove OptiFineTransformationService.
                Map<String, TransformationServiceDecorator> serviceLookup = (Map<String, TransformationServiceDecorator>) FieldUtils.readDeclaredField(transformationServicesHandler, "serviceLookup", true);
                if (serviceLookup.containsKey("OptiFine")) {
                    Constructor<TransformationServiceDecorator> constructor = TransformationServiceDecorator.class.getDeclaredConstructor(ITransformationService.class);
                    constructor.setAccessible(true);
                    serviceLookup.put("OptiFine", constructor.newInstance(new FakeOptiFineTransformationService()));
                }
            }
        } catch (Exception e) {
            LOGGER.error("An unexpected issue occurred when checking versions: ", e);
        }
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
        if (checked) {
            // See https://github.com/cpw/modlauncher/issues/37
            // OptiFine transformer overwrite all JavaScript field and method transformers.
            // We are now called after all ITransformationService#transformers so that we can wrap all transformation systems.
            try {
                Object transformationServicesHandler = FieldUtils.readDeclaredField(Launcher.INSTANCE, "transformationServicesHandler", true);
                TransformStore transformStore = (TransformStore) FieldUtils.readDeclaredField(transformationServicesHandler, "transformStore", true);
                EnumMap<TransformTargetLabel.LabelType, TransformList<?>> transformers = (EnumMap<TransformTargetLabel.LabelType, TransformList<?>>) FieldUtils.readDeclaredField(transformStore, "transformers", true);
                for (Map.Entry<TransformTargetLabel.LabelType, TransformList<?>> transformListEntry : transformers.entrySet()) {
                    if (transformListEntry.getKey().equals(TransformTargetLabel.LabelType.CLASS)) {
                        continue;
                    }
                    Map<TransformTargetLabel, List<ITransformer<?>>> transformersMap = (Map<TransformTargetLabel, List<ITransformer<?>>>) FieldUtils.readDeclaredField(transformListEntry.getValue(), "transformers", true);
                    for (Map.Entry<TransformTargetLabel, List<ITransformer<?>>> entry : transformersMap.entrySet()) {
                        for (ITransformer<?> transformer : entry.getValue()) {
                            // assert transformer instanceof TransformerHolder
                            String className = ((Type) MethodUtils.invokeMethod(entry.getKey(), true, "getClassName")).getInternalName();
                            MethodUtils.invokeMethod(transformers.get(TransformTargetLabel.LabelType.CLASS), true, "addTransformer", new TransformTargetLabel(className, TransformTargetLabel.LabelType.CLASS), new TransformerHolder<>(new OptiForgeWrapperTransformer<>(transformer, transformListEntry.getKey().equals(TransformTargetLabel.LabelType.FIELD) ? ITransformer.Target.targetField(className, entry.getKey().getElementName()) : ITransformer.Target.targetMethod(className, entry.getKey().getElementName(), entry.getKey().getElementDescriptor().getInternalName())), ((TransformerHolder<?>) transformer).owner()));
                        }
                    }
                    transformersMap.clear();
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        return new ArrayList<>();
    }
}
