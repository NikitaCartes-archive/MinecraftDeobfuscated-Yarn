package net.minecraft.util.profiling.jfr;

import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.util.profiling.jfr.sample.ChunkGenerationSample;
import net.minecraft.util.profiling.jfr.sample.CpuLoadSample;
import net.minecraft.util.profiling.jfr.sample.FileIoSample;
import net.minecraft.util.profiling.jfr.sample.GcHeapSummarySample;
import net.minecraft.util.profiling.jfr.sample.LongRunningSampleStatistics;
import net.minecraft.util.profiling.jfr.sample.PacketSample;
import net.minecraft.util.profiling.jfr.sample.ServerTickTimeSample;
import net.minecraft.util.profiling.jfr.sample.ThreadAllocationStatisticsSample;
import net.minecraft.world.chunk.ChunkStatus;
import org.apache.commons.lang3.StringUtils;

public class JfrTextReport implements JfrReport {
	public static final String LINE_SEPARATOR = System.lineSeparator();
	public static final String MIN_AVERAGE_MAX_FORMAT = "min(%.2f%%) avg(%.2f%%) max(%.2f%%)";

	@Override
	public String toString(JfrProfile profile) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(String.format("Started: %s", profile.getStartTime())).append(LINE_SEPARATOR);
		stringBuilder.append(String.format("Ended: %s", profile.getEndTime())).append(LINE_SEPARATOR);
		stringBuilder.append(String.format("Duration: %s", profile.getDuration())).append(LINE_SEPARATOR);
		stringBuilder.append("== World gen ==").append(LINE_SEPARATOR);
		profile.getWorldGenDuration().ifPresent(duration -> stringBuilder.append("World gen: ").append(duration).append(LINE_SEPARATOR));
		this.addChunkGenSection(stringBuilder, profile.getChunkGenerationSampleStatistics());
		this.addGcSection(stringBuilder, profile.getGcHeapSummaryStatistics());
		this.addThreadAllocationSection(stringBuilder, profile.getThreadAllocationMap());
		this.addCpuUsageSection(stringBuilder, profile.getCpuLoadSamples());
		this.addServerTicksSection(stringBuilder, profile.getServerTickTimeSamples());
		stringBuilder.append(LINE_SEPARATOR).append("== Network IO ==").append(LINE_SEPARATOR);
		this.addPacketSection(stringBuilder, profile.getPacketSentStatistics(), "sent");
		this.addPacketSection(stringBuilder, profile.getPacketReadStatistics(), "received");
		this.addFileIoSections(stringBuilder, profile.getFileWriteStatistics(), profile.getFileReadStatistics());
		return stringBuilder.toString();
	}

	private void addThreadAllocationSection(StringBuilder builder, ThreadAllocationStatisticsSample.AllocationMap allocationMap) {
		builder.append(LINE_SEPARATOR).append("== Allocations /s by thread ==").append(LINE_SEPARATOR);
		allocationMap.allocations
			.forEach(
				(threadName, allocatedBytesPerSecond) -> builder.append(String.format("%s: %.2fKb", threadName, toKilobytes(allocatedBytesPerSecond)))
						.append(LINE_SEPARATOR)
			);
	}

	private void addFileIoSections(StringBuilder builder, FileIoSample.Statistics writeStatistics, FileIoSample.Statistics readStatistics) {
		builder.append(LINE_SEPARATOR).append("== File IO ==").append(LINE_SEPARATOR);
		builder.append(String.format("Bytes written /s: %.2fKb", toKilobytes(writeStatistics.getBytesPerSecond()))).append(LINE_SEPARATOR);
		builder.append(String.format("Total writes: %.2fKb", toKilobytes(writeStatistics.getTotalBytes()))).append(LINE_SEPARATOR);
		builder.append("Time spent writing: ").append(writeStatistics.getTotalDuration()).append(LINE_SEPARATOR);
		builder.append("Top write contributors:").append(LINE_SEPARATOR);
		writeStatistics.getTopContributors()
			.limit(10L)
			.forEach(entry -> builder.append((String)entry.getLeft()).append(String.format(": %.2fKb", toKilobytes((Long)entry.getRight()))).append(LINE_SEPARATOR));
		builder.append(String.format("Bytes read /s: %.2fKb", toKilobytes(readStatistics.getBytesPerSecond()))).append(LINE_SEPARATOR);
		builder.append(String.format("Total read bytes: %.2fKb", toKilobytes(readStatistics.getTotalBytes()))).append(LINE_SEPARATOR);
		builder.append("Time spent reading: ").append(readStatistics.getTotalDuration()).append(LINE_SEPARATOR);
		builder.append("Top read contributors:").append(LINE_SEPARATOR);
		readStatistics.getTopContributors()
			.limit(10L)
			.forEach(entry -> builder.append((String)entry.getLeft()).append(String.format(": %.2fKb", toKilobytes((Long)entry.getRight()))).append(LINE_SEPARATOR));
	}

	private void addPacketSection(StringBuilder builder, PacketSample.Statistics statistics, String type) {
		builder.append(String.format("Total packets %s: count(%s) size(%.2fMb)", type, statistics.getCount(), toMegabytes(statistics.getTotalBytes())))
			.append(LINE_SEPARATOR);
		builder.append(
				String.format("Packets %s / second: count(%.2f) size(%.2fKb)", type, statistics.getCountPerSecond(), toKilobytes(statistics.getBytesPerSecond()))
			)
			.append(LINE_SEPARATOR);
		builder.append(String.format("Top %s by total size:", type)).append(LINE_SEPARATOR);

		for (Pair<String, Long> pair : statistics.getTopContributors()) {
			String string = pair.getFirst();
			long l = pair.getSecond();
			double d = (double)l / (double)statistics.getDuration().getSeconds();
			builder.append(string).append(": ").append(String.format("total(%.2fKb) /s(%.2fKb)", toKilobytes(l), toKilobytes(d))).append(LINE_SEPARATOR);
		}
	}

	private void addServerTicksSection(StringBuilder builder, List<ServerTickTimeSample> samples) {
		builder.append(LINE_SEPARATOR).append("== Server ticks ==").append(LINE_SEPARATOR);
		DoubleSummaryStatistics doubleSummaryStatistics = samples.stream().mapToDouble(sample -> (double)sample.averageTickMs).summaryStatistics();
		builder.append("tick time (ms): ")
			.append(
				String.format("min(%.2f) avg(%.2f) max(%.2f)", doubleSummaryStatistics.getMin(), doubleSummaryStatistics.getAverage(), doubleSummaryStatistics.getMax())
			)
			.append(LINE_SEPARATOR);
	}

	private void addCpuUsageSection(StringBuilder builder, List<CpuLoadSample> samples) {
		builder.append(LINE_SEPARATOR).append("== CPU usage ==").append(LINE_SEPARATOR);
		int i = 100;
		DoubleSummaryStatistics doubleSummaryStatistics = samples.stream().mapToDouble(sample -> sample.jvm * (double)i).summaryStatistics();
		DoubleSummaryStatistics doubleSummaryStatistics2 = samples.stream().mapToDouble(sample -> sample.system * (double)i).summaryStatistics();
		DoubleSummaryStatistics doubleSummaryStatistics3 = samples.stream().mapToDouble(sample -> sample.userJvm * (double)i).summaryStatistics();
		builder.append("jvm: ")
			.append(
				String.format(
					"min(%.2f%%) avg(%.2f%%) max(%.2f%%)", doubleSummaryStatistics.getMin(), doubleSummaryStatistics.getAverage(), doubleSummaryStatistics.getMax()
				)
			)
			.append(LINE_SEPARATOR);
		builder.append("userJvm: ")
			.append(
				String.format(
					"min(%.2f%%) avg(%.2f%%) max(%.2f%%)", doubleSummaryStatistics3.getMin(), doubleSummaryStatistics3.getAverage(), doubleSummaryStatistics3.getMax()
				)
			)
			.append(LINE_SEPARATOR);
		builder.append("system: ")
			.append(
				String.format(
					"min(%.2f%%) avg(%.2f%%) max(%.2f%%)", doubleSummaryStatistics2.getMin(), doubleSummaryStatistics2.getAverage(), doubleSummaryStatistics2.getMax()
				)
			)
			.append(LINE_SEPARATOR);
	}

	private void addGcSection(StringBuilder builder, GcHeapSummarySample.Statistics statistics) {
		builder.append(LINE_SEPARATOR).append("== Garbage collections ==").append(LINE_SEPARATOR);
		builder.append("Total duration: ").append(statistics.gcDuration).append(LINE_SEPARATOR);
		builder.append("Number of GC's: ").append(statistics.count).append(LINE_SEPARATOR);
		builder.append(String.format("GC overhead: %.2f%%", statistics.gcDurationRatio() * 100.0F)).append(LINE_SEPARATOR);
		builder.append(String.format("Allocation rate /s: %.2fMb", toMegabytes(statistics.allocatedBytesPerSecond))).append(LINE_SEPARATOR);
	}

	private void addChunkGenSection(StringBuilder builder, List<Pair<ChunkStatus, LongRunningSampleStatistics<ChunkGenerationSample>>> statistics) {
		int i = statistics.stream().mapToInt(pairx -> ((ChunkStatus)pairx.getFirst()).getId().length()).max().getAsInt();

		for (Pair<ChunkStatus, LongRunningSampleStatistics<ChunkGenerationSample>> pair : statistics) {
			LongRunningSampleStatistics<ChunkGenerationSample> longRunningSampleStatistics = pair.getSecond();
			builder.append(StringUtils.rightPad(pair.getFirst().getId(), i)).append(" : ").append(this.format(longRunningSampleStatistics)).append(LINE_SEPARATOR);
		}
	}

	private String format(LongRunningSampleStatistics<ChunkGenerationSample> statistics) {
		return String.format(
			"(%s), count(%s), total_duration(%s), slowest(%s), second_slowest(%s), fastest(%s)",
			Arrays.stream(LongRunningSampleStatistics.QUANTILES)
				.mapToObj(quantile -> String.format("p%d=%s", quantile, statistics.quantiles.get(quantile)))
				.collect(Collectors.joining("/")),
			statistics.count,
			statistics.totalDuration,
			this.format(statistics.slowestSample),
			statistics.secondSlowestSample.map(this::format).orElse("n/a"),
			this.format(statistics.fastestSample)
		);
	}

	private String format(ChunkGenerationSample sample) {
		return String.format(
			"%sms, chunkPos: %s, blockPos: %s",
			sample.duration.toMillis(),
			"[" + sample.chunkPos.x + ", " + sample.chunkPos.z + "]",
			"[" + sample.centerPos.getX() + ", " + sample.centerPos.getY() + ", " + sample.centerPos.getZ() + "]"
		);
	}

	private static double toMegabytes(long bytes) {
		return toKilobytes(bytes) / 1024.0;
	}

	private static double toMegabytes(double bytes) {
		return toKilobytes(bytes) / 1024.0;
	}

	private static double toKilobytes(long bytes) {
		return (double)bytes / 1024.0;
	}

	private static double toKilobytes(double bytes) {
		return bytes / 1024.0;
	}
}
