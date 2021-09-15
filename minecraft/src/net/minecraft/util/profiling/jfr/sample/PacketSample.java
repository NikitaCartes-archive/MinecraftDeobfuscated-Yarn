package net.minecraft.util.profiling.jfr.sample;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;

public record PacketSample() implements TimedSample {
	private final Instant time;
	private final String packetName;
	private final int bytes;

	public PacketSample(Instant instant, String string, int i) {
		this.time = instant;
		this.packetName = string;
		this.bytes = i;
	}

	public static PacketSample fromEvent(RecordedEvent event) {
		return new PacketSample(event.getStartTime(), event.getString("packetName"), event.getInt("bytes"));
	}

	public static PacketSample.Statistics toStatistics(Duration duration, List<PacketSample> samples) {
		IntSummaryStatistics intSummaryStatistics = samples.stream().mapToInt(sample -> sample.bytes).summaryStatistics();
		long l = (long)samples.size();
		long m = intSummaryStatistics.getSum();
		List<Pair<String, Long>> list = ((Map)samples.stream()
				.collect(Collectors.groupingBy(sample -> sample.packetName, Collectors.summingLong(sample -> (long)sample.bytes))))
			.entrySet()
			.stream()
			.sorted(Entry.comparingByValue(Comparator.reverseOrder()))
			.limit(5L)
			.map(entry -> Pair.of((String)entry.getKey(), (Long)entry.getValue()))
			.toList();
		return new PacketSample.Statistics(l, m, list, duration);
	}

	@Override
	public Instant getTime() {
		return this.time;
	}

	public static record Statistics() {
		private final long count;
		private final long totalBytes;
		private final List<Pair<String, Long>> topContributors;
		private final Duration duration;

		public Statistics(long l, long m, List<Pair<String, Long>> list, Duration duration) {
			this.count = l;
			this.totalBytes = m;
			this.topContributors = list;
			this.duration = duration;
		}

		public double getCountPerSecond() {
			return (double)this.count / (double)this.duration.getSeconds();
		}

		public double getBytesPerSecond() {
			return (double)this.totalBytes / (double)this.duration.getSeconds();
		}
	}
}
