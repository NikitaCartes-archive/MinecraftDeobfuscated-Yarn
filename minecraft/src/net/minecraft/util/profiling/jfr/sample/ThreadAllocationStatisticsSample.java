package net.minecraft.util.profiling.jfr.sample;

import com.google.common.base.MoreObjects;
import java.lang.runtime.ObjectMethods;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;

public final class ThreadAllocationStatisticsSample extends Record {
	private final Instant time;
	private final String threadName;
	private final long allocated;
	private static final String UNKNOWN = "unknown";

	public ThreadAllocationStatisticsSample(Instant instant, String string, long l) {
		this.time = instant;
		this.threadName = string;
		this.allocated = l;
	}

	public static ThreadAllocationStatisticsSample fromEvent(RecordedEvent event) {
		RecordedThread recordedThread = event.getThread("thread");
		String string = recordedThread == null ? "unknown" : MoreObjects.firstNonNull(recordedThread.getJavaName(), "unknown");
		return new ThreadAllocationStatisticsSample(event.getStartTime(), string, event.getLong("allocated"));
	}

	public static ThreadAllocationStatisticsSample.AllocationMap toAllocationMap(List<ThreadAllocationStatisticsSample> samples) {
		Map<String, Double> map = new TreeMap();
		Map<String, List<ThreadAllocationStatisticsSample>> map2 = (Map)samples.stream().collect(Collectors.groupingBy(sample -> sample.threadName));
		map2.forEach((threadName, groupedSamples) -> {
			if (groupedSamples.size() >= 2) {
				ThreadAllocationStatisticsSample threadAllocationStatisticsSample = (ThreadAllocationStatisticsSample)groupedSamples.get(0);
				ThreadAllocationStatisticsSample threadAllocationStatisticsSample2 = (ThreadAllocationStatisticsSample)groupedSamples.get(groupedSamples.size() - 1);
				long l = Duration.between(threadAllocationStatisticsSample.time, threadAllocationStatisticsSample2.time).getSeconds();
				long m = threadAllocationStatisticsSample2.allocated - threadAllocationStatisticsSample.allocated;
				map.put(threadName, (double)m / (double)l);
			}
		});
		return new ThreadAllocationStatisticsSample.AllocationMap(map);
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",ThreadAllocationStatisticsSample,"timestamp;threadName;totalBytes",ThreadAllocationStatisticsSample::time,ThreadAllocationStatisticsSample::threadName,ThreadAllocationStatisticsSample::allocated>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",ThreadAllocationStatisticsSample,"timestamp;threadName;totalBytes",ThreadAllocationStatisticsSample::time,ThreadAllocationStatisticsSample::threadName,ThreadAllocationStatisticsSample::allocated>(
			this
		);
	}

	public final boolean equals(Object o) {
		return ObjectMethods.bootstrap<"equals",ThreadAllocationStatisticsSample,"timestamp;threadName;totalBytes",ThreadAllocationStatisticsSample::time,ThreadAllocationStatisticsSample::threadName,ThreadAllocationStatisticsSample::allocated>(
			this, o
		);
	}

	public Instant time() {
		return this.time;
	}

	public String threadName() {
		return this.threadName;
	}

	public long allocated() {
		return this.allocated;
	}

	public static final class AllocationMap extends Record {
		private final Map<String, Double> allocations;

		public AllocationMap(Map<String, Double> map) {
			this.allocations = map;
		}

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",ThreadAllocationStatisticsSample.AllocationMap,"allocationsPerSecondByThread",ThreadAllocationStatisticsSample.AllocationMap::allocations>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",ThreadAllocationStatisticsSample.AllocationMap,"allocationsPerSecondByThread",ThreadAllocationStatisticsSample.AllocationMap::allocations>(
				this
			);
		}

		public final boolean equals(Object o) {
			return ObjectMethods.bootstrap<"equals",ThreadAllocationStatisticsSample.AllocationMap,"allocationsPerSecondByThread",ThreadAllocationStatisticsSample.AllocationMap::allocations>(
				this, o
			);
		}

		public Map<String, Double> allocations() {
			return this.allocations;
		}
	}
}
