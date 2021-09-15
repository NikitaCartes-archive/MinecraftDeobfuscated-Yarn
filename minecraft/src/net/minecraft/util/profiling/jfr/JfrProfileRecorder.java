package net.minecraft.util.profiling.jfr;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import net.minecraft.util.profiling.jfr.sample.ChunkGenerationSample;
import net.minecraft.util.profiling.jfr.sample.CpuLoadSample;
import net.minecraft.util.profiling.jfr.sample.FileIoSample;
import net.minecraft.util.profiling.jfr.sample.GcHeapSummarySample;
import net.minecraft.util.profiling.jfr.sample.PacketSample;
import net.minecraft.util.profiling.jfr.sample.ServerTickTimeSample;
import net.minecraft.util.profiling.jfr.sample.ThreadAllocationStatisticsSample;

public class JfrProfileRecorder {
	private Instant startTime = Instant.EPOCH;
	private Instant endTime = Instant.EPOCH;
	private final List<ChunkGenerationSample> chunkGenerationSamples = Lists.<ChunkGenerationSample>newArrayList();
	private final List<CpuLoadSample> cpuLoadSamples = Lists.<CpuLoadSample>newArrayList();
	private final List<PacketSample> packetReadSamples = Lists.<PacketSample>newArrayList();
	private final List<PacketSample> packetSentSamples = Lists.<PacketSample>newArrayList();
	private final List<FileIoSample> fileWriteSamples = Lists.<FileIoSample>newArrayList();
	private final List<FileIoSample> fileReadSamples = Lists.<FileIoSample>newArrayList();
	private int gcCount;
	private Duration gcDuration = Duration.ZERO;
	private final List<GcHeapSummarySample> gcHeapSummarySamples = Lists.<GcHeapSummarySample>newArrayList();
	private final List<ThreadAllocationStatisticsSample> threadAllocationStatisticsSamples = Lists.<ThreadAllocationStatisticsSample>newArrayList();
	private final List<ServerTickTimeSample> serverTickTimeSamples = Lists.<ServerTickTimeSample>newArrayList();
	@Nullable
	private Duration worldGenDuration = null;

	private JfrProfileRecorder(Stream<RecordedEvent> events) {
		this.handleEvents(events);
	}

	public static JfrProfile readProfile(Path path) {
		try {
			final RecordingFile recordingFile = new RecordingFile(path);

			JfrProfile var4;
			try {
				Iterator<RecordedEvent> iterator = new Iterator<RecordedEvent>() {
					public boolean hasNext() {
						return recordingFile.hasMoreEvents();
					}

					public RecordedEvent next() {
						if (!this.hasNext()) {
							throw new NoSuchElementException();
						} else {
							try {
								return recordingFile.readEvent();
							} catch (IOException var2) {
								throw new UncheckedIOException(var2);
							}
						}
					}
				};
				Stream<RecordedEvent> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 1297), false);
				var4 = new JfrProfileRecorder(stream).createProfile();
			} catch (Throwable var6) {
				try {
					recordingFile.close();
				} catch (Throwable var5) {
					var6.addSuppressed(var5);
				}

				throw var6;
			}

			recordingFile.close();
			return var4;
		} catch (IOException var7) {
			throw new UncheckedIOException(var7);
		}
	}

	private JfrProfile createProfile() {
		Duration duration = Duration.between(this.startTime, this.endTime);
		return new JfrProfile(
			this.startTime,
			this.endTime,
			duration,
			this.worldGenDuration,
			this.serverTickTimeSamples,
			this.cpuLoadSamples,
			GcHeapSummarySample.toStatistics(duration, this.gcHeapSummarySamples, this.gcDuration, this.gcCount),
			ThreadAllocationStatisticsSample.toAllocationMap(this.threadAllocationStatisticsSamples),
			PacketSample.toStatistics(duration, this.packetReadSamples),
			PacketSample.toStatistics(duration, this.packetSentSamples),
			FileIoSample.toStatistics(duration, this.fileWriteSamples),
			FileIoSample.toStatistics(duration, this.fileReadSamples),
			this.chunkGenerationSamples
		);
	}

	private void handleEvents(Stream<RecordedEvent> events) {
		events.forEach(event -> {
			if (event.getEndTime().isAfter(this.endTime) || this.endTime.equals(Instant.EPOCH)) {
				this.endTime = event.getEndTime();
			}

			if (event.getStartTime().isBefore(this.startTime) || this.startTime.equals(Instant.EPOCH)) {
				this.startTime = event.getStartTime();
			}

			String var2 = event.getEventType().getName();
			switch (var2) {
				case "minecraft.ChunkGeneration":
					this.chunkGenerationSamples.add(ChunkGenerationSample.fromEvent(event));
					break;
				case "minecraft.WorldLoadFinishedEvent":
					this.worldGenDuration = event.getDuration();
					break;
				case "minecraft.ServerTickTime":
					this.serverTickTimeSamples.add(ServerTickTimeSample.fromEvent(event));
					break;
				case "minecraft.PacketRead":
					this.packetReadSamples.add(PacketSample.fromEvent(event));
					break;
				case "minecraft.PacketSent":
					this.packetSentSamples.add(PacketSample.fromEvent(event));
					break;
				case "jdk.ThreadAllocationStatistics":
					this.threadAllocationStatisticsSamples.add(ThreadAllocationStatisticsSample.fromEvent(event));
					break;
				case "jdk.GCHeapSummary":
					this.gcHeapSummarySamples.add(GcHeapSummarySample.fromEvent(event));
					break;
				case "jdk.CPULoad":
					this.cpuLoadSamples.add(CpuLoadSample.fromEvent(event));
					break;
				case "jdk.FileWrite":
					this.addFileIoSample(event, this.fileWriteSamples, "bytesWritten");
					break;
				case "jdk.FileRead":
					this.addFileIoSample(event, this.fileReadSamples, "bytesRead");
					break;
				case "jdk.GarbageCollection":
					this.gcCount++;
					this.gcDuration = this.gcDuration.plus(event.getDuration());
			}
		});
	}

	private void addFileIoSample(RecordedEvent event, List<FileIoSample> samples, String bytesKey) {
		samples.add(new FileIoSample(event.getDuration(), event.getString("path"), event.getLong(bytesKey)));
	}
}
