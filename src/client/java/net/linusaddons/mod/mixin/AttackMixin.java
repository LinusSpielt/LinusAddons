package net.linusaddons.mod.mixin;

import net.linusaddons.mod.events.EventBus;
import net.linusaddons.mod.events.impl.AttackEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class AttackMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void la$onAttack(Player player, Entity entity, CallbackInfo ci) {
        AttackEvent event = EventBus.post(
                new AttackEvent(entity)
        );

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
