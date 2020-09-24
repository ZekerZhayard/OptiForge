package io.github.zekerzhayard.optiforge.asm.transformers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import javax.annotation.Nonnull;

import com.google.common.collect.Sets;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public interface ITransformerImpl extends ITransformer<ClassNode> {
    Logger LOGGER = LogManager.getLogger();

    String targetClass();

    ClassNode transform(ClassNode input);

    @Nonnull
    @Override
    default ClassNode transform(@Nonnull ClassNode input, @Nonnull ITransformerVotingContext context) {
        LOGGER.info("Transform: " + input.name);

        input = this.transform(input);

        if (System.getProperty("optiforge.dumpclass", "false").equals("true")) {
            try {
                Path dumpPath = Paths.get("dumpclass");

                if (!Files.isDirectory(dumpPath)) {
                    Files.createDirectories(dumpPath);
                }

                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                input.accept(cw);

                Path dumpclassPath = dumpPath.resolve(input.name + ".class");
                if (!Files.isDirectory(dumpclassPath.getParent())) {
                    Files.createDirectories(dumpclassPath.getParent());
                }
                Files.write(dumpclassPath, cw.toByteArray());
            } catch (Throwable t) {
                LOGGER.catching(t);
            }
        }

        return input;
    }

    @Nonnull
    @Override
    default TransformerVoteResult castVote(@Nonnull ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Nonnull
    default Set<Target> targets() {
        return Sets.newHashSet(Target.targetPreClass(this.targetClass()));
    }
}
