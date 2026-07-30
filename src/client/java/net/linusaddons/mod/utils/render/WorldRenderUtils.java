package net.linusaddons.mod.utils.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.experimental.UtilityClass;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import java.util.Optional;

@UtilityClass
public class WorldRenderUtils {

    private static final Minecraft mc = Minecraft.getInstance();


    public static void drawFilled(
            @NotNull PoseStack matrices, MultiBufferSource.BufferSource consumer,
            @NotNull Vec3 cameraPos, @NotNull AABB box, boolean throughWalls,
            @NotNull RenderColor color
    ) {
        matrices.pushPose();
        Vec3 cam = cameraPos;
        matrices.translate(-cam.x, -cam.y, -cam.z);

        VertexConsumer buf = consumer.getBuffer(
                throughWalls ? Layers.BOX_FILLED_NO_CULL : Layers.BOX_FILLED);
        PrimitiveHelper.addChainedFilledBoxVertices(
                matrices.last(), buf,
                (float) box.minX, (float) box.minY, (float) box.minZ,
                (float) box.maxX, (float) box.maxY, (float) box.maxZ,
                color.r, color.g, color.b, color.a);
        matrices.popPose();
    }

    public static void drawOutline(
            @NotNull PoseStack matrices, MultiBufferSource.BufferSource consumer,
            @NotNull Vec3 cameraPos, @NotNull AABB box, boolean throughWalls,
            @NotNull RenderColor color
    ) {
        matrices.pushPose();
        Vec3 cam = cameraPos;
        matrices.translate(-cam.x, -cam.y, -cam.z);

        VertexConsumer buf = consumer.getBuffer(
                throughWalls ? Layers.BOX_OUTLINE_NO_CULL : Layers.BOX_OUTLINE);
        PrimitiveHelper.renderLineBox(
                matrices.last(), buf, box,
                color.r, color.g, color.b, color.a, 3f);
        matrices.popPose();
    }

    public static void drawFilledCircle(
            @NotNull PoseStack matrices, MultiBufferSource.BufferSource consumer,
            @NotNull Vec3 cameraPos, @NotNull Vec3 center,
            float radius, int segments, boolean throughWalls, @NotNull RenderColor color
    ) {
        if (segments < 3) segments = 3;
        matrices.pushPose();
        Vec3 cam = cameraPos;
        matrices.translate(-cam.x, -cam.y, -cam.z);

        PoseStack.Pose entry = matrices.last();
        VertexConsumer buf = consumer.getBuffer(
                throughWalls ? Layers.CIRCLE_FILLED_NO_CULL : Layers.CIRCLE_FILLED);

        for (int i = 0; i < segments; i++) {
            double a1 = Math.PI * 2.0 * i / segments;
            double a2 = Math.PI * 2.0 * (i + 1) / segments;
            buf.addVertex(entry, (float) center.x, (float) center.y, (float) center.z)
                    .setColor(color.r, color.g, color.b, color.a);
            buf.addVertex(entry,
                    (float) (center.x + Math.cos(a1) * radius),
                    (float) center.y,
                    (float) (center.z + Math.sin(a1) * radius))
                    .setColor(color.r, color.g, color.b, color.a);
            buf.addVertex(entry,
                    (float) (center.x + Math.cos(a2) * radius),
                    (float) center.y,
                    (float) (center.z + Math.sin(a2) * radius))
                    .setColor(color.r, color.g, color.b, color.a);
        }
        matrices.popPose();
    }

    public static void drawCircleOutline(
            @NotNull PoseStack matrices, MultiBufferSource.BufferSource consumer,
            @NotNull Vec3 cameraPos, @NotNull Vec3 center,
            float radius, int segments, boolean throughWalls, @NotNull RenderColor color
    ) {
        if (segments < 3) segments = 3;

        matrices.pushPose();
        Vec3 cam = cameraPos;
        matrices.translate(-cam.x, -cam.y, -cam.z);

        PoseStack.Pose pose = matrices.last();

        VertexConsumer buf = consumer.getBuffer(
                throughWalls ? Layers.CIRCLE_OUTLINE_NO_CULL : Layers.CIRCLE_OUTLINE
        );

        float nx = 0f;
        float ny = 1f;
        float nz = 0f;

        float lineWidth = 3f;

        double lastAngle = 0;
        double lastX = center.x + Math.cos(lastAngle) * radius;
        double lastZ = center.z + Math.sin(lastAngle) * radius;

        for (int i = 1; i <= segments; i++) {
            double angle = Math.PI * 2.0 * i / segments;

            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;

            buf.addVertex(pose, (float) lastX, (float) center.y, (float) lastZ)
                    .setColor(color.r, color.g, color.b, color.a)
                    .setNormal(pose, nx, ny, nz)
                    .setLineWidth(lineWidth);

            buf.addVertex(pose, (float) x, (float) center.y, (float) z)
                    .setColor(color.r, color.g, color.b, color.a)
                    .setNormal(pose, nx, ny, nz)
                    .setLineWidth(lineWidth);

            lastX = x;
            lastZ = z;
        }

        matrices.popPose();
    }

