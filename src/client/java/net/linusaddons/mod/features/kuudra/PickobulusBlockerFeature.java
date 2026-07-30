package net.linusaddons.mod.features.kuudra;

import net.linusaddons.mod.features.Feature;
import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.config.categories.LinusAddonsConfig;
import net.linusaddons.mod.events.impl.ItemUseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.linusaddons.mod.manager.KuudraStateManager;

@Slf4j
public class PickobulusBlockerFeature extends Feature {

    private final KuudraStateManager stateManager = KuudraStateManager.get();

    public PickobulusBlockerFeature() {
        super("PickobulusBlocker",
                "Pickobulus Blocker",
                () -> LinusAddonsConfig.PickobulusBlocker
        );
    }

    @Override
    protected void onActivate() {
        subscribe(ItemUseEvent.class, this::onItemUse);
    }

    private void onItemUse(@NotNull ItemUseEvent event) {
        if (!stateManager.context().isInRun()) return;
        if (isPlayerInBox(-127.5, 42, -133.5, -187.5, 18, -193.5)) return;

        if (mc.player == null || event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        Item item = stack.getItem();
        if (item != Items.DIAMOND_PICKAXE && item != Items.PRISMARINE_SHARD) return;

        event.setCancelled(true);
    }

    public static boolean isPlayerInBox(double x1, double y1, double z1,
                                        double x2, double y2, double z2) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;

        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

        return x >= Math.min(x1, x2) && x <= Math.max(x1, x2)
                && y >= Math.min(y1, y2) && y <= Math.max(y1, y2)
                && z >= Math.min(z1, z2) && z <= Math.max(z1, z2);
    }
}