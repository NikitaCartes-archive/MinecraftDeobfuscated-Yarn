/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.gui.FetchRateLimiter;
import org.jetbrains.annotations.Nullable;

/**
 * An operational rate limiter.
 */
@Environment(value=EnvType.CLIENT)
public class BasicFetchRateLimiter
implements FetchRateLimiter {
    private final Duration period;
    private final Supplier<Clock> clock;
    @Nullable
    private Instant lastRun;

    public BasicFetchRateLimiter(Duration period) {
        this.period = period;
        this.clock = Clock::systemUTC;
    }

    @Override
    public void onRun() {
        this.lastRun = Instant.now(this.clock.get());
    }

    @Override
    public long getRemainingPeriod() {
        if (this.lastRun == null) {
            return 0L;
        }
        return Math.max(0L, Duration.between(Instant.now(this.clock.get()), this.lastRun.plus(this.period)).toMillis());
    }
}

