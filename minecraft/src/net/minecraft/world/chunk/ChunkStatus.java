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
import net.minecraft.server.world.chunk.light.ServerLightingProvider;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ChunkStatus {
	public static final ChunkStatus EMPTY = register("empty", null, -1, false, ChunkStatus.ChunkType.PROTOCHUNK, (world, chunkGenerator, list, chunk) -> {
	});
	public static final ChunkStatus STRUCTURE_STARTS = register(
		"structure_starts",
		EMPTY,
		0,
		false,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(chunkStatus, world, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> {
			if (!chunk.getStatus().isAfter(getLightStatus()) || !chunk.isLightOn()) {
				ChunkPos chunkPos = chunk.getPos();
				int i = chunkPos.x;
				int j = chunkPos.z;
				serverLightingProvider.method_15557(i, j, true);
			}

			if (!chunk.getStatus().isAfter(chunkStatus)) {
				if (world.getLevelProperties().hasStructures()) {
					chunkGenerator.setStructureStarts(chunk, chunkGenerator, structureManager);
				}

				if (chunk instanceof ProtoChunk) {
					((ProtoChunk)chunk).setStatus(chunkStatus);
				}
			}

			return CompletableFuture.completedFuture(chunk);
		}
	);
	public static final ChunkStatus STRUCTURE_REFERENCES = register(
		"structure_references",
		STRUCTURE_STARTS,
		8,
		false,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(world, chunkGenerator, list, chunk) -> chunkGenerator.addStructureReferences(new ChunkRegion(world, list), chunk)
	);
	public static final ChunkStatus BIOMES = register(
		"biomes", STRUCTURE_REFERENCES, 0, false, ChunkStatus.ChunkType.PROTOCHUNK, (world, chunkGenerator, list, chunk) -> chunkGenerator.populateBiomes(chunk)
	);
	public static final ChunkStatus NOISE = register(
		"noise",
		BIOMES,
		8,
		false,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(world, chunkGenerator, list, chunk) -> chunkGenerator.populateNoise(new ChunkRegion(world, list), chunk)
	);
	public static final ChunkStatus SURFACE = register(
		"surface", NOISE, 0, false, ChunkStatus.ChunkType.PROTOCHUNK, (world, chunkGenerator, list, chunk) -> chunkGenerator.buildSurface(chunk)
	);
	public static final ChunkStatus CARVERS = register(
		"carvers",
		SURFACE,
		0,
		false,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(world, chunkGenerator, list, chunk) -> chunkGenerator.carve(chunk, GenerationStep.Carver.AIR)
	);
	public static final ChunkStatus LIQUID_CARVERS = register(
		"liquid_carvers",
		CARVERS,
		0,
		true,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(world, chunkGenerator, list, chunk) -> chunkGenerator.carve(chunk, GenerationStep.Carver.LIQUID)
	);
	public static final ChunkStatus FEATURES = register(
		"features",
		LIQUID_CARVERS,
		8,
		true,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(world, chunkGenerator, list, chunk) -> {
			Heightmap.populateHeightmaps(
				chunk, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE)
			);
			chunkGenerator.generateFeatures(new ChunkRegion(world, list));
		}
	);
	public static final ChunkStatus LIGHT = register(
		"light",
		FEATURES,
		1,
		true,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(chunkStatus, world, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> {
			chunk.setLightingProvider(serverLightingProvider);
			ChunkPos chunkPos = chunk.getPos();
			int i = chunkPos.x;
			int j = chunkPos.z;
			boolean bl = chunk.getStatus().isAfter(chunkStatus) && chunk.isLightOn();
			if (!chunk.getStatus().isAfter(chunkStatus)) {
				((ProtoChunk)chunk).setStatus(chunkStatus);
			}

			return serverLightingProvider.light(chunk, i, j, bl);
		}
	);
	public static final ChunkStatus SPAWN = register(
		"spawn",
		LIGHT,
		0,
		true,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(world, chunkGenerator, list, chunk) -> chunkGenerator.populateEntities(new ChunkRegion(world, list))
	);
	public static final ChunkStatus HEIGHTMAPS = register(
		"heightmaps", SPAWN, 0, true, ChunkStatus.ChunkType.PROTOCHUNK, (world, chunkGenerator, list, chunk) -> {
		}
	);
	public static final ChunkStatus FULL = register(
		"full",
		HEIGHTMAPS,
		0,
		true,
		ChunkStatus.ChunkType.LEVELCHUNK,
		(chunkStatus, world, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> (CompletableFuture<Chunk>)function.apply(chunk)
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

	private static ChunkStatus register(
		String string, @Nullable ChunkStatus chunkStatus, int i, boolean bl, ChunkStatus.ChunkType chunkType, ChunkStatus.SimpleTask simpleTask
	) {
		return register(string, chunkStatus, i, bl, chunkType, (ChunkStatus.Task)simpleTask);
	}

	private static ChunkStatus register(
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

	public CompletableFuture<Chunk> runTask(
		World world,
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		ServerLightingProvider serverLightingProvider,
		Function<Chunk, CompletableFuture<Chunk>> function,
		List<Chunk> list
	) {
		return this.task.doWork(this, world, chunkGenerator, structureManager, serverLightingProvider, function, list, (Chunk)list.get(list.size() / 2));
	}

	public int getTaskMargin() {
		return this.taskMargin;
	}

	public ChunkStatus.ChunkType getChunkType() {
		return this.chunkType;
	}

	public static ChunkStatus get(String string) {
		return Registry.CHUNK_STATUS.get(Identifier.create(string));
	}

	public boolean isSurfaceGenerated() {
		return this.surfaceGenerated;
	}

	public boolean isAfter(ChunkStatus chunkStatus) {
		return this.getIndex() >= chunkStatus.getIndex();
	}

	public String toString() {
		return Registry.CHUNK_STATUS.getId(this).toString();
	}

	public static enum ChunkType {
		PROTOCHUNK,
		LEVELCHUNK;
	}

	interface SimpleTask extends ChunkStatus.Task {
		@Override
		default CompletableFuture<Chunk> doWork(
			ChunkStatus chunkStatus,
			World world,
			ChunkGenerator<?> chunkGenerator,
			StructureManager structureManager,
			ServerLightingProvider serverLightingProvider,
			Function<Chunk, CompletableFuture<Chunk>> function,
			List<Chunk> list,
			Chunk chunk
		) {
			if (!chunk.getStatus().isAfter(chunkStatus)) {
				this.doWork(world, chunkGenerator, list, chunk);
				if (chunk instanceof ProtoChunk) {
					((ProtoChunk)chunk).setStatus(chunkStatus);
				}
			}

			return CompletableFuture.completedFuture(chunk);
		}

		void doWork(World world, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk);
	}

	interface Task {
		CompletableFuture<Chunk> doWork(
			ChunkStatus chunkStatus,
			World world,
			ChunkGenerator<?> chunkGenerator,
			StructureManager structureManager,
			ServerLightingProvider serverLightingProvider,
			Function<Chunk, CompletableFuture<Chunk>> function,
			List<Chunk> list,
			Chunk chunk
		);
	}
}
