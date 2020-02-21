package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.entity;

import java.util.Map;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityRendererManager.class)
public abstract class MixinEntityRendererManager {
    @Final
    @Shadow
    public Map<EntityType<?>, EntityRenderer<?>> renderers;

    @SuppressWarnings("deprecation")
    public void validateRendererExistence() {
        for(EntityType<?> entitytype : Registry.ENTITY_TYPE) {
            if (entitytype != EntityType.PLAYER && !this.renderers.containsKey(entitytype)) {
                throw new IllegalStateException("No renderer registered for " + Registry.ENTITY_TYPE.getKey(entitytype));
            }
        }
    }
}
