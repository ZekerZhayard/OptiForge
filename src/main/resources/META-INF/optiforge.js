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
        "BlockState": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.block.BlockState"
            },
            "transformer": function (cn) {
                logClS(cn.name);
                cn.interfaces.add("net/minecraftforge/common/extensions/IForgeBlockState");
                logClE(cn.name);
                return cn;
            }
        },
        "ActiveRenderInfo": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.client.renderer.ActiveRenderInfo"
            },
            "transformer": function (cn) {
                logClS(cn.name);
                var mn = new MethodNode(Opcodes.ACC_PUBLIC, "getBlockAtCamera", "()Lnet/minecraft/block/BlockState;", null, null);
                mn.visitVarInsn(Opcodes.ALOAD, 0);
                mn.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/ActiveRenderInfo", "getBlockState", "()Lnet/minecraft/block/BlockState;", false);
                mn.visitInsn(Opcodes.ARETURN);
                cn.methods.add(mn);
                logClE(cn.name);
                return cn;
            }
        },
        "BlockModelRenderer": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.client.renderer.BlockModelRenderer"
            },
            "transformer": function (cn) {
                logClS(cn.name);
                var mnArray = [
                    new MethodNode(Opcodes.ACC_PUBLIC, "renderModel", "(Lnet/minecraft/world/IEnviromentBlockReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZLjava/util/Random;JLnet/minecraftforge/client/model/data/IModelData;)Z", null, null),
                    new MethodNode(Opcodes.ACC_PUBLIC, "renderModelSmooth", "(Lnet/minecraft/world/IEnviromentBlockReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZLjava/util/Random;JLnet/minecraftforge/client/model/data/IModelData;)Z", null, null),
                    new MethodNode(Opcodes.ACC_PUBLIC, "renderModelFlat", "(Lnet/minecraft/world/IEnviromentBlockReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZLjava/util/Random;JLnet/minecraftforge/client/model/data/IModelData;)Z", null, null)
                ];
                var funcArray = [
                    "func_217631_a",
                    "func_217634_b",
                    "func_217635_c"
                ];
                for (var i = 0; i < mnArray.length; i++) {
                    for (var j = 0; j < 6; j++) {
                        mnArray[i].visitVarInsn(Opcodes.ALOAD, j);
                    }
                    mnArray[i].visitVarInsn(Opcodes.ILOAD, 6);
                    mnArray[i].visitVarInsn(Opcodes.ALOAD, 7);
                    mnArray[i].visitVarInsn(Opcodes.LLOAD, 8);
                    mnArray[i].visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/BlockModelRenderer", funcArray[i], "(Lnet/minecraft/world/IEnviromentBlockReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZLjava/util/Random;J)Z", false);
                    mnArray[i].visitInsn(Opcodes.IRETURN);
                    cn.methods.add(mnArray[i]);
                }
                logClE(cn.name);
                return cn;
            }
        },
        "FogRenderer": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.client.renderer.FogRenderer"
            },
            "transformer": function (cn) {
                logClS(cn.name);
                var mn = new MethodNode(Opcodes.ACC_PUBLIC, "setupFog", "(Lnet/minecraft/client/renderer/ActiveRenderInfo;IF)V", null, null);
                mn.visitVarInsn(Opcodes.ALOAD, 0);
                mn.visitVarInsn(Opcodes.ALOAD, 1);
                mn.visitVarInsn(Opcodes.ILOAD, 2);
                mn.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/FogRenderer", "func_217618_a", "(Lnet/minecraft/client/renderer/ActiveRenderInfo;I)V", false);
                mn.visitInsn(Opcodes.RETURN);
                cn.methods.add(mn);
                for (var iterator0 = cn.methods.iterator(); iterator0.hasNext();) {
                    var mn = iterator0.next();
                    if (mn.name == "func_217618_a") {
                        logMdS(mn.name);
                        for (var iterator1 = mn.instructions.iterator(); iterator1.hasNext();) {
                            var ain0 = iterator1.next();
                            if (ain0.getOpcode() == Opcodes.GETSTATIC && ain0.owner == "net/optifine/reflect/Reflector" && ain0.name == "ForgeHooksClient_getFogDensity" && ain0.desc == "Lnet/optifine/reflect/ReflectorMethod;") {
                                logNdS(ain0.getOpcode() + " " + ain0.owner + " " + ain0.name);
                                do {
                                    var ain1 = ain0.getNext();
                                    mn.instructions.remove(ain0);
                                    ain0 = ain1;
                                } while (!(ain0.getOpcode() == Opcodes.INVOKESTATIC && ain0.owner == "net/optifine/reflect/Reflector" && ain0.name == "callFloat" && ain0.desc == "(Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)F"));
                                mn.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.ALOAD, 0));
                                mn.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.ALOAD, 0));
                                mn.instructions.insertBefore(ain0, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/FogRenderer", "field_205104_n", "Lnet/minecraft/client/renderer/GameRenderer;"));
                                mn.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.ALOAD, 1));
                                mn.instructions.insertBefore(ain0, new InsnNode(Opcodes.FCONST_0));
                                mn.instructions.insertBefore(ain0, new LdcInsnNode(0.1));
                                mn.instructions.insertBefore(ain0, new InsnNode(Opcodes.D2F));
                                mn.instructions.insertBefore(ain0, new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/client/ForgeHooksClient", "getFogDensity", "(Lnet/minecraft/client/renderer/FogRenderer;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/ActiveRenderInfo;FF)F", false));
                                mn.instructions.remove(ain0);
                                logNdE(ain0.getOpcode() + " " + ain0.owner + " " + ain0.name);
                            } else if (ain0.getOpcode() == Opcodes.GETSTATIC && ain0.owner == "net/optifine/reflect/Reflector" && ain0.name == "ForgeHooksClient_onFogRender" && ain0.desc == "Lnet/optifine/reflect/ReflectorMethod;") {
                                logNdS(ain0.getOpcode() + " " + ain0.owner + " " + ain0.name);
                                do {
                                    var ain2 = ain0.getNext();
                                    mn.instructions.remove(ain0);
                                    ain0 = ain2;
                                } while (!(ain0.getOpcode() == Opcodes.INVOKESTATIC && ain0.owner == "net/optifine/reflect/Reflector" && ain0.name == "callVoid" && ain0.desc == "(Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)V"));
                                mn.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.ALOAD, 0));
                                mn.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.ALOAD, 0));
                                mn.instructions.insertBefore(ain0, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/FogRenderer", "field_205104_n", "Lnet/minecraft/client/renderer/GameRenderer;"));
                                mn.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.ALOAD, 1));
                                mn.instructions.insertBefore(ain0, new InsnNode(Opcodes.FCONST_0));
                                mn.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.ILOAD, 2));
                                mn.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.FLOAD, 7));
                                mn.instructions.insertBefore(ain0, new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/client/ForgeHooksClient", "onFogRender", "(Lnet/minecraft/client/renderer/FogRenderer;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/ActiveRenderInfo;FIF)V", false));
                                mn.instructions.remove(ain0);
                                logNdE(ain0.getOpcode() + " " + ain0.owner + " " + ain0.name);
                            }
                        }
                        logMdE(mn.name);
                    }
                }
                logClE(cn.name);
                return cn;
            }
        },
        "GameRenderer": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.client.renderer.GameRenderer"
            },
            "transformer": function (cn) {
                logClS(cn.name);
                for (var iterator0 = cn.methods.iterator(); iterator0.hasNext();) {
                    var mn = iterator0.next();
                    if (mn.name == "func_215311_a") {
                        logMdS(mn.name);
                        for (var iterator1 = mn.instructions.iterator(); iterator1.hasNext();) {
                            var ain0 = iterator1.next();
                            if (ain0.getOpcode() == Opcodes.GETSTATIC && ain0.owner == "net/optifine/reflect/Reflector" && ain0.name == "ForgeHooksClient_getFOVModifier" && ain0.desc == "Lnet/optifine/reflect/ReflectorMethod;") {
                                logNdS(ain0.getOpcode() + " " + ain0.owner + " " + ain0.name);
                                do {
                                    var ain1 = ain0.getNext();
                                    mn.instructions.remove(ain0);
                                    ain0 = ain1;
                                } while (!(ain0.getOpcode() == Opcodes.INVOKESTATIC && ain0.owner == "net/optifine/reflect/Reflector" && ain0.name == "callDouble" && ain0.desc == "(Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)D"));
                                mn.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.ALOAD, 0));
                                mn.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.ALOAD, 1));
                                mn.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.FLOAD, 2));
                                mn.instructions.insertBefore(ain0, new InsnNode(Opcodes.F2D));
                                mn.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.DLOAD, 4));
                                mn.instructions.insertBefore(ain0, new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/client/ForgeHooksClient", "getFOVModifier", "(Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/ActiveRenderInfo;DD)D", false));
                                mn.instructions.remove(ain0);
                                logNdE(ain0.getOpcode() + " " + ain0.owner + " " + ain0.name);
                            }
                        }
                        logMdE(mn.name);
                    } else if (mn.name == "func_181560_a") {
                        logMdS(mn.name);
                        for (var iterator2 = mn.instructions.iterator(); iterator2.hasNext();) {
                            var ain2 = iterator2.next();
                            if (ain2.getOpcode() == Opcodes.GETSTATIC && ain2.owner == "net/optifine/reflect/Reflector" && ain2.name == "ForgeHooksClient_onDrawBlockHighlight" && ain2.desc == "Lnet/optifine/reflect/ReflectorMethod;") {
                                logNdS(ain2.getOpcode() + " " + ain2.owner + " " + ain2.name);
                                do {
                                    var ain3 = ain2.getNext();
                                    mn.instructions.remove(ain2);
                                    ain2 = ain3;
                                } while (!(ain2.getOpcode() == Opcodes.INVOKESTATIC && ain2.owner == "net/optifine/reflect/Reflector" && ain2.name == "callBoolean" && ain2.desc == "(Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Z"));
                                mn.instructions.insertBefore(ain2, new VarInsnNode(Opcodes.ALOAD, 5));
                                mn.instructions.insertBefore(ain2, new VarInsnNode(Opcodes.ALOAD, 8));
                                mn.instructions.insertBefore(ain2, new VarInsnNode(Opcodes.ALOAD, 0));
                                mn.instructions.insertBefore(ain2, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/GameRenderer", "field_78531_r", "Lnet/minecraft/client/Minecraft;"));
                                mn.instructions.insertBefore(ain2, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71476_x", "Lnet/minecraft/util/math/RayTraceResult;"));
                                mn.instructions.insertBefore(ain2, new InsnNode(Opcodes.ICONST_0));
                                mn.instructions.insertBefore(ain2, new VarInsnNode(Opcodes.FLOAD, 1));
                                mn.instructions.insertBefore(ain2, new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/client/ForgeHooksClient", "onDrawBlockHighlight", "(Lnet/minecraft/client/renderer/WorldRenderer;Lnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/util/math/RayTraceResult;IF)Z", false));
                                mn.instructions.remove(ain2);
                                logNdE(ain2.getOpcode() + " " + ain2.owner + " " + ain2.name);
                            }
                        }
                        logMdE(mn.name);
                    }
                }
                logClE(cn.name);
                return cn;
            }
        },
        "Matrix4f": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.client.renderer.Matrix4f"
            },
            "transformer": function (cn) {
                logClS(cn.name);
                var mn = new MethodNode(Opcodes.ACC_PUBLIC, "get", "(II)F", null, null);
                mn.visitVarInsn(Opcodes.ALOAD, 0);
                mn.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/renderer/Matrix4f", "field_195888_a", "[F");
                mn.visitVarInsn(Opcodes.ILOAD, 1);
                mn.visitInsn(Opcodes.ICONST_4);
                mn.visitVarInsn(Opcodes.ILOAD, 2);
                mn.visitInsn(Opcodes.IMUL);
                mn.visitInsn(Opcodes.IADD);
                mn.visitInsn(Opcodes.FALOAD);
                mn.visitInsn(Opcodes.FRETURN);
                cn.methods.add(mn);
                logClE(cn.name);
                return cn;
            }
        },
        "ChunkRenderDispatcher": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher"
            },
            "transformer": function (cn) {
                logClS(cn.name);
                var mn = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "(ZI)V", null, null);
                mn.visitVarInsn(Opcodes.ALOAD, 0);
                mn.visitVarInsn(Opcodes.ILOAD, 1);
                mn.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/client/renderer/chunk/ChunkRenderDispatcher", "<init>", "(Z)V", false);
                mn.visitInsn(Opcodes.RETURN);
                cn.methods.add(mn);
                logClE(cn.name);
                return cn;
            }
        },
        "FaceBakery": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.client.renderer.model.FaceBakery"
            },
            "transformer": function (cn) {
                logClS(cn.name);
                var mn0 = new MethodNode(Opcodes.ACC_PUBLIC, "makeBakedQuad", "(Lnet/minecraft/client/renderer/Vector3f;Lnet/minecraft/client/renderer/Vector3f;Lnet/minecraft/client/renderer/model/BlockPartFace;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/minecraft/util/Direction;Lnet/minecraft/client/renderer/texture/ISprite;Lnet/minecraft/client/renderer/model/BlockPartRotation;Z)Lnet/minecraft/client/renderer/model/BakedQuad;", null, null);
                for (var i = 0; i < 8; i++) {
                    mn0.visitVarInsn(Opcodes.ALOAD, i);
                }
                mn0.visitVarInsn(Opcodes.ILOAD, 8);
                mn0.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/model/FaceBakery", "func_217648_a", "(Lnet/minecraft/client/renderer/Vector3f;Lnet/minecraft/client/renderer/Vector3f;Lnet/minecraft/client/renderer/model/BlockPartFace;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/minecraft/util/Direction;Lnet/minecraft/client/renderer/texture/ISprite;Lnet/minecraft/client/renderer/model/BlockPartRotation;Z)Lnet/minecraft/client/renderer/model/BakedQuad;", false);
                mn0.visitInsn(Opcodes.ARETURN);
                cn.methods.add(mn0);
                var mn1 = new MethodNode(Opcodes.ACC_PUBLIC, "rotateVertex", "(Lnet/minecraft/client/renderer/Vector3f;Lnet/minecraft/util/Direction;ILnet/minecraftforge/common/model/ITransformation;)I", null, null);
                mn1.visitVarInsn(Opcodes.ALOAD, 0);
                mn1.visitVarInsn(Opcodes.ALOAD, 1);
                mn1.visitVarInsn(Opcodes.ALOAD, 2);
                mn1.visitVarInsn(Opcodes.ILOAD, 3);
                mn1.visitVarInsn(Opcodes.ALOAD, 4);
                mn1.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/client/renderer/model/ModelRotation");
                mn1.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/model/FaceBakery", "func_199335_a", "(Lnet/minecraft/client/renderer/Vector3f;Lnet/minecraft/util/Direction;ILnet/minecraft/client/renderer/model/ModelRotation;)I", false);
                mn1.visitInsn(Opcodes.IRETURN);
                cn.methods.add(mn1);
                logClE(cn.name);
                return cn;
            }
        },
        "ItemOverrideList": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.client.renderer.model.ItemOverrideList"
            },
            "transformer": function (cn) {
                logClS(cn.name);
                var mn = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "(Lnet/minecraft/client/renderer/model/ModelBakery;Lnet/minecraft/client/renderer/model/IUnbakedModel;Ljava/util/function/Function;Ljava/util/function/Function;Ljava/util/List;Lnet/minecraft/client/renderer/vertex/VertexFormat;)V", "(Lnet/minecraft/client/renderer/model/ModelBakery;Lnet/minecraft/client/renderer/model/IUnbakedModel;Ljava/util/function/Function<Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/model/IUnbakedModel;>;Ljava/util/function/Function<Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;>;Ljava/util/List<Lnet/minecraft/client/renderer/model/ItemOverride;>;Lnet/minecraft/client/renderer/vertex/VertexFormat;)V", null);
                mn.visitVarInsn(Opcodes.ALOAD, 0);
                mn.visitVarInsn(Opcodes.ALOAD, 1);
                mn.visitVarInsn(Opcodes.ALOAD, 2);
                mn.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/client/renderer/model/BlockModel");
                mn.visitVarInsn(Opcodes.ALOAD, 3);
                mn.visitVarInsn(Opcodes.ALOAD, 5);
                mn.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/client/renderer/model/ItemOverrideList", "<init>", "(Lnet/minecraft/client/renderer/model/ModelBakery;Lnet/minecraft/client/renderer/model/BlockModel;Ljava/util/function/Function;Ljava/util/List;)V", false);
                mn.visitInsn(Opcodes.RETURN);
                cn.methods.add(mn);
                logClE(cn.name);
                return cn;
            }
        },
        "ModelBakery": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.client.renderer.model.ModelBakery"
            },
            "transformer": function (cn) {
                logClS(cn.name);
                var mn = new MethodNode(Opcodes.ACC_PUBLIC, "getBakedModel", "(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/texture/ISprite;Ljava/util/function/Function;Lnet/minecraft/client/renderer/vertex/VertexFormat;)Lnet/minecraft/client/renderer/model/IBakedModel;", "(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/texture/ISprite;Ljava/util/function/Function<Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;>;Lnet/minecraft/client/renderer/vertex/VertexFormat;)Lnet/minecraft/client/renderer/model/IBakedModel;", null);
                mn.visitAnnotation("Ljavax/annotation/Nullable;", true);
                mn.visitVarInsn(Opcodes.ALOAD, 0);
                mn.visitVarInsn(Opcodes.ALOAD, 1);
                mn.visitVarInsn(Opcodes.ALOAD, 2);
                mn.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/model/ModelBakery", "func_217845_a", "(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/texture/ISprite;)Lnet/minecraft/client/renderer/model/IBakedModel;", false);
                mn.visitInsn(Opcodes.ARETURN);
                cn.methods.add(mn);
                logClE(cn.name);
                return cn;
            }
        },
        "TileEntityRendererDispatcher": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher"
            },
            "transformer": function (cn) {
                logClS(cn.name);
                var mn = new MethodNode(Opcodes.ACC_PUBLIC, "drawBatch", "()V", null, null);
                mn.visitVarInsn(Opcodes.ALOAD, 0);
                mn.visitInsn(Opcodes.ICONST_0);
                mn.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher", "drawBatch", "(I)V", false);
                mn.visitInsn(Opcodes.RETURN);
                cn.methods.add(mn);
                logClE(cn.name);
                return cn;
            }
        },
        "ClientWorld": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.client.world.ClientWorld"
            },
            "transformer": function (cn) {
                logClS(cn.name);
                for (var iterator0 = cn.methods.iterator(); iterator0.hasNext();) {
                    var mn = iterator0.next();
                    if (mn.name == "func_217418_a" || mn.name == "lambda$tickEntities$1") {
                        logMdS(mn.name);
                        for (var iterator1 = mn.instructions.iterator(); iterator1.hasNext();) {
                            var ain0 = iterator1.next();
                            if (ain0.getOpcode() == Opcodes.INVOKEVIRTUAL && ain0.owner == "net/minecraft/entity/Entity" && ain0.name == "func_70071_h_" && ain0.desc == "()V") {
                                logNdS(ain0.getOpcode() + " " + ain0.owner + " " + ain0.name);
                                var ln0 = new LabelNode();
                                var i = ain0.getPrevious().var;
                                mn.instructions.insertBefore(ain0, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/Entity", "canUpdate", "()Z", false));
                                mn.instructions.insertBefore(ain0, new JumpInsnNode(Opcodes.IFEQ, ln0));
                                mn.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.ALOAD, i));
                                mn.instructions.insert(ain0, ln0);
                                logNdE(ain0.getOpcode() + " " + ain0.owner + " " + ain0.name);
                            }
                        }
                        logMdE(mn.name);
                    } else if (mn.name == "func_217409_a") {
                        logMdS(mn.name);
                        for (var iterator2 = mn.instructions.iterator(); iterator2.hasNext();) {
                            var ain1 = iterator2.next();
                            if (ain1.getOpcode() == Opcodes.GETFIELD && ain1.owner == "net/minecraft/client/world/ClientWorld" && ain1.name == "field_147483_b" && ain1.desc == "Ljava/util/List;") {
                                logNdS(ain1.getOpcode() + " " + ain1.owner + " " + ain1.name);
                                ain1.desc = "Ljava/util/Set;";
                                logNdE(ain1.getOpcode() + " " + ain1.owner + " " + ain1.name);
                            } else if (ain1.getOpcode() == Opcodes.INVOKEINTERFACE && ain1.owner == "java/util/List" && ain1.name == "addAll" && ain1.desc == "(Ljava/util/Collection;)Z") {
                                logNdS(ain1.getOpcode() + " " + ain1.owner + " " + ain1.name);
                                ain1.owner = "java/util/Set";
                                logNdE(ain1.getOpcode() + " " + ain1.owner + " " + ain1.name);
                            }
                        }
                        logMdE(mn.name);
                    } else if (mn.name == "func_217424_b") {
                        logMdS(mn.name);
                        var ain2 = mn.instructions.getFirst();
                        var ln1 = new LabelNode();
                        mn.instructions.insertBefore(ain2, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/eventbus/api/IEventBus;"));
                        mn.instructions.insertBefore(ain2, new TypeInsnNode(Opcodes.NEW, "net/minecraftforge/event/entity/EntityJoinWorldEvent"));
                        mn.instructions.insertBefore(ain2, new InsnNode(Opcodes.DUP));
                        mn.instructions.insertBefore(ain2, new VarInsnNode(Opcodes.ALOAD, 2));
                        mn.instructions.insertBefore(ain2, new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.insertBefore(ain2, new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraftforge/event/entity/EntityJoinWorldEvent", "<init>", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/World;)V", false));
                        mn.instructions.insertBefore(ain2, new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/eventbus/api/IEventBus", "post", "(Lnet/minecraftforge/eventbus/api/Event;)Z", true));
                        mn.instructions.insertBefore(ain2, new JumpInsnNode(Opcodes.IFEQ, ln1));
                        mn.instructions.insertBefore(ain2, new InsnNode(Opcodes.RETURN));
                        mn.instructions.insertBefore(ain2, ln1);
                        for (var iterator3 = mn.instructions.iterator(); iterator3.hasNext();) {
                            var ain3 = iterator3.next();
                            if (ain3.getOpcode() == Opcodes.INVOKEVIRTUAL && ain3.owner == "net/minecraft/client/world/ClientWorld" && ain3.name == "onEntityAdded" && ain3.desc == "(Lnet/minecraft/entity/Entity;)V") {
                                logNdS(ain3.getOpcode() + " " + ain3.owner + " " + ain3.name);
                                mn.instructions.insert(ain3, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/Entity", "onAddedToWorld", "()V", false));
                                mn.instructions.insert(ain3, new VarInsnNode(Opcodes.ALOAD, 2));
                                logNdE(ain3.getOpcode() + " " + ain3.owner + " " + ain3.name);
                            }
                        }
                        logMdE(mn.name);
                    } else if (mn.name == "func_217414_d") {
                        logMdS(mn.name);
                        for (var iterator4 = mn.instructions.iterator(); iterator4.hasNext();) {
                            var ain4 = iterator4.next();
                            if (ain4.getOpcode() == Opcodes.INVOKEVIRTUAL && ain4.owner == "net/minecraft/client/world/ClientWorld" && ain4.name == "onEntityRemoved" && ain4.desc == "(Lnet/minecraft/entity/Entity;)V") {
                                logNdS(ain4.getOpcode() + " " + ain4.owner + " " + ain4.name);
                                mn.instructions.insert(ain4, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/Entity", "onRemovedFromWorld", "()V", false));
                                mn.instructions.insert(ain4, new VarInsnNode(Opcodes.ALOAD, 1));
                                logNdE(ain4.getOpcode() + " " + ain4.owner + " " + ain4.name);
                            }
                        }
                        logMdE(mn.name);
                    } else if (mn.name == "func_184148_a" || mn.name == "func_217384_a") {
                        logMdS(mn.name);
                        var ain5 = mn.instructions.getFirst();
                        var j = mn.maxLocals;
                        var ln3 = new LabelNode();
                        var ln4 = new LabelNode();
                        mn.instructions.insertBefore(ain5, new VarInsnNode(Opcodes.ALOAD, 1));
                        mn.instructions.insertBefore(ain5, new VarInsnNode(Opcodes.ALOAD, j - 4));
                        mn.instructions.insertBefore(ain5, new VarInsnNode(Opcodes.ALOAD, j - 3));
                        mn.instructions.insertBefore(ain5, new VarInsnNode(Opcodes.FLOAD, j - 2));
                        mn.instructions.insertBefore(ain5, new VarInsnNode(Opcodes.FLOAD, j - 1));
                        mn.instructions.insertBefore(ain5, new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/event/ForgeEventFactory", "onPlaySoundAtEntity", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FF)Lnet/minecraftforge/event/entity/PlaySoundAtEntityEvent;", false));
                        mn.instructions.insertBefore(ain5, new VarInsnNode(Opcodes.ASTORE, j));
                        mn.instructions.insertBefore(ain5, new VarInsnNode(Opcodes.ALOAD, j));
                        mn.instructions.insertBefore(ain5, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/event/entity/PlaySoundAtEntityEvent", "isCanceled", "()Z", false));
                        mn.instructions.insertBefore(ain5, new JumpInsnNode(Opcodes.IFNE, ln3));
                        mn.instructions.insertBefore(ain5, new VarInsnNode(Opcodes.ALOAD, j));
                        mn.instructions.insertBefore(ain5, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/event/entity/PlaySoundAtEntityEvent", "getSound", "()Lnet/minecraft/util/SoundEvent;", false));
                        mn.instructions.insertBefore(ain5, new JumpInsnNode(Opcodes.IFNONNULL, ln4));
                        mn.instructions.insertBefore(ain5, ln3);
                        mn.instructions.insertBefore(ain5, new InsnNode(Opcodes.RETURN));
                        mn.instructions.insertBefore(ain5, ln4);
                        mn.instructions.insertBefore(ain5, new VarInsnNode(Opcodes.ALOAD, j));
                        mn.instructions.insertBefore(ain5, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/event/entity/PlaySoundAtEntityEvent", "getSound", "()Lnet/minecraft/util/SoundEvent;", false));
                        mn.instructions.insertBefore(ain5, new VarInsnNode(Opcodes.ASTORE, j - 4));
                        mn.instructions.insertBefore(ain5, new VarInsnNode(Opcodes.ALOAD, j));
                        mn.instructions.insertBefore(ain5, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/event/entity/PlaySoundAtEntityEvent", "getCategory", "()Lnet/minecraft/util/SoundCategory;", false));
                        mn.instructions.insertBefore(ain5, new VarInsnNode(Opcodes.ASTORE, j - 3));
                        mn.instructions.insertBefore(ain5, new VarInsnNode(Opcodes.ALOAD, j));
                        mn.instructions.insertBefore(ain5, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/event/entity/PlaySoundAtEntityEvent", "getVolume", "()F", false));
                        mn.instructions.insertBefore(ain5, new VarInsnNode(Opcodes.FSTORE, j - 2));
                        logMdE(mn.name);
                    }
                }
                logClE(cn.name);
                return cn;
            }
        },
        "ResourceLoadProgressGui_render": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.client.gui.ResourceLoadProgressGui",
                "methodName": "render",
                "methodDesc": "(IIF)V"
            },
            "transformer": function (mn) {
                logMdS(mn.name);
                for (var iterator = mn.instructions.iterator(); iterator.hasNext();) {
                    var ain = iterator.next();
                    if (ain.getOpcode() == Opcodes.PUTFIELD && ain.owner == "net/minecraft/client/gui/ResourceLoadProgressGui" && ain.name == "field_212978_f" && ain.desc == "F") {
                        logNdS(ain.getOpcode() + " " + ain.owner + " " + ain.name);
                        mn.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/fml/client/ClientModLoader", "renderProgressText", "()V", false));
                        logNdE(ain.getOpcode() + " " + ain.owner + " " + ain.name);
                    }
                }
                logMdE(mn.name);
                return mn;
            }
        },
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
        },
        "ItemRenderer_func_191967_a": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.client.renderer.ItemRenderer",
                "methodName": "func_191967_a",
                "methodDesc": "(Lnet/minecraft/client/renderer/model/IBakedModel;ILnet/minecraft/item/ItemStack;)V"
            },
            "transformer": function (mn) {
                logMdS(mn.name);
                var ain = mn.instructions.getFirst();
                var ln = new LabelNode();
                mn.instructions.insertBefore(ain, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/ForgeConfig", "CLIENT", "Lnet/minecraftforge/common/ForgeConfig$Client;"));
                mn.instructions.insertBefore(ain, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/common/ForgeConfig$Client", "allowEmissiveItems", "Lnet/minecraftforge/common/ForgeConfigSpec$BooleanValue;"));
                mn.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/common/ForgeConfigSpec$BooleanValue", "get", "()Ljava/lang/Object;", false));
                mn.instructions.insertBefore(ain, new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Boolean"));
                mn.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
                mn.instructions.insertBefore(ain, new JumpInsnNode(Opcodes.IFEQ, ln));
                mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 1));
                mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ILOAD, 2));
                mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 3));
                mn.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/client/ForgeHooksClient", "renderLitItem", "(Lnet/minecraft/client/renderer/ItemRenderer;Lnet/minecraft/client/renderer/model/IBakedModel;ILnet/minecraft/item/ItemStack;)V", false));
                mn.instructions.insertBefore(ain, new InsnNode(Opcodes.RETURN));
                mn.instructions.insertBefore(ain, ln);
                logMdE(mn.name);
                return mn;
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
        },
        "AtlasTexture_func_215254_a": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.client.renderer.texture.AtlasTexture",
                "methodName": "func_215254_a",
                "methodDesc": "(Lnet/minecraft/resources/IResourceManager;Ljava/lang/Iterable;Lnet/minecraft/profiler/IProfiler;)Lnet/minecraft/client/renderer/texture/AtlasTexture$SheetData;"
            },
            "transformer": function (mn) {
                logMdS(mn.name);
                for (var iterator = mn.instructions.iterator(); iterator.hasNext();) {
                    var ain0 = iterator.next();
                    if (ain0.getOpcode() == Opcodes.GETSTATIC && ain0.owner == "net/optifine/reflect/Reflector" && ain0.name == "ForgeHooksClient_onTextureStitchedPre" && ain0.desc == "Lnet/optifine/reflect/ReflectorMethod;") {
                        logNdS(ain0.getOpcode() + " " + ain0.owner + " " + ain0.name);
                        do {
                            var ain1 = ain0.getNext();
                            mn.instructions.remove(ain0);
                            ain0 = ain1;
                        } while (!(ain0.getOpcode() == Opcodes.INVOKESTATIC && ain0.owner == "net/optifine/reflect/Reflector" && ain0.name == "callVoid" && ain0.desc == "(Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)V"));
                        mn.instructions.remove(ain0);
                        logNdE(ain0.getOpcode() + " " + ain0.owner + " " + ain0.name);
                    } else if (ain0.getOpcode() == Opcodes.INVOKESTATIC && ain0.owner == "net/optifine/util/TextureUtils" && ain0.name == "getGLMaximumTextureSize" && ain0.desc == "()I") {
                        logNdS(ain0.getOpcode() + " " + ain0.owner + " " + ain0.name);
                        mn.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.insertBefore(ain0, new VarInsnNode(Opcodes.ALOAD, 4));
                        mn.instructions.insertBefore(ain0, new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/client/ForgeHooksClient", "onTextureStitchedPre", "(Lnet/minecraft/client/renderer/texture/AtlasTexture;Ljava/util/Set;)V", false));
                        logNdE(ain0.getOpcode() + " " + ain0.owner + " " + ain0.name);
                    }
                }
                logMdE(mn.name);
                return mn;
            }
        },
        "ReflectorClass_getTargetClass": {
            "target": {
                "type": "METHOD",
                "class": "net.optifine.reflect.ReflectorClass",
                "methodName": "getTargetClass",
                "methodDesc": "()Ljava/lang/Class;"
            },
            "transformer": function (mn) {
                logMdS(mn.name);
                for (var iterator = mn.instructions.iterator(); iterator.hasNext();) {
                    var ain = iterator.next();
                    if (ain.getOpcode() == Opcodes.INVOKESTATIC && ain.owner == "java/lang/Class" && ain.name == "forName" && ain.desc == "(Ljava/lang/String;)Ljava/lang/Class;") {
                        logNdS(ain.getOpcode() + " " + ain.owner + " " + ain.name);
                        mn.instructions.insertBefore(ain, new InsnNode(Opcodes.ICONST_0));
                        mn.instructions.insertBefore(ain, new LdcInsnNode(Type.getType("Lnet/optifine/reflect/ReflectorClass;")));
                        mn.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false));
                        ain.desc = "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;";
                        logNdE(ain.getOpcode() + " " + ain.owner + " " + ain.name);
                    }                    
                }
                logMdE(mn.name);
                return mn;
            }
        }
    };
}