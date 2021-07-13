package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;

public class GcHeapSummarySample {
	final Instant time;
	private final int gcId;
	final long heapUsed;
	final GcHeapSummarySample.SummaryType summaryType;

	public GcHeapSummarySample(RecordedEvent event) {
		this.time = event.getStartTime();
		this.gcId = event.getInt("gcId");
		this.heapUsed = event.getLong("heapUsed");
		this.summaryType = event.getString("when").equalsIgnoreCase("before gc")
			? GcHeapSummarySample.SummaryType.BEFORE_GC
			: GcHeapSummarySample.SummaryType.AFTER_GC;
	}

	public static class Statistics {
		public final double allocatedBytesPerSecond;
		public final int count;
		public final Duration gcDuration;
		private final Duration duration;

		public Statistics(Duration duration, List<GcHeapSummarySample> samples, Duration gcDuration, int count) {
			this.duration = duration;
			this.gcDuration = gcDuration;
			this.count = count;
			this.allocatedBytesPerSecond = getAllocatedBytesPerSecond(samples);
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

		public float gcDurationRatio() {
			return (float)this.gcDuration.toMillis() / (float)this.duration.toMillis();
		}
	}

	static enum SummaryType {
		BEFORE_GC,
		AFTER_GC;
	}
}
