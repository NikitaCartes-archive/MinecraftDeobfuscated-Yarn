package net.minecraft.client.util.profiler;

import java.util.Date;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.profiler.ProfileResult;

@Environment(EnvType.CLIENT)
public final class Sample {
	public final Date samplingTimer;
	public final int ticks;
	public final ProfileResult result;

	public Sample(Date samplingTimer, int ticks, ProfileResult result) {
		this.samplingTimer = samplingTimer;
		this.ticks = ticks;
		this.result = result;
	}
}
