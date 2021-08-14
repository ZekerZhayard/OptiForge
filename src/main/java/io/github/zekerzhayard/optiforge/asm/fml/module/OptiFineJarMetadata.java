package io.github.zekerzhayard.optiforge.asm.fml.module;

import java.lang.module.ModuleDescriptor;
import java.nio.file.Path;
import java.util.Locale;

import cpw.mods.jarhandling.JarMetadata;
import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.api.ITransformationService;
import io.github.zekerzhayard.optiforge.asm.fml.VersionChecker;

public class OptiFineJarMetadata implements JarMetadata {
    private final ITransformationService optifineTransformationService;
    private final Path optifinePath;
    private final SecureJar jar;

    public OptiFineJarMetadata(Path optifinePath, ITransformationService optifineTransformationService, SecureJar jar) {
        this.optifinePath = optifinePath;
        this.optifineTransformationService = optifineTransformationService;
        this.jar = jar;
    }

    @Override
    public String name() {
        return this.optifineTransformationService.name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String version() {
        return VersionChecker.parseOptiFineVersion(VersionChecker.getOptiFineVersionFromJar(this.optifinePath), null);
    }

    @Override
    public ModuleDescriptor descriptor() {
        // OptiFine has no providers.
        return ModuleDescriptor.newAutomaticModule(this.name()).version(this.version()).packages(this.jar.getPackages()).build();
    }
}
