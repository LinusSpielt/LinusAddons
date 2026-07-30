package net.linusaddons.mod.mixin;

import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.events.EventBus;
import net.linusaddons.mod.events.impl.HudRenderEvent;
import net.linusaddons.mod.events.impl.TitleReceivedEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Slf4j
@Mixin(Gui.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Nullable
    private Component title;

    @Shadow
    @Nullable
    private Component subtitle;

    @Unique
    private String la$lastTitleMessage = "";
    @Unique
    private String la$lastSubtitleMessage = "";
    @Unique
    private boolean la$lastTitleCancelled;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void la$onRenderHud(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
        if (minecraft.player == null) return;

        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();

        EventBus.post(new HudRenderEvent(
                context,
                tickCounter.getGameTimeDeltaPartialTick(true),
                width,
                height
        ));
    }

    @Inject(method = "extractTitle", at = @At("HEAD"), cancellable = true)
    private void la$onRenderTitleAndSubtitle(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
        boolean hasTitle = title != null && !title.getString().isEmpty();
        boolean hasSubtitle = subtitle != null && !subtitle.getString().isEmpty();
        if (!hasTitle && !hasSubtitle) {
            la$lastTitleMessage = "";
            la$lastSubtitleMessage = "";
            la$lastTitleCancelled = false;
            return;
        }

        String currentTitleMessage = title == null ? "" : title.getString();
        String currentSubtitleMessage = subtitle == null ? "" : subtitle.getString();
        boolean isDuplicateTitle = currentTitleMessage.equals(la$lastTitleMessage)
                && currentSubtitleMessage.equals(la$lastSubtitleMessage);

        if (isDuplicateTitle) {
            if (la$lastTitleCancelled) {
                ci.cancel();
            }

            return;
        }
        la$lastTitleMessage = currentTitleMessage;
        la$lastSubtitleMessage = currentSubtitleMessage;

        TitleReceivedEvent event = EventBus.post(
                new TitleReceivedEvent(title, subtitle)
        );

        la$lastTitleCancelled = event.isCancelled();
        if (la$lastTitleCancelled) {
            ci.cancel();
        }
    }

}