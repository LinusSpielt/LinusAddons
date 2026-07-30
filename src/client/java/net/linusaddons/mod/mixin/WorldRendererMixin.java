package net.linusaddons.mod.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.linusaddons.mod.events.EventBus;
import net.linusaddons.mod.events.impl.WorldRenderEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {

    @Unique
    private final MultiBufferSource.BufferSource la$immediate =
            MultiBufferSource.immediate(new ByteBufferBuilder(1536 * 20));

    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void la$onWorldRender(
            GraphicsResourceAllocator allocator,
            DeltaTracker tickCounter,
            boolean renderBlockOutline,
            CameraRenderState cameraState,
            Matrix4fc projectionMatrix,
            GpuBufferSlice fogBuffer,
            Vector4f fogColor,
            boolean renderSky,
            ChunkSectionsToRender chunkSections,
            CallbackInfo ci
    ) {
        PoseStack matrices = new PoseStack();
        matrices.mulPose(new org.joml.Matrix4f(projectionMatrix));

        EventBus.post(new WorldRenderEvent(
                la$immediate,
                matrices,
                new org.joml.Matrix4f(projectionMatrix),
                cameraState.pos,
                cameraState.orientation,
                tickCounter
        ));

        la$immediate.endBatch();
    }
}
