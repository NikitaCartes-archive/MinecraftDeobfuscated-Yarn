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

public class PacketSample implements TimedSample {
	final Instant time;
	final String packetName;
	final int bytes;

	public PacketSample(RecordedEvent event) {
		this.time = event.getStartTime();
		this.packetName = event.getString("packetName");
		this.bytes = event.getInt("bytes");
	}

	@Override
	public Instant getTime() {
		return this.time;
	}

	public static class Statistics {
		private final long count;
		private final long totalBytes;
		private final List<Pair<String, Long>> topContributors;
		private final Duration duration;

		public Statistics(Duration duration, List<PacketSample> samples) {
			this.duration = duration;
			IntSummaryStatistics intSummaryStatistics = samples.stream().mapToInt(sample -> sample.bytes).summaryStatistics();
			this.totalBytes = intSummaryStatistics.getSum();
			this.count = (long)samples.size();
			this.topContributors = (List<Pair<String, Long>>)((Map)samples.stream()
					.collect(Collectors.groupingBy(sample -> sample.packetName, Collectors.summingLong(sample -> (long)sample.bytes))))
				.entrySet()
				.stream()
				.sorted(Entry.comparingByValue(Comparator.reverseOrder()))
				.limit(5L)
				.map(entry -> Pair.of((String)entry.getKey(), (Long)entry.getValue()))
				.collect(Collectors.toList());
		}

		public long getCount() {
			return this.count;
		}

		public double getCountPerSecond() {
			return (double)this.count / (double)this.duration.getSeconds();
		}

		public double getBytesPerSecond() {
			return (double)this.totalBytes / (double)this.duration.getSeconds();
		}

		public long getTotalBytes() {
			return this.totalBytes;
		}

		public List<Pair<String, Long>> getTopContributors() {
			return this.topContributors;
		}

		public Duration getDuration() {
			return this.duration;
		}
	}
}
