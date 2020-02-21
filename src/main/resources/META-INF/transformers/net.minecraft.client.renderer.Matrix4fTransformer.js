var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var MethodNode = Java.type("org.objectweb.asm.tree.MethodNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

function initializeCoreMod() {
    return {
        "Matrix4f": {
            "target": {
                "type": "CLASS",
                "name": "net/minecraft/client/renderer/Matrix4f"
            },
            "transformer": function (input) {
                var mn = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "([F)V", null, null);
                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V"));
                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/client/renderer/Matrix4f", "optiforge_newInstance", "([F)V"));
                mn.instructions.add(new InsnNode(Opcodes.RETURN));
                input.methods.add(mn);
                return input;
            }
        }
    };
}