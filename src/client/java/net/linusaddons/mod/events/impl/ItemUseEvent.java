package net.linusaddons.mod.events.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.linusaddons.mod.events.Cancellable;
import net.linusaddons.mod.events.Event;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

@Getter
@Setter
@RequiredArgsConstructor
public class ItemUseEvent implements Event, Cancellable {

    private final InteractionHand hand;
    private final ItemStack itemStack;

    private boolean cancelled;

}
