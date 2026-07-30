package net.linusaddons.mod.events.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.linusaddons.mod.events.Cancellable;
import net.linusaddons.mod.events.Event;
import net.minecraft.world.entity.Entity;

@Getter
@Setter
@RequiredArgsConstructor
public class AttackEvent implements Event, Cancellable {

    private final Entity target;

    private boolean cancelled;

}
