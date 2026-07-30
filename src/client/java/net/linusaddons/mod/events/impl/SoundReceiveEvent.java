package net.linusaddons.mod.events.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.linusaddons.mod.events.Cancellable;
import net.linusaddons.mod.events.Event;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;


@Getter
@RequiredArgsConstructor
public class SoundReceiveEvent implements Event, Cancellable {

    private final ClientboundSoundPacket packet;

    @Setter
    private boolean cancelled;
}