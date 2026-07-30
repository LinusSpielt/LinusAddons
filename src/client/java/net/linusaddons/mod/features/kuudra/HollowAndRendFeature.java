package net.linusaddons.mod.features.kuudra;

import net.linusaddons.mod.events.impl.AttackEvent;
import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.config.categories.LinusAddonsConfig;
import net.linusaddons.mod.features.Feature;
import net.linusaddons.mod.manager.KuudraStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;

@Slf4j
public class HollowAndRendFeature extends Feature {

    private final KuudraStateManager stateManager = KuudraStateManager.get();

    public HollowAndRendFeature() {
        super("HollowAndRendFix", "Hollow And Rend Fix",
                () -> LinusAddonsConfig.HollowAndRendFix
        );
    }

    @Override
    protected void onActivate() {
        subscribe(AttackEvent.class, this::onAttack);
    }

    private void onAttack(AttackEvent event) {
        if (!stateManager.context().isInRun()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        ItemStack held = mc.player.getMainHandItem();

        if (!held.getHoverName().getString().contains("Hollow Wand") && !isHoldingRend())
            return;

        event.setCancelled(true);

        HitResult hit = mc.player.pick(4.5, mc.getDeltaTracker().getGameTimeDeltaPartialTick(true), false);

        if (hit.getType() != HitResult.Type.BLOCK) return;

        BlockHitResult blockHit = (BlockHitResult) hit;

        assert mc.gameMode != null;
        mc.gameMode.startDestroyBlock(blockHit.getBlockPos(), blockHit.getDirection());

    }

    private boolean isHoldingRend() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return false;

        ItemStack held = mc.player.getMainHandItem();
        ItemLore lore = held.get(DataComponents.LORE);

        if (lore != null) {
            for (Component line : lore.styledLines()) {
                String text = line.getString();

                if (text.contains("Rend")) {
                    return true;
                }
            }
        }
        return false;
    }
}