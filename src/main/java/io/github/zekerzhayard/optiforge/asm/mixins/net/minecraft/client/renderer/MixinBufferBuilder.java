package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer;

import java.nio.ByteBuffer;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.BufferBuilder;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder {
    @Shadow
    private ByteBuffer byteBuffer;

    @Shadow
    private int nextElementBytes;

    @Inject(
        method = "Lnet/minecraft/client/renderer/BufferBuilder;getNextBuffer()Lcom/mojang/datafixers/util/Pair;",
        at = @At(
            value = "INVOKE",
            target = "Ljava/nio/ByteBuffer;clear()Ljava/nio/Buffer;",
            remap = false
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        require = 1,
        allow = 1
    )
    private void inject$getNextBuffer$0(CallbackInfoReturnable<Pair<BufferBuilder.DrawState, ByteBuffer>> cir, BufferBuilder.DrawState drawState, ByteBuffer byteBuffer) {
        byteBuffer.order(this.byteBuffer.order());
    }

    @Inject(
        method = "Lnet/minecraft/client/renderer/BufferBuilder;putBulkData(Ljava/nio/ByteBuffer;)V",
        at = @At(
            value = "FIELD",
            shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/renderer/BufferBuilder;vertexCount:I",
            opcode = Opcodes.PUTFIELD
        ),
        require = 1,
        allow = 1
    )
    private void inject$putBulkData$0(ByteBuffer buffer, CallbackInfo ci) {
        this.nextElementBytes += buffer.limit();
    }
}