    public static void drawText(
            @NotNull PoseStack matrices, MultiBufferSource.BufferSource consumer,
            @NotNull Vec3 cameraPos, @NotNull Quaternionf cameraOrientation, @NotNull Vec3 pos, Component text,
            float scale, boolean throughWalls, @NotNull RenderColor color
    ) {
        matrices.pushPose();
        Vec3 cam = cameraPos;
        matrices.translate(pos.x - cam.x, pos.y - cam.y, pos.z - cam.z);
        matrices.mulPose(cameraOrientation);
        matrices.scale(scale, -scale, scale);

        mc.font.drawInBatch(
                text,
                -mc.font.width(text) / 2f, 0f,
                color.argb, true,
                matrices.last().pose(), consumer,
                throughWalls ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL,
                0, LightCoordsUtil.FULL_BRIGHT);

        consumer.endBatch();
        matrices.popPose();
    }

    public static void drawBeam(
            @NotNull PoseStack matrices, MultiBufferSource.BufferSource consumer,
            @NotNull Vec3 cameraPos, @NotNull Vec3 pos, int height,
            boolean throughWalls, @NotNull RenderColor color
    ) {
        drawFilled(matrices, consumer, cameraPos,
                AABB.ofSize(pos, 0.5, 0, 0.5).expandTowards(0, height, 0),
                throughWalls, color);
    }

    public static void drawStyledBox(
            @NotNull PoseStack matrices, MultiBufferSource.BufferSource consumer,
            @NotNull Vec3 cameraPos, @NotNull AABB box, boolean throughWalls,
            @NotNull RenderColor color, @NotNull RenderStyle style
    ) {
        switch (style) {
            case SOLID   -> drawFilled(matrices, consumer, cameraPos, box, throughWalls, color);
            case OUTLINE -> drawOutline(matrices, consumer, cameraPos, box, throughWalls, color);
            case BOTH    -> {
                drawFilled(matrices, consumer, cameraPos, box, throughWalls,
                        color.withOpacity(color.a * 0.5f));
                drawOutline(matrices, consumer, cameraPos, box, throughWalls, color);
            }
        }
    }

    public static void drawStyledHitBox(
            @NotNull PoseStack matrices, MultiBufferSource.BufferSource consumer,
            @NotNull Vec3 cameraPos, @NotNull Entity entity,
            @NotNull DeltaTracker tickCounter, boolean throughWalls,
            @NotNull RenderColor color, @NotNull RenderStyle style
    ) {
        drawStyledBox(matrices, consumer, cameraPos,
                getEntityBox(entity, tickCounter.getGameTimeDeltaPartialTick(true)),
                throughWalls, color, style);
    }

    public static void drawHitBox(
            @NotNull PoseStack matrices, MultiBufferSource.BufferSource consumer,
            @NotNull Vec3 cameraPos, @NotNull Entity entity,
            @NotNull DeltaTracker tickCounter, boolean throughWalls,
            @NotNull RenderColor color
    ) {
        drawOutline(matrices, consumer,cameraPos,
                getEntityBox(entity, tickCounter.getGameTimeDeltaPartialTick(true)),
                throughWalls, color);
    }

    public static void drawRotatedBoxOutline(
            @NotNull PoseStack matrices, MultiBufferSource.BufferSource consumer,
            @NotNull Vec3 cameraPos, @NotNull Vec3[] corners,
            boolean throughWalls, @NotNull RenderColor color
    ) {
        matrices.pushPose();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        VertexConsumer buf = consumer.getBuffer(
                throughWalls ? Layers.BOX_OUTLINE_NO_CULL : Layers.BOX_OUTLINE);
        PrimitiveHelper.renderRotatedLineBox(
                matrices.last(), buf, corners,
                color.r, color.g, color.b, color.a, 3f);

        matrices.popPose();
    }

