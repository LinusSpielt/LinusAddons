package net.linusaddons.mod.events.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.linusaddons.mod.events.Cancellable;
import net.linusaddons.mod.events.Event;
import net.minecraft.client.gui.screens.Screen;

@Getter
@RequiredArgsConstructor
public class ScreenOpenEvent implements Event, Cancellable {

    private final Screen screen;

    @Setter
    private boolean cancelled;

}