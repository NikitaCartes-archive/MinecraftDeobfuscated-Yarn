/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.gui.BasicFetchRateLimiter;
import net.minecraft.client.realms.gui.DummyFetchRateLimiter;
import net.minecraft.client.realms.gui.FetchRateLimiter;

/**
 * A task for {@link RealmsDataFetcher} that runs at a fixed interval. It
 * optionally supports rate limits and stops when the fetcher is stopped.
 */
@Environment(value=EnvType.CLIENT)
public class FetchTask
implements Runnable {
    private final BooleanSupplier condition;
    private final FetchRateLimiter rateLimiter;
    private final Duration period;
    private final Runnable command;

    private FetchTask(Runnable command, Duration period, BooleanSupplier condition, FetchRateLimiter rateLimiter) {
        this.command = command;
        this.period = period;
        this.condition = condition;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void run() {
        if (this.condition.getAsBoolean()) {
            this.rateLimiter.onRun();
            this.command.run();
        }
    }

    public ScheduledFuture<?> schedule(ScheduledExecutorService scheduler) {
        return scheduler.scheduleAtFixedRate(this, this.rateLimiter.getRemainingPeriod(), this.period.toMillis(), TimeUnit.MILLISECONDS);
    }

    public static FetchTask createRateLimited(Runnable command, Duration period, BooleanSupplier condition) {
        return new FetchTask(command, period, condition, new BasicFetchRateLimiter(period));
    }

    public static FetchTask create(Runnable command, Duration period, BooleanSupplier condition) {
        return new FetchTask(command, period, condition, new DummyFetchRateLimiter());
    }
}

