package net.minecraft.util.profiler;

import java.io.File;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface ProfileResult {
	@Environment(EnvType.CLIENT)
	List<ProfilerTiming> getTimings(String string);

	boolean saveToFile(File file);

	long getStartTime();

	int getStartTick();

	long getEndTime();

	int getEndTick();

	default long getTimeSpan() {
		return this.getEndTime() - this.getStartTime();
	}

	default int getTickSpan() {
		return this.getEndTick() - this.getStartTick();
	}

	String method_18052();
}
