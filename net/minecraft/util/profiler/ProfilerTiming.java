/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class ProfilerTiming
implements Comparable<ProfilerTiming> {
    public final double parentSectionUsagePercentage;
    public final double totalUsagePercentage;
    public final String name;

    public ProfilerTiming(String string, double d, double e) {
        this.name = string;
        this.parentSectionUsagePercentage = d;
        this.totalUsagePercentage = e;
    }

    public int method_15408(ProfilerTiming profilerTiming) {
        if (profilerTiming.parentSectionUsagePercentage < this.parentSectionUsagePercentage) {
            return -1;
        }
        if (profilerTiming.parentSectionUsagePercentage > this.parentSectionUsagePercentage) {
            return 1;
        }
        return profilerTiming.name.compareTo(this.name);
    }

    @Environment(value=EnvType.CLIENT)
    public int getColor() {
        return (this.name.hashCode() & 0xAAAAAA) + 0x444444;
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.method_15408((ProfilerTiming)object);
    }
}

