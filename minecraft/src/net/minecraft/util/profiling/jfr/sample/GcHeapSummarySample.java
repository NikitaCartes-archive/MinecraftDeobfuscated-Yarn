package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;

public record GcHeapSummarySample(Instant time, long heapUsed, GcHeapSummarySample.SummaryType summaryType) {
	public static GcHeapSummarySample fromEvent(RecordedEvent event) {
		return new GcHeapSummarySample(
			event.getStartTime(),
			event.getLong("heapUsed"),
			event.getString("when").equalsIgnoreCase("before gc") ? GcHeapSummarySample.SummaryType.BEFORE_GC : GcHeapSummarySample.SummaryType.AFTER_GC
		);
	}

	public static GcHeapSummarySample.Statistics toStatistics(Duration duration, List<GcHeapSummarySample> samples, Duration gcDuration, int count) {
		return new GcHeapSummarySample.Statistics(duration, gcDuration, count, getAllocatedBytesPerSecond(samples));
	}

	private static double getAllocatedBytesPerSecond(List<GcHeapSummarySample> samples) {
		long l = 0L;
		Map<GcHeapSummarySample.SummaryType, List<GcHeapSummarySample>> map = (Map<GcHeapSummarySample.SummaryType, List<GcHeapSummarySample>>)samples.stream()
			.collect(Collectors.groupingBy(gcHeapSummarySamplex -> gcHeapSummarySamplex.summaryType));
		List<GcHeapSummarySample> list = (List<GcHeapSummarySample>)map.get(GcHeapSummarySample.SummaryType.BEFORE_GC);
		List<GcHeapSummarySample> list2 = (List<GcHeapSummarySample>)map.get(GcHeapSummarySample.SummaryType.AFTER_GC);

		for (int i = 1; i < list.size(); i++) {
			GcHeapSummarySample gcHeapSummarySample = (GcHeapSummarySample)list.get(i);
			GcHeapSummarySample gcHeapSummarySample2 = (GcHeapSummarySample)list2.get(i - 1);
			l += gcHeapSummarySample.heapUsed - gcHeapSummarySample2.heapUsed;
		}

		Duration duration = Duration.between(((GcHeapSummarySample)samples.get(1)).time, ((GcHeapSummarySample)samples.get(samples.size() - 1)).time);
		return (double)l / (double)duration.getSeconds();
	}

	public static record Statistics(Duration duration, Duration gcDuration, int count, double allocatedBytesPerSecond) {
		public float getGcDurationRatio() {
			return (float)this.gcDuration.toMillis() / (float)this.duration.toMillis();
		}
	}

	static enum SummaryType {
		BEFORE_GC,
		AFTER_GC;
	}
}
