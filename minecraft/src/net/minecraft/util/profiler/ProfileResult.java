package net.minecraft.util.profiler;

import java.nio.file.Path;
import java.util.List;

public interface ProfileResult {
	char field_29924 = '\u001e';

	List<ProfilerTiming> getTimings(String parentPath);

	boolean save(Path path);

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

	String method_34970();

	static String getHumanReadableName(String path) {
		return path.replace('\u001e', '.');
	}
}
