package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.crash;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class CrashReportTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.crash.CrashReport";
    }

    @Override
    public ClassNode transform(ClassNode input) {
        // https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/patches/minecraft/net/minecraft/crash/CrashReport.java.patch#L15-L24
        //
        //           p_71506_1_.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
        // -         p_71506_1_.append("Stacktrace:\n");
        // -
        // -         for(StackTraceElement stacktraceelement : this.field_85060_g) {
        // -            p_71506_1_.append("\t").append("at ").append((Object)stacktraceelement);
        // -            p_71506_1_.append("\n");
        // -         }
        // -
        // -         p_71506_1_.append("\n");
        // +         p_71506_1_.append("Stacktrace:");
        // +         p_71506_1_.append(net.minecraftforge.fml.CrashReportExtender.generateEnhancedStackTrace(this.field_85060_g));
        //        }
        //

        MethodNode getSectionsInStringBuilder = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_71506_a"), "(Ljava/lang/StringBuilder;)V"));

        for (AbstractInsnNode ain : getSectionsInStringBuilder.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("net/optifine/reflect/ReflectorMethod") && min.name.equals("callString") && min.desc.equals("([Ljava/lang/Object;)Ljava/lang/String;")) {

                    // aload 1
                    // getstatic ReflectorMethod Reflector.CrashReportExtender_generateEnhancedStackTraceSTE
                    // add -> iconst_1
                    // add -> anewarray Object
                    // add -> dup
                    // add -> iconst_0
                    // aload 0
                    // getfield StackTraceElement[] CrashReport.field_85060_g
                    // add -> aastore
                    // invokevirtual String ReflectorMethod.callString(Object[])
                    // invokevirtual StringBuilder StringBuilder.append(String)
                    // pop
                    AbstractInsnNode ain0 = min.getPrevious();
                    while (ain0.getOpcode() != Opcodes.ALOAD || ((VarInsnNode) ain0).var != 0) {
                        ain0 = ain0.getPrevious();
                    }

                    InsnList il = new InsnList();
                    il.add(new InsnNode(Opcodes.ICONST_1));
                    il.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"));
                    il.add(new InsnNode(Opcodes.DUP));
                    il.add(new InsnNode(Opcodes.ICONST_0));

                    getSectionsInStringBuilder.instructions.insertBefore(ain0, il);

                    getSectionsInStringBuilder.instructions.insertBefore(min, new InsnNode(Opcodes.AASTORE));

                    break;
                }
            }
        }

        return input;
    }
}
