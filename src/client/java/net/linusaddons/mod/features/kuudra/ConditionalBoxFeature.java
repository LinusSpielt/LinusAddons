package net.linusaddons.mod.features.kuudra;

import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.config.categories.LinusAddonsConfig;
import net.linusaddons.mod.config.loader.ConditionalBoxConfigLoader;
import net.linusaddons.mod.events.impl.WorldRenderEvent;
import net.linusaddons.mod.features.KuudraFeature;
import net.linusaddons.mod.manager.ConditionalRenderBox;
import net.linusaddons.mod.manager.TentacleBoxState;
import net.linusaddons.mod.model.kuudra.KuudraPhase;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Slf4j
public class ConditionalBoxFeature extends KuudraFeature {

    private final TentacleDetectFeature tentacleDetect;
    private final ConditionalBoxConfigLoader configLoader = ConditionalBoxConfigLoader.get();

    public ConditionalBoxFeature(@NotNull TentacleDetectFeature tentacleDetect) {
        super(
                "conditionalBoxes",
                "Conditional Tentacle Boxes",
                () -> LinusAddonsConfig.ConditionalBoxes,
                KuudraPhase.SUPPLIES
        );
        this.tentacleDetect = tentacleDetect;
        this.configLoader.load();
    }

    @Override
    protected void onKuudraActivate() {
        List<ConditionalRenderBox> boxes = configLoader.getCached();
        log.debug("ConditionalBoxFeature activated with {} box(es) from config", boxes.size());
        subscribe(WorldRenderEvent.class, this::onRender);
    }

    private void onRender(@NotNull WorldRenderEvent event) {
        Map<String, TentacleBoxState> stateMap = tentacleDetect.getSpawnBoxStates();
        List<ConditionalRenderBox> boxes = configLoader.getCached();
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        var playerPos = mc.player != null ? mc.player.getPosition(partialTick) : null;

        for (ConditionalRenderBox box : boxes) {
            if (playerPos != null && LinusAddonsConfig.ConditionalBoxesRenderDist != 0 && box.center().distanceTo(playerPos) > LinusAddonsConfig.ConditionalBoxesRenderDist) continue;
            if (!box.shouldRender(stateMap)) continue;

            event.drawStyledBox(
                    box.toAABB(),
                    true,
                    box.color(),
                    box.style()
            );
        }
    }
}
