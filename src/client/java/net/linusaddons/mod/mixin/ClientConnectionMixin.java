package net.linusaddons.mod.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.linusaddons.mod.events.EventBus;
import net.linusaddons.mod.events.impl.*;
import net.linusaddons.mod.utils.ServerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ClientConnectionMixin {

    @Unique
    private static final Minecraft client = Minecraft.getInstance();

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void la$onPacketReceive(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {

        PacketReceiveEvent PRevent = EventBus.post(
                new PacketReceiveEvent(packet)
        );

        if (PRevent.isCancelled()) {
            ci.cancel();
        }


        if (packet instanceof ClientboundSoundPacket soundPacket) {
            var event = EventBus.post(new SoundReceiveEvent(soundPacket));
            if (event.isCancelled()) {
                ci.cancel();
                return;
            }
        }

        if (packet instanceof ClientboundPingPacket pingPacket && pingPacket.getId() != 0) {
            client.execute(() -> EventBus.post(ClientTickEvent.create(client)));
        }

        if (packet instanceof ClientboundSystemChatPacket(Component content, boolean overlay) && !overlay) {
            var event = EventBus.post(new ChatReceivedEvent(content));
            if (event.isCancelled()) {
                ci.cancel();
                return;
            }
        }

        if (packet instanceof ClientboundSetTimePacket) {
            ServerUtils.onWorldTimeUpdate();
        }
    }
    @Inject(
            method = "send(Lnet/minecraft/network/protocol/Packet;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void la$onPacketSend(Packet<?> packet, CallbackInfo ci) {

        PacketSendEvent event = EventBus.post(
                new PacketSendEvent(packet)
        );

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

}
