package net.linusaddons.mod.events.dispatcher;

import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.events.EventBus;
import net.linusaddons.mod.events.dispatcher.detector.DirectionDetector;
import net.linusaddons.mod.events.dispatcher.detector.FreshDetector;
import net.linusaddons.mod.events.dispatcher.detector.SupplyDetector;
import net.linusaddons.mod.events.impl.ChatReceivedEvent;
import net.linusaddons.mod.events.impl.ClientTickEvent;
import net.linusaddons.mod.events.impl.ScreenClickEvent;
import net.linusaddons.mod.events.impl.TitleReceivedEvent;
import net.linusaddons.mod.events.impl.skyblock.KuudraPhaseChangeEvent;
import net.linusaddons.mod.events.impl.skyblock.SkyblockAreaChangeEvent;
import net.linusaddons.mod.manager.KuudraStateManager;
import net.linusaddons.mod.manager.SupplyStateManager;
import net.linusaddons.mod.utils.ScoreboardUtils;
import org.jetbrains.annotations.NotNull;

import static net.linusaddons.mod.LAConstants.*;

@Slf4j
public class KuudraEventsDispatcher extends EventDispatcher {

    private static final long SKYBLOCK_EXIT_CONFIRMATION_MS = 1200L;

    private KuudraStateManager kuudraStateManager;

    private final SupplyDetector supplyDetector = new SupplyDetector(SupplyStateManager.get());
    private final FreshDetector freshDetector = new FreshDetector();
    private final DirectionDetector directionDetector = new DirectionDetector();

    private volatile boolean onSkyBlock = false;
    private volatile String currentArea = "";
    private volatile long pendingSkyBlockExitSinceMillis = -1L;

    private volatile long lastTickCount = 0L;

    @Override
    public void start() {
        kuudraStateManager = KuudraStateManager.get();

        subscribe(ClientTickEvent.class, this::onClientTick);
        subscribe(ChatReceivedEvent.class, this::onChat);
        subscribe(ScreenClickEvent.class, this::onScreenClick);
        subscribe(TitleReceivedEvent.class, this::onTitleReceived);
        subscribe(KuudraPhaseChangeEvent.class, this::onKuudraPhaseChange);
    }

    private void onClientTick(@NotNull ClientTickEvent event) {
        lastTickCount = event.tickCount();
        directionDetector.detect(event, kuudraStateManager.context(), EventBus::post);

        if (!event.isInGame() || !event.isNthTick(DEFAULT_CHECK_INTERVAL_TICKS)) {
            return;
        }

        {
            boolean scoreboardReportsSkyBlock = ScoreboardUtils.hasTitle(SKYBLOCK_AREA_ID);
            if (scoreboardReportsSkyBlock) {
                clearPendingSkyBlockExit();
            } else if (onSkyBlock) {
                armPendingSkyBlockExit();
                if (!isSkyBlockExitConfirmed()) {
                    return;
                }
            }

            boolean resolvedOnSkyBlock = scoreboardReportsSkyBlock || (onSkyBlock && !isSkyBlockExitConfirmed());
            boolean wasOnSkyBlock = onSkyBlock;
            onSkyBlock = resolvedOnSkyBlock;
            if (wasOnSkyBlock != resolvedOnSkyBlock) {
                EventBus.post(new SkyblockAreaChangeEvent(
                        resolvedOnSkyBlock, currentArea,
                        resolvedOnSkyBlock ? "joined" : "left")
                );

                log.info("SkyBlock status: {}", resolvedOnSkyBlock ? "joined" : "left");
            }

            if (!onSkyBlock) {
                if (!currentArea.isEmpty()) {
                    currentArea = "";
                }

                return;
            }

            String newArea = ScoreboardUtils.getArea();
            // Ignore transient empty area reads (e.g. during scoreboard format changes between phases).
            // A genuinely empty area while on SkyBlock just means the scoreboard line is momentarily absent.
            if (newArea.isEmpty()) {
                return;
            }
            if (!newArea.equals(currentArea)) {
                String previousArea = currentArea;
                currentArea = newArea;

                EventBus.post(new SkyblockAreaChangeEvent(
                        onSkyBlock, previousArea, newArea));
                log.info("Area: {} -> {}", previousArea, newArea);
            }
        }
    }

    private void armPendingSkyBlockExit() {
        if (pendingSkyBlockExitSinceMillis < 0L) {
            pendingSkyBlockExitSinceMillis = System.currentTimeMillis();
        }
    }

    private void clearPendingSkyBlockExit() {
        pendingSkyBlockExitSinceMillis = -1L;
    }

    private boolean isSkyBlockExitConfirmed() {
        return pendingSkyBlockExitSinceMillis >= 0L
                && (System.currentTimeMillis() - pendingSkyBlockExitSinceMillis) >= SKYBLOCK_EXIT_CONFIRMATION_MS;
    }

    private void onChat(@NotNull ChatReceivedEvent event) {
        if (!onSkyBlock) return;

        String message = event.getStrippedMessage();

        if (!isOutOfKuudra()) {
            supplyDetector.detect(event, message, EventBus::post);
            freshDetector.detect(event, message, EventBus::post);
        }
    }

    private void onScreenClick(@NotNull ScreenClickEvent event) {
        if (!onSkyBlock) return;

    }

    private void onTitleReceived(TitleReceivedEvent event) {
        if (!onSkyBlock || isOutOfKuudra()) return;

        supplyDetector.detectProgress(event, EventBus::post);
    }

    private void onKuudraPhaseChange(KuudraPhaseChangeEvent event) {
        directionDetector.reset();
    }

    private boolean isOutOfKuudra() {
        return !currentArea.toLowerCase().contains(KUUDRA_AREA_ID.toLowerCase());
    }

    @Override
    public void stop() {}
}
