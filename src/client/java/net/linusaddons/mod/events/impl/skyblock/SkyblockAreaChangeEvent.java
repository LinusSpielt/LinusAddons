package net.linusaddons.mod.events.impl.skyblock;

import net.linusaddons.mod.events.Event;
import org.jetbrains.annotations.NotNull;

public record SkyblockAreaChangeEvent(
        boolean onSkyBlock,
        String previousArea,
        @NotNull String newArea
) implements Event {
}
