package io.github.zekerzhayard.optiforge.asm.mixins.net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ItemRenderer.class)
@SuppressWarnings("deprecation")
public abstract class MixinItemRenderer {
    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/ItemRenderer;doRender(Lnet/minecraft/entity/item/ItemEntity;DDDFF)V",
        slice = @Slice(
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/model/IBakedModel;isGui3d()Z"
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/model/IBakedModel;getItemCameraTransforms()Lnet/minecraft/client/renderer/model/ItemCameraTransforms;"
        ),
        require = 3,
        allow = 3
    )
    private net.minecraft.client.renderer.model.ItemCameraTransforms redirect$doRender$0(IBakedModel iBakedModel) {
        return null;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/ItemRenderer;doRender(Lnet/minecraft/entity/item/ItemEntity;DDDFF)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/model/ItemCameraTransforms;ground:Lnet/minecraft/client/renderer/model/ItemTransformVec3f;"
        ),
        require = 3,
        allow = 3
    )
    private net.minecraft.client.renderer.model.ItemTransformVec3f redirect$doRender$1(net.minecraft.client.renderer.model.ItemCameraTransforms itemCameraTransforms) {
        return null;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/ItemRenderer;doRender(Lnet/minecraft/entity/item/ItemEntity;DDDFF)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/model/ItemTransformVec3f;scale:Lnet/minecraft/client/renderer/Vector3f;"
        ),
        require = 3,
        allow = 3
    )
    private Vector3f redirect$doRender$2(net.minecraft.client.renderer.model.ItemTransformVec3f ground) {
        return null;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/ItemRenderer;doRender(Lnet/minecraft/entity/item/ItemEntity;DDDFF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/Vector3f;getX()F"
        ),
        require = 1,
        allow = 1
    )
    private float redirect$doRender$3(Vector3f scale) {
        return 1.0F;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/ItemRenderer;doRender(Lnet/minecraft/entity/item/ItemEntity;DDDFF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/Vector3f;getY()F"
        ),
        require = 1,
        allow = 1
    )
    private float redirect$doRender$4(Vector3f scale) {
        return 1.0F;
    }

    @Redirect(
        method = "Lnet/minecraft/client/renderer/entity/ItemRenderer;doRender(Lnet/minecraft/entity/item/ItemEntity;DDDFF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/Vector3f;getZ()F"
        ),
        require = 1,
        allow = 1
    )
    private float redirect$doRender$5(Vector3f scale) {
        return 1.0F;
    }
}
