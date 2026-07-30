package net.linusaddons.mod.events.impl.skyblock;

import net.linusaddons.mod.events.Event;

public record PlayerFreshEvent(
        boolean selfFresh,
        String playerName,
        int playerEntityId,
        int buildingProgress,
        long freshAt
) implements Event {
}
