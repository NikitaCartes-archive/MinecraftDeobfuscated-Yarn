package net.minecraft.util.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfilerSystem implements ReadableProfiler {
	private static final long TIMEOUT_NANOSECONDS = Duration.ofMillis(100L).toNanos();
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<String> path = Lists.<String>newArrayList();
	private final LongList timeList = new LongArrayList();
	private final Map<String, ProfilerSystem.LocatedInfo> locationInfos = Maps.<String, ProfilerSystem.LocatedInfo>newHashMap();
	private final IntSupplier endTickGetter;
	private final LongSupplier timeGetter;
	private final long startTime;
	private final int startTick;
	private String fullPath = "";
	private boolean tickStarted;
	@Nullable
	private ProfilerSystem.LocatedInfo currentInfo;
	private final boolean checkTimeout;
	private final Set<Pair<String, SampleType>> sampleTypes = new ObjectArraySet<>();

	public ProfilerSystem(LongSupplier timeGetter, IntSupplier tickGetter, boolean checkTimeout) {
		this.startTime = timeGetter.getAsLong();
		this.timeGetter = timeGetter;
		this.startTick = tickGetter.getAsInt();
		this.endTickGetter = tickGetter;
		this.checkTimeout = checkTimeout;
	}

	@Override
	public void startTick() {
		if (this.tickStarted) {
			LOGGER.error("Profiler tick already started - missing endTick()?");
		} else {
			this.tickStarted = true;
			this.fullPath = "";
			this.path.clear();
			this.push("root");
		}
	}

	@Override
	public void endTick() {
		if (!this.tickStarted) {
			LOGGER.error("Profiler tick already ended - missing startTick()?");
		} else {
			this.pop();
			this.tickStarted = false;
			if (!this.fullPath.isEmpty()) {
				LOGGER.error(
					"Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", () -> ProfileResult.getHumanReadableName(this.fullPath)
				);
			}
		}
	}

	@Override
	public void push(String location) {
		if (!this.tickStarted) {
			LOGGER.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", location);
		} else {
			if (!this.fullPath.isEmpty()) {
				this.fullPath = this.fullPath + "\u001e";
			}

			this.fullPath = this.fullPath + location;
			this.path.add(this.fullPath);
			this.timeList.add(Util.getMeasuringTimeNano());
			this.currentInfo = null;
		}
	}

	@Override
	public void push(Supplier<String> locationGetter) {
		this.push((String)locationGetter.get());
	}

	@Override
	public void markSampleType(SampleType type) {
		this.sampleTypes.add(Pair.of(this.fullPath, type));
	}

	@Override
	public void pop() {
		if (!this.tickStarted) {
			LOGGER.error("Cannot pop from profiler if profiler tick hasn't started - missing startTick()?");
		} else if (this.timeList.isEmpty()) {
			LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
		} else {
			long l = Util.getMeasuringTimeNano();
			long m = this.timeList.removeLong(this.timeList.size() - 1);
			this.path.remove(this.path.size() - 1);
			long n = l - m;
			ProfilerSystem.LocatedInfo locatedInfo = this.getCurrentInfo();
			locatedInfo.totalTime += n;
			locatedInfo.visits++;
			locatedInfo.maxTime = Math.max(locatedInfo.maxTime, n);
			locatedInfo.minTime = Math.min(locatedInfo.minTime, n);
			if (this.checkTimeout && n > TIMEOUT_NANOSECONDS) {
				LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", () -> ProfileResult.getHumanReadableName(this.fullPath), () -> (double)n / 1000000.0);
			}

			this.fullPath = this.path.isEmpty() ? "" : (String)this.path.get(this.path.size() - 1);
			this.currentInfo = null;
		}
	}

	@Override
	public void swap(String location) {
		this.pop();
		this.push(location);
	}

	@Override
	public void swap(Supplier<String> locationGetter) {
		this.pop();
		this.push(locationGetter);
	}

	private ProfilerSystem.LocatedInfo getCurrentInfo() {
		if (this.currentInfo == null) {
			this.currentInfo = (ProfilerSystem.LocatedInfo)this.locationInfos.computeIfAbsent(this.fullPath, k -> new ProfilerSystem.LocatedInfo());
		}

		return this.currentInfo;
	}

	@Override
	public void visit(String marker, int i) {
		this.getCurrentInfo().counts.addTo(marker, (long)i);
	}

	@Override
	public void visit(Supplier<String> markerGetter, int i) {
		this.getCurrentInfo().counts.addTo((String)markerGetter.get(), (long)i);
	}

	@Override
	public ProfileResult getResult() {
		return new ProfileResultImpl(this.locationInfos, this.startTime, this.startTick, this.timeGetter.getAsLong(), this.endTickGetter.getAsInt());
	}

	@Nullable
	@Override
	public ProfilerSystem.LocatedInfo getInfo(String name) {
		return (ProfilerSystem.LocatedInfo)this.locationInfos.get(name);
	}

	@Override
	public Set<Pair<String, SampleType>> getSampleTargets() {
		return this.sampleTypes;
	}

	public static class LocatedInfo implements ProfileLocationInfo {
		long maxTime = Long.MIN_VALUE;
		long minTime = Long.MAX_VALUE;
		long totalTime;
		long visits;
		final Object2LongOpenHashMap<String> counts = new Object2LongOpenHashMap<>();

		@Override
		public long getTotalTime() {
			return this.totalTime;
		}

		@Override
		public long getMaxTime() {
			return this.maxTime;
		}

		@Override
		public long getVisitCount() {
			return this.visits;
		}

		@Override
		public Object2LongMap<String> getCounts() {
			return Object2LongMaps.unmodifiable(this.counts);
		}
	}
}
