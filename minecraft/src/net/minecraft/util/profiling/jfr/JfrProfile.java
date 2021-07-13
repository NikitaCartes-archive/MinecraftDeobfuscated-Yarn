package net.minecraft.util.profiling.jfr;

import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

public class JfrProfile {
	private final Instant startTime;
	private final Instant endTime;
	private final JfrProfile.OptionalDuration worldGenDuration;
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
		Instant startTime,
		Instant endTime,
		JfrProfile.OptionalDuration worldGenDuration,
		List<ServerTickTimeSample> serverTickTimeSamples,
		List<CpuLoadSample> cpuLoadSamples,
		GcHeapSummarySample.Statistics gcHeapSummaryStatistics,
		ThreadAllocationStatisticsSample.AllocationMap threadAllocationMap,
		PacketSample.Statistics packetReadStatistics,
		PacketSample.Statistics packetSentStatistics,
		FileIoSample.Statistics fileWriteStatistics,
		FileIoSample.Statistics fileReadStatistics,
		List<ChunkGenerationSample> chunkGenerationSamples
	) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.worldGenDuration = worldGenDuration;
		this.serverTickTimeSamples = serverTickTimeSamples;
		this.cpuLoadSamples = cpuLoadSamples;
		this.gcHeapSummaryStatistics = gcHeapSummaryStatistics;
		this.threadAllocationMap = threadAllocationMap;
		this.packetReadStatistics = packetReadStatistics;
		this.packetSentStatistics = packetSentStatistics;
		this.fileWriteStatistics = fileWriteStatistics;
		this.fileReadStatistics = fileReadStatistics;
		this.chunkGenerationSamples = chunkGenerationSamples;
	}

	public Instant getStartTime() {
		return this.startTime;
	}

	public Instant getEndTime() {
		return this.endTime;
	}

	public Duration getDuration() {
		return Duration.between(this.startTime, this.endTime);
	}

	public Optional<Duration> getWorldGenDuration() {
		return this.worldGenDuration.toOptional();
	}

	public List<ServerTickTimeSample> getServerTickTimeSamples() {
		return this.serverTickTimeSamples;
	}

	public GcHeapSummarySample.Statistics getGcHeapSummaryStatistics() {
		return this.gcHeapSummaryStatistics;
	}

	public ThreadAllocationStatisticsSample.AllocationMap getThreadAllocationMap() {
		return this.threadAllocationMap;
	}

	public PacketSample.Statistics getPacketReadStatistics() {
		return this.packetReadStatistics;
	}

	public PacketSample.Statistics getPacketSentStatistics() {
		return this.packetSentStatistics;
	}

	public FileIoSample.Statistics getFileWriteStatistics() {
		return this.fileWriteStatistics;
	}

	public FileIoSample.Statistics getFileReadStatistics() {
		return this.fileReadStatistics;
	}

	public List<Pair<ChunkStatus, LongRunningSampleStatistics<ChunkGenerationSample>>> getChunkGenerationSampleStatistics() {
		Map<ChunkStatus, List<ChunkGenerationSample>> map = (Map<ChunkStatus, List<ChunkGenerationSample>>)this.chunkGenerationSamples
			.stream()
			.collect(Collectors.groupingBy(sample -> sample.chunkStatus));
		return (List<Pair<ChunkStatus, LongRunningSampleStatistics<ChunkGenerationSample>>>)map.entrySet()
			.stream()
			.map(entry -> Pair.of((ChunkStatus)entry.getKey(), new LongRunningSampleStatistics((List)entry.getValue())))
			.sorted(Comparator.comparing(entry -> ((LongRunningSampleStatistics)entry.getSecond()).totalDuration).reversed())
			.collect(Collectors.toList());
	}

	public List<CpuLoadSample> getCpuLoadSamples() {
		return this.cpuLoadSamples;
	}

	public String collect() {
		return new JfrTextReport().toString(this);
	}

	public String collectJson() throws IOException {
		return new JfrJsonReport().toString(this);
	}

	public static class OptionalDuration {
		@Nullable
		private final Duration duration;

		public OptionalDuration(@Nullable Duration duration) {
			this.duration = duration;
		}

		Optional<Duration> toOptional() {
			return Optional.ofNullable(this.duration);
		}
	}
}
