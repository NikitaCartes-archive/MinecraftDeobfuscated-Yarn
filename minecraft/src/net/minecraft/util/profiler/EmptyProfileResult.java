package net.minecraft.util.profiler;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class EmptyProfileResult implements ProfileResult {
	public static final EmptyProfileResult INSTANCE = new EmptyProfileResult();

	private EmptyProfileResult() {
	}

	@Override
	public List<ProfilerTiming> getTimings(String parentPath) {
		return Collections.emptyList();
	}

	@Override
	public boolean save(Path path) {
		return false;
	}

	@Override
	public long getStartTime() {
		return 0L;
	}

	@Override
	public int getStartTick() {
		return 0;
	}

	@Override
	public long getEndTime() {
		return 0L;
	}

	@Override
	public int getEndTick() {
		return 0;
	}

	@Override
	public String getRootTimings() {
		return "";
	}
}
