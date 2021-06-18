/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import java.time.Instant;
import net.minecraft.util.profiler.ProfileResult;

public final class Deviation {
    public final Instant instant;
    public final int ticks;
    public final ProfileResult result;

    public Deviation(Instant instant, int ticks, ProfileResult result) {
        this.instant = instant;
        this.ticks = ticks;
        this.result = result;
    }
}

