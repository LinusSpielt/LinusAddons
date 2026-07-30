package net.linusaddons.mod.lifecycle.modules;

import net.linusaddons.mod.features.widgets.*;
import net.linusaddons.mod.hud.HudManager;
import net.linusaddons.mod.lifecycle.LifecycleComponent;

public class WidgetModule implements LifecycleComponent {

    private HudManager hudManager;

    @Override
    public void start() {
        hudManager = new HudManager();
        hudManager.initialize();

        hudManager.register(
                new KuudraDirectionWidget(), new SimpleBuildProgressWidget(), new BuildStartCountdownWidget()
        );
    }

    @Override
    public void stop() {
        hudManager.stop();
    }
}
