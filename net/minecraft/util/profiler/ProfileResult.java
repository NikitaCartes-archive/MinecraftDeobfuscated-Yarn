/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import java.io.File;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.profiler.ProfilerTiming;

public interface ProfileResult {
    @Environment(value=EnvType.CLIENT)
    public List<ProfilerTiming> getTimings(String var1);

    public boolean save(File var1);

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

    public String getTimingTreeString();

    public static String method_21721(String string) {
        return string.replace('\u001e', '.');
    }
}

