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
import net.minecraft.util.profiling.jfr.sample.PacketSample;
import net.minecraft.util.profiling.jfr.sample.ServerTickTimeSample;
import net.minecraft.util.profiling.jfr.sample.ThreadAllocationStatisticsSample;
import net.minecraft.world.chunk.ChunkStatus;

public record JfrProfile() {
	private final Instant startTime;
	private final Instant endTime;
	private final Duration duration;
	@Nullable
	private final Duration worldGenDuration;
	private final List<ServerTickTimeSample> serverTickTimeSamples;
	private final List<CpuLoadSample> cpuLoadSamples;
	private final GcHeapSummarySample.Statistics gcHeapSummaryStatistics;
	private final ThreadAllocationStatisticsSample.AllocationMap threadAllocationMap;
	private final PacketSample.Statistics packetReadStatistics;
	private final PacketSample.Statistics packetSentStatistics;
	private final FileIoSample.Statistics fileWriteStatistics;
	private final FileIoSample.Statistics fileReadStatistics;
	private final List<ChunkGenerationSample> chunkGenerationSamples;

	public JfrProfile(
		Instant instant,
		Instant instant2,
		Duration duration,
		@Nullable Duration duration2,
		List<ServerTickTimeSample> list,
		List<CpuLoadSample> list2,
		GcHeapSummarySample.Statistics statistics,
		ThreadAllocationStatisticsSample.AllocationMap allocationMap,
		PacketSample.Statistics statistics2,
		PacketSample.Statistics statistics3,
		FileIoSample.Statistics statistics4,
		FileIoSample.Statistics statistics5,
		List<ChunkGenerationSample> list3
	) {
		this.startTime = instant;
		this.endTime = instant2;
		this.duration = duration;
		this.worldGenDuration = duration2;
		this.serverTickTimeSamples = list;
		this.cpuLoadSamples = list2;
		this.gcHeapSummaryStatistics = statistics;
		this.threadAllocationMap = allocationMap;
		this.packetReadStatistics = statistics2;
		this.packetSentStatistics = statistics3;
		this.fileWriteStatistics = statistics4;
		this.fileReadStatistics = statistics5;
		this.chunkGenerationSamples = list3;
	}

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
