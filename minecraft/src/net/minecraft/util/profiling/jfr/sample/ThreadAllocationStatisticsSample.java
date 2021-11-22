package net.minecraft.util.profiling.jfr.sample;

import com.google.common.base.MoreObjects;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;

public record ThreadAllocationStatisticsSample(Instant time, String threadName, long allocated) {
	private static final String UNKNOWN = "unknown";

	public static ThreadAllocationStatisticsSample fromEvent(RecordedEvent event) {
		RecordedThread recordedThread = event.getThread("thread");
		String string = recordedThread == null ? "unknown" : MoreObjects.firstNonNull(recordedThread.getJavaName(), "unknown");
		return new ThreadAllocationStatisticsSample(event.getStartTime(), string, event.getLong("allocated"));
	}

	public static ThreadAllocationStatisticsSample.AllocationMap toAllocationMap(List<ThreadAllocationStatisticsSample> samples) {
		Map<String, Double> map = new TreeMap();
		Map<String, List<ThreadAllocationStatisticsSample>> map2 = (Map<String, List<ThreadAllocationStatisticsSample>>)samples.stream()
			.collect(Collectors.groupingBy(sample -> sample.threadName));
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

	public static record AllocationMap(Map<String, Double> allocations) {
	}
}
