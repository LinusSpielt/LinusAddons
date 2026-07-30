package net.linusaddons.mod.events.dispatcher.detector;

import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.events.Event;
import net.linusaddons.mod.events.impl.ClientTickEvent;
import net.linusaddons.mod.events.impl.skyblock.KuudraDirectionChangeEvent;
import net.linusaddons.mod.model.kuudra.KuudraContext;
import net.linusaddons.mod.model.kuudra.KuudraPhase;
import net.linusaddons.mod.utils.KuudraLocationUtil;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static net.linusaddons.mod.utils.KuudraLocationUtil.SpawnDirection.UNKNOWN;

@Slf4j
public class DirectionDetector {

    private volatile KuudraLocationUtil.SpawnDirection currentDirection = UNKNOWN;

    public void detect(@NotNull ClientTickEvent event, KuudraContext context, Consumer<Event> postEvent) {
        if (!event.isInGame()) return;

        var phase = context.phase();
        if (phase != KuudraPhase.SKIP && phase != KuudraPhase.BOSS) return;

        var bossInfo = context.bossInfo();
        var kuudraEntity = bossInfo.isAlive()
                ? bossInfo.bossEntity()
                : KuudraLocationUtil.findKuudra().orElse(null);

        if (kuudraEntity == null || !kuudraEntity.isAlive()) return;
        var direction = KuudraLocationUtil.getDirection(kuudraEntity);
        if (direction != UNKNOWN && direction != currentDirection) {
            postEvent.accept(new KuudraDirectionChangeEvent(
                    currentDirection,
                    direction
            ));

            currentDirection = direction;
            log.info("Kuudra direction changed: {}", direction);
        }
    }

    public void reset() {
        currentDirection = UNKNOWN;
    }
}
