package net.minecraft.util.profiling.jfr.sample;

import java.lang.runtime.ObjectMethods;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.math.Quantiles;

public final class LongRunningSampleStatistics extends Record {
	private final T fastestSample;
	private final T slowestSample;
	@Nullable
	private final T secondSlowestSample;
	private final int count;
	private final Map<Integer, Double> quantiles;
	private final Duration totalDuration;

	public LongRunningSampleStatistics(
		T longRunningSample, T longRunningSample2, @Nullable T longRunningSample3, int i, Map<Integer, Double> map, Duration duration
	) {
		this.fastestSample = longRunningSample;
		this.slowestSample = longRunningSample2;
		this.secondSlowestSample = longRunningSample3;
		this.count = i;
		this.quantiles = map;
		this.totalDuration = duration;
	}

	public static <T extends LongRunningSample> LongRunningSampleStatistics<T> fromSamples(List<T> samples) {
		if (samples.isEmpty()) {
			throw new IllegalArgumentException("No values");
		} else {
			List<T> list = samples.stream().sorted(Comparator.comparing(LongRunningSample::duration)).toList();
			Duration duration = (Duration)list.stream().map(LongRunningSample::duration).reduce(Duration::plus).orElse(Duration.ZERO);
			T longRunningSample = (T)list.get(0);
			T longRunningSample2 = (T)list.get(list.size() - 1);
			T longRunningSample3 = (T)(list.size() > 1 ? list.get(list.size() - 2) : null);
			int i = list.size();
			Map<Integer, Double> map = Quantiles.create(list.stream().mapToLong(sample -> sample.duration().toNanos()).toArray());
			return new LongRunningSampleStatistics(longRunningSample, longRunningSample2, longRunningSample3, i, map, duration);
		}
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",LongRunningSampleStatistics,"fastest;slowest;secondSlowest;count;percentilesNanos;totalDuration",LongRunningSampleStatistics::fastestSample,LongRunningSampleStatistics::slowestSample,LongRunningSampleStatistics::secondSlowestSample,LongRunningSampleStatistics::count,LongRunningSampleStatistics::quantiles,LongRunningSampleStatistics::totalDuration>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",LongRunningSampleStatistics,"fastest;slowest;secondSlowest;count;percentilesNanos;totalDuration",LongRunningSampleStatistics::fastestSample,LongRunningSampleStatistics::slowestSample,LongRunningSampleStatistics::secondSlowestSample,LongRunningSampleStatistics::count,LongRunningSampleStatistics::quantiles,LongRunningSampleStatistics::totalDuration>(
			this
		);
	}

	public final boolean equals(Object o) {
		return ObjectMethods.bootstrap<"equals",LongRunningSampleStatistics,"fastest;slowest;secondSlowest;count;percentilesNanos;totalDuration",LongRunningSampleStatistics::fastestSample,LongRunningSampleStatistics::slowestSample,LongRunningSampleStatistics::secondSlowestSample,LongRunningSampleStatistics::count,LongRunningSampleStatistics::quantiles,LongRunningSampleStatistics::totalDuration>(
			this, o
		);
	}

	public T fastestSample() {
		return this.fastestSample;
	}

	public T slowestSample() {
		return this.slowestSample;
	}

	@Nullable
	public T secondSlowestSample() {
		return this.secondSlowestSample;
	}

	public int count() {
		return this.count;
	}

	public Map<Integer, Double> quantiles() {
		return this.quantiles;
	}

	public Duration totalDuration() {
		return this.totalDuration;
	}
}
