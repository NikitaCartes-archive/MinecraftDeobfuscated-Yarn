package net.minecraft.util.profiling.jfr;

import com.mojang.datafixers.util.Pair;
import java.lang.runtime.ObjectMethods;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

public final class JfrProfile extends Record {
	private final Instant startTime;
	private final Instant endTime;
	private final Duration duration;
	@Nullable
	private final Duration worldGenDuration;
	private final List<ServerTickTimeSample> serverTickTimeSamples;
	private final List<CpuLoadSample> cpuLoadSamples;
	private final GcHeapSummarySample.Statistics gcHeapSummaryStatistics;
	private final ThreadAllocationStatisticsSample.AllocationMap threadAllocationMap;
	private final NetworkIoStatistics packetReadStatistics;
	private final NetworkIoStatistics packetSentStatistics;
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
		NetworkIoStatistics networkIoStatistics,
		NetworkIoStatistics networkIoStatistics2,
		FileIoSample.Statistics statistics2,
		FileIoSample.Statistics statistics3,
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
		this.packetReadStatistics = networkIoStatistics;
		this.packetSentStatistics = networkIoStatistics2;
		this.fileWriteStatistics = statistics2;
		this.fileReadStatistics = statistics3;
		this.chunkGenerationSamples = list3;
	}

	public List<Pair<ChunkStatus, LongRunningSampleStatistics<ChunkGenerationSample>>> getChunkGenerationSampleStatistics() {
		Map<ChunkStatus, List<ChunkGenerationSample>> map = (Map)this.chunkGenerationSamples
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

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",JfrProfile,"recordingStarted;recordingEnded;recordingDuration;worldCreationDuration;tickTimes;cpuLoadStats;heapSummary;threadAllocationSummary;receivedPacketsSummary;sentPacketsSummary;fileWrites;fileReads;chunkGenStats",JfrProfile::startTime,JfrProfile::endTime,JfrProfile::duration,JfrProfile::worldGenDuration,JfrProfile::serverTickTimeSamples,JfrProfile::cpuLoadSamples,JfrProfile::gcHeapSummaryStatistics,JfrProfile::threadAllocationMap,JfrProfile::packetReadStatistics,JfrProfile::packetSentStatistics,JfrProfile::fileWriteStatistics,JfrProfile::fileReadStatistics,JfrProfile::chunkGenerationSamples>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",JfrProfile,"recordingStarted;recordingEnded;recordingDuration;worldCreationDuration;tickTimes;cpuLoadStats;heapSummary;threadAllocationSummary;receivedPacketsSummary;sentPacketsSummary;fileWrites;fileReads;chunkGenStats",JfrProfile::startTime,JfrProfile::endTime,JfrProfile::duration,JfrProfile::worldGenDuration,JfrProfile::serverTickTimeSamples,JfrProfile::cpuLoadSamples,JfrProfile::gcHeapSummaryStatistics,JfrProfile::threadAllocationMap,JfrProfile::packetReadStatistics,JfrProfile::packetSentStatistics,JfrProfile::fileWriteStatistics,JfrProfile::fileReadStatistics,JfrProfile::chunkGenerationSamples>(
			this
		);
	}

	public final boolean equals(Object o) {
		return ObjectMethods.bootstrap<"equals",JfrProfile,"recordingStarted;recordingEnded;recordingDuration;worldCreationDuration;tickTimes;cpuLoadStats;heapSummary;threadAllocationSummary;receivedPacketsSummary;sentPacketsSummary;fileWrites;fileReads;chunkGenStats",JfrProfile::startTime,JfrProfile::endTime,JfrProfile::duration,JfrProfile::worldGenDuration,JfrProfile::serverTickTimeSamples,JfrProfile::cpuLoadSamples,JfrProfile::gcHeapSummaryStatistics,JfrProfile::threadAllocationMap,JfrProfile::packetReadStatistics,JfrProfile::packetSentStatistics,JfrProfile::fileWriteStatistics,JfrProfile::fileReadStatistics,JfrProfile::chunkGenerationSamples>(
			this, o
		);
	}

	public Instant startTime() {
		return this.startTime;
	}

	public Instant endTime() {
		return this.endTime;
	}

	public Duration duration() {
		return this.duration;
	}

	@Nullable
	public Duration worldGenDuration() {
		return this.worldGenDuration;
	}

	public List<ServerTickTimeSample> serverTickTimeSamples() {
		return this.serverTickTimeSamples;
	}

	public List<CpuLoadSample> cpuLoadSamples() {
		return this.cpuLoadSamples;
	}

	public GcHeapSummarySample.Statistics gcHeapSummaryStatistics() {
		return this.gcHeapSummaryStatistics;
	}

	public ThreadAllocationStatisticsSample.AllocationMap threadAllocationMap() {
		return this.threadAllocationMap;
	}

	public NetworkIoStatistics packetReadStatistics() {
		return this.packetReadStatistics;
	}

	public NetworkIoStatistics packetSentStatistics() {
		return this.packetSentStatistics;
	}

	public FileIoSample.Statistics fileWriteStatistics() {
		return this.fileWriteStatistics;
	}

	public FileIoSample.Statistics fileReadStatistics() {
		return this.fileReadStatistics;
	}

	public List<ChunkGenerationSample> chunkGenerationSamples() {
		return this.chunkGenerationSamples;
	}
}
