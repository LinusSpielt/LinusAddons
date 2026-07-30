package net.linusaddons.mod.features.kuudra;

import net.linusaddons.mod.config.categories.LinusAddonsConfig;
import net.linusaddons.mod.events.impl.ClientTickEvent;
import net.linusaddons.mod.events.impl.ItemUseEvent;
import net.linusaddons.mod.features.Feature;
import net.linusaddons.mod.manager.BackboneAlertManager;
import net.linusaddons.mod.utils.StringUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BackboneAlertFeature extends Feature {

    private static final int BACKBONE_TICKS = 22;
    private static final int BACKBONE_COOLDOWN_TICKS = 32;

    private final BackboneAlertManager manager = BackboneAlertManager.get();

    public BackboneAlertFeature() {
        super(
                "backboneAlert",
                "Backbone Alert",
                () -> LinusAddonsConfig.backBoneInfo
        );
    }

    @Override
    protected void onActivate() {
        subscribe(ItemUseEvent.class, this::onItemUse);

        manager.reset();
    }

    @Override
    protected void onDeactivate() {
        manager.reset();
    }

    private void onItemUse(@NotNull ItemUseEvent event) {
        if (mc.player == null || event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;

        String itemName = StringUtils.stripFormatting(stack.getHoverName().getString()).toLowerCase();
        if (!itemName.contains("bonemerang") || manager.isOnCooldown()) {
            return;
        }

        manager.setCooldownTicks(BACKBONE_COOLDOWN_TICKS);
        manager.startBackboneTimer(BACKBONE_TICKS);
    }
}
