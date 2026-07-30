package net.linusaddons.mod.features.kuudra;

import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.config.categories.LinusAddonsConfig;
import net.linusaddons.mod.events.impl.ChatReceivedEvent;
import net.linusaddons.mod.events.impl.ClientTickEvent;
import net.linusaddons.mod.events.impl.ItemUseEvent;
import net.linusaddons.mod.events.impl.skyblock.KuudraPhaseChangeEvent;
import net.linusaddons.mod.features.Feature;
import net.linusaddons.mod.manager.KuudraStateManager;
import net.linusaddons.mod.model.kuudra.KuudraPhase;
import net.linusaddons.mod.utils.StringUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class AutoGFSFeature extends Feature {

    private static final int RUN_THRESHOLD = LinusAddonsConfig.pearlTreshold;
    private static final String ELLE_MESSAGE = "[NPC] Elle: Okay adventurers, I will go and fish up Kuudra!";

    private long lastGFS = 0L;
    private final KuudraStateManager stateManager = KuudraStateManager.get();

    public AutoGFSFeature() {
        super(
                "AutoGFS",
                "Auto GFS Pearl Refill",
                () -> LinusAddonsConfig.AutoGFS
        );
    }

    @Override
    protected void onActivate() {
        subscribe(ChatReceivedEvent.class, this::onChatStart);
        subscribe(ItemUseEvent.class, this::onItemUse);
        subscribe(ClientTickEvent.class, this::onTick);
        subscribe(KuudraPhaseChangeEvent.class, this::onPhaseChange);
    }

    private void onItemUse(@NotNull ItemUseEvent event) {
        if (!stateManager.context().isInRun()) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (mc.player == null) return;

        ItemStack held = event.getItemStack();
        if (held.isEmpty() || held.getItem() != Items.ENDER_PEARL) return;
        if (isOnCooldown()) return;

        int countAfterThrow = held.getCount() - 1;
        if (countAfterThrow >= RUN_THRESHOLD) return;

        int refillAmount = 16 - countAfterThrow;
        runGFS(refillAmount);
    }


    private void onChatStart(@NotNull ChatReceivedEvent event) {
        String stripped = StringUtils.stripFormatting(event.getText().getString());
        if (!stripped.equals(ELLE_MESSAGE)) return;
        if (isOnCooldown(500L)) return;

        Integer refillAmount = getPearlRefillAmount();
        if (refillAmount == null) return;

        runGFS(refillAmount);
    }

    private void onPhaseChange(@NotNull KuudraPhaseChangeEvent event) {
        if (stateManager.phase() != KuudraPhase.EATEN) return;

        if (LinusAddonsConfig.toxicAmmount <= 0) return;
        if (mc.player == null) return;
        mc.player.connection.sendCommand("gfs TOXIC_ARROW_POISON " + LinusAddonsConfig.toxicAmmount);
    }


    private void onTick(ClientTickEvent event) {
        if (!stateManager.context().isInRun()) return;
        if (mc.player == null) return;

        if (isOnCooldown(500L)) return;
        Integer refillAmount = getPearlRefillAmount();
        if (refillAmount == null || refillAmount < 12) return;

        int HauntSlot = findHaunt(mc.player.inventoryMenu);
        if (HauntSlot != -1) return;
        runGFS(refillAmount);
    }

    private @Nullable Integer getPearlRefillAmount() {
        if (mc.player == null) return null;

        var inventory = mc.player.getInventory();
        boolean hasFreeHotbarSlot = false;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);

            if (i < 9 && stack.isEmpty()) {
                hasFreeHotbarSlot = true;
            }

            if (!stack.isEmpty() && stack.getItem() == Items.ENDER_PEARL) {
                if (stack.getCount() < 16) {
                    return 16 - stack.getCount();
                }
                return null;
            }
        }

        if (hasFreeHotbarSlot) {
            return 16;
        }

        return null;
    }

    private void runGFS(int amount) {
        if (mc.player == null) return;
        lastGFS = System.currentTimeMillis();
        mc.player.connection.sendCommand("gfs ENDER_PEARL " + amount);
        log.info("[AutoGFS] Sent /gfs ENDER_PEARL {}", amount);
    }

    private boolean isOnCooldown() {
        return isOnCooldown(200L);
    }

    private boolean isOnCooldown(long ms) {
        return (System.currentTimeMillis() - lastGFS) < ms;
    }

    private int findHaunt(AbstractContainerMenu menu) {
        for (Slot slot : menu.slots) {
            if (!slot.hasItem()) continue;

            ItemStack stack = slot.getItem();

            String name = stack.getHoverName().getString();

            if (name.toLowerCase().contains("haunt")) {
                return slot.index;
            }
        }
        return -1;
    }
}