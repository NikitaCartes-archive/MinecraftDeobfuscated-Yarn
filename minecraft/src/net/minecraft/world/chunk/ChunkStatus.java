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
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ChunkStatus {
	public static final ChunkStatus EMPTY = method_16555("empty", null, -1, false, ChunkStatus.ChunkType.PROTOCHUNK, (world, chunkGenerator, list, chunk) -> {
	});
	public static final ChunkStatus STRUCTURE_STARTS = method_16557(
		"structure_starts",
		EMPTY,
		0,
		false,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(chunkStatus, world, chunkGenerator, serverLightingProvider, function, list, chunk) -> {
			if (!chunk.getStatus().isAfter(method_16561()) || !chunk.isLightOn()) {
				ChunkPos chunkPos = chunk.getPos();
				int i = chunkPos.x;
				int j = chunkPos.z;
				serverLightingProvider.method_15557(i, j, true);
			}

			if (!chunk.getStatus().isAfter(chunkStatus) && world.getLevelProperties().hasStructures()) {
				chunkGenerator.method_16129(chunk, chunkGenerator, world.getSaveHandler().getStructureManager());
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
		(world, chunkGenerator, list, chunk) -> chunkGenerator.addStructureReferences(new ChunkRegion(world, list), chunk)
	);
	public static final ChunkStatus BIOMES = method_16555(
		"biomes", STRUCTURE_REFERENCES, 0, false, ChunkStatus.ChunkType.PROTOCHUNK, (world, chunkGenerator, list, chunk) -> chunkGenerator.populateBiomes(chunk)
	);
	public static final ChunkStatus NOISE = method_16555(
		"noise",
		BIOMES,
		8,
		false,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(world, chunkGenerator, list, chunk) -> chunkGenerator.populateNoise(new ChunkRegion(world, list), chunk)
	);
	public static final ChunkStatus SURFACE = method_16555(
		"surface", NOISE, 0, false, ChunkStatus.ChunkType.PROTOCHUNK, (world, chunkGenerator, list, chunk) -> chunkGenerator.buildSurface(chunk)
	);
	public static final ChunkStatus CARVERS = method_16555(
		"carvers",
		SURFACE,
		0,
		false,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(world, chunkGenerator, list, chunk) -> chunkGenerator.carve(chunk, GenerationStep.Carver.field_13169)
	);
	public static final ChunkStatus LIQUID_CARVERS = method_16555(
		"liquid_carvers",
		CARVERS,
		0,
		true,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(world, chunkGenerator, list, chunk) -> chunkGenerator.carve(chunk, GenerationStep.Carver.field_13166)
	);
	public static final ChunkStatus FEATURES = method_16555(
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
	public static final ChunkStatus LIGHT = method_16557(
		"light", FEATURES, 1, true, ChunkStatus.ChunkType.PROTOCHUNK, (chunkStatus, world, chunkGenerator, serverLightingProvider, function, list, chunk) -> {
			chunk.setLightingProvider(serverLightingProvider);
			ChunkPos chunkPos = chunk.getPos();
			int i = chunkPos.x;
			int j = chunkPos.z;
			boolean bl = chunk.getStatus().isAfter(chunkStatus) && chunk.isLightOn();
			if (!chunk.getStatus().isAfter(chunkStatus)) {
				((ProtoChunk)chunk).setStatus(chunkStatus);
			}

			return serverLightingProvider.method_17310(chunk, i, j, bl);
		}
	);
	public static final ChunkStatus SPAWN = method_16555(
		"spawn",
		LIGHT,
		0,
		true,
		ChunkStatus.ChunkType.PROTOCHUNK,
		(world, chunkGenerator, list, chunk) -> chunkGenerator.populateEntities(new ChunkRegion(world, list))
	);
	public static final ChunkStatus HEIGHTMAPS = method_16555(
		"heightmaps", SPAWN, 0, true, ChunkStatus.ChunkType.PROTOCHUNK, (world, chunkGenerator, list, chunk) -> {
		}
	);
	public static final ChunkStatus FULL = method_16557(
		"full",
		HEIGHTMAPS,
		0,
		true,
		ChunkStatus.ChunkType.LEVELCHUNK,
		(chunkStatus, world, chunkGenerator, serverLightingProvider, function, list, chunk) -> (CompletableFuture<Chunk>)function.apply(chunk)
	);
	private static final List<ChunkStatus> ORDERED = ImmutableList.of(
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
	private static final IntList STATUS_ORDER = SystemUtil.consume(new IntArrayList(createOrderedList().size()), intArrayList -> {
		int i = 0;

		for (int j = createOrderedList().size() - 1; j >= 0; j--) {
			while (i + 1 < ORDERED.size() && j <= ((ChunkStatus)ORDERED.get(i + 1)).getIndex()) {
				i++;
			}

			intArrayList.add(0, i);
		}
	});
	private final String name;
	private final int index;
	private final ChunkStatus previous;
	private final ChunkStatus.class_2807 field_12792;
	private final int field_12802;
	private final ChunkStatus.ChunkType chunkType;
	private final boolean field_12793;

	private static ChunkStatus method_16555(
		String string, @Nullable ChunkStatus chunkStatus, int i, boolean bl, ChunkStatus.ChunkType chunkType, ChunkStatus.class_3768 arg
	) {
		return method_16557(string, chunkStatus, i, bl, chunkType, arg);
	}

	private static ChunkStatus method_16557(
		String string, @Nullable ChunkStatus chunkStatus, int i, boolean bl, ChunkStatus.ChunkType chunkType, ChunkStatus.class_2807 arg
	) {
		return Registry.register(Registry.CHUNK_STATUS, string, new ChunkStatus(string, chunkStatus, i, bl, chunkType, arg));
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

	private static ChunkStatus method_16561() {
		return LIGHT;
	}

	public static ChunkStatus getByIndex(int i) {
		if (i >= ORDERED.size()) {
			return EMPTY;
		} else {
			return i < 0 ? FULL : (ChunkStatus)ORDERED.get(i);
		}
	}

	public static int getStatusCount() {
		return ORDERED.size();
	}

	public static int getIndex(ChunkStatus chunkStatus) {
		return STATUS_ORDER.getInt(chunkStatus.getIndex());
	}

	ChunkStatus(String string, @Nullable ChunkStatus chunkStatus, int i, boolean bl, ChunkStatus.ChunkType chunkType, ChunkStatus.class_2807 arg) {
		this.name = string;
		this.previous = chunkStatus == null ? this : chunkStatus;
		this.field_12792 = arg;
		this.field_12802 = i;
		this.chunkType = chunkType;
		this.field_12793 = bl;
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
		World world,
		ChunkGenerator<?> chunkGenerator,
		ServerLightingProvider serverLightingProvider,
		Function<Chunk, CompletableFuture<Chunk>> function,
		List<Chunk> list
	) {
		Chunk chunk = (Chunk)list.get(list.size() / 2);
		CompletableFuture<Chunk> completableFuture = this.field_12792.doWork(this, world, chunkGenerator, serverLightingProvider, function, list, chunk);
		return this.chunkType == ChunkStatus.ChunkType.PROTOCHUNK ? completableFuture.thenApply(chunkx -> {
			((ProtoChunk)chunkx).setStatus(this);
			return chunkx;
		}) : completableFuture;
	}

	public int method_12152() {
		return this.field_12802;
	}

	public ChunkStatus.ChunkType getChunkType() {
		return this.chunkType;
	}

	public static ChunkStatus get(String string) {
		return Registry.CHUNK_STATUS.get(Identifier.create(string));
	}

	public boolean method_12160() {
		return this.field_12793;
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

	interface class_2807 {
		CompletableFuture<Chunk> doWork(
			ChunkStatus chunkStatus,
			World world,
			ChunkGenerator<?> chunkGenerator,
			ServerLightingProvider serverLightingProvider,
			Function<Chunk, CompletableFuture<Chunk>> function,
			List<Chunk> list,
			Chunk chunk
		);
	}

	interface class_3768 extends ChunkStatus.class_2807 {
		@Override
		default CompletableFuture<Chunk> doWork(
			ChunkStatus chunkStatus,
			World world,
			ChunkGenerator<?> chunkGenerator,
			ServerLightingProvider serverLightingProvider,
			Function<Chunk, CompletableFuture<Chunk>> function,
			List<Chunk> list,
			Chunk chunk
		) {
			if (!chunk.getStatus().isAfter(chunkStatus)) {
				this.doWork(world, chunkGenerator, list, chunk);
			}

			return CompletableFuture.completedFuture(chunk);
		}

		void doWork(World world, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk);
	}
}
