package net.linusaddons.mod.events.impl.skyblock;

import net.linusaddons.mod.events.Event;
import net.linusaddons.mod.model.kuudra.KuudraPhase;

public record KuudraPhaseChangeEvent(
        KuudraPhase previousPhase,
        KuudraPhase currentPhase,
        long phaseDurationMillis
) implements Event {

    public boolean isEnteringKuudra() {
        return previousPhase == KuudraPhase.NONE && currentPhase != KuudraPhase.NONE;
    }

    public boolean isExitingKuudra() {
        return previousPhase != KuudraPhase.NONE && currentPhase == KuudraPhase.NONE;
    }

    public boolean isRunCompleted() {
        return currentPhase == KuudraPhase.COMPLETED;
    }
}
