package net.linusaddons.mod.events.dispatcher;

import net.linusaddons.mod.events.SubscriptionOwner;
import net.linusaddons.mod.lifecycle.LifecycleComponent;
import net.minecraft.client.Minecraft;

public abstract class EventDispatcher
        extends SubscriptionOwner
        implements LifecycleComponent
{

    protected static final Minecraft client = Minecraft.getInstance();
}
