package net.linusaddons.mod.mixin;

import net.linusaddons.mod.events.EventBus;
import net.linusaddons.mod.events.impl.ItemUseEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(MultiPlayerGameMode.class)
public class ItemUseMixin {

    @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
    private void la$onUseItem(@NotNull Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        ItemUseEvent event = EventBus.post(
                new ItemUseEvent(hand, stack.copy())
        );

        if (event.isCancelled()) {
            cir.cancel();
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void la$onUseItemOn(@NotNull LocalPlayer player, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getActiveItem();
        ItemUseEvent event = EventBus.post(
                new ItemUseEvent(hand, stack.copy())
        );

        if (event.isCancelled()) {
            cir.cancel();
        }
    }
}
