package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;

public record GcHeapSummarySample() {
	private final Instant time;
	private final long heapUsed;
	private final GcHeapSummarySample.SummaryType summaryType;

	public GcHeapSummarySample(Instant instant, long l, GcHeapSummarySample.SummaryType summaryType) {
		this.time = instant;
		this.heapUsed = l;
		this.summaryType = summaryType;
	}

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

	public static record Statistics() {
		private final Duration duration;
		private final Duration gcDuration;
		private final int count;
		private final double allocatedBytesPerSecond;

		public Statistics(Duration duration, Duration duration2, int i, double d) {
			this.duration = duration;
			this.gcDuration = duration2;
			this.count = i;
			this.allocatedBytesPerSecond = d;
		}

		public float getGcDurationRatio() {
			return (float)this.gcDuration.toMillis() / (float)this.duration.toMillis();
		}
	}

	static enum SummaryType {
		BEFORE_GC,
		AFTER_GC;
	}
}
