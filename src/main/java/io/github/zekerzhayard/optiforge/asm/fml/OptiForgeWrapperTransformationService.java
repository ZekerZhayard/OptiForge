package io.github.zekerzhayard.optiforge.asm.fml;

import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import javax.annotation.Nonnull;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.ServiceLoaderStreamUtils;
import cpw.mods.modlauncher.TransformationServiceDecorator;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import net.minecraftforge.fml.loading.FMLServiceProvider;
import net.minecraftforge.fml.loading.ModDirTransformerDiscoverer;
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
            if (Files.isDirectory(path) || currentFMLVersion.length() == 0) {
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
    @SuppressWarnings("rawtypes")
    public List<ITransformer> transformers() {
        return new ArrayList<>();
    }
}
