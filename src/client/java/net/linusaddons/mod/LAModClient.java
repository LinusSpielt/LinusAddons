package net.linusaddons.mod;

import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.linusaddons.mod.commands.LACommand;
import net.linusaddons.mod.config.Configuration;
import net.linusaddons.mod.events.dispatcher.KuudraEventsDispatcher;
import net.linusaddons.mod.integration.DiscordRPCIntegration;
import net.linusaddons.mod.lifecycle.LifecycleComponent;
import net.linusaddons.mod.lifecycle.modules.FeatureModule;
import net.linusaddons.mod.lifecycle.modules.KuudraModule;
import net.linusaddons.mod.lifecycle.modules.WidgetModule;
import net.linusaddons.mod.utils.update.ModrinthUpdateChecker;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class LAModClient implements ClientModInitializer {

    private static final String MOD_ID = "linusaddons";
    private static LAModClient instance;

    public static Minecraft mc = Minecraft.getInstance();

    private Configurator configurator;

    private final List<LifecycleComponent> components = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        instance = this;

        configurator = new Configurator(MOD_ID);
        configurator.register(Configuration.class);

        initializeModules(
                new KuudraModule(), new KuudraEventsDispatcher(),
                new FeatureModule(), new WidgetModule()
        );

        LAKeyBindings.register();
        registerCommands();
        ModrinthUpdateChecker.INSTANCE.register();

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            components.forEach(LifecycleComponent::stop);

            DiscordRPCIntegration.INSTANCE.shutdown();
            ModrinthUpdateChecker.INSTANCE.shutdown();
        });

        log.info("LinusAddons has booted!");
    }

    private void initializeModules(LifecycleComponent @NotNull ... components) {
        for (LifecycleComponent component : components) {
            this.components.add(component);
            component.start();
        }
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                LACommand.register(dispatcher)
        );
    }

    public static LAModClient get() {
        return instance;
    }
}
