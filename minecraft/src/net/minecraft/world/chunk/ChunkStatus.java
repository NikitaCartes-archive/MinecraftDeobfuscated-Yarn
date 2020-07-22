package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ChunkStatus {
	private static final EnumSet<Heightmap.Type> PRE_CARVER_HEIGHTMAPS = EnumSet.of(Heightmap.Type.OCEAN_FLOOR_WG, Heightmap.Type.WORLD_SURFACE_WG);
	private static final EnumSet<Heightmap.Type> POST_CARVER_HEIGHTMAPS = EnumSet.of(
		Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE, Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES
	);
	/**
	 * A load task which only bumps the chunk status of the chunk.
	 */
	private static final ChunkStatus.LoadTask STATUS_BUMP_LOAD_TASK = (targetStatus, world, structureManager, lightingProvider, function, chunk) -> {
		if (chunk instanceof ProtoChunk && !chunk.getStatus().isAtLeast(targetStatus)) {
			((ProtoChunk)chunk).setStatus(targetStatus);
		}

		return CompletableFuture.completedFuture(Either.left(chunk));
	};
	public static final ChunkStatus EMPTY = register(
		"empty", null, -1, PRE_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.field_12808, (world, generator, surroundingChunks, chunk) -> {
		}
	);
	public static final ChunkStatus STRUCTURE_STARTS = register(
		"structure_starts",
		EMPTY,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(targetStatus, world, generator, structureManager, lightingProvider, function, surroundingChunks, chunk) -> {
			if (!chunk.getStatus().isAtLeast(targetStatus)) {
				if (world.getServer().getSaveProperties().getGeneratorOptions().shouldGenerateStructures()) {
					generator.setStructureStarts(world.getRegistryManager(), world.getStructureAccessor(), chunk, structureManager, world.getSeed());
				}

				if (chunk instanceof ProtoChunk) {
					((ProtoChunk)chunk).setStatus(targetStatus);
				}
			}

			return CompletableFuture.completedFuture(Either.left(chunk));
		}
	);
	public static final ChunkStatus STRUCTURE_REFERENCES = register(
		"structure_references", STRUCTURE_STARTS, 8, PRE_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.field_12808, (world, generator, surroundingChunks, chunk) -> {
			ChunkRegion chunkRegion = new ChunkRegion(world, surroundingChunks);
			generator.addStructureReferences(chunkRegion, world.getStructureAccessor().forRegion(chunkRegion), chunk);
		}
	);
	public static final ChunkStatus BIOMES = register(
		"biomes",
		STRUCTURE_REFERENCES,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(world, generator, surroundingChunks, chunk) -> generator.populateBiomes(world.getRegistryManager().get(Registry.BIOME_KEY), chunk)
	);
	public static final ChunkStatus NOISE = register(
		"noise", BIOMES, 8, PRE_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.field_12808, (world, generator, surroundingChunks, chunk) -> {
			ChunkRegion chunkRegion = new ChunkRegion(world, surroundingChunks);
			generator.populateNoise(chunkRegion, world.getStructureAccessor().forRegion(chunkRegion), chunk);
		}
	);
	public static final ChunkStatus SURFACE = register(
		"surface",
		NOISE,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(world, generator, surroundingChunks, chunk) -> generator.buildSurface(new ChunkRegion(world, surroundingChunks), chunk)
	);
	public static final ChunkStatus CARVERS = register(
		"carvers",
		SURFACE,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(world, generator, surroundingChunks, chunk) -> generator.carve(world.getSeed(), world.getBiomeAccess(), chunk, GenerationStep.Carver.AIR)
	);
	public static final ChunkStatus LIQUID_CARVERS = register(
		"liquid_carvers",
		CARVERS,
		0,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(world, generator, surroundingChunks, chunk) -> generator.carve(world.getSeed(), world.getBiomeAccess(), chunk, GenerationStep.Carver.LIQUID)
	);
	public static final ChunkStatus FEATURES = register(
		"features",
		LIQUID_CARVERS,
		8,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(status, world, generator, structureManager, lightingProvider, function, surroundingChunks, chunk) -> {
			ProtoChunk protoChunk = (ProtoChunk)chunk;
			protoChunk.setLightingProvider(lightingProvider);
			if (!chunk.getStatus().isAtLeast(status)) {
				Heightmap.populateHeightmaps(
					chunk, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE)
				);
				ChunkRegion chunkRegion = new ChunkRegion(world, surroundingChunks);
				generator.generateFeatures(chunkRegion, world.getStructureAccessor().forRegion(chunkRegion));
				protoChunk.setStatus(status);
			}

			return CompletableFuture.completedFuture(Either.left(chunk));
		}
	);
	public static final ChunkStatus LIGHT = register(
		"light",
		FEATURES,
		1,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(targetStatus, world, generator, structureManager, lightingProvider, function, surroundingChunks, chunk) -> getLightingFuture(
				targetStatus, lightingProvider, chunk
			),
		(status, world, structureManager, lightingProvider, function, chunk) -> getLightingFuture(status, lightingProvider, chunk)
	);
	public static final ChunkStatus SPAWN = register(
		"spawn",
		LIGHT,
		0,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(targetStatus, generator, surroundingChunks, chunk) -> generator.populateEntities(new ChunkRegion(targetStatus, surroundingChunks))
	);
	public static final ChunkStatus HEIGHTMAPS = register(
		"heightmaps", SPAWN, 0, POST_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.field_12808, (world, generator, surroundingChunks, chunk) -> {
		}
	);
	public static final ChunkStatus FULL = register(
		"full",
		HEIGHTMAPS,
		0,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12807,
		(targetStatus, world, generator, structureManager, lightingProvider, function, surroundingChunks, chunk) -> (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)function.apply(
				chunk
			),
		(status, world, structureManager, lightingProvider, function, chunk) -> (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)function.apply(chunk)
	);
	private static final List<ChunkStatus> DISTANCE_TO_STATUS = ImmutableList.of(
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
	private static final IntList STATUS_TO_DISTANCE = Util.make(new IntArrayList(createOrderedList().size()), intArrayList -> {
		int i = 0;

		for (int j = createOrderedList().size() - 1; j >= 0; j--) {
			while (i + 1 < DISTANCE_TO_STATUS.size() && j <= ((ChunkStatus)DISTANCE_TO_STATUS.get(i + 1)).getIndex()) {
				i++;
			}

			intArrayList.add(0, i);
		}
	});
	private final String id;
	private final int index;
	private final ChunkStatus previous;
	private final ChunkStatus.GenerationTask generationTask;
	private final ChunkStatus.LoadTask loadTask;
	private final int taskMargin;
	private final ChunkStatus.ChunkType chunkType;
	private final EnumSet<Heightmap.Type> heightMapTypes;

	private static CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getLightingFuture(
		ChunkStatus status, ServerLightingProvider lightingProvider, Chunk chunk
	) {
		boolean bl = shouldExcludeBlockLight(status, chunk);
		if (!chunk.getStatus().isAtLeast(status)) {
			((ProtoChunk)chunk).setStatus(status);
		}

		return lightingProvider.light(chunk, bl).thenApply(Either::left);
	}

	private static ChunkStatus register(
		String id,
		@Nullable ChunkStatus previous,
		int taskMargin,
		EnumSet<Heightmap.Type> heightMapTypes,
		ChunkStatus.ChunkType chunkType,
		ChunkStatus.SimpleGenerationTask task
	) {
		return register(id, previous, taskMargin, heightMapTypes, chunkType, (ChunkStatus.GenerationTask)task);
	}

	private static ChunkStatus register(
		String id,
		@Nullable ChunkStatus previous,
		int taskMargin,
		EnumSet<Heightmap.Type> heightMapTypes,
		ChunkStatus.ChunkType chunkType,
		ChunkStatus.GenerationTask task
	) {
		return register(id, previous, taskMargin, heightMapTypes, chunkType, task, STATUS_BUMP_LOAD_TASK);
	}

	private static ChunkStatus register(
		String id,
		@Nullable ChunkStatus previous,
		int taskMargin,
		EnumSet<Heightmap.Type> heightMapTypes,
		ChunkStatus.ChunkType chunkType,
		ChunkStatus.GenerationTask task,
		ChunkStatus.LoadTask loadTask
	) {
		return Registry.register(Registry.CHUNK_STATUS, id, new ChunkStatus(id, previous, taskMargin, heightMapTypes, chunkType, task, loadTask));
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

	private static boolean shouldExcludeBlockLight(ChunkStatus status, Chunk chunk) {
		return chunk.getStatus().isAtLeast(status) && chunk.isLightOn();
	}

	public static ChunkStatus byDistanceFromFull(int level) {
		if (level >= DISTANCE_TO_STATUS.size()) {
			return EMPTY;
		} else {
			return level < 0 ? FULL : (ChunkStatus)DISTANCE_TO_STATUS.get(level);
		}
	}

	public static int getMaxDistanceFromFull() {
		return DISTANCE_TO_STATUS.size();
	}

	public static int getDistanceFromFull(ChunkStatus status) {
		return STATUS_TO_DISTANCE.getInt(status.getIndex());
	}

	ChunkStatus(
		String id,
		@Nullable ChunkStatus previous,
		int taskMargin,
		EnumSet<Heightmap.Type> heightMapTypes,
		ChunkStatus.ChunkType chunkType,
		ChunkStatus.GenerationTask generationTask,
		ChunkStatus.LoadTask loadTask
	) {
		this.id = id;
		this.previous = previous == null ? this : previous;
		this.generationTask = generationTask;
		this.loadTask = loadTask;
		this.taskMargin = taskMargin;
		this.chunkType = chunkType;
		this.heightMapTypes = heightMapTypes;
		this.index = previous == null ? 0 : previous.getIndex() + 1;
	}

	public int getIndex() {
		return this.index;
	}

	public String getId() {
		return this.id;
	}

	public ChunkStatus getPrevious() {
		return this.previous;
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> runGenerationTask(
		ServerWorld world,
		ChunkGenerator chunkGenerator,
		StructureManager structureManager,
		ServerLightingProvider lightingProvider,
		Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
		List<Chunk> chunks
	) {
		return this.generationTask.doWork(this, world, chunkGenerator, structureManager, lightingProvider, function, chunks, (Chunk)chunks.get(chunks.size() / 2));
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> runLoadTask(
		ServerWorld world,
		StructureManager structureManager,
		ServerLightingProvider lightingProvider,
		Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
		Chunk chunk
	) {
		return this.loadTask.doWork(this, world, structureManager, lightingProvider, function, chunk);
	}

	public int getTaskMargin() {
		return this.taskMargin;
	}

	public ChunkStatus.ChunkType getChunkType() {
		return this.chunkType;
	}

	public static ChunkStatus byId(String id) {
		return Registry.CHUNK_STATUS.get(Identifier.tryParse(id));
	}

	public EnumSet<Heightmap.Type> getHeightmapTypes() {
		return this.heightMapTypes;
	}

	public boolean isAtLeast(ChunkStatus chunk) {
		return this.getIndex() >= chunk.getIndex();
	}

	public String toString() {
		return Registry.CHUNK_STATUS.getId(this).toString();
	}

	/**
	 * Specifies the type of a chunk
	 */
	public static enum ChunkType {
		/**
		 * A chunk which is incomplete and not loaded to the world yet.
		 */
		field_12808,
		/**
		 * A chunk which is complete and bound to a world.
		 */
		field_12807;
	}

	/**
	 * A task called when a chunk needs to be generated.
	 */
	interface GenerationTask {
		/**
		 * @param targetStatus the status the chunk will be set to after the task is completed
		 * @param chunk the primary chunk this task will be applied to
		 */
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(
			ChunkStatus targetStatus,
			ServerWorld world,
			ChunkGenerator generator,
			StructureManager structureManager,
			ServerLightingProvider lightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
			List<Chunk> surroundingChunks,
			Chunk chunk
		);
	}

	/**
	 * A task called when a chunk is loaded but does not need to be generated.
	 */
	interface LoadTask {
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(
			ChunkStatus targetStatus,
			ServerWorld world,
			StructureManager structureManager,
			ServerLightingProvider lightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
			Chunk chunk
		);
	}

	interface SimpleGenerationTask extends ChunkStatus.GenerationTask {
		@Override
		default CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(
			ChunkStatus chunkStatus,
			ServerWorld serverWorld,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ServerLightingProvider serverLightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
			List<Chunk> list,
			Chunk chunk
		) {
			if (!chunk.getStatus().isAtLeast(chunkStatus)) {
				this.doWork(serverWorld, chunkGenerator, list, chunk);
				if (chunk instanceof ProtoChunk) {
					((ProtoChunk)chunk).setStatus(chunkStatus);
				}
			}

			return CompletableFuture.completedFuture(Either.left(chunk));
		}

		void doWork(ServerWorld world, ChunkGenerator generator, List<Chunk> surroundingChunks, Chunk chunk);
	}
}
