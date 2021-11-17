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
import net.minecraft.util.profiling.jfr.Finishable;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ChunkStatus {
	public static final int field_35470 = 8;
	private static final EnumSet<Heightmap.Type> PRE_CARVER_HEIGHTMAPS = EnumSet.of(Heightmap.Type.OCEAN_FLOOR_WG, Heightmap.Type.WORLD_SURFACE_WG);
	public static final EnumSet<Heightmap.Type> POST_CARVER_HEIGHTMAPS = EnumSet.of(
		Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE, Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES
	);
	/**
	 * A load task which only bumps the chunk status of the chunk.
	 */
	private static final ChunkStatus.LoadTask STATUS_BUMP_LOAD_TASK = (targetStatus, world, structureManager, lightingProvider, function, chunk) -> {
		if (chunk instanceof ProtoChunk protoChunk && !chunk.getStatus().isAtLeast(targetStatus)) {
			protoChunk.setStatus(targetStatus);
		}

		return CompletableFuture.completedFuture(Either.left(chunk));
	};
	public static final ChunkStatus EMPTY = register(
		"empty", null, -1, PRE_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.PROTOCHUNK, (targetStatus, world, generator, chunks, chunk) -> {
		}
	);
	public static final ChunkStatus STRUCTURE_STARTS = register(
		"structure_starts",
		EMPTY,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(targetStatus, executor, world, generator, structureManager, lightingProvider, function, chunks, chunk, bl) -> {
			if (!chunk.getStatus().isAtLeast(targetStatus)) {
				if (world.getServer().getSaveProperties().getGeneratorOptions().shouldGenerateStructures()) {
					generator.setStructureStarts(world.getRegistryManager(), world.getStructureAccessor(), chunk, structureManager, world.getSeed());
				}

				if (chunk instanceof ProtoChunk protoChunk) {
					protoChunk.setStatus(targetStatus);
				}

				world.method_39778(chunk);
			}

			return CompletableFuture.completedFuture(Either.left(chunk));
		},
		(chunkStatus, serverWorld, structureManager, serverLightingProvider, function, chunk) -> {
			if (!chunk.getStatus().isAtLeast(chunkStatus)) {
				if (chunk instanceof ProtoChunk protoChunk) {
					protoChunk.setStatus(chunkStatus);
				}

				serverWorld.method_39778(chunk);
			}

			return CompletableFuture.completedFuture(Either.left(chunk));
		}
	);
	public static final ChunkStatus STRUCTURE_REFERENCES = register(
		"structure_references", STRUCTURE_STARTS, 8, PRE_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.PROTOCHUNK, (targetStatus, world, generator, chunks, chunk) -> {
			ChunkRegion chunkRegion = new ChunkRegion(world, chunks, targetStatus, -1);
			generator.addStructureReferences(chunkRegion, world.getStructureAccessor().forRegion(chunkRegion), chunk);
		}
	);
	public static final ChunkStatus BIOMES = register(
		"biomes",
		STRUCTURE_REFERENCES,
		8,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(targetStatus, executor, world, generator, structureManager, lightingProvider, function, chunks, chunk, bl) -> {
			if (!bl && chunk.getStatus().isAtLeast(targetStatus)) {
				return CompletableFuture.completedFuture(Either.left(chunk));
			} else {
				ChunkRegion chunkRegion = new ChunkRegion(world, chunks, targetStatus, -1);
				return generator.populateBiomes(
						world.getRegistryManager().get(Registry.BIOME_KEY), executor, Blender.getBlender(chunkRegion), world.getStructureAccessor().forRegion(chunkRegion), chunk
					)
					.thenApply(chunkx -> {
						if (chunkx instanceof ProtoChunk) {
							((ProtoChunk)chunkx).setStatus(targetStatus);
						}

						return Either.left(chunkx);
					});
			}
		}
	);
	public static final ChunkStatus NOISE = register(
		"noise",
		BIOMES,
		8,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(targetStatus, executor, world, generator, structureManager, lightingProvider, function, chunks, chunk, bl) -> {
			if (!bl && chunk.getStatus().isAtLeast(targetStatus)) {
				return CompletableFuture.completedFuture(Either.left(chunk));
			} else {
				ChunkRegion chunkRegion = new ChunkRegion(world, chunks, targetStatus, 0);
				return generator.populateNoise(executor, Blender.getBlender(chunkRegion), world.getStructureAccessor().forRegion(chunkRegion), chunk).thenApply(chunkx -> {
					if (chunkx instanceof ProtoChunk protoChunk) {
						BelowZeroRetrogen belowZeroRetrogen = protoChunk.getBelowZeroRetrogen();
						if (belowZeroRetrogen != null) {
							BelowZeroRetrogen.replaceOldBedrock(protoChunk);
							if (belowZeroRetrogen.hasNoBedrock()) {
								BelowZeroRetrogen.fillChunkWithAir(protoChunk);
							}
						}

						protoChunk.setStatus(targetStatus);
					}

					return Either.left(chunkx);
				});
			}
		}
	);
	public static final ChunkStatus SURFACE = register(
		"surface", NOISE, 8, PRE_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.PROTOCHUNK, (targetStatus, world, generator, chunks, chunk) -> {
			ChunkRegion chunkRegion = new ChunkRegion(world, chunks, targetStatus, 0);
			generator.buildSurface(chunkRegion, world.getStructureAccessor().forRegion(chunkRegion), chunk);
		}
	);
	public static final ChunkStatus CARVERS = register(
		"carvers", SURFACE, 8, PRE_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.PROTOCHUNK, (targetStatus, world, generator, chunks, chunk) -> {
			ChunkRegion chunkRegion = new ChunkRegion(world, chunks, targetStatus, 0);
			if (chunk instanceof ProtoChunk protoChunk) {
				Blender.method_39809(chunkRegion, protoChunk);
			}

			generator.carve(chunkRegion, world.getSeed(), world.getBiomeAccess(), world.getStructureAccessor().forRegion(chunkRegion), chunk, GenerationStep.Carver.AIR);
		}
	);
	public static final ChunkStatus LIQUID_CARVERS = register(
		"liquid_carvers", CARVERS, 8, POST_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.PROTOCHUNK, (chunkStatus, serverWorld, chunkGenerator, list, chunk) -> {
		}
	);
	public static final ChunkStatus FEATURES = register(
		"features",
		LIQUID_CARVERS,
		8,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(targetStatus, executor, world, generator, structureManager, lightingProvider, function, chunks, chunk, bl) -> {
			ProtoChunk protoChunk = (ProtoChunk)chunk;
			protoChunk.setLightingProvider(lightingProvider);
			if (bl || !chunk.getStatus().isAtLeast(targetStatus)) {
				Heightmap.populateHeightmaps(
					chunk, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE)
				);
				ChunkRegion chunkRegion = new ChunkRegion(world, chunks, targetStatus, 1);
				generator.generateFeatures(chunkRegion, chunk, world.getStructureAccessor().forRegion(chunkRegion));
				Blender.tickLeavesAndFluids(chunkRegion, chunk);
				protoChunk.setStatus(targetStatus);
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
		(targetStatus, executor, world, generator, structureManager, lightingProvider, function, chunks, chunk, bl) -> getLightingFuture(
				targetStatus, lightingProvider, chunk
			),
		(chunkStatus, serverWorld, structureManager, serverLightingProvider, function, chunk) -> getLightingFuture(chunkStatus, serverLightingProvider, chunk)
	);
	public static final ChunkStatus SPAWN = register(
		"spawn", LIGHT, 0, POST_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.PROTOCHUNK, (targetStatus, world, generator, chunks, chunk) -> {
			if (!chunk.hasBelowZeroRetrogen()) {
				generator.populateEntities(new ChunkRegion(world, chunks, targetStatus, -1));
			}
		}
	);
	public static final ChunkStatus HEIGHTMAPS = register(
		"heightmaps", SPAWN, 0, POST_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.PROTOCHUNK, (targetStatus, world, generator, chunks, chunk) -> {
		}
	);
	public static final ChunkStatus FULL = register(
		"full",
		HEIGHTMAPS,
		0,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.LEVELCHUNK,
		(targetStatus, executor, world, generator, structureManager, lightingProvider, function, chunks, chunk, bl) -> (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)function.apply(
				chunk
			),
		(chunkStatus, serverWorld, structureManager, serverLightingProvider, function, chunk) -> (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)function.apply(
				chunk
			)
	);
	private static final List<ChunkStatus> DISTANCE_TO_STATUS = ImmutableList.of(
		FULL,
		FEATURES,
		LIQUID_CARVERS,
		BIOMES,
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
		Finishable finishable = FlightProfiler.INSTANCE.startChunkGenerationProfiling(chunk.getPos(), world.getRegistryKey(), this.id);
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.generationTask
			.doWork(this, executor, world, generator, structureManager, lightingProvider, fullChunkConverter, chunks, chunk, bl);
		return finishable != null ? completableFuture.thenApply(either -> {
			finishable.finish();
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

	public boolean isAtLeast(ChunkStatus chunkStatus) {
		return this.getIndex() >= chunkStatus.getIndex();
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
			List<Chunk> chunks,
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

		void doWork(ChunkStatus targetStatus, ServerWorld world, ChunkGenerator chunkGenerator, List<Chunk> chunks, Chunk chunk);
	}
}
