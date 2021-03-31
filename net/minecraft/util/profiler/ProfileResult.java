/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import java.nio.file.Path;
import java.util.List;
import net.minecraft.util.profiler.ProfilerTiming;

public interface ProfileResult {
    public static final char field_29924 = '\u001e';

    public List<ProfilerTiming> getTimings(String var1);

    public boolean save(Path var1);

    public long getStartTime();

    public int getStartTick();

    public long getEndTime();

    public int getEndTick();

    default public long getTimeSpan() {
        return this.getEndTime() - this.getStartTime();
    }

    default public int getTickSpan() {
        return this.getEndTick() - this.getStartTick();
    }

    public String method_34970();

    public static String getHumanReadableName(String path) {
        return path.replace('\u001e', '.');
    }
}

