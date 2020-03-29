var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

function initializeCoreMod() {
    return {
        "OverlayRenderer": {
            "target": {
                "type": "CLASS",
                "name": "net/minecraft/client/renderer/OverlayRenderer"
            },
            "transformer": function (input) {
                for (var i = input.methods.iterator(); i.hasNext();) {
                    var mn = i.next();
                    if (mn.name.equals("func_230018_a_") && mn.desc.equals("(Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;")) {
                        mn.name = "getOverlayBlock";
                        mn.desc = "(Lnet/minecraft/entity/player/PlayerEntity;)Lorg/apache/commons/lang3/tuple/Pair;";
                        for (var j = mn.instructions.iterator(); j.hasNext();) {
                            var ain = j.next();
                            if (ain.getOpcode() === Opcodes.ARETURN) {
                                mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 1));
                                mn.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos$Mutable", "func_185334_h", "()Lnet/minecraft/util/math/BlockPos;"));
                                mn.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "org/apache/commons/lang3/tuple/Pair", "of", "(Ljava/lang/Object;Ljava/lang/Object;)Lorg/apache/commons/lang3/tuple/Pair;"));
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