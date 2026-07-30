package net.linusaddons.mod.events.impl;

import net.linusaddons.mod.events.Event;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

public record ScreenDrawSlotEvent(
        AbstractContainerScreen<?> screen,
        GuiGraphicsExtractor drawContext,
        Slot slot,
        int x,
        int y
) implements Event {
}
