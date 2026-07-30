package net.linusaddons.mod.lifecycle.modules;

import lombok.Getter;
import net.linusaddons.mod.lifecycle.LifecycleComponent;
import net.linusaddons.mod.manager.KuudraStateManager;

@Getter
public class KuudraModule implements LifecycleComponent {

    private KuudraStateManager kuudraStateManager;

    @Override
    public void start() {
        kuudraStateManager = new KuudraStateManager();
        kuudraStateManager.start();
    }

    @Override
    public void stop() {}
}
