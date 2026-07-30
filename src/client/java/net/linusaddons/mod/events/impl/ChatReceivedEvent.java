package net.linusaddons.mod.events.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.linusaddons.mod.events.Cancellable;
import net.linusaddons.mod.events.Event;
import net.linusaddons.mod.utils.StringUtils;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class ChatReceivedEvent implements Event, Cancellable {

    private final Component text;
    private final String message;
    private final String strippedMessage;

    @Setter
    private boolean cancelled;

    public ChatReceivedEvent(@NotNull Component text) {
        this.text = text;
        this.message = text.getString();
        this.strippedMessage = StringUtils.stripFormatting(message);
    }

    public boolean contains(@NotNull String str) {
        return message.toLowerCase().contains(str.toLowerCase());
    }

    public boolean startsWith(String prefix) {
        return message.startsWith(prefix);
    }

}
