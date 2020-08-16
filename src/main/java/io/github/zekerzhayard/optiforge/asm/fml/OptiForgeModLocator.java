package io.github.zekerzhayard.optiforge.asm.fml;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FML will exclude all jar files which use ITransformationService, so we need this mod locator.
 */
public class OptiForgeModLocator extends AbstractJarFileLocator {
    private final static Logger LOGGER = LogManager.getLogger();

    @Override
    public List<IModFile> scanMods() {
        List<IModFile> list = new ArrayList<>();
        try {
            IModFile file = ModFile.newFMLInstance(Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()), this);
            this.modJars.compute(file, (mf, fs) -> this.createFileSystem(mf));
            list.add(file);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return list;
    }

    @Override
    public String name() {
        return "optiforge-modlocator";
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {

    }
}
