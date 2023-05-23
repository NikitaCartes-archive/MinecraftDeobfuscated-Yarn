package net.minecraft.util.profiling.jfr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.LongSerializationPolicy;
import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.DoubleStream;
import net.minecraft.util.Util;
import net.minecraft.util.math.Quantiles;
import net.minecraft.util.profiling.jfr.sample.ChunkGenerationSample;
import net.minecraft.util.profiling.jfr.sample.CpuLoadSample;
import net.minecraft.util.profiling.jfr.sample.FileIoSample;
import net.minecraft.util.profiling.jfr.sample.GcHeapSummarySample;
import net.minecraft.util.profiling.jfr.sample.LongRunningSampleStatistics;
import net.minecraft.util.profiling.jfr.sample.NetworkIoStatistics;
import net.minecraft.util.profiling.jfr.sample.ServerTickTimeSample;
import net.minecraft.util.profiling.jfr.sample.ThreadAllocationStatisticsSample;
import net.minecraft.world.chunk.ChunkStatus;

public class JfrJsonReport {
	private static final String BYTES_PER_SECOND = "bytesPerSecond";
	private static final String COUNT = "count";
	private static final String DURATION_NANOS_TOTAL = "durationNanosTotal";
	private static final String TOTAL_BYTES = "totalBytes";
	private static final String COUNT_PER_SECOND = "countPerSecond";
	final Gson gson = new GsonBuilder().setPrettyPrinting().setLongSerializationPolicy(LongSerializationPolicy.DEFAULT).create();

	public String toString(JfrProfile profile) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("startedEpoch", profile.startTime().toEpochMilli());
		jsonObject.addProperty("endedEpoch", profile.endTime().toEpochMilli());
		jsonObject.addProperty("durationMs", profile.duration().toMillis());
		Duration duration = profile.worldGenDuration();
		if (duration != null) {
			jsonObject.addProperty("worldGenDurationMs", duration.toMillis());
		}

