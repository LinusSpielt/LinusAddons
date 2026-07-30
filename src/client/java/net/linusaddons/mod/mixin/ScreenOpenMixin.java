package net.linusaddons.mod.mixin;

import net.linusaddons.mod.events.EventBus;
import net.linusaddons.mod.events.impl.ScreenOpenEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Minecraft.class)
public class ScreenOpenMixin {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void onScreenOpen(Screen screen, CallbackInfo ci) {
        if (screen == null) {
            return;
        }
        ScreenOpenEvent event = EventBus.post(
                new ScreenOpenEvent(screen)
        );

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