    @Contract("_, _ -> new")
    private static @NotNull AABB getEntityBox(@NotNull Entity entity, float tickDelta) {
        double x = entity.xo + (entity.getX() - entity.xo) * tickDelta;
        double y = entity.yo + (entity.getY() - entity.yo) * tickDelta;
        double z = entity.zo + (entity.getZ() - entity.zo) * tickDelta;
        float hw = entity.getBbWidth() / 2f;
        float h  = entity.getBbHeight();
        return new AABB(x - hw, y, z - hw, x + hw, y + h, z + hw);
    }

    public enum RenderStyle { SOLID, OUTLINE, BOTH }

    public static final class Pipelines {

        public static final RenderPipeline FILLED_ESP = RenderPipelines.register(
                RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                        .withDepthStencilState(Optional.empty())
                        .withLocation(Identifier.fromNamespaceAndPath("linusaddons", "pipeline/filled_esp"))
                        .build());

        public static final RenderPipeline FILLED = RenderPipelines.register(
                RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                        .withLocation(Identifier.fromNamespaceAndPath("linusaddons", "pipeline/filled"))
                        .build());

        public static final RenderPipeline LINES_ESP = RenderPipelines.register(
                RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                        .withDepthStencilState(Optional.empty())
                        .withLocation(Identifier.fromNamespaceAndPath("linusaddons", "pipeline/lines_esp"))
                        .build());

        public static final RenderPipeline LINES = RenderPipelines.register(
                RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                        .withLocation(Identifier.fromNamespaceAndPath("linusaddons", "pipeline/lines"))
                        .build());

        public static final RenderPipeline CIRCLE_ESP = RenderPipelines.register(
                RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                        .withDepthStencilState(Optional.empty())
                        .withLocation(Identifier.fromNamespaceAndPath("linusaddons", "pipeline/circle_esp"))
                        .build());

        public static final RenderPipeline CIRCLE = RenderPipelines.register(
                RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                        .withLocation(Identifier.fromNamespaceAndPath("linusaddons", "pipeline/circle"))
                        .build());
    }

    public static final class Layers {

        public static final RenderType BOX_FILLED = RenderType.create(
                "linusaddons_box_filled",
                RenderSetup.builder(Pipelines.FILLED)
                        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                        .createRenderSetup());

        public static final RenderType BOX_FILLED_NO_CULL = RenderType.create(
                "linusaddons_box_filled_esp",
                RenderSetup.builder(Pipelines.FILLED_ESP)
                        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                        .createRenderSetup());

        public static final RenderType BOX_OUTLINE = RenderType.create(
                "linusaddons_box_outline",
                RenderSetup.builder(Pipelines.LINES)
                        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                        .createRenderSetup());

        public static final RenderType BOX_OUTLINE_NO_CULL = RenderType.create(
                "linusaddons_box_outline_esp",
                RenderSetup.builder(Pipelines.LINES_ESP)
                        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                        .createRenderSetup());

        public static final RenderType TRACER = RenderType.create(
                "linusaddons_tracer",
                RenderSetup.builder(Pipelines.LINES_ESP)
                        .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                        .createRenderSetup());

        public static final RenderType CIRCLE_FILLED = RenderType.create(
                "linusaddons_circle_filled",
                RenderSetup.builder(Pipelines.CIRCLE)
                        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                        .createRenderSetup());

        public static final RenderType CIRCLE_FILLED_NO_CULL = RenderType.create(
                "linusaddons_circle_filled_esp",
                RenderSetup.builder(Pipelines.CIRCLE_ESP)
                        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                        .createRenderSetup());

        public static final RenderType CIRCLE_OUTLINE = RenderType.create(
                "linusaddons_circle_outline",
                RenderSetup.builder(Pipelines.LINES)
                        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                        .createRenderSetup());

        public static final RenderType CIRCLE_OUTLINE_NO_CULL = RenderType.create(
                "linusaddons_circle_outline_esp",
                RenderSetup.builder(Pipelines.LINES_ESP)
                        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                        .createRenderSetup());
    }

    public static final class PrimitiveHelper {

        private static final int[] EDGES = {
            0,1, 1,5, 5,4, 4,0,
            3,2, 2,6, 6,7, 7,3,
            0,3, 1,2, 5,6, 4,7
        };

