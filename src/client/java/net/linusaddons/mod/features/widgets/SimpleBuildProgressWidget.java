package net.linusaddons.mod.features.widgets;

import net.linusaddons.mod.events.impl.ClientTickEvent;
import net.linusaddons.mod.hud.component.HudLine;
import net.linusaddons.mod.hud.element.HudAnchor;
import net.linusaddons.mod.hud.element.HudWidget;
import net.linusaddons.mod.manager.KuudraStateManager;
import net.linusaddons.mod.model.kuudra.KuudraPhase;
import net.linusaddons.mod.utils.BuildProgressOverlayUtil;
import org.jetbrains.annotations.NotNull;

public class SimpleBuildProgressWidget extends HudWidget {

    private final KuudraStateManager stateManager = KuudraStateManager.get();
    private final HudLine buildLine;

    private int currentProgress = 0;

    public SimpleBuildProgressWidget() {
        super(
                "simpleBuildProgress",
                "Simple Build Progress",
                200.0f, 100.0f,
                1.2f,
                HudAnchor.TOP_LEFT
        );

        buildLine = HudLine.of("§b§lBuild: 0%");

        setEnabledSupplier(BuildProgressOverlayUtil::isSimpleOverlayEnabled);
        setVisibilityCondition(() -> stateManager.phase() == KuudraPhase.BUILD);

        setExampleLines(HudLine.of("§b§lBuild: §e§l50%"));
    }

    @Override
    protected void onActivate() {
        currentProgress = 0;
        updateBuildLine();

        clearLines();
        addLine(buildLine);

        subscribe(ClientTickEvent.class, this::onTick);
    }

    @Override
    protected void onDeactivate() {
        currentProgress = 0;
    }

    private void onTick(@NotNull ClientTickEvent event) {
        if (!event.isInGame()) return;
        if (stateManager.phase() != KuudraPhase.BUILD) return;

        BuildProgressOverlayUtil.BuildProgressData data = BuildProgressOverlayUtil.getBuildProgressFromArmorStand();
        if (data == null) return;

        Integer aggregatedProgress = BuildProgressOverlayUtil.getAggregatedPileProgress();
        int resolvedProgress = aggregatedProgress != null ? aggregatedProgress : data.progress();

        if (resolvedProgress == currentProgress) return;

        currentProgress = resolvedProgress;
        updateBuildLine();
    }

    private void updateBuildLine() {
        buildLine.text(String.format("§b§lBuild: %s§l%d%%", getProgressColor(currentProgress), currentProgress));
        markDimensionsDirty();
    }

    private @NotNull String getProgressColor(int progress) {
        if (progress >= 80) return "§a";
        if (progress >= 60) return "§2";
        if (progress >= 40) return "§e";
        if (progress >= 20) return "§6";
        return "§c";
    }
}