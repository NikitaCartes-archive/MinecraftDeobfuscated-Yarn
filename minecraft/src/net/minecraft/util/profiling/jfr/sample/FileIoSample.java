package net.minecraft.util.profiling.jfr.sample;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public record FileIoSample() {
	private final Duration duration;
	@Nullable
	private final String path;
	private final long bytes;

	public FileIoSample(Duration duration, @Nullable String string, long l) {
		this.duration = duration;
		this.path = string;
		this.bytes = l;
	}

	public static FileIoSample.Statistics toStatistics(Duration duration, List<FileIoSample> samples) {
		long l = samples.stream().mapToLong(sample -> sample.bytes).sum();
		return new FileIoSample.Statistics(
			l,
			(double)l / (double)duration.getSeconds(),
			(long)samples.size(),
			(double)samples.size() / (double)duration.getSeconds(),
			(Duration)samples.stream().map(FileIoSample::duration).reduce(Duration.ZERO, Duration::plus),
			((Map)samples.stream()
					.filter(sample -> sample.path != null)
					.collect(Collectors.groupingBy(sample -> sample.path, Collectors.summingLong(sample -> sample.bytes))))
				.entrySet()
				.stream()
				.sorted(Entry.comparingByValue().reversed())
				.map(entry -> Pair.of((String)entry.getKey(), (Long)entry.getValue()))
				.limit(10L)
				.toList()
		);
	}

	public static record Statistics() {
		private final long totalBytes;
		private final double bytesPerSecond;
		private final long count;
		private final double countPerSecond;
		private final Duration totalDuration;
		private final List<Pair<String, Long>> topContributors;

		public Statistics(long l, double d, long m, double e, Duration duration, List<Pair<String, Long>> list) {
			this.totalBytes = l;
			this.bytesPerSecond = d;
			this.count = m;
			this.countPerSecond = e;
			this.totalDuration = duration;
			this.topContributors = list;
		}
	}
}
