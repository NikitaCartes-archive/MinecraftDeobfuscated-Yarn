/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import java.util.function.Consumer;
import net.minecraft.util.math.MathHelper;

public class AnimationState {
    private static final long STOPPED = Long.MAX_VALUE;
    private long updatedAt = Long.MAX_VALUE;
    private long timeRunning;

    public void start(int age) {
        this.updatedAt = (long)age * 1000L / 20L;
        this.timeRunning = 0L;
    }

    public void startIfNotRunning(int age) {
        if (!this.isRunning()) {
            this.start(age);
        }
    }

    public void setRunning(boolean running, int age) {
        if (running) {
            this.startIfNotRunning(age);
        } else {
            this.stop();
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

