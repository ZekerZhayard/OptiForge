package io.github.zekerzhayard.optiforge.asm.fml;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;

/**
 * If someone used wrong forge version or didn't install MixinBootstrap, use this class to replace OptiFineTransformationService.
 */
public class FakeOptiFineTransformationService implements ITransformationService {
    @Nonnull
    @Override
    public String name() {
        return "OptiFine";
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
    @SuppressWarnings("rawtypes")
    public List<ITransformer> transformers() {
        return Collections.emptyList();
    }
}
