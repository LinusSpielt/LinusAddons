package net.linusaddons.mod.events.impl.skyblock;

import net.linusaddons.mod.events.Event;

public record KuudraChestRerollEvent(
        int windowId,
        RerollType rerollType
) implements Event {

    public enum RerollType {
        ITEMS,
        SHARD
    }
}
