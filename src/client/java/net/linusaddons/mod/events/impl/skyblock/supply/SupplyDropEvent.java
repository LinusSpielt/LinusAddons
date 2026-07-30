package net.linusaddons.mod.events.impl.skyblock.supply;

import net.linusaddons.mod.events.Event;
import org.jetbrains.annotations.NotNull;

public record SupplyDropEvent(
        @NotNull String playerName
) implements Event {}