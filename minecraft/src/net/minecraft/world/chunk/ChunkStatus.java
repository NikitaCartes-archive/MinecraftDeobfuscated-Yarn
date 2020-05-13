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
	private static final ChunkStatus.NoGenTask STATUS_BUMP_NO_GEN_TASK = (chunkStatus, serverWorld, structureManager, serverLightingProvider, function, chunk) -> {
		if (chunk instanceof ProtoChunk && !chunk.getStatus().isAtLeast(chunkStatus)) {
			((ProtoChunk)chunk).setStatus(chunkStatus);
		}

		return CompletableFuture.completedFuture(Either.left(chunk));
	};
	public static final ChunkStatus EMPTY = register(
		"empty", null, -1, PRE_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.PROTOCHUNK, (serverWorld, chunkGenerator, list, chunk) -> {
		}
	);
	public static final ChunkStatus STRUCTURE_STARTS = register(
		"structure_starts",
		EMPTY,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> {
			if (!chunk.getStatus().isAtLeast(chunkStatus)) {
				if (serverWorld.getServer().method_27728().method_28057().method_28029()) {
					chunkGenerator.setStructureStarts(
						serverWorld.getStructureAccessor(),
						serverWorld.getBiomeAccess().withSource(chunkGenerator.getBiomeSource()),
						chunk,
						chunkGenerator,
						structureManager,
						serverWorld.getSeed()
					);
				}

				if (chunk instanceof ProtoChunk) {
					((ProtoChunk)chunk).setStatus(chunkStatus);
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
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.addStructureReferences(
				new ChunkRegion(serverWorld, list), serverWorld.getStructureAccessor(), chunk
			)
	);
	public static final ChunkStatus BIOMES = register(
		"biomes",
		STRUCTURE_REFERENCES,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.populateBiomes(chunk)
	);
	public static final ChunkStatus NOISE = register(
		"noise",
		BIOMES,
		8,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.populateNoise(new ChunkRegion(serverWorld, list), serverWorld.getStructureAccessor(), chunk)
	);
	public static final ChunkStatus SURFACE = register(
		"surface",
		NOISE,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.buildSurface(new ChunkRegion(serverWorld, list), chunk)
	);
	public static final ChunkStatus CARVERS = register(
		"carvers",
		SURFACE,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.carve(
				serverWorld.getSeed(), serverWorld.getBiomeAccess().withSource(chunkGenerator.getBiomeSource()), chunk, GenerationStep.Carver.AIR
			)
	);
	public static final ChunkStatus LIQUID_CARVERS = register(
		"liquid_carvers",
		CARVERS,
		0,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.carve(
				serverWorld.getSeed(), serverWorld.getBiomeAccess().withSource(chunkGenerator.getBiomeSource()), chunk, GenerationStep.Carver.LIQUID
			)
	);
	public static final ChunkStatus FEATURES = register(
		"features",
		LIQUID_CARVERS,
		8,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> {
			ProtoChunk protoChunk = (ProtoChunk)chunk;
			protoChunk.setLightingProvider(serverLightingProvider);
			if (!chunk.getStatus().isAtLeast(chunkStatus)) {
				Heightmap.populateHeightmaps(
					chunk, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE)
				);
				chunkGenerator.generateFeatures(new ChunkRegion(serverWorld, list), serverWorld.getStructureAccessor());
				protoChunk.setStatus(chunkStatus);
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
		(chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> getLightingFuture(
				chunkStatus, serverLightingProvider, chunk
			),
		(chunkStatus, serverWorld, structureManager, serverLightingProvider, function, chunk) -> getLightingFuture(chunkStatus, serverLightingProvider, chunk)
	);
	public static final ChunkStatus SPAWN = register(
		"spawn",
		LIGHT,
		0,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.populateEntities(new ChunkRegion(serverWorld, list))
	);
	public static final ChunkStatus HEIGHTMAPS = register(
		"heightmaps", SPAWN, 0, POST_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.PROTOCHUNK, (serverWorld, chunkGenerator, list, chunk) -> {
		}
	);
	public static final ChunkStatus FULL = register(
		"full",
		HEIGHTMAPS,
		0,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.LEVELCHUNK,
		(chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)function.apply(
				chunk
			),
		(chunkStatus, serverWorld, structureManager, serverLightingProvider, function, chunk) -> (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)function.apply(
				chunk
			)
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
	private static final IntList STATUS_TO_TARGET_GENERATION_RADIUS = Util.make(new IntArrayList(createOrderedList().size()), intArrayList -> {
		int i = 0;

		for (int j = createOrderedList().size() - 1; j >= 0; j--) {
			while (i + 1 < DISTANCE_TO_TARGET_GENERATION_STATUS.size() && j <= ((ChunkStatus)DISTANCE_TO_TARGET_GENERATION_STATUS.get(i + 1)).getIndex()) {
				i++;
			}

			intArrayList.add(0, i);
		}
	});
	private final String id;
	private final int index;
	private final ChunkStatus previous;
	private final ChunkStatus.Task task;
	private final ChunkStatus.NoGenTask noGenTask;
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
		ChunkStatus.SimpleTask task
	) {
		return register(id, previous, taskMargin, heightMapTypes, chunkType, (ChunkStatus.Task)task);
	}

	private static ChunkStatus register(
		String id, @Nullable ChunkStatus previous, int taskMargin, EnumSet<Heightmap.Type> heightMapTypes, ChunkStatus.ChunkType chunkType, ChunkStatus.Task task
	) {
		return register(id, previous, taskMargin, heightMapTypes, chunkType, task, STATUS_BUMP_NO_GEN_TASK);
	}

	private static ChunkStatus register(
		String id,
		@Nullable ChunkStatus previous,
		int taskMargin,
		EnumSet<Heightmap.Type> heightMapTypes,
		ChunkStatus.ChunkType chunkType,
		ChunkStatus.Task task,
		ChunkStatus.NoGenTask noGenTask
	) {
		return Registry.register(Registry.CHUNK_STATUS, id, new ChunkStatus(id, previous, taskMargin, heightMapTypes, chunkType, task, noGenTask));
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

	public static ChunkStatus getTargetGenerationStatus(int distance) {
		if (distance >= DISTANCE_TO_TARGET_GENERATION_STATUS.size()) {
			return EMPTY;
		} else {
			return distance < 0 ? FULL : (ChunkStatus)DISTANCE_TO_TARGET_GENERATION_STATUS.get(distance);
		}
	}

	public static int getMaxTargetGenerationRadius() {
		return DISTANCE_TO_TARGET_GENERATION_STATUS.size();
	}

	public static int getTargetGenerationRadius(ChunkStatus status) {
		return STATUS_TO_TARGET_GENERATION_RADIUS.getInt(status.getIndex());
	}

	ChunkStatus(
		String id,
		@Nullable ChunkStatus previous,
		int taskMargin,
		EnumSet<Heightmap.Type> heightMapTypes,
		ChunkStatus.ChunkType chunkType,
		ChunkStatus.Task task,
		ChunkStatus.NoGenTask noGenTask
	) {
		this.id = id;
		this.previous = previous == null ? this : previous;
		this.task = task;
		this.noGenTask = noGenTask;
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

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> runTask(
		ServerWorld serverWorld,
		ChunkGenerator chunkGenerator,
		StructureManager structureManager,
		ServerLightingProvider serverLightingProvider,
		Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
		List<Chunk> list
	) {
		return this.task.doWork(this, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, (Chunk)list.get(list.size() / 2));
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> runNoGenTask(
		ServerWorld serverWorld,
		StructureManager structureManager,
		ServerLightingProvider serverLightingProvider,
		Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
		Chunk chunk
	) {
		return this.noGenTask.doWork(this, serverWorld, structureManager, serverLightingProvider, function, chunk);
	}

	public int getTaskMargin() {
		return this.taskMargin;
	}

	public ChunkStatus.ChunkType getChunkType() {
		return this.chunkType;
	}

	public static ChunkStatus get(String id) {
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

	public static enum ChunkType {
		PROTOCHUNK,
		LEVELCHUNK;
	}

	interface NoGenTask {
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(
			ChunkStatus chunkStatus,
			ServerWorld serverWorld,
			StructureManager structureManager,
			ServerLightingProvider serverLightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
			Chunk chunk
		);
	}

	interface SimpleTask extends ChunkStatus.Task {
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

		void doWork(ServerWorld serverWorld, ChunkGenerator generator, List<Chunk> list, Chunk chunk);
	}

	interface Task {
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(
			ChunkStatus targetStatus,
			ServerWorld serverWorld,
			ChunkGenerator generator,
			StructureManager structureManager,
			ServerLightingProvider serverLightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
			List<Chunk> list,
			Chunk chunk
		);
	}
}
