package net.linusaddons.mod.events.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.linusaddons.mod.events.Cancellable;
import net.linusaddons.mod.events.Event;
import net.minecraft.network.protocol.Packet;


@Getter
@RequiredArgsConstructor
public class PacketSendEvent implements Event, Cancellable {

    private final Packet<?> packet;

    @Setter
    private boolean cancelled;
}