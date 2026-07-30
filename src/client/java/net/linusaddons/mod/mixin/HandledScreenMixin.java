package net.linusaddons.mod.mixin;

import net.linusaddons.mod.events.EventBus;
import net.linusaddons.mod.events.impl.ScreenClickEvent;
import net.linusaddons.mod.events.impl.ScreenClickedEvent;
import net.linusaddons.mod.events.impl.ScreenDrawSlotEvent;
import net.linusaddons.mod.events.impl.ScreenKeyPressEvent;
import net.linusaddons.mod.hud.HudManager;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public class HandledScreenMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void la$renderHudOverHandledScreen(
            GuiGraphicsExtractor context,
            int mouseX, int mouseY,
            float deltaTicks,
            CallbackInfo ci
    ) {
        HudManager.get().renderOnHandledScreen(context, mouseX, mouseY, deltaTicks);
    }

    @Inject(method = "extractSlot", at = @At("TAIL"))
    private void la$highlightOpenedCroesusChests(GuiGraphicsExtractor context, Slot slot, int x, int y, CallbackInfo ci) {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        EventBus.post(new ScreenDrawSlotEvent(
                screen,
                context,
                slot,
                x, y
        ));
    }

    @Inject(
            method = "slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ContainerInput;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onSlotClick(Slot slot, int slotId, int button, ContainerInput actionType, CallbackInfo ci) {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        ScreenClickEvent event = EventBus.post(new ScreenClickEvent(
                screen,
                slot,
                actionType
        ));

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "keyPressed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void la$onKeyPressed(@NotNull KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        ScreenKeyPressEvent event = EventBus.post(
                new ScreenKeyPressEvent((AbstractContainerScreen<?>) (Object) this, input.key(), input.scancode(), input.modifiers())
        );

        if (event.isCancelled()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "mouseClicked",
            at = @At("HEAD"),
            cancellable = true
    )
    private void la$onMouseClicked(MouseButtonEvent ButtonEvent, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        ScreenClickedEvent event = EventBus.post(new ScreenClickedEvent(
                screen,
                ButtonEvent,
                doubleClick
        ));

        if (event.isCancelled()) {
            cir.setReturnValue(true);
        }
    }
}
