package net.linusaddons.mod.mixin;

import net.linusaddons.mod.events.EventBus;
import net.linusaddons.mod.events.impl.BossBarRenderEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public class BossBarHudMixin {


    @Inject(method = "extractBar*", at = @At("HEAD"), cancellable = true)
    private void la$onRenderBossBar(GuiGraphicsExtractor context, int x, int y, BossEvent bossBar, CallbackInfo ci) {
        BossBarRenderEvent event = EventBus.post(new BossBarRenderEvent(bossBar));
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
