package net.minecraft.util.profiler;

import java.nio.file.Path;
import java.util.List;

public interface ProfileResult {
	char SPLITTER_CHAR = '\u001e';

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

	String getRootTimings();

	static String getHumanReadableName(String path) {
		return path.replace('\u001e', '.');
	}
}
