package net.minecraft.util.profiling.jfr;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.jfr.sample.ChunkGenerationSample;
import net.minecraft.util.profiling.jfr.sample.CpuLoadSample;
import net.minecraft.util.profiling.jfr.sample.FileIoSample;
import net.minecraft.util.profiling.jfr.sample.GcHeapSummarySample;
import net.minecraft.util.profiling.jfr.sample.LongRunningSampleStatistics;
import net.minecraft.util.profiling.jfr.sample.NetworkIoStatistics;
import net.minecraft.util.profiling.jfr.sample.ServerTickTimeSample;
import net.minecraft.util.profiling.jfr.sample.ThreadAllocationStatisticsSample;
import net.minecraft.world.chunk.ChunkStatus;

public record JfrProfile(
	Instant startTime,
	Instant endTime,
	Duration duration,
	@Nullable Duration worldGenDuration,
	List<ServerTickTimeSample> serverTickTimeSamples,
	List<CpuLoadSample> cpuLoadSamples,
	GcHeapSummarySample.Statistics gcHeapSummaryStatistics,
	ThreadAllocationStatisticsSample.AllocationMap threadAllocationMap,
	NetworkIoStatistics packetReadStatistics,
	NetworkIoStatistics packetSentStatistics,
	FileIoSample.Statistics fileWriteStatistics,
	FileIoSample.Statistics fileReadStatistics,
	List<ChunkGenerationSample> chunkGenerationSamples
) {
	public List<Pair<ChunkStatus, LongRunningSampleStatistics<ChunkGenerationSample>>> getChunkGenerationSampleStatistics() {
		Map<ChunkStatus, List<ChunkGenerationSample>> map = (Map<ChunkStatus, List<ChunkGenerationSample>>)this.chunkGenerationSamples
			.stream()
			.collect(Collectors.groupingBy(ChunkGenerationSample::chunkStatus));
		return map.entrySet()
			.stream()
			.map(entry -> Pair.of((ChunkStatus)entry.getKey(), LongRunningSampleStatistics.fromSamples((List)entry.getValue())))
			.sorted(Comparator.comparing(pair -> ((LongRunningSampleStatistics)pair.getSecond()).totalDuration()).reversed())
			.toList();
	}

	public String toJson() {
		return new JfrJsonReport().toString(this);
	}
}
