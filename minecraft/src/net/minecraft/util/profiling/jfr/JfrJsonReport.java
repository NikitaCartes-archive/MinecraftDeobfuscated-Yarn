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

		for (Pair<ChunkStatus, LongRunningSampleStatistics<ChunkGenerationSample>> pair : statistics) {
			LongRunningSampleStatistics<ChunkGenerationSample> longRunningSampleStatistics = pair.getSecond();
			JsonObject jsonObject2 = Util.make(new JsonObject(), jsonArray::add);
			jsonObject2.addProperty("state", pair.getFirst().toString());
			jsonObject2.addProperty("count", longRunningSampleStatistics.count());
			jsonObject2.addProperty("durationNanosTotal", longRunningSampleStatistics.totalDuration().toNanos());
			jsonObject2.addProperty("durationNanosAvg", longRunningSampleStatistics.totalDuration().toNanos() / (long)longRunningSampleStatistics.count());
			JsonObject jsonObject3 = Util.make(new JsonObject(), json -> jsonObject2.add("durationNanosPercentiles", json));
			longRunningSampleStatistics.quantiles().forEach((quantile, value) -> jsonObject3.addProperty("p" + quantile, value));
			Function<ChunkGenerationSample, JsonElement> function = sample -> {
				JsonObject jsonObjectx = new JsonObject();
				jsonObjectx.addProperty("durationNanos", sample.duration().toNanos());
				jsonObjectx.addProperty("level", sample.worldKey());
				jsonObjectx.addProperty("chunkPosX", sample.chunkPos().x);
				jsonObjectx.addProperty("chunkPosZ", sample.chunkPos().z);
				jsonObjectx.addProperty("worldPosX", sample.centerPos().x());
				jsonObjectx.addProperty("worldPosZ", sample.centerPos().z());
				return jsonObjectx;
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
			JsonObject jsonObjectx = new JsonObject();
			jsonArray.add(jsonObjectx);
			jsonObjectx.addProperty("path", (String)pair.getFirst());
			jsonObjectx.addProperty("totalBytes", (Number)pair.getSecond());
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
			JsonObject jsonObjectx = new JsonObject();
			jsonArray.add(jsonObjectx);
			NetworkIoStatistics.Packet packet = (NetworkIoStatistics.Packet)pair.getFirst();
			NetworkIoStatistics.PacketStatistics packetStatistics = (NetworkIoStatistics.PacketStatistics)pair.getSecond();
			jsonObjectx.addProperty("protocolId", packet.protocolId());
			jsonObjectx.addProperty("packetId", packet.packetId());
			jsonObjectx.addProperty("packetName", packet.getName());
			jsonObjectx.addProperty("totalBytes", packetStatistics.totalSize());
			jsonObjectx.addProperty("count", packetStatistics.totalCount());
		});
		return jsonObject;
	}

	private JsonElement collectCpuPercentSection(List<CpuLoadSample> samples) {
		JsonObject jsonObject = new JsonObject();
		BiFunction<List<CpuLoadSample>, ToDoubleFunction<CpuLoadSample>, JsonObject> biFunction = (samplesx, valueGetter) -> {
			JsonObject jsonObjectx = new JsonObject();
			DoubleSummaryStatistics doubleSummaryStatistics = samplesx.stream().mapToDouble(valueGetter).summaryStatistics();
			jsonObjectx.addProperty("min", doubleSummaryStatistics.getMin());
			jsonObjectx.addProperty("average", doubleSummaryStatistics.getAverage());
			jsonObjectx.addProperty("max", doubleSummaryStatistics.getMax());
			return jsonObjectx;
		};
		jsonObject.add("jvm", (JsonElement)biFunction.apply(samples, CpuLoadSample::jvm));
		jsonObject.add("userJvm", (JsonElement)biFunction.apply(samples, CpuLoadSample::userJvm));
		jsonObject.add("system", (JsonElement)biFunction.apply(samples, CpuLoadSample::system));
		return jsonObject;
	}
}
