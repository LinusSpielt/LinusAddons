package net.linusaddons.mod.events.impl.skyblock.supply;

import net.linusaddons.mod.events.Event;
import net.linusaddons.mod.model.spot.PreSpot;
import net.linusaddons.mod.model.spot.SupplyPosition;

public record SupplyPickupEvent(
        PreSpot spot,
        SupplyPosition position,
        long pickupAt
) implements Event {}
