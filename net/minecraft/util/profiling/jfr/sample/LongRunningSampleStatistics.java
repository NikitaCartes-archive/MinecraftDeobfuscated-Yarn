/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.util.math.Quantiles;
import net.minecraft.util.profiling.jfr.sample.LongRunningSample;
import org.jetbrains.annotations.Nullable;

public record LongRunningSampleStatistics<T extends LongRunningSample>(T fastestSample, T slowestSample, @Nullable T secondSlowestSample, int count, Map<Integer, Double> quantiles, Duration totalDuration) {
    public static <T extends LongRunningSample> LongRunningSampleStatistics<T> fromSamples(List<T> samples) {
        if (samples.isEmpty()) {
            throw new IllegalArgumentException("No values");
        }
        List<LongRunningSample> list = samples.stream().sorted(Comparator.comparing(LongRunningSample::duration)).toList();
        Duration duration = list.stream().map(LongRunningSample::duration).reduce(Duration::plus).orElse(Duration.ZERO);
        LongRunningSample longRunningSample = list.get(0);
        LongRunningSample longRunningSample2 = list.get(list.size() - 1);
        LongRunningSample longRunningSample3 = list.size() > 1 ? list.get(list.size() - 2) : null;
        int i = list.size();
        Map<Integer, Double> map = Quantiles.create(list.stream().mapToLong(sample -> sample.duration().toNanos()).toArray());
        return new LongRunningSampleStatistics<LongRunningSample>(longRunningSample, longRunningSample2, longRunningSample3, i, map, duration);
    }

    @Nullable
    public T secondSlowestSample() {
        return this.secondSlowestSample;
    }
}

