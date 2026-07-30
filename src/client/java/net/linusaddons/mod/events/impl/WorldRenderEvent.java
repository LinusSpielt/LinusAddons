package net.linusaddons.mod.events.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import net.linusaddons.mod.events.Event;
import net.linusaddons.mod.utils.render.RenderColor;
import net.linusaddons.mod.utils.render.WorldRenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
public record WorldRenderEvent(
        MultiBufferSource.BufferSource consumer,
        PoseStack matrices,
        Matrix4f projectionMatrix,
        Vec3 cameraPos,
        Quaternionf cameraOrientation,
        DeltaTracker tickCounter
) implements Event {

    public void drawFilled(AABB box, boolean throughWalls, RenderColor color) {
        WorldRenderUtils.drawFilled(matrices, consumer, cameraPos, box, throughWalls, color);
    }

    public void drawOutline(AABB box, boolean throughWalls, RenderColor color) {
        WorldRenderUtils.drawOutline(matrices, consumer, cameraPos, box, throughWalls, color);
    }

    public void drawFilledCircle(Vec3 center, float radius, int segments, boolean throughWalls, RenderColor color) {
        WorldRenderUtils.drawFilledCircle(matrices, consumer, cameraPos, center, radius, segments, throughWalls, color);
    }

    public void drawCircleOutline(Vec3 center, float radius, int segments, boolean throughWalls, RenderColor color) {
        WorldRenderUtils.drawCircleOutline(matrices, consumer, cameraPos, center, radius, segments, throughWalls, color);
    }

    public void drawStyledBox(@NotNull AABB box, boolean throughWalls, @NotNull RenderColor color, WorldRenderUtils.RenderStyle style) {
        WorldRenderUtils.drawStyledBox(matrices, consumer, cameraPos, box, throughWalls, color, style);
    }

    public void drawText(Vec3 pos, Component text, float scale, boolean throughWalls, RenderColor color) {
        WorldRenderUtils.drawText(matrices, consumer, cameraPos, cameraOrientation, pos, text, scale, throughWalls, color);
    }

    public void drawRotatedBoxOutline(Vec3[] corners, boolean throughWalls, RenderColor color) {
        WorldRenderUtils.drawRotatedBoxOutline(matrices, consumer, cameraPos, corners, throughWalls, color);
    }

    public void drawBeam(Vec3 pos, int height, boolean throughWalls, RenderColor color) {
        WorldRenderUtils.drawBeam(matrices, consumer, cameraPos, pos, height, throughWalls, color);
    }

    public void drawFilledWithBeam(AABB box, int height, boolean throughWalls, RenderColor color) {
        WorldRenderUtils.drawFilled(matrices, consumer, cameraPos, box, throughWalls, color);
        Vec3 center = box.getCenter();
        WorldRenderUtils.drawBeam(matrices, consumer, cameraPos,
                center.add(0, box.maxY - center.y(), 0), height, throughWalls, color);
    }

    public void drawStyledWithBeam(AABB box, int height, boolean throughWalls, RenderColor color, WorldRenderUtils.RenderStyle style) {
        WorldRenderUtils.drawStyledBox(matrices, consumer, cameraPos, box, throughWalls, color, style);
        Vec3 center = box.getCenter();
        WorldRenderUtils.drawBeam(matrices, consumer, cameraPos,
                center.add(0, box.maxY - center.y(), 0), height, throughWalls, color);
    }

    public void drawHitbox(Entity entity, boolean throughWalls, RenderColor color) {
        WorldRenderUtils.drawHitBox(matrices, consumer, cameraPos, entity, tickCounter, throughWalls, color);
    }

    public void drawStyledHitbox(@NotNull Entity entity, boolean throughWalls,
                                  @NotNull RenderColor color, WorldRenderUtils.RenderStyle style) {
        WorldRenderUtils.drawStyledHitBox(matrices, consumer, cameraPos, entity, tickCounter, throughWalls, color, style);
    }
}
