package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.crash;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
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

        int crashReportSectionsCount = 0;
        for (AbstractInsnNode ain : getSectionsInStringBuilder.instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.GETFIELD) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.owner.equals("net/minecraft/crash/CrashReport") && fin.name.equals(ASMAPI.mapField("field_71512_c")) && fin.desc.equals("Ljava/util/List;")) {
                    crashReportSectionsCount++;
                    if (crashReportSectionsCount == 3) {

                        // label 6
                        // line 136
                        // aload 1
                        // remove -> ldc String "Stacktrace:\n"
                        // remove -> invokevirtual StringBuilder StringBuilder.append(String)
                        // remove -> pop
                        // remove -> label 7
                        // remove -> line 138
                        // remove -> aload 0
                        // remove -> getfield StackTraceElement[] CrashReport.field_85060_g
                        // remove -> astore 2
                        // remove -> aload 2
                        // remove -> arraylength
                        // remove -> istore 3
                        // remove -> iconst_0
                        // remove -> istore 4
                        // remove -> label 8
                        // remove -> f_new 5 0
                        // remove -> iload 4
                        // remove -> iload 3
                        // remove -> if_icmpge 12
                        // remove -> aload 2
                        // remove -> iload 4
                        // remove -> aaload
                        // remove -> astore 5
                        // remove -> label 9
                        // remove -> line 140
                        // remove -> aload 1
                        // remove -> ldc String "\t"
                        // remove -> invokevirtual StringBuilder StringBuilder.append(String)
                        // remove -> ldc String "at "
                        // remove -> invokevirtual StringBuilder StringBuilder.append(String)
                        // remove -> aload 5
                        // remove -> invokevirtual StringBuilder StringBuilder.append(Object)
                        // remove -> pop
                        // remove -> label 10
                        // remove -> line 141
                        // remove -> aload 1
                        // remove -> ldc String "\n"
                        // remove -> invokevirtual StringBuilder StringBuilder.append(String)
                        // remove -> pop
                        // remove -> label 11
                        // remove -> line 138
                        // remove -> iinc 4 1
                        // remove -> goto 8
                        // remove -> label 12
                        // remove -> line 144
                        // remove -> f_new 2 0
                        // remove -> aload 1
                        // remove -> ldc String "\n"
                        // remove -> invokevirtual StringBuilder StringBuilder.append(String)
                        // remove -> pop
                        // label 13
                        // line 147
                        // f_new 2 0
                        // aload 0
                        // getfield List CrashReport.field_71512_c
                        AbstractInsnNode ain0 = fin.getPrevious();
                        while (ain0.getPrevious().getOpcode() != Opcodes.POP) {
                            ain0 = ain0.getPrevious();
                        }
                        while (!(ain0.getPrevious() instanceof LdcInsnNode) || !((LdcInsnNode) ain0.getPrevious()).cst.equals("Stacktrace:\n")) {
                            getSectionsInStringBuilder.instructions.remove(ain0.getPrevious());
                        }
                        getSectionsInStringBuilder.instructions.remove(ain0.getPrevious());

                        InsnList il = new InsnList();
                        il.add(new LdcInsnNode("Stacktrace:"));
                        il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
                        il.add(new InsnNode(Opcodes.POP));
                        il.add(new LabelNode());

                        il.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/crash/CrashReport", ASMAPI.mapField("field_85060_g"), "[Ljava/lang/StackTraceElement;"));
                        il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/fml/CrashReportExtender", "generateEnhancedStackTrace", "([Ljava/lang/StackTraceElement;)Ljava/lang/String;", false));
                        il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
                        il.add(new InsnNode(Opcodes.POP));

                        getSectionsInStringBuilder.instructions.insertBefore(ain0, il);

                        break;
                    }
                }
            }
        }

        return input;
    }
}
