package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.util.math.Quantiles;

public class LongRunningSampleStatistics<T extends LongRunningSample> {
	public static final int[] QUANTILES = new int[]{50, 75, 90, 99};
	public final T fastestSample;
	public final T slowestSample;
	public final Optional<T> secondSlowestSample;
	public final int count;
	public final Map<Integer, Double> quantiles;
	public final Duration totalDuration;

	public LongRunningSampleStatistics(List<T> samples) {
		if (samples.isEmpty()) {
			throw new IllegalArgumentException("No values");
		} else {
			List<T> list = (List<T>)samples.stream().sorted(Comparator.comparing(LongRunningSample::getDuration)).collect(Collectors.toList());
			this.totalDuration = (Duration)list.stream().map(LongRunningSample::getDuration).reduce(Duration::plus).orElse(Duration.ZERO);
			this.fastestSample = (T)list.get(0);
			this.slowestSample = (T)list.get(list.size() - 1);
			this.secondSlowestSample = list.size() > 1 ? Optional.of((LongRunningSample)list.get(list.size() - 2)) : Optional.empty();
			this.count = list.size();
			this.quantiles = Quantiles.create(list.stream().mapToLong(sample -> sample.getDuration().toNanos()).toArray());
		}
	}
}
