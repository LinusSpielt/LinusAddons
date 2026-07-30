package net.linusaddons.mod.features.kuudra;

import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.config.categories.LinusAddonsConfig;
import net.linusaddons.mod.events.impl.WorldRenderEvent;
import net.linusaddons.mod.events.impl.skyblock.supply.SupplyPlaceEvent;
import net.linusaddons.mod.features.KuudraFeature;
import net.linusaddons.mod.manager.SupplyStateManager;
import net.linusaddons.mod.model.spot.PreSpot;
import net.linusaddons.mod.utils.BuildProgressOverlayUtil;
import net.linusaddons.mod.utils.render.RenderColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class PreSpotWaypointFeature extends KuudraFeature {

    private final SupplyStateManager supplyState = SupplyStateManager.get();

    private @Nullable PreSpot savedPreSpot = null;
    private long countdownEndMs = -1L;

    public PreSpotWaypointFeature() {
        super(
                "preSpotWaypoint",
                "Pre Spot Waypoint",
                () -> LinusAddonsConfig.preSpotWaypoint
        );
    }

    @Override
    protected void onKuudraActivate() {
        countdownEndMs = -1L;

        subscribe(SupplyPlaceEvent.class, this::onSupplyPlace);
        subscribe(WorldRenderEvent.class, this::onRender);
    }

    @Override
    protected void onKuudraDeactivate() {
        savedPreSpot = null;
        countdownEndMs = -1L;
    }

    private void onSupplyPlace(@NotNull SupplyPlaceEvent event) {
        if (savedPreSpot == null) {
            savedPreSpot = supplyState.getDetectedPreSpot();
        }
        if (event.currentSupply() < 6) return;

        countdownEndMs = System.currentTimeMillis() + BuildProgressOverlayUtil.BUILD_START_COUNTDOWN_MS;
    }

    private void onRender(@NotNull WorldRenderEvent event) {
        if (savedPreSpot == null) return;
        if (countdownEndMs < 0 || System.currentTimeMillis() > countdownEndMs) return;

        Vec3 pos = switch (savedPreSpot) {
            case X        -> new Vec3(-106, 79, -113);
            case TRIANGLE -> new Vec3(-98,  79, -113);
            case EQUALS   -> new Vec3(-98,  79, -99);
            case SLASH    -> new Vec3(-106, 79, -99);
        };

        float half = 0.5f;
        AABB box = new AABB(
                pos.x() - half, pos.y(), pos.z() - half,
                pos.x() + half, pos.y() + 1.0, pos.z() + half
        );

        event.drawStyledBox(box, true,
                RenderColor.fromArgb(LinusAddonsConfig.preSpotWaypointColor),
                LinusAddonsConfig.preSpotWaypointStyle
        );
    }

}