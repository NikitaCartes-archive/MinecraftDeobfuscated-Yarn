/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import java.util.function.Consumer;
import net.minecraft.util.math.MathHelper;

public class AnimationState {
    private static final long JUST_STARTED = Long.MIN_VALUE;
    private static final long STOPPED = Long.MAX_VALUE;
    private long updatedAt = Long.MAX_VALUE;
    private long timeRunning;

    public void start() {
        this.updatedAt = Long.MIN_VALUE;
        this.timeRunning = 0L;
    }

    public void startIfNotRunning() {
        if (!this.isRunning()) {
            this.start();
        }
    }

    public void stop() {
        this.updatedAt = Long.MAX_VALUE;
    }

    public void run(Consumer<AnimationState> consumer) {
        if (this.isRunning()) {
            consumer.accept(this);
        }
    }

    public void update(float animationProgress, float speedMultiplier) {
        if (!this.isRunning()) {
            return;
        }
        long l = MathHelper.lfloor(animationProgress * 1000.0f / 20.0f);
        if (this.updatedAt == Long.MIN_VALUE) {
            this.updatedAt = l;
        }
        this.timeRunning += (long)((float)(l - this.updatedAt) * speedMultiplier);
        this.updatedAt = l;
    }

    public long getTimeRunning() {
        return this.timeRunning;
    }

    public boolean isRunning() {
        return this.updatedAt != Long.MAX_VALUE;
    }
}

