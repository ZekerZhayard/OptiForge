package io.github.zekerzhayard.optiforge.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import javax.annotation.Nonnull;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.fml.OptiForgeWrapperTransformationService;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import io.github.zekerzhayard.optiforge.asm.utils.ModuleUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OptiForgeTransformationService implements ITransformationService {
    private final static Logger LOGGER = LogManager.getLogger();
    private final static String NAME;

    static {
        NAME = new String("optiforge");
        try {
            ModuleUtils.bootstrap();
            // Make sure OptiForge Transformers ordered after OptiFine
            FieldUtils.writeDeclaredField(NAME, "hash", 0xFF11FB13, true);
        } catch (Throwable t) {
            LOGGER.catching(t);
        }
    }

    @Nonnull
    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void onLoad(@Nonnull IEnvironment env, @Nonnull Set<String> otherServices) {

    }

    @Override
    public void initialize(@Nonnull IEnvironment environment) {

    }

    @Nonnull
    @Override
    @SuppressWarnings("rawtypes")
    public List<ITransformer> transformers() {
        List<ITransformer> list = new ArrayList<>();
        // If OptiFine doesn't exist, these transformers shouldn't be applied because they are based on OptiFine classes.
        if (!Boolean.FALSE.equals(OptiForgeWrapperTransformationService.checked)) {
            ServiceLoader<ITransformerImpl> sl = ServiceLoader.load(ITransformerImpl.class, this.getClass().getClassLoader());
            for (ITransformerImpl t : sl) {
                list.add(t);
            }
        }
        return list;
    }
}
