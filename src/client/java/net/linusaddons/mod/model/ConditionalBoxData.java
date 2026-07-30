package net.linusaddons.mod.model;

import net.linusaddons.mod.manager.ConditionalRenderBox;
import net.linusaddons.mod.manager.TentacleBoxState;
import net.linusaddons.mod.manager.TentacleCondition;
import net.linusaddons.mod.utils.render.RenderColor;
import net.linusaddons.mod.utils.render.WorldRenderUtils;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Plain data class that mirrors one entry in {@code conditional_boxes.json}.
 * Converted to a live {@link ConditionalRenderBox} via {@link #toRenderBox()}.
 *
 * JSON shape:
 * <pre>
 * {
 *   "label":  "Left safe",
 *   "center": [-114.5, 68.5, -137.0],
 *   "sizeX":  1.0,          // optional, default 1.0
 *   "sizeY":  1.0,          // optional, default 1.0
 *   "sizeZ":  1.0,          // optional, default 1.0
 *   "color":  [85, 255, 85, 136],  // [R, G, B, A]  A optional, default 255
 *   "style":  "OUTLINE",    // OUTLINE | FILLED | BOTH | NONE
 *   "conditions": [
 *     { "type": "MUST_BE_EMPTY",   "box": "1" },
 *     { "type": "MAX_SIZE_MEDIUM", "box": "3" },
 *     { "type": "MAX_SIZE_MINI",   "box": "2" },
 *     { "type": "OCCUPIED",        "box": "4" },
 *     { "type": "MIN_SIZE",        "box": "5", "size": "BIG" },
 *     { "type": "EXACT_SIZE",      "box": "7", "size": "MEDIUM" }
 *   ]
 * }
 * </pre>
 */
public record ConditionalBoxData(
        String label,
        double[] center,
        double sizeX,
        double sizeY,
        double sizeZ,
        int[] color,
        String style,
        List<ConditionData> conditions
) {

    /** One condition entry inside the JSON "conditions" array. */
    public record ConditionData(
            String type,
            String box,
            String size   // only used for MIN_SIZE and EXACT_SIZE
    ) {}

    // -----------------------------------------------------------------------
    // Conversion
    // -----------------------------------------------------------------------

    /**
     * Builds a live {@link ConditionalRenderBox} from this data object.
     * Returns {@code null} (and logs nothing — callers handle logging) if
     * any required field is missing or invalid.
     */
    public ConditionalRenderBox toRenderBox() {
        if (label == null || center == null || center.length < 3) return null;

        Vec3 centerVec = new Vec3(center[0], center[1], center[2]);

        RenderColor renderColor = parseColor();
        WorldRenderUtils.RenderStyle renderStyle = parseStyle();

        List<TentacleCondition> parsedConditions = new ArrayList<>();
        if (conditions != null) {
            for (ConditionData cond : conditions) {
                TentacleCondition tc = parseCondition(cond);
                if (tc != null) parsedConditions.add(tc);
            }
        }

        return new ConditionalRenderBox(
                label,
                centerVec,
                sizeX <= 0 ? 1.0 : sizeX,
                sizeY <= 0 ? 1.0 : sizeY,
                sizeZ <= 0 ? 1.0 : sizeZ,
                renderColor,
                renderStyle,
                parsedConditions
        );
    }

    private RenderColor parseColor() {
        if (color == null || color.length < 3) return RenderColor.fromArgb(0x8855FF55);
        int a = color.length >= 4 ? color[3] : 255;
        return new RenderColor(color[0], color[1], color[2], a);
    }

    private WorldRenderUtils.RenderStyle parseStyle() {
        if (style == null) return WorldRenderUtils.RenderStyle.OUTLINE;
        return switch (style.toUpperCase()) {
            case "SOLID" -> WorldRenderUtils.RenderStyle.SOLID;
            case "BOTH"   -> WorldRenderUtils.RenderStyle.BOTH;
            default       -> WorldRenderUtils.RenderStyle.OUTLINE;
        };
    }

    private static TentacleCondition parseCondition(ConditionData cond) {
        if (cond == null || cond.type() == null || cond.box() == null) return null;
        return switch (cond.type().toUpperCase()) {
            case "MUST_BE_EMPTY"   -> TentacleCondition.mustBeEmpty(cond.box());
            case "MAX_SIZE_MEDIUM" -> TentacleCondition.maxSizeMedium(cond.box());
            case "MAX_SIZE_MINI"   -> TentacleCondition.maxSizeMini(cond.box());
            case "OCCUPIED"        -> TentacleCondition.occupied(cond.box());
            case "MIN_SIZE"        -> {
                TentacleBoxState s = parseSizeEnum(cond.size());
                yield s != null ? TentacleCondition.minSize(s, cond.box()) : null;
            }
            case "EXACT_SIZE"      -> {
                TentacleBoxState s = parseSizeEnum(cond.size());
                yield s != null ? TentacleCondition.exactSize(s, cond.box()) : null;
            }
            default -> null;
        };
    }

    private static TentacleBoxState parseSizeEnum(String s) {
        if (s == null) return null;
        return switch (s.toUpperCase()) {
            case "EMPTY"  -> TentacleBoxState.EMPTY;
            case "MINI"   -> TentacleBoxState.MINI;
            case "MEDIUM" -> TentacleBoxState.MEDIUM;
            case "BIG"    -> TentacleBoxState.BIG;
            default       -> null;
        };
    }
}