        public static void renderLineBox(
                @NotNull PoseStack.Pose pose, @NotNull VertexConsumer buf, @NotNull AABB box,
                float r, float g, float b, float a, float thickness
        ) {
            float x0 = (float) box.minX, y0 = (float) box.minY, z0 = (float) box.minZ;
            float x1 = (float) box.maxX, y1 = (float) box.maxY, z1 = (float) box.maxZ;
            float[] c = { x0,y0,z0, x1,y0,z0, x1,y1,z0, x0,y1,z0,
                           x0,y0,z1, x1,y0,z1, x1,y1,z1, x0,y1,z1 };

            for (int i = 0; i < EDGES.length; i += 2) {
                int a0 = EDGES[i] * 3, a1 = EDGES[i + 1] * 3;
                float dx = c[a1]   - c[a0];
                float dy = c[a1+1] - c[a0+1];
                float dz = c[a1+2] - c[a0+2];
                buf.addVertex(pose, c[a0],   c[a0+1], c[a0+2]).setColor(r,g,b,a).setNormal(pose,dx,dy,dz).setLineWidth(thickness);
                buf.addVertex(pose, c[a1],   c[a1+1], c[a1+2]).setColor(r,g,b,a).setNormal(pose,dx,dy,dz).setLineWidth(thickness);
            }
        }

        public static void renderRotatedLineBox(
                @NotNull PoseStack.Pose pose, @NotNull VertexConsumer buf,
                @NotNull Vec3[] corners,
                float r, float g, float b, float a, float thickness
        ) {
            int[] edges = {
                    0,1, 1,2, 2,3, 3,0,
                    4,5, 5,6, 6,7, 7,4,
                    0,4, 1,5, 2,6, 3,7
            };

            for (int i = 0; i < edges.length; i += 2) {
                Vec3 p0 = corners[edges[i]];
                Vec3 p1 = corners[edges[i + 1]];
                float dx = (float)(p1.x - p0.x);
                float dy = (float)(p1.y - p0.y);
                float dz = (float)(p1.z - p0.z);
                buf.addVertex(pose, (float)p0.x, (float)p0.y, (float)p0.z).setColor(r,g,b,a).setNormal(pose,dx,dy,dz).setLineWidth(thickness);
                buf.addVertex(pose, (float)p1.x, (float)p1.y, (float)p1.z).setColor(r,g,b,a).setNormal(pose,dx,dy,dz).setLineWidth(thickness);
            }
        }

        public static void addChainedFilledBoxVertices(
                @NotNull PoseStack.Pose pose, @NotNull VertexConsumer buf,
                float x0, float y0, float z0,
                float x1, float y1, float z1,
                float r, float g, float b, float a
        ) {
            Matrix4f m = pose.pose();
            // -X face
            buf.addVertex(m, x0,y0,z0).setColor(r,g,b,a);
            buf.addVertex(m, x0,y0,z1).setColor(r,g,b,a);
            buf.addVertex(m, x0,y1,z1).setColor(r,g,b,a);
            buf.addVertex(m, x0,y1,z0).setColor(r,g,b,a);
            // +X face
            buf.addVertex(m, x1,y0,z1).setColor(r,g,b,a);
            buf.addVertex(m, x1,y0,z0).setColor(r,g,b,a);
            buf.addVertex(m, x1,y1,z0).setColor(r,g,b,a);
            buf.addVertex(m, x1,y1,z1).setColor(r,g,b,a);
            // -Z face
            buf.addVertex(m, x0,y0,z0).setColor(r,g,b,a);
            buf.addVertex(m, x0,y1,z0).setColor(r,g,b,a);
            buf.addVertex(m, x1,y1,z0).setColor(r,g,b,a);
            buf.addVertex(m, x1,y0,z0).setColor(r,g,b,a);
            // +Z face
            buf.addVertex(m, x1,y0,z1).setColor(r,g,b,a);
            buf.addVertex(m, x1,y1,z1).setColor(r,g,b,a);
            buf.addVertex(m, x0,y1,z1).setColor(r,g,b,a);
            buf.addVertex(m, x0,y0,z1).setColor(r,g,b,a);
            // -Y face
            buf.addVertex(m, x0,y0,z0).setColor(r,g,b,a);
            buf.addVertex(m, x1,y0,z0).setColor(r,g,b,a);
            buf.addVertex(m, x1,y0,z1).setColor(r,g,b,a);
            buf.addVertex(m, x0,y0,z1).setColor(r,g,b,a);
            // +Y face
            buf.addVertex(m, x0,y1,z1).setColor(r,g,b,a);
            buf.addVertex(m, x1,y1,z1).setColor(r,g,b,a);
            buf.addVertex(m, x1,y1,z0).setColor(r,g,b,a);
            buf.addVertex(m, x0,y1,z0).setColor(r,g,b,a);
        }
    }
}
