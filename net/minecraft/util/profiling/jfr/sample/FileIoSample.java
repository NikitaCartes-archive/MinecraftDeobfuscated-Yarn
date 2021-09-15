/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiling.jfr.sample;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public record FileIoSample(Duration duration, @Nullable String path, long bytes) {
    public static Statistics toStatistics(Duration duration, List<FileIoSample> samples) {
        long l = samples.stream().mapToLong(sample -> sample.bytes).sum();
        return new Statistics(l, (double)l / (double)duration.getSeconds(), samples.size(), (double)samples.size() / (double)duration.getSeconds(), samples.stream().map(FileIoSample::duration).reduce(Duration.ZERO, Duration::plus), samples.stream().filter(sample -> sample.path != null).collect(Collectors.groupingBy(sample -> sample.path, Collectors.summingLong(sample -> sample.bytes))).entrySet().stream().sorted(Map.Entry.comparingByValue().reversed()).map(entry -> Pair.of((String)entry.getKey(), (Long)entry.getValue())).limit(10L).toList());
    }

    @Nullable
    public String path() {
        return this.path;
    }

    public record Statistics(long totalBytes, double bytesPerSecond, long count, double countPerSecond, Duration totalDuration, List<Pair<String, Long>> topContributors) {
    }
}

