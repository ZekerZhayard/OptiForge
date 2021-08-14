package io.github.zekerzhayard.optiforge.asm.fml.module;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import cpw.mods.jarhandling.impl.Jar;
import cpw.mods.modlauncher.api.ITransformationService;

public class OptiFineJar extends Jar {
    private final ITransformationService optifineTransformationService;

    private Map.Entry<Set<String>, Supplier<Function<String, Optional<URL>>>> classLocator;
    private Set<String> packages;

    public OptiFineJar(Path optifinePath, ITransformationService optifineTransformationService) {
        super(Manifest::new, jar -> new OptiFineJarMetadata(optifinePath, optifineTransformationService, jar), (pkg, s) -> true, optifinePath);
        this.optifineTransformationService = optifineTransformationService;
    }

    @Override
    public Set<String> getPackages() {
        if (this.classLocator == null || this.packages == null) {
            this.classLocator = this.optifineTransformationService.additionalClassesLocator();
            this.packages = this.classLocator.getKey().stream().flatMap(pkg -> {
                Path srgRoot = this.getPath("srg", pkg.replace('.', '/'));
                try {
                    return Files.walk(Files.exists(srgRoot) ? srgRoot : this.getPath("/" + pkg.replace('.', '/')))
                        .filter(path -> Files.exists(path) && !Files.isDirectory(path))
                        .filter(path -> path.getFileName().toString().endsWith(".class"))
                        .map(path -> path.subpath(path.getName(0).toString().equals("srg") ? 1 : 0, path.getNameCount() - 1)) // Remove "srg"
                        .map(path -> path.toString().replace('/', '.'))
                        .filter(pkg0 -> pkg0.length() != 0);
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }).collect(Collectors.toSet());
        }
        return this.packages;
    }

    @Override
    public Optional<URI> findFile(String name) {
        return this.classLocator.getValue().get().apply(name).map(url -> {
            try {
                return url.toURI();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        });
    }
}
