package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.math.Quantiles;

public record LongRunningSampleStatistics<T extends LongRunningSample>(
	T fastestSample, T slowestSample, @Nullable T secondSlowestSample, int count, Map<Integer, Double> quantiles, Duration totalDuration
) {
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
			return new LongRunningSampleStatistics<>(longRunningSample, longRunningSample2, longRunningSample3, i, map, duration);
		}
	}
}
