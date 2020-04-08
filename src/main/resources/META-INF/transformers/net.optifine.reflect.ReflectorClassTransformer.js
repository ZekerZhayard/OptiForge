var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

function initializeCoreMod() {
    return {
        "ReflectorClass": {
            "target": {
                "type": "CLASS",
                "name": "net/optifine/reflect/ReflectorClass"
            },
            "transformer": function (input) {
                for (var i = input.methods.iterator(); i.hasNext();) {
                    var mn = i.next();
                    if (mn.name.equals("getTargetClass") && mn.desc.equals("()Ljava/lang/Class;")) {
                        for (var j = mn.instructions.iterator(); j.hasNext();) {
                            var ain = j.next();
                            if (ain.getOpcode() === Opcodes.INVOKESTATIC && ain.owner.equals("java/lang/Class") && ain.name.equals("forName") && ain.desc.equals("(Ljava/lang/String;)Ljava/lang/Class;")) {
                                mn.instructions.insertBefore(ain, new InsnNode(Opcodes.ICONST_0));
                                mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 0));
                                mn.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/optifine/reflect/ReflectorClass", "getClass", "()Ljava/lang/Class;"));
                                mn.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;"));
                                ain.desc = "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;";
                                break;
                            }
                        }
                        break;
                    }
                }
                return input;
            }
        }
    };
}