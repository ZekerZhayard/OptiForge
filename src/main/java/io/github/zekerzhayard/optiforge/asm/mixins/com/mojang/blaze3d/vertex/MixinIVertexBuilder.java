package io.github.zekerzhayard.optiforge.asm.mixins.com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraftforge.client.extensions.IForgeVertexBuilder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IVertexBuilder.class)
public interface MixinIVertexBuilder extends IForgeVertexBuilder {

}
