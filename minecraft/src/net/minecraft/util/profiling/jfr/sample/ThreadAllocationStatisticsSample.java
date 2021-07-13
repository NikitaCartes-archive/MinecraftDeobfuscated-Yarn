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

public class ThreadAllocationStatisticsSample {
	private static final String UNKNOWN = "unknown";
	final Instant time;
	final String threadName;
	final long allocated;

	public ThreadAllocationStatisticsSample(RecordedEvent event) {
		this.time = event.getStartTime();
		RecordedThread recordedThread = event.getThread("thread");
		this.threadName = recordedThread == null ? "unknown" : MoreObjects.firstNonNull(recordedThread.getJavaName(), "unknown");
		this.allocated = event.getLong("allocated");
	}

	public static class AllocationMap {
		public final Map<String, Long> allocations = new TreeMap();

		public AllocationMap(List<ThreadAllocationStatisticsSample> samples) {
			Map<String, List<ThreadAllocationStatisticsSample>> map = (Map<String, List<ThreadAllocationStatisticsSample>>)samples.stream()
				.collect(Collectors.groupingBy(sample -> sample.threadName));
			map.forEach((threadName, groupedSamples) -> {
				if (groupedSamples.size() >= 2) {
					ThreadAllocationStatisticsSample threadAllocationStatisticsSample = (ThreadAllocationStatisticsSample)groupedSamples.get(0);
					ThreadAllocationStatisticsSample threadAllocationStatisticsSample2 = (ThreadAllocationStatisticsSample)groupedSamples.get(groupedSamples.size() - 1);
					long l = Duration.between(threadAllocationStatisticsSample.time, threadAllocationStatisticsSample2.time).getSeconds();
					this.allocations.put(threadName, (threadAllocationStatisticsSample2.allocated - threadAllocationStatisticsSample.allocated) / l);
				}
			});
		}
	}
}
