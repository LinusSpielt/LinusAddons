package net.linusaddons.mod.lifecycle.modules;

import net.linusaddons.mod.commands.LACommand;
import net.linusaddons.mod.features.FeatureManager;
import net.linusaddons.mod.features.generic.*;
import net.linusaddons.mod.features.kuudra.*;
import net.linusaddons.mod.lifecycle.LifecycleComponent;

public class FeatureModule implements LifecycleComponent {

    private FeatureManager features;
    private TentacleDetectFeature tentacleDetect;
    @Override
    public void start() {
        features = new FeatureManager();
        tentacleDetect = new TentacleDetectFeature();

        features.register(
                new BackboneAlertFeature(), new HollowAndRendFeature(), new HideRendCooldownFeature(),
                new AutoGFSFeature(), new PickobulusBlockerFeature(), new DiscordRPCFeature(),
                new PreSpotWaypointFeature(), new BossBlockWaypointFeature(), new DpsWaypointsFeature(),
                tentacleDetect, new ConditionalBoxFeature(tentacleDetect), new SupplyInteractionCircleFeature(),
                new StunWaypointsFeature(), new RendAimFeature()
        );
        LACommand.setTentacleDetect(tentacleDetect);

        features.start();
    }

    @Override
    public void stop() {
        features.stop();
    }
}