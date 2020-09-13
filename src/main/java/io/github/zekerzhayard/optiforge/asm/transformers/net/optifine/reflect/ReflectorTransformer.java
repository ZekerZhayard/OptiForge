package io.github.zekerzhayard.optiforge.asm.transformers.net.optifine.reflect;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class ReflectorTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.optifine.reflect.Reflector";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        MethodNode _clinit_ = Objects.requireNonNull(Bytecode.findMethod(input, "<clinit>", "()V"));

        for (AbstractInsnNode ain : _clinit_.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.PUTSTATIC) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.owner.equals("net/optifine/reflect/Reflector") && fin.name.equals("CrashReportExtender_generateEnhancedStackTrace") && fin.desc.equals("Lnet/optifine/reflect/ReflectorMethod;")) {
                    // - CrashReportExtender_generateEnhancedStackTrace = new ReflectorMethod(CrashReportExtender, "generateEnhancedStackTrace");
                    // + CrashReportExtender_generateEnhancedStackTrace = new ReflectorMethod(CrashReportExtender, "generateEnhancedStackTrace", new Class[] {Throwable.class});

                    AbstractInsnNode ain0 = fin.getPrevious();
                    while (ain0.getOpcode() != Opcodes.INVOKESPECIAL) {
                        ain0 = ain0.getPrevious();
                    }
                    MethodInsnNode min = (MethodInsnNode) ain0;

                    if (min.owner.equals("net/optifine/reflect/ReflectorMethod") && min.name.equals("<init>") && min.desc.equals("(Lnet/optifine/reflect/ReflectorClass;Ljava/lang/String;)V")) {
                        min.desc = "(Lnet/optifine/reflect/ReflectorClass;Ljava/lang/String;[Ljava/lang/Class;)V";

                        InsnList il = new InsnList();
                        il.add(new InsnNode(Opcodes.ICONST_1));
                        il.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Class"));
                        il.add(new InsnNode(Opcodes.DUP));
                        il.add(new InsnNode(Opcodes.ICONST_0));
                        il.add(new LdcInsnNode(Type.getType("Ljava/lang/Throwable;")));
                        il.add(new InsnNode(Opcodes.AASTORE));

                        _clinit_.instructions.insertBefore(min, il);
                    }
                } else if (fin.owner.equals("net/optifine/reflect/Reflector") && fin.name.equals("RenderNameplateEvent_Constructor") && fin.desc.equals("Lnet/optifine/reflect/ReflectorConstructor;")) {
                    // - RenderNameplateEvent_Constructor = new ReflectorConstructor(RenderNameplateEvent, new Class[]{Entity.class, ITextComponent.class, EntityRenderer.class, MatrixStack.class, IRenderTypeBuffer.class, Integer.TYPE});
                    // + RenderNameplateEvent_Constructor = new ReflectorConstructor(RenderNameplateEvent, new Class[]{Entity.class, ITextComponent.class, EntityRenderer.class, MatrixStack.class, IRenderTypeBuffer.class, Integer.TYPE, Float});

                    AbstractInsnNode ain0 = fin.getPrevious();
                    while (ain0.getOpcode() != Opcodes.BIPUSH) {
                        ain0 = ain0.getPrevious();
                    }
                    IntInsnNode iin = (IntInsnNode) ain0;
                    iin.operand++;

                    InsnList il = new InsnList();
                    il.add(new InsnNode(Opcodes.DUP));
                    il.add(new IntInsnNode(Opcodes.BIPUSH, iin.operand - 1));
                    il.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;"));
                    il.add(new InsnNode(Opcodes.AASTORE));

                    _clinit_.instructions.insertBefore(fin.getPrevious(), il);
                }
            }
        }

        return input;
    }
}
