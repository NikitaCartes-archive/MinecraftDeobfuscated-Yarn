package net.minecraft.util.profiling.jfr.sample;

import com.mojang.datafixers.util.Pair;
import java.lang.runtime.ObjectMethods;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public final class FileIoSample extends Record {
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

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",FileIoSample,"duration;path;bytes",FileIoSample::duration,FileIoSample::path,FileIoSample::bytes>(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",FileIoSample,"duration;path;bytes",FileIoSample::duration,FileIoSample::path,FileIoSample::bytes>(this);
	}

	public final boolean equals(Object o) {
		return ObjectMethods.bootstrap<"equals",FileIoSample,"duration;path;bytes",FileIoSample::duration,FileIoSample::path,FileIoSample::bytes>(this, o);
	}

	public Duration duration() {
		return this.duration;
	}

	@Nullable
	public String path() {
		return this.path;
	}

	public long bytes() {
		return this.bytes;
	}

	public static final class Statistics extends Record {
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

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",FileIoSample.Statistics,"totalBytes;bytesPerSecond;counts;countsPerSecond;timeSpentInIO;topTenContributorsByTotalBytes",FileIoSample.Statistics::totalBytes,FileIoSample.Statistics::bytesPerSecond,FileIoSample.Statistics::count,FileIoSample.Statistics::countPerSecond,FileIoSample.Statistics::totalDuration,FileIoSample.Statistics::topContributors>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",FileIoSample.Statistics,"totalBytes;bytesPerSecond;counts;countsPerSecond;timeSpentInIO;topTenContributorsByTotalBytes",FileIoSample.Statistics::totalBytes,FileIoSample.Statistics::bytesPerSecond,FileIoSample.Statistics::count,FileIoSample.Statistics::countPerSecond,FileIoSample.Statistics::totalDuration,FileIoSample.Statistics::topContributors>(
				this
			);
		}

		public final boolean equals(Object o) {
			return ObjectMethods.bootstrap<"equals",FileIoSample.Statistics,"totalBytes;bytesPerSecond;counts;countsPerSecond;timeSpentInIO;topTenContributorsByTotalBytes",FileIoSample.Statistics::totalBytes,FileIoSample.Statistics::bytesPerSecond,FileIoSample.Statistics::count,FileIoSample.Statistics::countPerSecond,FileIoSample.Statistics::totalDuration,FileIoSample.Statistics::topContributors>(
				this, o
			);
		}

		public long totalBytes() {
			return this.totalBytes;
		}

		public double bytesPerSecond() {
			return this.bytesPerSecond;
		}

		public long count() {
			return this.count;
		}

		public double countPerSecond() {
			return this.countPerSecond;
		}

		public Duration totalDuration() {
			return this.totalDuration;
		}

		public List<Pair<String, Long>> topContributors() {
			return this.topContributors;
		}
	}
}
