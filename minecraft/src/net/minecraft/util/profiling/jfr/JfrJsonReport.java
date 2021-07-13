package net.minecraft.util.profiling.jfr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.LongSerializationPolicy;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.time.Duration;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import net.minecraft.util.Util;
import net.minecraft.util.math.Quantiles;
import net.minecraft.util.profiling.jfr.sample.ChunkGenerationSample;
import net.minecraft.util.profiling.jfr.sample.CpuLoadSample;
import net.minecraft.util.profiling.jfr.sample.FileIoSample;
import net.minecraft.util.profiling.jfr.sample.GcHeapSummarySample;
import net.minecraft.util.profiling.jfr.sample.LongRunningSampleStatistics;
import net.minecraft.util.profiling.jfr.sample.PacketSample;
import net.minecraft.util.profiling.jfr.sample.ServerTickTimeSample;
import net.minecraft.util.profiling.jfr.sample.ThreadAllocationStatisticsSample;
import net.minecraft.world.chunk.ChunkStatus;

public class JfrJsonReport implements JfrReport {
	private static final String BYTES_PER_SECOND = "bytesPerSecond";
	private static final String COUNT = "count";
	private static final String DURATION_NANOS_TOTAL = "durationNanosTotal";
	private static final String TOTAL_BYTES = "totalBytes";
	private static final String COUNT_PER_SECOND = "countPerSecond";
	final Gson gson = new GsonBuilder().setPrettyPrinting().setLongSerializationPolicy(LongSerializationPolicy.DEFAULT).create();

	@Override
	public String toString(JfrProfile profile) throws IOException {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("startedEpoch", profile.getStartTime().toEpochMilli());
		jsonObject.addProperty("endedEpoch", profile.getEndTime().toEpochMilli());
		jsonObject.addProperty("durationMs", Duration.between(profile.getStartTime(), profile.getEndTime()).toMillis());
		profile.getWorldGenDuration().ifPresent(duration -> jsonObject.addProperty("worldGenDurationMs", duration.toMillis()));
		jsonObject.add("heap", this.collectHeapSection(profile.getGcHeapSummaryStatistics()));
		jsonObject.add("cpuPercent", this.collectCpuPercentSection(profile.getCpuLoadSamples()));
		jsonObject.add("network", this.collectNetworkSection(profile));
		jsonObject.add("fileIO", this.collectFileIoSection(profile));
		jsonObject.add("serverTick", this.collectServerTickSection(profile.getServerTickTimeSamples()));
		jsonObject.add("threadAllocation", this.collectThreadAllocationSection(profile.getThreadAllocationMap()));
		jsonObject.add("chunkGen", this.collectChunkGenSection(profile.getChunkGenerationSampleStatistics()));
		return this.gson.toJson((JsonElement)jsonObject);
	}

