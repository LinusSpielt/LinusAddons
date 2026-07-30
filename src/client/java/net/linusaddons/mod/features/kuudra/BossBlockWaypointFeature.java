package net.linusaddons.mod.features.kuudra;

import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.config.categories.LinusAddonsConfig;
import net.linusaddons.mod.events.impl.WorldRenderEvent;
import net.linusaddons.mod.features.KuudraFeature;
import net.linusaddons.mod.model.kuudra.KuudraPhase;
import net.linusaddons.mod.utils.render.RenderColor;
import net.linusaddons.mod.utils.render.WorldRenderUtils;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class BossBlockWaypointFeature extends KuudraFeature {

    private static final RenderColor BLOCKCOLOR = new RenderColor(168, 0, 0, 255);
    private static final AABB WAYPOINT_BOX =
            new AABB(-129, 12, -104,
                    -128, 13, -103);

    public BossBlockWaypointFeature() {
        super(
                "bossBlockWaypoint",
                "Boss Block Waypoint",
                () -> LinusAddonsConfig.bossBlockWaypoint,
                KuudraPhase.BOSS
        );
    }

    @Override
    protected void onKuudraActivate() {

        subscribe(WorldRenderEvent.class, this::onRender);
    }

    private void onRender(@NotNull WorldRenderEvent event) {
        event.drawStyledBox(WAYPOINT_BOX, true,
                BLOCKCOLOR,
                WorldRenderUtils.RenderStyle.BOTH
        );
    }
}



