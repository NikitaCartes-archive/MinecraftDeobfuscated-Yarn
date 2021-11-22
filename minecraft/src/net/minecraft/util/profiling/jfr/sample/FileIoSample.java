package net.minecraft.util.profiling.jfr.sample;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public record FileIoSample(Duration duration, @Nullable String path, long bytes) {
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

	public static record Statistics(
		long totalBytes, double bytesPerSecond, long count, double countPerSecond, Duration totalDuration, List<Pair<String, Long>> topContributors
	) {
	}
}
