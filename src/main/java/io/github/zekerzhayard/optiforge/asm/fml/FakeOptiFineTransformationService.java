package io.github.zekerzhayard.optiforge.asm.fml;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * If someone used wrong forge version or didn't install MixinBootstrap, use this class to replace OptiFineTransformationService.
 */
public class FakeOptiFineTransformationService implements ITransformationService {
    private final static Logger LOGGER = LogManager.getLogger();
    private static FakeOptiFineTransformationService instance;
    public ITransformationService service;
    private String name;
    private IEnvironment env;
    private Set<String> otherServices;

    public FakeOptiFineTransformationService() {
        instance = this;
        try {
            this.service = (ITransformationService) Class.forName("optifine.OptiFineTransformationService").getConstructor().newInstance();
        } catch (ClassNotFoundException cnfe) {
            // OptiForge 1.17.1 will force searching libraries folder.
            // This part will be reserved because OptiFine may be compatible with forge in the future.
            LOGGER.info("It looks like you do not put real OptiFine to the mods folder. Scan libraries folder to find OptiFine later.");
        } catch (Throwable t) {
            LOGGER.catching(t);
        }
    }

    public static FakeOptiFineTransformationService getInstance() {
        return Objects.requireNonNullElseGet(instance, FakeOptiFineTransformationService::new);
    }

    @Nonnull
    @Override
    public String name() {
        if (this.service != null && this.name == null) {
            this.name = "Fake" + this.service.name();
            this.service = null;
        }
        return this.name != null ? this.name : (this.name = "OptiFine");
    }

    @Override
    public void initialize(@Nonnull IEnvironment environment) {
        if (this.service != null) {
            this.service.initialize(environment);
        }
    }

    @Override
    public void onLoad(@Nonnull IEnvironment env, @Nonnull Set<String> otherServices) throws IncompatibleEnvironmentException {
        if (this.service != null) {
            this.service.onLoad(env, otherServices);
        } else {
            this.env = env;
            this.otherServices = otherServices;
        }
    }

    @Override
    public Map.Entry<Set<String>, Supplier<Function<String, Optional<URL>>>> additionalResourcesLocator() {
        if (this.service != null) {
            return this.service.additionalResourcesLocator();
        } else {
            return ITransformationService.super.additionalResourcesLocator();
        }
    }

    @Override
    public Map.Entry<Set<String>, Supplier<Function<String, Optional<URL>>>> additionalClassesLocator() {
        if (this.service != null) {
            return this.service.additionalClassesLocator();
        } else {
            return ITransformationService.super.additionalClassesLocator();
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("rawtypes")
    public List<ITransformer> transformers() {
        if (this.service != null) {
            return this.service.transformers();
        } else {
            return new ArrayList<>();
        }
    }

    public IEnvironment getEnv() {
        return this.env;
    }

    public Set<String> getOtherServices() {
        return this.otherServices;
    }
}
