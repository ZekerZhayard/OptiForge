var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");

function initializeCoreMod() {
    return {
        "Matrix4f": {
            "target": {
                "type": "CLASS",
                "name": "net/minecraft/client/renderer/entity/EntityRendererManager"
            },
            "transformer": function (input) {
                for (var i = input.methods.iterator(); i.hasNext();) {
                    var mn = i.next();
                    if (mn.name.equals("<init>") && mn.desc.equals("(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/renderer/ItemRenderer;Lnet/minecraft/resources/IReloadableResourceManager;Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/GameSettings;)V")) {
                        for (var j = mn.instructions.iterator(); j.hasNext();) {
                            var ain = j.next();
                            if (ain.getOpcode() === Opcodes.GETSTATIC && ain.owner.equals("net/minecraft/util/registry/Registry") && ain.name.equals("field_212629_r") && ain.desc.equals("Lnet/minecraft/util/registry/DefaultedRegistry;")) {
                                mn.instructions.insertBefore(ain, new InsnNode(Opcodes.RETURN));
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