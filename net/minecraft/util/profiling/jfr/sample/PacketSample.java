/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiling.jfr.sample;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.util.profiling.jfr.sample.TimedSample;

public record PacketSample(Instant time, String packetName, int bytes) implements TimedSample
{
    public static PacketSample fromEvent(RecordedEvent event) {
        return new PacketSample(event.getStartTime(), event.getString("packetName"), event.getInt("bytes"));
    }

    public static Statistics toStatistics(Duration duration, List<PacketSample> samples) {
        IntSummaryStatistics intSummaryStatistics = samples.stream().mapToInt(sample -> sample.bytes).summaryStatistics();
        long l = samples.size();
        long m = intSummaryStatistics.getSum();
        List<Pair<String, Long>> list = samples.stream().collect(Collectors.groupingBy(sample -> sample.packetName, Collectors.summingLong(sample -> sample.bytes))).entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(5L).map(entry -> Pair.of((String)entry.getKey(), (Long)entry.getValue())).toList();
        return new Statistics(l, m, list, duration);
    }

    @Override
    public Instant getTime() {
        return this.time;
    }

    public record Statistics(long count, long totalBytes, List<Pair<String, Long>> topContributors, Duration duration) {
        public double getCountPerSecond() {
            return (double)this.count / (double)this.duration.getSeconds();
        }

        public double getBytesPerSecond() {
            return (double)this.totalBytes / (double)this.duration.getSeconds();
        }
    }
}

