package net.linusaddons.mod.events.impl.skyblock.supply;

import net.linusaddons.mod.events.Event;
import net.linusaddons.mod.model.pearl.PearlWaypoint;
import net.linusaddons.mod.model.pearl.WaypointArea;
import org.jetbrains.annotations.NotNull;

/**
 * Fired exactly once, on the tick the supply progress crosses the target
 * tick of the currently selected {@link PearlWaypoint} - i.e. the perfect
 * moment to throw the pearl. Carries the yaw/pitch needed to look at the
 * target at the moment this fires, so listeners don't need to recompute it.
 */
public record PearlThrowReadyEvent(
        @NotNull WaypointArea area,
        @NotNull PearlWaypoint waypoint,
        double x,
        double y
) implements Event {
}
