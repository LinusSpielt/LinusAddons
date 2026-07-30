package net.linusaddons.mod.events.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.linusaddons.mod.events.Cancellable;
import net.linusaddons.mod.events.Event;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;

@Getter
@RequiredArgsConstructor
public class ScreenClickedEvent implements Event, Cancellable {

    private final AbstractContainerScreen<?> screen;
    private final MouseButtonEvent ButtonEvent;
    private final boolean doubleClick;

    @Setter
    private boolean cancelled;
}
