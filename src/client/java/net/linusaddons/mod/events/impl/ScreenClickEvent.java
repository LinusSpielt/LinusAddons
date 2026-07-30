package net.linusaddons.mod.events.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.linusaddons.mod.events.Cancellable;
import net.linusaddons.mod.events.Event;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;

@Getter
@RequiredArgsConstructor
public class ScreenClickEvent implements Event, Cancellable {

    private final AbstractContainerScreen<?> screen;
    private final Slot slot;
    private final ContainerInput actionType;

    @Setter
    private boolean cancelled;
}
