package net.linusaddons.mod.events.impl.skyblock.supply;

import net.linusaddons.mod.events.Event;

public record SupplyPlaceEvent(
        String originalMessage,
        String playerName,
        int currentSupply,
        double placedAt
) implements Event {}