	private JsonElement collectHeapSection(GcHeapSummarySample.Statistics statistics) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("allocationRateBytesPerSecond", statistics.allocatedBytesPerSecond);
		jsonObject.addProperty("gcCount", statistics.count);
		jsonObject.addProperty("gcOverHeadPercent", statistics.gcDurationRatio());
		jsonObject.addProperty("gcTotalDurationMs", statistics.gcDuration.toMillis());
		return jsonObject;
	}

	private JsonElement collectChunkGenSection(List<Pair<ChunkStatus, LongRunningSampleStatistics<ChunkGenerationSample>>> statistics) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(
			"durationNanosTotal", statistics.stream().mapToDouble(pairx -> (double)((LongRunningSampleStatistics)pairx.getSecond()).totalDuration.toNanos()).sum()
		);
		JsonArray jsonArray = Util.make(new JsonArray(), json -> jsonObject.add("status", json));

		for (Pair<ChunkStatus, LongRunningSampleStatistics<ChunkGenerationSample>> pair : statistics) {
			LongRunningSampleStatistics<ChunkGenerationSample> longRunningSampleStatistics = pair.getSecond();
			JsonObject jsonObject2 = Util.make(new JsonObject(), jsonArray::add);
			jsonObject2.addProperty("state", pair.getFirst().getId());
			jsonObject2.addProperty("count", longRunningSampleStatistics.count);
			jsonObject2.addProperty("durationNanosTotal", longRunningSampleStatistics.totalDuration.toNanos());
			jsonObject2.addProperty("durationNanosAvg", longRunningSampleStatistics.totalDuration.toNanos() / (long)longRunningSampleStatistics.count);
			JsonObject jsonObject3 = Util.make(new JsonObject(), json -> jsonObject2.add("durationNanosPercentiles", json));
			longRunningSampleStatistics.quantiles.forEach((quantile, duration) -> jsonObject3.addProperty("p" + quantile, duration));
			Function<ChunkGenerationSample, JsonElement> function = sample -> {
				JsonObject jsonObjectx = new JsonObject();
				jsonObjectx.addProperty("durationNanos", sample.duration.toNanos());
				jsonObjectx.addProperty("level", sample.worldKey);
				jsonObjectx.addProperty("blockPosX", sample.centerPos.getX());
				jsonObjectx.addProperty("blockPosZ", sample.centerPos.getZ());
				return jsonObjectx;
			};
			jsonObject2.add("fastest", (JsonElement)function.apply(longRunningSampleStatistics.fastestSample));
			jsonObject2.add("slowest", (JsonElement)function.apply(longRunningSampleStatistics.slowestSample));
			jsonObject2.add("secondSlowest", (JsonElement)longRunningSampleStatistics.secondSlowestSample.map(function).orElse(JsonNull.INSTANCE));
		}

		return jsonObject;
	}

	private JsonElement collectThreadAllocationSection(ThreadAllocationStatisticsSample.AllocationMap allocationMap) {
		JsonArray jsonArray = new JsonArray();
		allocationMap.allocations.forEach((threadName, allocatedBytesPerSecond) -> jsonArray.add(Util.make(new JsonObject(), json -> {
				json.addProperty("thread", threadName);
				json.addProperty("bytesPerSecond", allocatedBytesPerSecond);
			})));
		return jsonArray;
	}

	private JsonElement collectServerTickSection(List<ServerTickTimeSample> samples) {
		if (samples.isEmpty()) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			DoubleSummaryStatistics doubleSummaryStatistics = samples.stream().mapToDouble(sample -> (double)sample.averageTickMs).summaryStatistics();
			jsonObject.addProperty("minMs", doubleSummaryStatistics.getMin());
			jsonObject.addProperty("averageMs", doubleSummaryStatistics.getAverage());
			jsonObject.addProperty("maxMs", doubleSummaryStatistics.getMax());
			Quantiles.create(samples.stream().mapToDouble(sample -> (double)sample.averageTickMs).toArray())
				.forEach((quantile, averageTickMs) -> jsonObject.addProperty("p" + quantile, averageTickMs));
			return jsonObject;
		}
	}

	private JsonElement collectFileIoSection(JfrProfile profile) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("write", this.collectFileIoSection(profile.getFileWriteStatistics()));
		jsonObject.add("read", this.collectFileIoSection(profile.getFileReadStatistics()));
		return jsonObject;
	}

	private JsonElement collectFileIoSection(FileIoSample.Statistics statistics) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("totalBytes", statistics.getTotalBytes());
		jsonObject.addProperty("count", statistics.getCount());
		jsonObject.addProperty("bytesPerSecond", statistics.getBytesPerSecond());
		jsonObject.addProperty("countPerSecond", statistics.getCountPerSecond());
		JsonArray jsonArray = new JsonArray();
		jsonObject.add("topContributors", jsonArray);
		statistics.getTopContributors().limit(10L).forEach(entry -> {
			JsonObject jsonObjectx = new JsonObject();
			jsonArray.add(jsonObjectx);
			jsonObjectx.addProperty("path", (String)entry.getLeft());
			jsonObjectx.addProperty("totalBytes", (Number)entry.getRight());
		});
		return jsonObject;
	}

	private JsonElement collectNetworkSection(JfrProfile profile) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("sent", this.collectPacketSection(profile.getPacketSentStatistics()));
		jsonObject.add("received", this.collectPacketSection(profile.getPacketReadStatistics()));
		return jsonObject;
	}

	private JsonElement collectPacketSection(PacketSample.Statistics statistics) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("totalBytes", statistics.getTotalBytes());
		jsonObject.addProperty("count", statistics.getCount());
		jsonObject.addProperty("bytesPerSecond", statistics.getBytesPerSecond());
		jsonObject.addProperty("countPerSecond", statistics.getCountPerSecond());
		JsonArray jsonArray = new JsonArray();
		jsonObject.add("topContributors", jsonArray);
		statistics.getTopContributors().stream().limit(10L).forEach(entry -> {
			JsonObject jsonObjectx = new JsonObject();
			jsonArray.add(jsonObjectx);
			jsonObjectx.addProperty("packetName", (String)entry.getFirst());
			jsonObjectx.addProperty("totalBytes", (Number)entry.getSecond());
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
		jsonObject.add("jvm", (JsonElement)biFunction.apply(samples, (ToDoubleFunction)sample -> sample.jvm));
		jsonObject.add("userJvm", (JsonElement)biFunction.apply(samples, (ToDoubleFunction)sample -> sample.userJvm));
		jsonObject.add("system", (JsonElement)biFunction.apply(samples, (ToDoubleFunction)sample -> sample.system));
		return jsonObject;
	}
}
