package io.github.zekerzhayard.optiforge.asm.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.EnumSet;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraftforge.accesstransformer.service.AccessTransformerService;
import sun.misc.Unsafe;

public class ModuleUtils {
    private final static MethodHandles.Lookup IMPL_LOOKUP = getImplLookup();

    private static MethodHandles.Lookup getImplLookup() {
        try {
            // Get theUnsafe
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);

            // Get IMPL_LOOKUP
            Field implLookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            return (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(implLookupField), unsafe.staticFieldOffset(implLookupField));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @SuppressWarnings("unchecked")
    public static void bootstrap() throws Throwable {
        ModuleLayer.boot().findModule("java.base").ifPresent(m0 -> Launcher.INSTANCE.findLayerManager().flatMap(mlm -> mlm.getLayer(IModuleLayerManager.Layer.BOOT).flatMap(ml -> ml.findModule("org.apache.commons.lang3"))).ifPresent(m1 -> {
            try {
                addOpens(m0, "java.lang", m1);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }));

        EnumSet<ILaunchPluginService.Phase> set = (EnumSet<ILaunchPluginService.Phase>) IMPL_LOOKUP.findStaticGetter(AccessTransformerService.class, "YAY", EnumSet.class).invokeWithArguments();
        set.clear();
        set.add(ILaunchPluginService.Phase.AFTER);
    }

    public static void addExports(Module from, String pkg, Module to) throws Throwable {
        if (to == null) {
            MethodHandle implAddExportsToAllUnnamedMH = IMPL_LOOKUP.findVirtual(Module.class, "implAddExportsToAllUnnamed", MethodType.methodType(void.class, String.class));
            implAddExportsToAllUnnamedMH.invokeWithArguments(from, pkg);
        } else {
            MethodHandle implAddExportsMH = IMPL_LOOKUP.findVirtual(Module.class, "implAddExports", MethodType.methodType(void.class, String.class, Module.class));
            implAddExportsMH.invokeWithArguments(from, pkg, to);
        }
    }

    public static void addOpens(Module from, String pkg, Module to) throws Throwable {
        if (to == null) {
            MethodHandle implAddOpensToAllUnnamedMH = IMPL_LOOKUP.findVirtual(Module.class, "implAddOpensToAllUnnamed", MethodType.methodType(void.class, String.class));
            implAddOpensToAllUnnamedMH.invokeWithArguments(from, pkg);
        } else {
            MethodHandle implAddOpensMH = IMPL_LOOKUP.findVirtual(Module.class, "implAddOpens", MethodType.methodType(void.class, String.class, Module.class));
            implAddOpensMH.invokeWithArguments(from, pkg, to);
        }
    }
}
