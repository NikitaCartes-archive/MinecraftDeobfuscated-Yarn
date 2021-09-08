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
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiling.jfr.event.worldgen.ChunkGenerationEvent;
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
		"empty", null, -1, PRE_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.PROTOCHUNK, (chunkStatus, serverWorld, chunkGenerator, list, chunk) -> {
		}
	);
	public static final ChunkStatus STRUCTURE_STARTS = register(
		"structure_starts",
		EMPTY,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(targetStatus, executor, world, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk, bl) -> {
			if (!chunk.getStatus().isAtLeast(targetStatus)) {
				if (world.getServer().getSaveProperties().getGeneratorOptions().shouldGenerateStructures()) {
					chunkGenerator.setStructureStarts(world.getRegistryManager(), world.getStructureAccessor(), chunk, structureManager, world.getSeed());
				}

				if (chunk instanceof ProtoChunk) {
					((ProtoChunk)chunk).setStatus(targetStatus);
				}
			}

			return CompletableFuture.completedFuture(Either.left(chunk));
		}
	);
	public static final ChunkStatus STRUCTURE_REFERENCES = register(
		"structure_references",
		STRUCTURE_STARTS,
		8,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(chunkStatus, serverWorld, chunkGenerator, list, chunk) -> {
			ChunkRegion chunkRegion = new ChunkRegion(serverWorld, list, chunkStatus, -1);
			chunkGenerator.addStructureReferences(chunkRegion, serverWorld.getStructureAccessor().forRegion(chunkRegion), chunk);
		}
	);
	public static final ChunkStatus BIOMES = register(
		"biomes",
		STRUCTURE_REFERENCES,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(chunkStatus, serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.populateBiomes(serverWorld.getRegistryManager().get(Registry.BIOME_KEY), chunk)
	);
	public static final ChunkStatus NOISE = register(
		"noise",
		BIOMES,
		8,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(chunkStatus, executor, world, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk, bl) -> {
			if (!bl && chunk.getStatus().isAtLeast(chunkStatus)) {
				return CompletableFuture.completedFuture(Either.left(chunk));
			} else {
				ChunkRegion chunkRegion = new ChunkRegion(world, list, chunkStatus, 0);
				return chunkGenerator.populateNoise(executor, world.getStructureAccessor().forRegion(chunkRegion), chunk).thenApply(chunkx -> {
					if (chunkx instanceof ProtoChunk) {
						((ProtoChunk)chunkx).setStatus(chunkStatus);
					}

					return Either.left(chunkx);
				});
			}
		}
	);
	public static final ChunkStatus SURFACE = register(
		"surface",
		NOISE,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(chunkStatus, serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.buildSurface(new ChunkRegion(serverWorld, list, chunkStatus, 0), chunk)
	);
	public static final ChunkStatus CARVERS = register(
		"carvers",
		SURFACE,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(chunkStatus, serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.carve(
				serverWorld.getSeed(), serverWorld.getBiomeAccess(), chunk, GenerationStep.Carver.AIR
			)
	);
	public static final ChunkStatus LIQUID_CARVERS = register(
		"liquid_carvers",
		CARVERS,
		0,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(chunkStatus, serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.carve(
				serverWorld.getSeed(), serverWorld.getBiomeAccess(), chunk, GenerationStep.Carver.LIQUID
			)
	);
	public static final ChunkStatus FEATURES = register(
		"features",
		LIQUID_CARVERS,
		8,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(status, executor, serverWorld, chunkGenerator, structureManager, lightingProvider, function, list, chunk, bl) -> {
			ProtoChunk protoChunk = (ProtoChunk)chunk;
			protoChunk.setLightingProvider(lightingProvider);
			if (bl || !chunk.getStatus().isAtLeast(status)) {
				Heightmap.populateHeightmaps(
					chunk,
					EnumSet.of(
						Heightmap.Type.MOTION_BLOCKING,
						Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
						Heightmap.Type.OCEAN_FLOOR,
						Heightmap.Type.WORLD_SURFACE,
						Heightmap.Type.WORLD_SURFACE_IGNORE_SNOW
					)
				);
				ChunkRegion chunkRegion = new ChunkRegion(serverWorld, list, status, 1);
				chunkGenerator.generateFeatures(chunkRegion, chunk.getPos(), serverWorld.getStructureAccessor().forRegion(chunkRegion));
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
		ChunkStatus.ChunkType.PROTOCHUNK,
		(targetStatus, executor, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk, bl) -> getLightingFuture(
				targetStatus, serverLightingProvider, chunk
			),
		(status, world, structureManager, lightingProvider, function, chunk) -> getLightingFuture(status, lightingProvider, chunk)
	);
	public static final ChunkStatus SPAWN = register(
		"spawn",
		LIGHT,
		0,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(chunkStatus, serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.populateEntities(new ChunkRegion(serverWorld, list, chunkStatus, -1))
	);
	public static final ChunkStatus HEIGHTMAPS = register(
		"heightmaps", SPAWN, 0, POST_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.PROTOCHUNK, (chunkStatus, serverWorld, chunkGenerator, list, chunk) -> {
		}
	);
	public static final ChunkStatus FULL = register(
		"full",
		HEIGHTMAPS,
		0,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.LEVELCHUNK,
		(targetStatus, executor, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk, bl) -> (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)function.apply(
				chunk
			),
		(status, world, structureManager, lightingProvider, convertCallback, chunk) -> (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)convertCallback.apply(
				chunk
			)
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
		Executor executor,
		ServerWorld world,
		ChunkGenerator generator,
		StructureManager structureManager,
		ServerLightingProvider lightingProvider,
		Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> fullChunkConverter,
		List<Chunk> chunks,
		boolean bl
	) {
		Chunk chunk = (Chunk)chunks.get(chunks.size() / 2);
		ChunkGenerationEvent chunkGenerationEvent = new ChunkGenerationEvent();
		if (chunkGenerationEvent.isEnabled()) {
			ChunkPos chunkPos = chunk.getPos();
			chunkGenerationEvent.targetStatus = this.id;
			chunkGenerationEvent.level = world.getRegistryKey().toString();
			chunkGenerationEvent.chunkPosX = chunkPos.x;
			chunkGenerationEvent.chunkPosZ = chunkPos.z;
			chunkGenerationEvent.centerBlockPosX = chunkPos.getCenterX();
			chunkGenerationEvent.centerBlockPosZ = chunkPos.getCenterZ();
			chunkGenerationEvent.begin();
		}

		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.generationTask
			.doWork(this, executor, world, generator, structureManager, lightingProvider, fullChunkConverter, chunks, chunk, bl);
		return chunkGenerationEvent.shouldCommit() ? completableFuture.thenApply(either -> {
			either.ifLeft(chunkx -> chunkGenerationEvent.success = true);
			chunkGenerationEvent.commit();
			return either;
		}) : completableFuture;
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
		PROTOCHUNK,
		/**
		 * A chunk which is complete and bound to a world.
		 */
		LEVELCHUNK;
	}

	/**
	 * A task called when a chunk needs to be generated.
	 */
	interface GenerationTask {
		/**
		 * @param targetStatus the status the chunk will be set to after the task is completed
		 * @param fullChunkConverter a function that can convert a raw chunk to a full chunk
		 */
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(
			ChunkStatus targetStatus,
			Executor executor,
			ServerWorld world,
			ChunkGenerator generator,
			StructureManager structureManager,
			ServerLightingProvider lightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> fullChunkConverter,
			List<Chunk> list,
			Chunk chunk,
			boolean bl
		);
	}

	/**
	 * A task called when a chunk is loaded but does not need to be generated.
	 */
	interface LoadTask {
		/**
		 * @param fullChunkConverter a function that can convert a raw chunk to a full chunk
		 */
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(
			ChunkStatus targetStatus,
			ServerWorld world,
			StructureManager structureManager,
			ServerLightingProvider lightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> fullChunkConverter,
			Chunk chunk
		);
	}

	interface SimpleGenerationTask extends ChunkStatus.GenerationTask {
		@Override
		default CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(
			ChunkStatus chunkStatus,
			Executor executor,
			ServerWorld serverWorld,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ServerLightingProvider serverLightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
			List<Chunk> list,
			Chunk chunk,
			boolean bl
		) {
			if (bl || !chunk.getStatus().isAtLeast(chunkStatus)) {
				this.doWork(chunkStatus, serverWorld, chunkGenerator, list, chunk);
				if (chunk instanceof ProtoChunk) {
					((ProtoChunk)chunk).setStatus(chunkStatus);
				}
			}

			return CompletableFuture.completedFuture(Either.left(chunk));
		}

		void doWork(ChunkStatus targetStatus, ServerWorld world, ChunkGenerator chunkGenerator, List<Chunk> list, Chunk chunk);
	}
}
