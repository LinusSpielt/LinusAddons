package net.linusaddons.mod.events.dispatcher.detector;

import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.events.Event;
import net.linusaddons.mod.events.impl.ChatReceivedEvent;
import net.linusaddons.mod.events.impl.TitleReceivedEvent;
import net.linusaddons.mod.events.impl.skyblock.supply.SupplyDropEvent;
import net.linusaddons.mod.events.impl.skyblock.supply.SupplyPickupEvent;
import net.linusaddons.mod.events.impl.skyblock.supply.SupplyPlaceEvent;
import net.linusaddons.mod.events.impl.skyblock.supply.SupplyProgressEvent;
import net.linusaddons.mod.manager.SupplyStateManager;
import net.linusaddons.mod.model.spot.PreSpot;
import net.linusaddons.mod.utils.StringUtils;
import net.linusaddons.mod.utils.TextFormatUtil;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.regex.Matcher;

import static net.linusaddons.mod.LAConstants.*;

@Slf4j
public final class SupplyDetector {

    private static final Minecraft client = Minecraft.getInstance();

    private final SupplyStateManager supplyStateManager;

    public SupplyDetector(@NotNull SupplyStateManager supplyStateManager) {
        this.supplyStateManager = supplyStateManager;
    }

    public void detect(@NotNull ChatReceivedEvent event, @NotNull String message, @NotNull Consumer<Event> postEvent) {
        if (message.contains(SUPPLY_PICKUP_MESSAGE)) {
            var player = client.player;
            if (player == null) return;

            var playerPos = player.position();
            if (playerPos == null) return;

            postEvent.accept(new SupplyPickupEvent(
                    PreSpot.fromPlayerPosition(playerPos),
                    supplyStateManager.findSupplyNear(playerPos, 3),
                    System.currentTimeMillis()
            ));
            return;
        }

        Matcher supplyPlacedMatcher = SUPPLY_PLACE_PATTERN.matcher(message);
        if (supplyPlacedMatcher.find()) {

            String supplyCount = supplyPlacedMatcher.group(2);
            String formattedMessage = TextFormatUtil.toLegacyString(event.getText());
            double timeSeconds = supplyStateManager.getElapsedTimeMillis() / 1000.0;

            postEvent.accept(new SupplyPlaceEvent(
                    formattedMessage,
                    StringUtils.extractFormattedPlayerName(formattedMessage),
                    Integer.parseInt(supplyCount),
                    timeSeconds
            ));
            return;
        }

        Matcher supplyDroppedMatcher = SUPPLY_DROPPED_PATTERN.matcher(message);
        if (supplyDroppedMatcher.find()) {
            String droppedBy = StringUtils.extractFormattedPlayerName(TextFormatUtil.toLegacyString(event.getText()));
            postEvent.accept(new SupplyDropEvent(droppedBy));
        }

        if (message.contains("You moved and the Chest slipped out of your hands!")) {
            var player = client.player;
            if (player == null) return;
            supplyStateManager.setSupplyProgress(0);
            postEvent.accept(new SupplyDropEvent(player.getName().getString()));
        }
    }

    public void detectProgress(@NotNull TitleReceivedEvent event, Consumer<Event> postEvent) {
        var strippedMessage = event.getStrippedMessage();
        Matcher progressMatcher = SUPPLY_PROGRESS_PATTERN.matcher(strippedMessage);
        if (progressMatcher.matches()) {
            int progress = Integer.parseInt(progressMatcher.group(1));
            supplyStateManager.setSupplyProgress(progress);
            var player = client.player;
            if (player == null) return;

            var playerPos = player.position();
            if (playerPos == null) return;

            var progressEvent = new SupplyProgressEvent(
                    supplyStateManager.findSupplyNear(playerPos, 3),
                    PreSpot.fromPlayerPosition(playerPos),
                    event.getMessage(),
                    progress
            );

            postEvent.accept(progressEvent);
            if (progressEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }
}
