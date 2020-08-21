package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.world;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.Difficulty;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.ClientWorldInfo.class)
public abstract class MixinClientWorld$ClientWorldInfo {
    @Shadow
    private Difficulty field_239153_j_;

    @Inject(
        method = "Lnet/minecraft/client/world/ClientWorld$ClientWorldInfo;func_239156_a_(Lnet/minecraft/world/Difficulty;)V",
        at = @At("HEAD"),
        require = 1,
        allow = 1
    )
    private void inject$func_239156_a_$0(Difficulty difficultyIn, CallbackInfo ci) {
        ForgeHooks.onDifficultyChange(difficultyIn, this.field_239153_j_);
    }
}
