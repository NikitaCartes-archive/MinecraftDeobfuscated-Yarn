package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.chunk.light.ServerLightingProvider;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ChunkStatus {
	public static final ChunkStatus EMPTY = method_16555(
		"empty", null, -1, false, ChunkStatus.ChunkType.PROTOCHUNK, (serverWorld, chunkGenerator, list, chunk) -> {
		}
	);
	public static final ChunkStatus STRUCTURE_STARTS = method_16557(
		"structure_starts",
		EMPTY,
		0,
		false,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> {
			if (!chunk.method_12009().isAfter(getLightStatus()) || !chunk.isLightOn()) {
				ChunkPos chunkPos = chunk.getPos();
				serverLightingProvider.method_15557(chunkPos, true);
			}

			if (!chunk.method_12009().isAfter(chunkStatus)) {
				if (serverWorld.method_8401().hasStructures()) {
					chunkGenerator.method_16129(chunk, chunkGenerator, structureManager);
				}

				if (chunk instanceof ProtoChunk) {
					((ProtoChunk)chunk).setStatus(chunkStatus);
				}
			}

			return CompletableFuture.completedFuture(chunk);
		}
	);
	public static final ChunkStatus STRUCTURE_REFERENCES = method_16555(
		"structure_references",
		STRUCTURE_STARTS,
		8,
		false,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.addStructureReferences(new ChunkRegion(serverWorld, list), chunk)
	);
	public static final ChunkStatus BIOMES = method_16555(
		"biomes",
		STRUCTURE_REFERENCES,
		0,
		false,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.populateBiomes(chunk)
	);
	public static final ChunkStatus NOISE = method_16555(
		"noise",
		BIOMES,
		8,
		false,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.populateNoise(new ChunkRegion(serverWorld, list), chunk)
	);
	public static final ChunkStatus SURFACE = method_16555(
		"surface", NOISE, 0, false, ChunkStatus.ChunkType.PROTOCHUNK, (serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.buildSurface(chunk)
	);
	public static final ChunkStatus CARVERS = method_16555(
		"carvers",
		SURFACE,
		0,
		false,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.method_12108(chunk, GenerationStep.Carver.AIR)
	);
	public static final ChunkStatus LIQUID_CARVERS = method_16555(
		"liquid_carvers",
		CARVERS,
		0,
		true,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.method_12108(chunk, GenerationStep.Carver.LIQUID)
	);
	public static final ChunkStatus FEATURES = method_16555(
		"features",
		LIQUID_CARVERS,
		8,
		true,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> {
			Heightmap.populateHeightmaps(
				chunk, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE)
			);
			chunkGenerator.method_12102(new ChunkRegion(serverWorld, list));
		}
	);
	public static final ChunkStatus LIGHT = method_16557(
		"light",
		FEATURES,
		1,
		true,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> {
			chunk.method_17032(serverLightingProvider);
			boolean bl = chunk.method_12009().isAfter(chunkStatus) && chunk.isLightOn();
			if (!chunk.method_12009().isAfter(chunkStatus)) {
				((ProtoChunk)chunk).setStatus(chunkStatus);
			}

			return serverLightingProvider.light(chunk, bl);
		}
	);
	public static final ChunkStatus SPAWN = method_16555(
		"spawn",
		LIGHT,
		0,
		true,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.method_12107(new ChunkRegion(serverWorld, list))
	);
	public static final ChunkStatus HEIGHTMAPS = method_16555(
		"heightmaps", SPAWN, 0, true, ChunkStatus.ChunkType.PROTOCHUNK, (serverWorld, chunkGenerator, list, chunk) -> {
		}
	);
	public static final ChunkStatus FULL = method_16557(
		"full",
		HEIGHTMAPS,
		0,
		true,
		ChunkStatus.ChunkType.LEVELCHUNK,
		(chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> (CompletableFuture<Chunk>)function.apply(chunk)
	);
	private static final List<ChunkStatus> DISTANCE_TO_TARGET_GENERATION_STATUS = ImmutableList.of(
		FULL,
		FEATURES,
		LIQUID_CARVERS,
		STRUCTURE_STARTS,
		STRUCTURE_STARTS,
		STRUCTURE_STARTS,
		STRUCTURE_STARTS,
		STRUCTURE_STARTS,
		STRUCTURE_STARTS,
		STRUCTURE_STARTS,
		STRUCTURE_STARTS
	);
	private static final IntList STATUS_TO_TARGET_GENERATION_RADIUS = SystemUtil.consume(new IntArrayList(createOrderedList().size()), intArrayList -> {
		int i = 0;

		for (int j = createOrderedList().size() - 1; j >= 0; j--) {
			while (i + 1 < DISTANCE_TO_TARGET_GENERATION_STATUS.size() && j <= ((ChunkStatus)DISTANCE_TO_TARGET_GENERATION_STATUS.get(i + 1)).getIndex()) {
				i++;
			}

			intArrayList.add(0, i);
		}
	});
	private final String name;
	private final int index;
	private final ChunkStatus previous;
	private final ChunkStatus.Task task;
	private final int taskMargin;
	private final ChunkStatus.ChunkType chunkType;
	private final boolean surfaceGenerated;

	private static ChunkStatus method_16555(
		String string, @Nullable ChunkStatus chunkStatus, int i, boolean bl, ChunkStatus.ChunkType chunkType, ChunkStatus.SimpleTask simpleTask
	) {
		return method_16557(string, chunkStatus, i, bl, chunkType, simpleTask);
	}

	private static ChunkStatus method_16557(
		String string, @Nullable ChunkStatus chunkStatus, int i, boolean bl, ChunkStatus.ChunkType chunkType, ChunkStatus.Task task
	) {
		return Registry.register(Registry.CHUNK_STATUS, string, new ChunkStatus(string, chunkStatus, i, bl, chunkType, task));
	}

	public static List<ChunkStatus> createOrderedList() {
		List<ChunkStatus> list = Lists.<ChunkStatus>newArrayList();

		ChunkStatus chunkStatus;
		for (chunkStatus = FULL; chunkStatus.getPrevious() != chunkStatus; chunkStatus = chunkStatus.getPrevious()) {
			list.add(chunkStatus);
		}

		list.add(chunkStatus);
		Collections.reverse(list);
		return list;
	}

	private static ChunkStatus getLightStatus() {
		return LIGHT;
	}

	public static ChunkStatus getTargetGenerationStatus(int i) {
		if (i >= DISTANCE_TO_TARGET_GENERATION_STATUS.size()) {
			return EMPTY;
		} else {
			return i < 0 ? FULL : (ChunkStatus)DISTANCE_TO_TARGET_GENERATION_STATUS.get(i);
		}
	}

	public static int getMaxTargetGenerationRadius() {
		return DISTANCE_TO_TARGET_GENERATION_STATUS.size();
	}

	public static int getTargetGenerationRadius(ChunkStatus chunkStatus) {
		return STATUS_TO_TARGET_GENERATION_RADIUS.getInt(chunkStatus.getIndex());
	}

	ChunkStatus(String string, @Nullable ChunkStatus chunkStatus, int i, boolean bl, ChunkStatus.ChunkType chunkType, ChunkStatus.Task task) {
		this.name = string;
		this.previous = chunkStatus == null ? this : chunkStatus;
		this.task = task;
		this.taskMargin = i;
		this.chunkType = chunkType;
		this.surfaceGenerated = bl;
		this.index = chunkStatus == null ? 0 : chunkStatus.getIndex() + 1;
	}

	public int getIndex() {
		return this.index;
	}

	public String getName() {
		return this.name;
	}

	public ChunkStatus getPrevious() {
		return this.previous;
	}

	public CompletableFuture<Chunk> method_12154(
		ServerWorld serverWorld,
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		ServerLightingProvider serverLightingProvider,
		Function<Chunk, CompletableFuture<Chunk>> function,
		List<Chunk> list
	) {
		return this.task.doWork(this, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, (Chunk)list.get(list.size() / 2));
	}

	public int getTaskMargin() {
		return this.taskMargin;
	}

	public ChunkStatus.ChunkType getChunkType() {
		return this.chunkType;
	}

	public static ChunkStatus get(String string) {
		return Registry.CHUNK_STATUS.method_10223(Identifier.create(string));
	}

	public boolean isSurfaceGenerated() {
		return this.surfaceGenerated;
	}

	public boolean isAfter(ChunkStatus chunkStatus) {
		return this.getIndex() >= chunkStatus.getIndex();
	}

	public String toString() {
		return Registry.CHUNK_STATUS.method_10221(this).toString();
	}

	public static enum ChunkType {
		PROTOCHUNK,
		LEVELCHUNK;
	}

	interface SimpleTask extends ChunkStatus.Task {
		@Override
		default CompletableFuture<Chunk> doWork(
			ChunkStatus chunkStatus,
			ServerWorld serverWorld,
			ChunkGenerator<?> chunkGenerator,
			StructureManager structureManager,
			ServerLightingProvider serverLightingProvider,
			Function<Chunk, CompletableFuture<Chunk>> function,
			List<Chunk> list,
			Chunk chunk
		) {
			if (!chunk.method_12009().isAfter(chunkStatus)) {
				this.doWork(serverWorld, chunkGenerator, list, chunk);
				if (chunk instanceof ProtoChunk) {
					((ProtoChunk)chunk).setStatus(chunkStatus);
				}
			}

			return CompletableFuture.completedFuture(chunk);
		}

		void doWork(ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk);
	}

	interface Task {
		CompletableFuture<Chunk> doWork(
			ChunkStatus chunkStatus,
			ServerWorld serverWorld,
			ChunkGenerator<?> chunkGenerator,
			StructureManager structureManager,
			ServerLightingProvider serverLightingProvider,
			Function<Chunk, CompletableFuture<Chunk>> function,
			List<Chunk> list,
			Chunk chunk
		);
	}
}
