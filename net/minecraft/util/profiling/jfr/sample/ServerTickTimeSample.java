/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiling.jfr.sample;

import java.time.Instant;
import jdk.jfr.consumer.RecordedEvent;

public record ServerTickTimeSample(Instant time, float averageTickMs) {
    public static ServerTickTimeSample fromEvent(RecordedEvent event) {
        return new ServerTickTimeSample(event.getStartTime(), event.getFloat("averageTickMs"));
    }
}

