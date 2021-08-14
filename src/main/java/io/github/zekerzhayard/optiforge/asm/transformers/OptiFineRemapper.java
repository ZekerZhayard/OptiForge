package io.github.zekerzhayard.optiforge.asm.transformers;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import io.github.zekerzhayard.optiforge.asm.fml.VersionChecker;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;

public class OptiFineRemapper extends Remapper implements ITransformer<ClassNode> {
    private final Path optifinePath = VersionChecker.getOptiFinePath().orElseThrow();
    // OptiFine srg name are all wrong, we should remap them at runtime.
    private final Map<OptiFineRemapper.FullDesc, String> fieldMappings = readMappings("/mappings/field.mappings");
    private final Map<OptiFineRemapper.FullDesc, String> methodMappings = readMappings("/mappings/method.mappings");
    // OptiFine AccessFixer is unable to combine package-private and private access, we should fix it manually.
    private final Set<OptiFineRemapper.FullDesc> accessFixer = new CopyOnWriteArraySet<>();

    private static Map<OptiFineRemapper.FullDesc, String> readMappings(String name) {
        Map<OptiFineRemapper.FullDesc, String> mappings = new ConcurrentHashMap<>();
        try {
            List<String> mapping = IOUtils.readLines(Objects.requireNonNull(OptiFineRemapper.class.getResourceAsStream(name), "Unable to read mappings!"), StandardCharsets.UTF_8);
            String currentClass = null;
            for (String current : mapping) {
                if (!current.startsWith("\t")) {
                    currentClass = current;
                } else {
                    String[] fullDesc = current.split("\\s+");
                    mappings.put(new OptiFineRemapper.FullDesc(currentClass, fullDesc[1], fullDesc[2]), fullDesc[3]);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mappings;
    }

    private static boolean isPrivate(int access) {
        return (access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE;
    }

    private static int fixPrivate(int access) {
        return access >>> 3 << 3;
    }

    @Override
    public @Nonnull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        // Fix access
        if (isPrivate(input.access) && this.accessFixer.contains(new OptiFineRemapper.FullDesc(input.name, "", ""))) {
            input.access = fixPrivate(input.access);
        }
        input.innerClasses.stream()
            .filter(ic -> isPrivate(ic.access) && this.accessFixer.contains(new OptiFineRemapper.FullDesc(ic.name, "", "")))
            .forEach(ic -> ic.access = fixPrivate(ic.access));
        input.fields.stream()
            .filter(f -> isPrivate(f.access) && this.accessFixer.contains(new OptiFineRemapper.FullDesc(input.name, f.name, f.desc)))
            .forEach(f -> f.access = fixPrivate(f.access));
        input.methods.stream()
            .filter(m -> isPrivate(m.access) && this.accessFixer.contains(new OptiFineRemapper.FullDesc(input.name, m.name, m.desc)))
            .forEach(m -> m.access = fixPrivate(m.access));

        // OptiFine 1.17 is compiled with Java 8, but some core mods may use high version features.
        //input.version = Opcodes.V16;

        // Remap
        ClassNode output = new ClassNode();
        input.accept(new ClassRemapper(output, this));
        return output;
    }

    @Override
    public @Nonnull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @Nonnull Set<ITransformer.Target> targets() {
        try {
            return Files.walk(FileSystems.getFileSystem(URI.create("jar:" + this.optifinePath.toUri())).getPath("/srg/"))
                .filter(p -> p.toString().endsWith(".class"))
                .peek(p -> {
                    try {
                        new ClassReader(Files.readAllBytes(p)).accept(new ClassVisitor(Opcodes.ASM9) {
                            private String className;

                            @Override
                            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                                this.className = name;
                                this.addPackagePrivate(access, name, "", "");
                                super.visit(version, access, name, signature, superName, interfaces);
                            }

                            @Override
                            public void visitInnerClass(String name, String outerName, String innerName, int access) {
                                this.addPackagePrivate(access, name, "", "");
                                super.visitInnerClass(name, outerName, innerName, access);
                            }

                            @Override
                            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                                this.addPackagePrivate(access, this.className, name, descriptor);
                                return super.visitField(access, name, descriptor, signature, value);
                            }

                            @Override
                            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                                this.addPackagePrivate(access, this.className, name, descriptor);
                                return super.visitMethod(access, name, descriptor, signature, exceptions);
                            }

                            private void addPackagePrivate(int access, String owner, String name, String descriptor) {
                                if (access >>> 3 << 3 == access) {
                                    OptiFineRemapper.this.accessFixer.add(new OptiFineRemapper.FullDesc(owner, name, descriptor));
                                }
                            }
                        }, 0);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(p -> p.toAbsolutePath().subpath(1, p.getNameCount()).toString())
                .map(s -> ITransformer.Target.targetPreClass(s.substring(0, s.length() - ".class".length())))
                .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        return this.methodMappings.getOrDefault(new OptiFineRemapper.FullDesc(owner, name, descriptor), name);
    }

    @Override
    public String mapInvokeDynamicMethodName(String name, String descriptor) {
        String owner = Type.getMethodType(descriptor).getReturnType().getInternalName();
        // We are unable to get the desc of the target method, here may have some issues.
        return this.methodMappings.entrySet().stream()
            .filter(e -> Objects.equals(e.getKey().owner(), owner) && Objects.equals(e.getKey().name(), name))
            .map(Map.Entry::getValue)
            .findFirst().orElse(name);
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        return this.fieldMappings.getOrDefault(new OptiFineRemapper.FullDesc(owner, name, descriptor), name);
    }

    private static record FullDesc(String owner, String name, String descriptor) {

    }
}
