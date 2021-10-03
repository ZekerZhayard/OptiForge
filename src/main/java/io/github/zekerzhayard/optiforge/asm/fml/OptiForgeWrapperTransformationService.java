package io.github.zekerzhayard.optiforge.asm.fml;

import java.net.URI;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.TransformationServiceDecorator;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import io.github.zekerzhayard.optiforge.asm.fml.module.OptiFineJar;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public static Boolean checked = null;

    private final static Logger LOGGER = LogManager.getLogger();
    private final static String NAME;

    static {
        NAME = new String("optiforgewrapper"); // Avoid compiler to replace strings automatically.
        try {
            // This can make sure this class is always ordered after all other services.
            FieldUtils.writeDeclaredField(NAME, "hash", -65536, true);
        } catch (Throwable t) {
            LOGGER.catching(t);
        }
    }

    @Nonnull
    @Override
    public String name() {
        return NAME;
    }

    // This method is called before FMLServiceProvider#runScan, so we can check Forge version and add custom mod locator.
    @Override
    public void initialize(@Nonnull IEnvironment environment) {
        StringBuilder currentFMLVersion = new StringBuilder();
        try {
            // The method "initialize" is called after "argumentValues", so we can read the field "targetForgeVersion" directly.
            this.getTransformationServiceByName("fml").ifPresent(s -> {
                try {
                    currentFMLVersion.append(FieldUtils.readDeclaredField(s, "targetForgeVersion", true));
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            });
        } catch (Exception e) {
            LOGGER.error("An unexpected issue occurred when detecting Forge version, ignore this if you are under development environment: ", e);
        }

        try {
            // We should check if it is under development environments or loaded required versions successfully.
            if (currentFMLVersion.length() != 0) {
                checked = VersionChecker.IS_LOADED && VersionChecker.checkOptiFineVersion(VersionChecker.DEFAULT_FUNCTION, true, this.getClass().getClassLoader()) && VersionChecker.checkForgeVersion(VersionChecker.DEFAULT_FUNCTION, currentFMLVersion.toString());
            }
        } catch (Exception e) {
            LOGGER.error("An unexpected issue occurred when checking versions: ", e);
        }
    }

    @Override
    public void onLoad(@Nonnull IEnvironment env, @Nonnull Set<String> otherServices) {

    }

    @Override
    public List<Resource> completeScan(IModuleLayerManager layerManager) {
        List<Resource> resources = new ArrayList<>();
        VersionChecker.getOptiFinePath().ifPresent(LamdbaExceptionUtils.rethrowConsumer(path -> {
            FileSystems.newFileSystem(URI.create("jar:" + path.toAbsolutePath().toUri()), Map.of("create", "true"));
            resources.add(new Resource(IModuleLayerManager.Layer.GAME, List.of(new OptiFineJar(path, this.getTransformationServiceByName("OptiFine").orElseThrow()))));
        }));
        return resources;
    }

    @Nonnull
    @Override
    @SuppressWarnings("rawtypes")
    public List<ITransformer> transformers() {
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private Optional<ITransformationService> getTransformationServiceByName(String name) {
        try {
            Object transformationServicesHandler = FieldUtils.readDeclaredField(Launcher.INSTANCE, "transformationServicesHandler", true);
            Map<String, TransformationServiceDecorator> serviceLookup = (Map<String, TransformationServiceDecorator>) FieldUtils.readDeclaredField(transformationServicesHandler, "serviceLookup", true);
            if (serviceLookup.containsKey(name)) {
                return Optional.of((ITransformationService) FieldUtils.readDeclaredField(serviceLookup.get(name), "service", true));
            }
        } catch (Throwable t) {
            LOGGER.catching(t);
        }
        return Optional.empty();
    }
}
