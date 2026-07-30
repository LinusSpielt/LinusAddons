package net.linusaddons.mod;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.linusaddons.mod.config.Configuration;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class LAKeyBindings {
    private static net.minecraft.client.gui.screens.Screen pendingScreen = null;
    private static final KeyMapping.Category LA_CATEGORY = KeyMapping.Category.register(Identifier.parse("linusaddons"));

    private static KeyMapping openConfigKey;

    public static void register() {
        openConfigKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.linusaddons.open-config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                LA_CATEGORY
        ));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (pendingScreen != null) {
                client.setScreen(pendingScreen);
                pendingScreen = null;
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.consumeClick()) {
                openConfigScreen(client);
            }
        });
    }
    public static void scheduleScreen(net.minecraft.client.gui.screens.Screen screen) {
        pendingScreen = screen;
    }

    public static void openConfigScreen(@NotNull Minecraft client) {
        scheduleScreen(
                ResourcefulConfigScreen.make(LAModClient.get().getConfigurator(), Configuration.class)
                        .withParent(null)
                        .build()
        );
    }
}
