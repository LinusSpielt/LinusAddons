package net.linusaddons.mod.integration;

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.linusaddons.mod.LAModClient;
import net.linusaddons.mod.config.Configuration;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> ResourcefulConfigScreen.make(LAModClient.get().getConfigurator(), Configuration.class)
                .withParent(screen)
                .build();
    }
}
