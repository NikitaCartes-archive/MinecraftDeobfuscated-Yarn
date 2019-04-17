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
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ChunkStatus {
	private static final EnumSet<Heightmap.Type> PRE_CARVER_HEIGHTMAPS = EnumSet.of(Heightmap.Type.field_13195, Heightmap.Type.field_13194);
	private static final EnumSet<Heightmap.Type> POST_CARVER_HEIGHTMAPS = EnumSet.of(
		Heightmap.Type.field_13200, Heightmap.Type.field_13202, Heightmap.Type.field_13197, Heightmap.Type.field_13203
	);
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
				if (serverWorld.getLevelProperties().hasStructures()) {
					chunkGenerator.setStructureStarts(chunk, chunkGenerator, structureManager);
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
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.addStructureReferences(new ChunkRegion(serverWorld, list), chunk)
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
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.populateNoise(new ChunkRegion(serverWorld, list), chunk)
	);
	public static final ChunkStatus SURFACE = register(
		"surface",
		NOISE,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.buildSurface(chunk)
	);
	public static final ChunkStatus CARVERS = register(
		"carvers",
		SURFACE,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.carve(chunk, GenerationStep.Carver.AIR)
	);
	public static final ChunkStatus LIQUID_CARVERS = register(
		"liquid_carvers",
		CARVERS,
		0,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.carve(chunk, GenerationStep.Carver.LIQUID)
	);
	public static final ChunkStatus FEATURES = register(
		"features",
		LIQUID_CARVERS,
		8,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> {
			chunk.setLightingProvider(serverLightingProvider);
			if (!chunk.getStatus().isAtLeast(chunkStatus)) {
				Heightmap.populateHeightmaps(
					chunk, EnumSet.of(Heightmap.Type.field_13197, Heightmap.Type.field_13203, Heightmap.Type.field_13200, Heightmap.Type.field_13202)
				);
				chunkGenerator.generateFeatures(new ChunkRegion(serverWorld, list));
				if (chunk instanceof ProtoChunk) {
					((ProtoChunk)chunk).setStatus(chunkStatus);
				}
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
		(chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> {
			boolean bl = chunk.getStatus().isAtLeast(chunkStatus) && chunk.isLightOn();
			if (!chunk.getStatus().isAtLeast(chunkStatus)) {
				((ProtoChunk)chunk).setStatus(chunkStatus);
			}

			return serverLightingProvider.light(chunk, bl).thenApply(Either::left);
		}
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
	private final EnumSet<Heightmap.Type> surfaceGenerated;

	private static ChunkStatus register(
		String string, @Nullable ChunkStatus chunkStatus, int i, EnumSet<Heightmap.Type> enumSet, ChunkStatus.ChunkType chunkType, ChunkStatus.SimpleTask simpleTask
	) {
		return register(string, chunkStatus, i, enumSet, chunkType, (ChunkStatus.Task)simpleTask);
	}

	private static ChunkStatus register(
		String string, @Nullable ChunkStatus chunkStatus, int i, EnumSet<Heightmap.Type> enumSet, ChunkStatus.ChunkType chunkType, ChunkStatus.Task task
	) {
		return Registry.register(Registry.CHUNK_STATUS, string, new ChunkStatus(string, chunkStatus, i, enumSet, chunkType, task));
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

	ChunkStatus(String string, @Nullable ChunkStatus chunkStatus, int i, EnumSet<Heightmap.Type> enumSet, ChunkStatus.ChunkType chunkType, ChunkStatus.Task task) {
		this.name = string;
		this.previous = chunkStatus == null ? this : chunkStatus;
		this.task = task;
		this.taskMargin = i;
		this.chunkType = chunkType;
		this.surfaceGenerated = enumSet;
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

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> runTask(
		ServerWorld serverWorld,
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		ServerLightingProvider serverLightingProvider,
		Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
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
		return Registry.CHUNK_STATUS.get(Identifier.create(string));
	}

	public EnumSet<Heightmap.Type> isSurfaceGenerated() {
		return this.surfaceGenerated;
	}

	public boolean isAtLeast(ChunkStatus chunkStatus) {
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
		default CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(
			ChunkStatus chunkStatus,
			ServerWorld serverWorld,
			ChunkGenerator<?> chunkGenerator,
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

		void doWork(ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk);
	}

	interface Task {
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(
			ChunkStatus chunkStatus,
			ServerWorld serverWorld,
			ChunkGenerator<?> chunkGenerator,
			StructureManager structureManager,
			ServerLightingProvider serverLightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
			List<Chunk> list,
			Chunk chunk
		);
	}
}
