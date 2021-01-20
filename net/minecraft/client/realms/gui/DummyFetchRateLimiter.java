/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.gui.FetchRateLimiter;

/**
 * A fetch rate limiter that does nothing.
 */
@Environment(value=EnvType.CLIENT)
public class DummyFetchRateLimiter
implements FetchRateLimiter {
    @Override
    public void onRun() {
    }

    @Override
    public long getRemainingPeriod() {
        return 0L;
    }
}

