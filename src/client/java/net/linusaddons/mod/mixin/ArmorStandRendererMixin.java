package net.linusaddons.mod.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.linusaddons.mod.events.EventBus;
import net.linusaddons.mod.events.impl.ArmorStandRenderEvent;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandRenderer.class)
public abstract class ArmorStandRendererMixin {

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/ArmorStandRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void la$onRenderArmorStand(
            ArmorStandRenderState renderState,
            PoseStack matrixStack,
            SubmitNodeCollector queue,
            CameraRenderState camera, CallbackInfo ci
    ) {
        ArmorStandRenderEvent event = EventBus.post(new ArmorStandRenderEvent(renderState, matrixStack, queue, camera));
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}