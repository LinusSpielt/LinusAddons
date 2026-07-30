package net.linusaddons.mod.features.kuudra;

import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.config.categories.LinusAddonsConfig;
import net.linusaddons.mod.events.impl.WorldRenderEvent;
import net.linusaddons.mod.features.KuudraFeature;
import net.linusaddons.mod.model.kuudra.KuudraPhase;
import net.linusaddons.mod.utils.render.RenderColor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class DpsWaypointsFeature extends KuudraFeature {

    private static final AABB waypointBox_1 =
            new AABB(-107, 79, -72,
                    -108, 80, -73);

    private static final AABB waypointBox_2 =
            new AABB(-111, 75, -69,
                    -110, 76, -68);

    public DpsWaypointsFeature() {
        super(
                "dpsWaypoints",
                "Dps Waypoints",
                () -> LinusAddonsConfig.dpsWaypoints,
                KuudraPhase.EATEN
        );
    }

    @Override
    protected void onKuudraActivate() {

        subscribe(WorldRenderEvent.class, this::onRender);
    }

    private void onRender(@NotNull WorldRenderEvent event) {

        event.drawStyledBox(waypointBox_1, true,
                RenderColor.fromArgb(LinusAddonsConfig.dpsWaypointColor),
                LinusAddonsConfig.dpsWaypointStyle
        );

        event.drawStyledBox(waypointBox_2, true,
                RenderColor.fromArgb(LinusAddonsConfig.dpsWaypointColor),
                LinusAddonsConfig.dpsWaypointStyle
        );
    }
}



