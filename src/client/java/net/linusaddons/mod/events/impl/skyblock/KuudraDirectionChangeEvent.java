package net.linusaddons.mod.events.impl.skyblock;

import net.linusaddons.mod.events.Event;
import net.linusaddons.mod.utils.KuudraLocationUtil;

public record KuudraDirectionChangeEvent(
        KuudraLocationUtil.SpawnDirection oldDirection,
        KuudraLocationUtil.SpawnDirection newDirection
) implements Event {
}
