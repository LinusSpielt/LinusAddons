package net.linusaddons.mod.config;

import com.teamresourceful.resourcefulconfig.api.annotations.*;
import net.linusaddons.mod.config.categories.*;
import net.linusaddons.mod.hud.HudManager;
import net.minecraft.client.Minecraft;

@Config(
        value = "linusaddons",
        categories = {
                LinusAddonsConfig.class
        }
)
@ConfigInfo(
        title = "Linus Addons",
        description = "LA is a Hypixel SkyBlock mod for Kuudra.",
        links = {
                @ConfigInfo.Link(value = "https://github.com/LinusSpielt/LinusAddons", icon = "code-2", text = "Github"),
                @ConfigInfo.Link(value = "https://discord.gg/fJeDhZbv2a", icon = "discord", text = "Discord")
        }
)

public class Configuration {

    private static final Minecraft mc = Minecraft.getInstance();

    @ConfigButton(
            title = "HUD Editor",
            text = "OPEN"
    )
    @Comment("Open the HUD Editor to move and customize HUD elements.")
    public static final Runnable hudEditor = () -> {
        mc.execute(() -> HudManager.get().openEditor());
    };

    @ConfigOption.Separator("Discord Integration")

    @ConfigEntry(
            id = "discordRichPresence",
            translation = "Discord Rich Presence"
    )
    @Comment("Show your Kuudra run status on Discord Rich Presence (1:1 copy from IQ lol).")
    public static boolean discordRichPresence = true;
}
