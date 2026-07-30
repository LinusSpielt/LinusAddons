package net.linusaddons.mod.features.kuudra;

import net.linusaddons.mod.events.impl.ChatReceivedEvent;
import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.config.categories.LinusAddonsConfig;
import net.linusaddons.mod.manager.KuudraStateManager;
import net.linusaddons.mod.model.kuudra.KuudraPhase;
import net.linusaddons.mod.features.Feature;


@Slf4j
public class HideRendCooldownFeature extends Feature {

    private final KuudraStateManager stateManager = KuudraStateManager.get();

    public HideRendCooldownFeature() {
        super("hideRendCooldown", "Hide Rend",
                () -> LinusAddonsConfig.hideRendCooldown
        );
    }

    @Override
    protected void onActivate() {
        subscribe(ChatReceivedEvent.class, this::onChat);
    }

    private void onChat(ChatReceivedEvent event) {
        if (stateManager.phase() != KuudraPhase.BUILD) return;

        String message = event.getStrippedMessage();

        if (message.equals("This ability is on cooldown for 1s.") || message.equals("This ability is on cooldown for 2s.")) {
            event.setCancelled(true);
        }
    }
}