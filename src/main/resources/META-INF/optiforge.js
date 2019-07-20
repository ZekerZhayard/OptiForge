var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");

var Attribute     = Java.type("org.objectweb.asm.Attribute");
var Handle        = Java.type("org.objectweb.asm.Handle");
var Label         = Java.type("org.objectweb.asm.Label");
var Opcodes       = Java.type("org.objectweb.asm.Opcodes");
var Type          = Java.type("org.objectweb.asm.Type");
var TypePath      = Java.type("org.objectweb.asm.TypePath");
var TypeReference = Java.type("org.objectweb.asm.TypeReference");

var AbstractInsnNode            = Java.type("org.objectweb.asm.tree.AbstractInsnNode");
var FieldInsnNode               = Java.type("org.objectweb.asm.tree.FieldInsnNode");
var FieldNode                   = Java.type("org.objectweb.asm.tree.FieldNode");
var FrameNode                   = Java.type("org.objectweb.asm.tree.FrameNode");
var IincInsnNode                = Java.type("org.objectweb.asm.tree.IincInsnNode");
var InsnList                    = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode                    = Java.type("org.objectweb.asm.tree.InsnNode");
var IntInsnNode                 = Java.type("org.objectweb.asm.tree.IntInsnNode");
var InvokeDynamicInsnNode       = Java.type("org.objectweb.asm.tree.InvokeDynamicInsnNode");
var JumpInsnNode                = Java.type("org.objectweb.asm.tree.JumpInsnNode");
var LabelNode                   = Java.type("org.objectweb.asm.tree.LabelNode");
var LdcInsnNode                 = Java.type("org.objectweb.asm.tree.LdcInsnNode");
var LineNumberNode              = Java.type("org.objectweb.asm.tree.LineNumberNode");
var LocalVariableAnnotationNode = Java.type("org.objectweb.asm.tree.LocalVariableAnnotationNode");
var LocalVariableNode           = Java.type("org.objectweb.asm.tree.LocalVariableNode");
var LookupSwitchInsnNode        = Java.type("org.objectweb.asm.tree.LookupSwitchInsnNode");
var MethodInsnNode              = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var MethodNode                  = Java.type("org.objectweb.asm.tree.MethodNode");
var MultiANewArrayInsnNode      = Java.type("org.objectweb.asm.tree.MultiANewArrayInsnNode");
var ParameterNode               = Java.type("org.objectweb.asm.tree.ParameterNode");
var TableSwitchInsnNode         = Java.type("org.objectweb.asm.tree.TableSwitchInsnNode");
var TryCatchBlockNode           = Java.type("org.objectweb.asm.tree.TryCatchBlockNode");
var TypeAnnotationNode          = Java.type("org.objectweb.asm.tree.TypeAnnotationNode");
var TypeInsnNode                = Java.type("org.objectweb.asm.tree.TypeInsnNode");
var VarInsnNode                 = Java.type("org.objectweb.asm.tree.VarInsnNode");

function log(str) {
    print("[OptiForge] " + str);
}

function logClS(str) {
    log("[Start] [Class] " + str);
}

function logClE(str) {
    log("[End] [Class] " + str);
}

function logMdS(str) {
    log("[Start] [Method] " + str);
}

function logMdE(str) {
    log("[End] [Method] " + str);
}

function logNdS(str) {
    log("[Start] [Node] " + str);
}

function logNdE(str) {
    log("[End] [Node] " + str);
}

function initializeCoreMod() {
    return {
        "ClientChunkProvider_func_212849_a_": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.client.multiplayer.ClientChunkProvider",
                "methodName": "func_212849_a_",
                "methodDesc": "(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;"
            },
            "transformer": function (mn) {
                logMdS(mn.name);
                for (var iterator = mn.instructions.iterator(); iterator.hasNext();) {
                    var ain = iterator.next();
                    if (ain.getOpcode() == Opcodes.ALOAD && ain.getNext().getOpcode() == Opcodes.ARETURN) {
                        logNdS(ain.getOpcode() + " " + ain.var);
                        mn.instructions.insertBefore(ain, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/eventbus/api/IEventBus;"));
                        mn.instructions.insertBefore(ain, new TypeInsnNode(Opcodes.NEW, "net/minecraftforge/event/world/ChunkEvent$Load"));
                        mn.instructions.insertBefore(ain, new InsnNode(Opcodes.DUP));
                        mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, ain.var));
                        mn.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraftforge/event/world/ChunkEvent$Load", "<init>", "(Lnet/minecraft/world/chunk/IChunk;)V", false));
                        mn.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/eventbus/api/IEventBus", "post", "(Lnet/minecraftforge/eventbus/api/Event;)Z", true));
                        mn.instructions.insertBefore(ain, new InsnNode(Opcodes.POP));
                        logNdE(ain.getOpcode() + " " + ain.var);
                    }
                }
                logMdE(mn.name);
                return mn;
            }
        }
    };
}