		jsonObject.add("heap", this.collectHeapSection(profile.gcHeapSummaryStatistics()));
		jsonObject.add("cpuPercent", this.collectCpuPercentSection(profile.cpuLoadSamples()));
		jsonObject.add("network", this.collectNetworkSection(profile));
		jsonObject.add("fileIO", this.collectFileIoSection(profile));
		jsonObject.add("serverTick", this.collectServerTickSection(profile.serverTickTimeSamples()));
		jsonObject.add("threadAllocation", this.collectThreadAllocationSection(profile.threadAllocationMap()));
		jsonObject.add("chunkGen", this.collectChunkGenSection(profile.getChunkGenerationSampleStatistics()));
		return this.gson.toJson((JsonElement)jsonObject);
	}

	private JsonElement collectHeapSection(GcHeapSummarySample.Statistics statistics) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("allocationRateBytesPerSecond", statistics.allocatedBytesPerSecond());
		jsonObject.addProperty("gcCount", statistics.count());
		jsonObject.addProperty("gcOverHeadPercent", statistics.getGcDurationRatio());
		jsonObject.addProperty("gcTotalDurationMs", statistics.gcDuration().toMillis());
		return jsonObject;
	}

	private JsonElement collectChunkGenSection(List<Pair<ChunkStatus, LongRunningSampleStatistics<ChunkGenerationSample>>> statistics) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(
			"durationNanosTotal", statistics.stream().mapToDouble(pairx -> (double)((LongRunningSampleStatistics)pairx.getSecond()).totalDuration().toNanos()).sum()
		);
		JsonArray jsonArray = Util.make(new JsonArray(), json -> jsonObject.add("status", json));

		for(Pair<ChunkStatus, LongRunningSampleStatistics<ChunkGenerationSample>> pair : statistics) {
			LongRunningSampleStatistics<ChunkGenerationSample> longRunningSampleStatistics = (LongRunningSampleStatistics)pair.getSecond();
			JsonObject jsonObject2 = Util.make(new JsonObject(), jsonArray::add);
			jsonObject2.addProperty("state", pair.getFirst().toString());
			jsonObject2.addProperty("count", longRunningSampleStatistics.count());
			jsonObject2.addProperty("durationNanosTotal", longRunningSampleStatistics.totalDuration().toNanos());
			jsonObject2.addProperty("durationNanosAvg", longRunningSampleStatistics.totalDuration().toNanos() / (long)longRunningSampleStatistics.count());
			JsonObject jsonObject3 = Util.make(new JsonObject(), json -> jsonObject2.add("durationNanosPercentiles", json));
			longRunningSampleStatistics.quantiles().forEach((quantile, value) -> jsonObject3.addProperty("p" + quantile, value));
			Function<ChunkGenerationSample, JsonElement> function = sample -> {
				JsonObject jsonObjectxx = new JsonObject();
				jsonObjectxx.addProperty("durationNanos", sample.duration().toNanos());
				jsonObjectxx.addProperty("level", sample.worldKey());
				jsonObjectxx.addProperty("chunkPosX", sample.chunkPos().x);
				jsonObjectxx.addProperty("chunkPosZ", sample.chunkPos().z);
				jsonObjectxx.addProperty("worldPosX", sample.centerPos().x());
				jsonObjectxx.addProperty("worldPosZ", sample.centerPos().z());
				return jsonObjectxx;
			};
			jsonObject2.add("fastest", (JsonElement)function.apply(longRunningSampleStatistics.fastestSample()));
			jsonObject2.add("slowest", (JsonElement)function.apply(longRunningSampleStatistics.slowestSample()));
			jsonObject2.add(
				"secondSlowest",
				(JsonElement)(longRunningSampleStatistics.secondSlowestSample() != null
					? (JsonElement)function.apply(longRunningSampleStatistics.secondSlowestSample())
					: JsonNull.INSTANCE)
			);
		}

		return jsonObject;
	}

	private JsonElement collectThreadAllocationSection(ThreadAllocationStatisticsSample.AllocationMap statistics) {
		JsonArray jsonArray = new JsonArray();
		statistics.allocations().forEach((threadName, allocation) -> jsonArray.add(Util.make(new JsonObject(), json -> {
				json.addProperty("thread", threadName);
				json.addProperty("bytesPerSecond", allocation);
			})));
		return jsonArray;
	}

	private JsonElement collectServerTickSection(List<ServerTickTimeSample> samples) {
		if (samples.isEmpty()) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			double[] ds = samples.stream().mapToDouble(sample -> (double)sample.averageTickMs().toNanos() / 1000000.0).toArray();
			DoubleSummaryStatistics doubleSummaryStatistics = DoubleStream.of(ds).summaryStatistics();
			jsonObject.addProperty("minMs", doubleSummaryStatistics.getMin());
			jsonObject.addProperty("averageMs", doubleSummaryStatistics.getAverage());
			jsonObject.addProperty("maxMs", doubleSummaryStatistics.getMax());
			Map<Integer, Double> map = Quantiles.create(ds);
			map.forEach((quantile, value) -> jsonObject.addProperty("p" + quantile, value));
			return jsonObject;
		}
	}

	private JsonElement collectFileIoSection(JfrProfile profile) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("write", this.collectFileIoSection(profile.fileWriteStatistics()));
		jsonObject.add("read", this.collectFileIoSection(profile.fileReadStatistics()));
		return jsonObject;
	}

	private JsonElement collectFileIoSection(FileIoSample.Statistics statistics) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("totalBytes", statistics.totalBytes());
		jsonObject.addProperty("count", statistics.count());
		jsonObject.addProperty("bytesPerSecond", statistics.bytesPerSecond());
		jsonObject.addProperty("countPerSecond", statistics.countPerSecond());
		JsonArray jsonArray = new JsonArray();
		jsonObject.add("topContributors", jsonArray);
		statistics.topContributors().forEach(pair -> {
			JsonObject jsonObjectxx = new JsonObject();
			jsonArray.add(jsonObjectxx);
			jsonObjectxx.addProperty("path", (String)pair.getFirst());
			jsonObjectxx.addProperty("totalBytes", (Number)pair.getSecond());
		});
		return jsonObject;
	}

	private JsonElement collectNetworkSection(JfrProfile profile) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("sent", this.collectPacketSection(profile.packetSentStatistics()));
		jsonObject.add("received", this.collectPacketSection(profile.packetReadStatistics()));
		return jsonObject;
	}

	private JsonElement collectPacketSection(NetworkIoStatistics statistics) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("totalBytes", statistics.getTotalSize());
		jsonObject.addProperty("count", statistics.getTotalCount());
		jsonObject.addProperty("bytesPerSecond", statistics.getBytesPerSecond());
		jsonObject.addProperty("countPerSecond", statistics.getCountPerSecond());
		JsonArray jsonArray = new JsonArray();
		jsonObject.add("topContributors", jsonArray);
		statistics.getTopContributors().forEach(pair -> {
			JsonObject jsonObjectxx = new JsonObject();
			jsonArray.add(jsonObjectxx);
			NetworkIoStatistics.Packet packet = (NetworkIoStatistics.Packet)pair.getFirst();
			NetworkIoStatistics.PacketStatistics packetStatistics = (NetworkIoStatistics.PacketStatistics)pair.getSecond();
			jsonObjectxx.addProperty("protocolId", packet.protocolId());
			jsonObjectxx.addProperty("packetId", packet.packetId());
			jsonObjectxx.addProperty("packetName", packet.getName());
			jsonObjectxx.addProperty("totalBytes", packetStatistics.totalSize());
			jsonObjectxx.addProperty("count", packetStatistics.totalCount());
		});
		return jsonObject;
	}

	private JsonElement collectCpuPercentSection(List<CpuLoadSample> samples) {
		JsonObject jsonObject = new JsonObject();
		BiFunction<List<CpuLoadSample>, ToDoubleFunction<CpuLoadSample>, JsonObject> biFunction = (samplesx, valueGetter) -> {
			JsonObject jsonObjectxx = new JsonObject();
			DoubleSummaryStatistics doubleSummaryStatistics = samplesx.stream().mapToDouble(valueGetter).summaryStatistics();
			jsonObjectxx.addProperty("min", doubleSummaryStatistics.getMin());
			jsonObjectxx.addProperty("average", doubleSummaryStatistics.getAverage());
			jsonObjectxx.addProperty("max", doubleSummaryStatistics.getMax());
			return jsonObjectxx;
		};
		jsonObject.add("jvm", (JsonElement)biFunction.apply(samples, CpuLoadSample::jvm));
		jsonObject.add("userJvm", (JsonElement)biFunction.apply(samples, CpuLoadSample::userJvm));
		jsonObject.add("system", (JsonElement)biFunction.apply(samples, CpuLoadSample::system));
		return jsonObject;
	}
}
