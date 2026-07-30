package net.linusaddons.mod.features.kuudra;

import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.config.categories.LinusAddonsConfig;
import net.linusaddons.mod.events.impl.WorldRenderEvent;
import net.linusaddons.mod.features.KuudraFeature;
import net.linusaddons.mod.manager.SupplyStateManager;
import net.linusaddons.mod.model.kuudra.KuudraPhase;
import net.linusaddons.mod.model.spot.SupplyPosition;
import net.linusaddons.mod.utils.render.RenderColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Slf4j
public class SupplyInteractionCircleFeature extends KuudraFeature {

    private final SupplyStateManager supplyState = SupplyStateManager.get();

    public SupplyInteractionCircleFeature() {
        super(
                "supplyCircle",
                "Supply Circle",
                () -> LinusAddonsConfig.supplyRodCircle,
                KuudraPhase.SUPPLIES
        );
    }

    @Override
    protected void onKuudraActivate() {
        subscribe(WorldRenderEvent.class, this::onRender);
    }

    private void onRender(@NotNull WorldRenderEvent event) {
        List<SupplyPosition> supplies = supplyState.getActiveSupplies();
        if (supplies.isEmpty()) return;

        for (SupplyPosition supply : supplies) {
            Vec3 renderPos = getInterpolatedSupplyPosition(event, supply);
            Vec3 middlePos = renderPos.add(0.5, 0, 1.5);
            event.drawCircleOutline(
                    middlePos, 6,
                    64, true,
                    RenderColor.fromArgb(LinusAddonsConfig.supplyRodColor)
            );
        }
    }

    private @NotNull Vec3 getInterpolatedSupplyPosition(@NotNull WorldRenderEvent event, @NotNull SupplyPosition supply) {
        if (mc.level == null) return supply.position();

        Entity entity = mc.level.getEntity(supply.entityId());
        if (!(entity instanceof Giant giant)) return supply.position();

        float tickDelta = event.tickCounter().getGameTimeDeltaPartialTick(true);
        double x = giant.xo + (giant.getX() - giant.xo) * tickDelta;
        double z = giant.zo + (giant.getZ() - giant.zo) * tickDelta;

        return SupplyPosition.fromGiant(x, z, giant.getYRot(), giant.getId()).position();
    }

}