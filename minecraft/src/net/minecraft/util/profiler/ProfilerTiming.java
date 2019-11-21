package net.minecraft.util.profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class ProfilerTiming implements Comparable<ProfilerTiming> {
	public final double parentSectionUsagePercentage;
	public final double totalUsagePercentage;
	public final long field_19384;
	public final String name;

	public ProfilerTiming(String name, double d, double e, long l) {
		this.name = name;
		this.parentSectionUsagePercentage = d;
		this.totalUsagePercentage = e;
		this.field_19384 = l;
	}

	public int compareTo(ProfilerTiming profilerTiming) {
		if (profilerTiming.parentSectionUsagePercentage < this.parentSectionUsagePercentage) {
			return -1;
		} else {
			return profilerTiming.parentSectionUsagePercentage > this.parentSectionUsagePercentage ? 1 : profilerTiming.name.compareTo(this.name);
		}
	}

	@Environment(EnvType.CLIENT)
	public int getColor() {
		return (this.name.hashCode() & 11184810) + 4473924;
	}
}
