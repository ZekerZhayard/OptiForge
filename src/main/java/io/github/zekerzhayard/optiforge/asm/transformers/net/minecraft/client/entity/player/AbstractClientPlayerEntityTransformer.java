package io.github.zekerzhayard.optiforge.asm.transformers.net.minecraft.client.entity.player;

import java.util.Objects;

import cpw.mods.modlauncher.api.ITransformer;
import io.github.zekerzhayard.optiforge.asm.transformers.ITransformerImpl;
import io.github.zekerzhayard.optiforge.asm.utils.ASMUtils;
import net.minecraftforge.coremod.api.ASMAPI;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.util.Bytecode;

public class AbstractClientPlayerEntityTransformer implements ITransformer<ClassNode>, ITransformerImpl {
    @Override
    public String targetClass() {
        return "net.minecraft.client.entity.player.AbstractClientPlayerEntity";
    }

    @Override
    public ClassNode transform(ClassNode input) {

        // https://github.com/MinecraftForge/MinecraftForge/commit/f169c993487609954c78103de3091d32c1d9cacc#diff-04ce24c6b791555fabaf05c3dd0ca374b42bc48bc027b9644d1e445ec03c949eL7-L8
        //
        //           f = 1.0F;
        //        }
        //
        // -      if (this.func_184587_cr() && this.func_184607_cu().func_77973_b() == Items.field_151031_f) {
        // +      if (this.func_184587_cr() && this.func_184607_cu().func_77973_b() instanceof net.minecraft.item.BowItem) {
        //           int i = this.func_184612_cw();
        //           float f1 = (float)i / 20.0F;
        //           if (f1 > 1.0F) {
        //

        ASMUtils.replaceInstance(Objects.requireNonNull(Bytecode.findMethod(input, ASMAPI.mapMethod("func_175156_o"), "()F")), "net/minecraft/item/BowItem", "field_151031_f");

        return input;
    }
}
