/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

/**
 * An implementation of an exponential backoff algorithm with the upper limit
 * on delay. The implementation does not require a specific time unit (such as ticks,
 * seconds, etc); the only requirement is that {@link #run} method is called
 * periodically.
 */
@Environment(value=EnvType.CLIENT)
public class ExponentialBackoff
implements Runnable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Runner runner;
    private final int maxSkippableRuns;
    private int runsToSkip;
    private int skippedRuns;

    public ExponentialBackoff(Runner runner, int maxSkippableRuns) {
        this.runner = runner;
        this.maxSkippableRuns = maxSkippableRuns;
    }

    @Override
    public void run() {
        if (this.runsToSkip > this.skippedRuns) {
            ++this.skippedRuns;
            return;
        }
        this.skippedRuns = 0;
        try {
            this.runner.run();
            this.runsToSkip = 0;
        } catch (Exception exception) {
            this.runsToSkip = this.runsToSkip == 0 ? 1 : Math.min(2 * this.runsToSkip, this.maxSkippableRuns);
            LOGGER.info("Skipping next {}", (Object)this.runsToSkip);
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface Runner {
        public void run() throws Exception;
    }
}

