package io.github.zekerzhayard.optiforge.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import javax.annotation.Nonnull;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OptiForgeTransformationService implements ITransformationService {
    private final static Logger LOGGER = LogManager.getLogger();
    private final static String NAME;

    static {
        NAME = "optiforge";
        try {
            // Make sure OptiForge Transformers ordered after OptiFine
            FieldUtils.writeDeclaredField(NAME, "hash", 0xFF11FB13, true);
        } catch (IllegalAccessException e) {
            LOGGER.catching(e);
        }
    }

    @Nonnull
    @Override
    public String name() {
        return NAME;
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
        ServiceLoader<ITransformerImpl> sl = ServiceLoader.load(ITransformerImpl.class, this.getClass().getClassLoader());
        List<ITransformer> list = new ArrayList<>();
        for (ITransformerImpl t : sl) {
            list.add(t);
        }
        return list;
    }
}
