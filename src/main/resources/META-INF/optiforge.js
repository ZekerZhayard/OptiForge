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
        "Matrix4f": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.client.renderer.Matrix4f"
            },
            "transformer": function (cn) {
                logClS(cn.name);
                var mn = new MethodNode(Opcodes.ACC_PUBLIC, "get", "(II)F", null, null);
                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/Matrix4f", "field_195888_a", "[F"));
                mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
                mn.instructions.add(new InsnNode(Opcodes.ICONST_4));
                mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
                mn.instructions.add(new InsnNode(Opcodes.IMUL));
                mn.instructions.add(new InsnNode(Opcodes.IADD));
                mn.instructions.add(new InsnNode(Opcodes.FALOAD));
                mn.instructions.add(new InsnNode(Opcodes.FRETURN));
                cn.methods.add(mn);
                logClE(cn.name);
                return cn;
            }
        },
        "ModelRotation_getMatrixVec": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.client.renderer.model.ModelRotation",
                "methodName": "getMatrixVec",
                "methodDesc": "()Ljavax/vecmath/Matrix4f;"
            },
            "transformer": function (mn) {
                logMdS(mn.name);
                for (var iterator = mn.instructions.iterator(); iterator.hasNext();) {
                    var ain = iterator.next();
                    if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL && ain.owner == "net/minecraftforge/common/model/TRSRTransformation" && ain.name == "getMatrix" && ain.desc == "()Ljavax/vecmath/Matrix4f;") {
                        logNdS(ain.getOpcode() + " " + ain.owner + " " + ain.name);
                        ain.name = "getMatrixVec";
                        logNdE(ain.getOpcode() + " " + ain.owner + " " + ain.name);
                    }
                }
                logMdE(mn.name);
                return mn;
            }
        }
    };
}