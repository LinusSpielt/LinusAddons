package net.linusaddons.mod.events.impl.skyblock.supply;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.linusaddons.mod.events.Cancellable;
import net.linusaddons.mod.events.Event;
import net.linusaddons.mod.model.spot.PreSpot;
import net.linusaddons.mod.model.spot.SupplyPosition;

@Data
@RequiredArgsConstructor
public class SupplyProgressEvent implements Event, Cancellable {

    private final SupplyPosition position;
    private final PreSpot spot;
    private final String progressText;
    private final int currentProgress;

    private boolean cancelled;
}
