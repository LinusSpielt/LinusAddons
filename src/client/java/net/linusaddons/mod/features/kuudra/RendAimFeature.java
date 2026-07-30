package net.linusaddons.mod.features.kuudra;

import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.config.categories.LinusAddonsConfig;
import net.linusaddons.mod.events.impl.WorldRenderEvent;
import net.linusaddons.mod.features.KuudraFeature;
import net.linusaddons.mod.model.kuudra.KuudraPhase;
import net.linusaddons.mod.utils.KuudraLocationUtil;
import net.linusaddons.mod.utils.render.RenderColor;
import net.linusaddons.mod.utils.render.WorldRenderUtils;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class RendAimFeature extends KuudraFeature {

    public RendAimFeature() {
        super(
                "autoRendAK",
                "Auto Rend AutoKuudra",
                () -> LinusAddonsConfig.boneAimWaypoint,
                KuudraPhase.BOSS
        );
    }

    @Override
    protected void onKuudraActivate() {
    subscribe(WorldRenderEvent.class, this::onRender);
    }

    private Vec3 getRendAimPoint(KuudraLocationUtil.SpawnDirection direction, double y) {
        return switch (direction) {
            case RIGHT -> new Vec3(-126.5, y+0.5, -105.5);
            case FRONT -> new Vec3(-103.5, y+0.5, -83.5);
            case LEFT -> new Vec3(-82.5, y+0.5, -105.5);
            case BACK -> new Vec3(-100.5, y+1.5, -129.5);
            case UNKNOWN -> null;
        };
    }

    private void onRender(@NotNull WorldRenderEvent event) {
        Vec3 rendAimPos = KuudraLocationUtil.findKuudra().map(kuudra -> getRendAimPoint(KuudraLocationUtil.getDirection(kuudra), kuudra.getY())).orElse(null);
        if (rendAimPos == null) return;
        AABB rendAimBlock = new AABB(rendAimPos.x()-0.5, rendAimPos.y()-0.5, rendAimPos.z()-0.5, rendAimPos.x()+0.5, rendAimPos.y()+0.5, rendAimPos.z()+0.5);
        event.drawStyledBox(rendAimBlock, true, new RenderColor(255, 0, 0, 255), WorldRenderUtils.RenderStyle.OUTLINE);
    }
}