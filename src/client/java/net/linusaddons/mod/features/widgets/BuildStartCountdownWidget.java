package net.linusaddons.mod.features.widgets;

import net.linusaddons.mod.events.impl.ClientTickEvent;
import net.linusaddons.mod.events.impl.skyblock.supply.SupplyPlaceEvent;
import net.linusaddons.mod.hud.component.HudLine;
import net.linusaddons.mod.hud.element.HudAnchor;
import net.linusaddons.mod.hud.element.HudWidget;
import net.linusaddons.mod.config.categories.LinusAddonsConfig;
import net.linusaddons.mod.manager.KuudraStateManager;
import net.linusaddons.mod.model.kuudra.KuudraPhase;
import net.linusaddons.mod.utils.BuildProgressOverlayUtil;
import net.linusaddons.mod.utils.CountdownLagCompensationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuildStartCountdownWidget extends HudWidget {

    private static final long CLIENT_TICK_INTERVAL_MS = 50L;

    private final KuudraStateManager stateManager = KuudraStateManager.get();

    private final HudLine countdownLine;

    private long countdownEndMillis = -1L;
    private long lastCountdownTickMillis = -1L;

    public BuildStartCountdownWidget() {
        super(
                "buildStartCountdown",
                "Build Start Countdown",
                430.0f, 320.0f,
                1.0f,
                HudAnchor.TOP_LEFT
        );

        countdownLine = HudLine.of("§c0.00s")
                .showWhen(this::hasActiveCountdown);

        setEnabledSupplier(() -> LinusAddonsConfig.buildStartCountdownOverlay);
        setVisibilityCondition(() -> {
            KuudraPhase phase = stateManager.phase();
            return phase == KuudraPhase.SUPPLIES || phase == KuudraPhase.BUILD;
        });

        setExampleLines(List.of(
                HudLine.of("§a3.42s")
        ));
    }

    @Override
    protected void onActivate() {
        countdownEndMillis = -1L;
        lastCountdownTickMillis = -1L;

        clearLines();
        addLines(countdownLine);

        updateDisplay();

        subscribe(ClientTickEvent.class, this::onTick);
        subscribe(SupplyPlaceEvent.class, this::onSupplyPlace);
    }

    @Override
    protected void onDeactivate() {
        countdownEndMillis = -1L;
        lastCountdownTickMillis = -1L;
        updateDisplay();
    }

    private void onTick(@NotNull ClientTickEvent event) {
        if (!event.isInGame()) return;

        long now = System.currentTimeMillis();
        if (countdownEndMillis > 0L) {
            countdownEndMillis = CountdownLagCompensationUtil.applyLagCompensation(
                    countdownEndMillis,
                    lastCountdownTickMillis,
                    now,
                    CLIENT_TICK_INTERVAL_MS
            );
            lastCountdownTickMillis = now;
        } else {
            lastCountdownTickMillis = -1L;
        }

        if (hasActiveCountdown()) {
            updateDisplay();
            return;
        }

        if (countdownEndMillis > 0) {
            countdownEndMillis = -1L;
            lastCountdownTickMillis = -1L;
            updateDisplay();
        }
    }

    private void onSupplyPlace(@NotNull SupplyPlaceEvent event) {
        if (!LinusAddonsConfig.buildStartCountdownOverlay) return;
        if (stateManager.phase() != KuudraPhase.SUPPLIES) return;
        if (event.currentSupply() < 6) return;

        countdownEndMillis = System.currentTimeMillis() + BuildProgressOverlayUtil.BUILD_START_COUNTDOWN_MS;
        lastCountdownTickMillis = System.currentTimeMillis();
        updateDisplay();
    }

    private void updateDisplay() {
        if (!hasActiveCountdown()) {
            markDimensionsDirty();
            return;
        }

        long remainingMs = Math.max(0L, countdownEndMillis - System.currentTimeMillis());
        String color = BuildProgressOverlayUtil.getCountdownColor(remainingMs);
        if (color == null) color = "§c";

        countdownLine.text(String.format("%s%ss", color, BuildProgressOverlayUtil.formatCountdownSeconds(remainingMs)));
        markDimensionsDirty();
    }

    private boolean hasActiveCountdown() {
        if (!LinusAddonsConfig.buildStartCountdownOverlay) return false;
        return countdownEndMillis > System.currentTimeMillis();
    }
}