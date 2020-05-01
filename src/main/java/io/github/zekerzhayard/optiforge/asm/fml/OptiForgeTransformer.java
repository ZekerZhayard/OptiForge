package io.github.zekerzhayard.optiforge.asm.fml;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

import com.google.common.collect.Sets;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class OptiForgeTransformer<T> implements ITransformer<ClassNode> {
    protected ITransformer<T> wrappedTransformer;
    protected Target target;

    public OptiForgeTransformer(ITransformer<T> wrappedTransformer, Target target) {
        this.wrappedTransformer = wrappedTransformer;
        this.target = target;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public ClassNode transform(@Nonnull ClassNode input, @Nonnull ITransformerVotingContext context) {
        List<T> elements = new ArrayList<>();
        switch (this.target.getTargetType()) {
            // For insurance purposes, we still need invoke class transformers.
            case CLASS: {
                input = (ClassNode) this.wrappedTransformer.transform((T) input, context);
                break;
            }
            case METHOD: {
                for (MethodNode mn : input.methods) {
                    if (mn.name.equals(this.target.getElementName()) && mn.desc.equals(this.target.getElementDescriptor())) {
                        mn = (MethodNode) this.wrappedTransformer.transform((T) mn, context);
                    }
                    elements.add((T) mn);
                }
                input.methods = (List<MethodNode>) elements;
                break;
            }
            case FIELD: {
                for (FieldNode fn : input.fields) {
                    if (fn.name.equals(this.target.getElementName())) {
                        fn = (FieldNode) this.wrappedTransformer.transform((T) fn, context);
                    }
                    elements.add((T) fn);
                }
                input.fields = (List<FieldNode>) elements;
                break;
            }
        }
        return input;
    }

    @Nonnull
    @Override
    public TransformerVoteResult castVote(@Nonnull ITransformerVotingContext context) {
        return this.wrappedTransformer.castVote(context);
    }

    @Nonnull
    @Override
    public Set<Target> targets() {
        return Sets.newHashSet(Target.targetClass(this.target.getClassName()));
    }
}
