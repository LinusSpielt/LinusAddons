package net.linusaddons.mod.manager;

import net.linusaddons.mod.utils.render.RenderColor;
import net.linusaddons.mod.utils.render.WorldRenderUtils;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;


public record ConditionalRenderBox(
        String label,
        Vec3 center,
        double sizeX,
        double sizeY,
        double sizeZ,
        RenderColor color,
        WorldRenderUtils.RenderStyle style,
        List<TentacleCondition> conditions
) {

    public ConditionalRenderBox(
            String label,
            Vec3 center,
            RenderColor color,
            WorldRenderUtils.RenderStyle style,
            List<TentacleCondition> conditions
    ) {
        this(label, center, 1.0, 1.0, 1.0, color, style, conditions);
    }

    public boolean shouldRender(Map<String, TentacleBoxState> stateMap) {
        for (TentacleCondition condition : conditions) {
            if (!condition.test(stateMap)) return false;
        }
        return true;
    }

    public AABB toAABB() {
        double hx = sizeX / 2.0;
        double hy = sizeY / 2.0;
        double hz = sizeZ / 2.0;
        return new AABB(
                center.x - hx, center.y - hy, center.z - hz,
                center.x + hx, center.y + hy, center.z + hz
        );
    }
}