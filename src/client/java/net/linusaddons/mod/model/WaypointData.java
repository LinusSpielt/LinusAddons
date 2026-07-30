package net.linusaddons.mod.model;

import java.time.Instant;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public record WaypointData(
        Component playerName,
        Vec3 position,
        Instant expiresAt,
        boolean isUrgent
) {

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public double distanceFrom(Vec3 other) {
        return position.distanceTo(other);
    }

    public double getDurationMultiplier() {
        return isUrgent ? 0.5 : 1.0;
    }
}
