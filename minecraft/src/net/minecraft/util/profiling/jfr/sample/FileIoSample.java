package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

public class FileIoSample {
	final Instant time;
	final Duration duration;
	@Nullable
	final String path;
	final long bytes;

	public FileIoSample(Instant time, Duration duration, @Nullable String path, long bytes) {
		this.time = time;
		this.duration = duration;
		this.path = path;
		this.bytes = bytes;
	}

	public static class Statistics {
		private final long totalBytes;
		private final int count;
		private final Duration duration;
		private final List<FileIoSample> samples;

		public Statistics(Duration duration, List<FileIoSample> samples) {
			this.samples = samples;
			this.totalBytes = samples.stream().mapToLong(sample -> sample.bytes).sum();
			this.count = samples.size();
			this.duration = duration;
		}

		public long getTotalBytes() {
			return this.totalBytes;
		}

		public int getCount() {
			return this.count;
		}

		public double getBytesPerSecond() {
			return (double)this.totalBytes / (double)this.duration.getSeconds();
		}

		public double getCountPerSecond() {
			return (double)this.count / (double)this.duration.getSeconds();
		}

		public Duration getTotalDuration() {
			return (Duration)this.samples.stream().map(sample -> sample.duration).reduce(Duration.ZERO, Duration::plus);
		}

		public Stream<Pair<String, Long>> getTopContributors() {
			return ((Map)this.samples
					.stream()
					.filter(sample -> sample.path != null)
					.collect(Collectors.groupingBy(sample -> sample.path, Collectors.summingLong(sample -> sample.bytes))))
				.entrySet()
				.stream()
				.sorted(Entry.comparingByValue().reversed())
				.map(entry -> Pair.of((String)entry.getKey(), (Long)entry.getValue()));
		}
	}
}
