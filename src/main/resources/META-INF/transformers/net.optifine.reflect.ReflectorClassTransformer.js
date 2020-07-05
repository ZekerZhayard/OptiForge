var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");

var Opcodes = Java.type("org.objectweb.asm.Opcodes");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

function initializeCoreMod() {
    return {
        "ReflectorClass_getTargetClass": {
            "target": {
                "type": "METHOD",
                "class": "net.optifine.reflect.ReflectorClass",
                "methodName": "getTargetClass",
                "methodDesc": "()Ljava/lang/Class;"
            },
            "transformer": function (input) {
                ASMAPI.insertInsnList(input, ASMAPI.MethodType.STATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;",
                    ASMAPI.listOf(
                        new InsnNode(Opcodes.ICONST_0),
                        new VarInsnNode(Opcodes.ALOAD, 0),
                        new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/optifine/reflect/ReflectorClass", "getClass", "()Ljava/lang/Class;"),
                        new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;"),
                        new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;")
                    ),
                    ASMAPI.InsertMode.REMOVE_ORIGINAL
                );
                return input;
            }
        }
    };
}