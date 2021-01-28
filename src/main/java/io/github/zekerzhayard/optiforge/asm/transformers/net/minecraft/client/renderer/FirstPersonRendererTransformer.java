package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.renderer;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.util.Bytecode;

public class FirstPersonRendererTransformer  implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.renderer.FirstPersonRenderer";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/commit/f169c993487609954c78103de3091d32c1d9cacc#diff-762345f7eeabf6d4fcde9d17e13d84c33e427ac3dd09cc92bdae459e4a86b4e4L16-L30
        //
        //           Hand hand1 = p_228396_4_.func_184600_cs();
        //           if (hand1 == Hand.MAIN_HAND) {
        //              ItemStack itemstack1 = p_228396_4_.func_184592_cb();
        // -            if (itemstack1.func_77973_b() == Items.field_222114_py && CrossbowItem.func_220012_d(itemstack1)) {
        // +            if (itemstack1.func_77973_b() instanceof CrossbowItem && CrossbowItem.func_220012_d(itemstack1)) {
        //                 flag1 = false;
        //              }
        //           }
        //        } else {
        //           ItemStack itemstack2 = p_228396_4_.func_184614_ca();
        //           ItemStack itemstack3 = p_228396_4_.func_184592_cb();
        // -         if (itemstack2.func_77973_b() == Items.field_222114_py && CrossbowItem.func_220012_d(itemstack2)) {
        // +         if (itemstack2.func_77973_b() instanceof CrossbowItem && CrossbowItem.func_220012_d(itemstack2)) {
        //              flag1 = !flag;
        //           }
        //
        // -         if (itemstack3.func_77973_b() == Items.field_222114_py && CrossbowItem.func_220012_d(itemstack3)) {
        // +         if (itemstack3.func_77973_b() instanceof CrossbowItem && CrossbowItem.func_220012_d(itemstack3)) {
        //              flag = !itemstack2.func_190926_b();
        //              flag1 = !flag;
        //           }
        //

        ASMUtils.replaceInstance(Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_228396_a_"), "(FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/entity/player/ClientPlayerEntity;I)V")), "net/minecraft/item/CrossbowItem", "field_222114_py");

        // https://github.com/MinecraftForge/MinecraftForge/commit/f169c993487609954c78103de3091d32c1d9cacc#diff-762345f7eeabf6d4fcde9d17e13d84c33e427ac3dd09cc92bdae459e4a86b4e4L53-L61
        //
        //           if (flag && !p_228405_1_.func_82150_aj()) {
        //              this.func_228401_a_(p_228405_8_, p_228405_9_, p_228405_10_, p_228405_7_, p_228405_5_, handside);
        //           }
        // -      } else if (p_228405_6_.func_77973_b() == Items.field_151098_aY) {
        // +      } else if (p_228405_6_.func_77973_b() instanceof FilledMapItem) {
        //           if (flag && this.field_187468_e.func_190926_b()) {
        //              this.func_228400_a_(p_228405_8_, p_228405_9_, p_228405_10_, p_228405_3_, p_228405_7_, p_228405_5_);
        //           } else {
        //              this.func_228402_a_(p_228405_8_, p_228405_9_, p_228405_10_, p_228405_7_, handside, p_228405_5_, p_228405_6_);
        //           }
        // -      } else if (p_228405_6_.func_77973_b() == Items.field_222114_py) {
        // +      } else if (p_228405_6_.func_77973_b() instanceof CrossbowItem) {
        //           boolean flag1 = CrossbowItem.func_220012_d(p_228405_6_);
        //           boolean flag2 = handside == HandSide.RIGHT;
        //           int i = flag2 ? 1 : -1;
        //

        MethodNode renderItemInFirstPerson = Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_228405_a_"), "(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V"));
        ASMUtils.replaceInstance(renderItemInFirstPerson, "net/minecraft/item/FilledMapItem", "field_151098_aY");
        ASMUtils.replaceInstance(renderItemInFirstPerson, "net/minecraft/item/CrossbowItem", "field_222114_py");

        return input;
    }
}
