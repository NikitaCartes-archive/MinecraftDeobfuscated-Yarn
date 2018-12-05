package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.class_2839;
import net.minecraft.class_3233;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ChunkStatus {
	public static final ChunkStatus field_12798 = register(
		"empty", null, -1, false, ChunkStatus.class_2808.field_12808, (serverChunkManager, world, chunkGenerator, list, chunk) -> {
		}
	);
	public static final ChunkStatus field_16423 = register(
		"structure_starts", field_12798, 0, false, ChunkStatus.class_2808.field_12808, (chunkStatus, serverChunkManager, world, chunkGenerator, list, chunk) -> {
			if (!chunk.getStatus().isAfter(method_16561()) || !chunk.method_12038()) {
				ChunkPos chunkPos = chunk.getPos();
				int i = chunkPos.x;
				int j = chunkPos.z;
				serverChunkManager.getLightingProvider().method_15557(i, j, true);
			}

			if (!chunk.getStatus().isAfter(chunkStatus) && world.getLevelProperties().hasStructures()) {
				chunkGenerator.method_16129(chunk, chunkGenerator, world.getSaveHandler().method_134());
			}

			return CompletableFuture.completedFuture(chunk);
		}
	);
	public static final ChunkStatus field_16422 = register(
		"structure_references",
		field_16423,
		8,
		false,
		ChunkStatus.class_2808.field_12808,
		(serverChunkManager, world, chunkGenerator, list, chunk) -> chunkGenerator.method_16130(new class_3233(world, list), chunk)
	);
	public static final ChunkStatus field_12794 = register(
		"biomes",
		field_16422,
		0,
		false,
		ChunkStatus.class_2808.field_12808,
		(serverChunkManager, world, chunkGenerator, list, chunk) -> chunkGenerator.populateBiomes(chunk)
	);
	public static final ChunkStatus field_12804 = register(
		"noise",
		field_12794,
		8,
		false,
		ChunkStatus.class_2808.field_12808,
		(serverChunkManager, world, chunkGenerator, list, chunk) -> chunkGenerator.populateNoise(new class_3233(world, list), chunk)
	);
	public static final ChunkStatus field_12796 = register(
		"surface",
		field_12804,
		0,
		false,
		ChunkStatus.class_2808.field_12808,
		(serverChunkManager, world, chunkGenerator, list, chunk) -> chunkGenerator.buildSurface(chunk)
	);
	public static final ChunkStatus field_12801 = register(
		"carvers",
		field_12796,
		0,
		false,
		ChunkStatus.class_2808.field_12808,
		(serverChunkManager, world, chunkGenerator, list, chunk) -> chunkGenerator.carve(chunk, GenerationStep.Carver.field_13169)
	);
	public static final ChunkStatus field_12790 = register(
		"liquid_carvers",
		field_12801,
		0,
		true,
		ChunkStatus.class_2808.field_12808,
		(serverChunkManager, world, chunkGenerator, list, chunk) -> chunkGenerator.carve(chunk, GenerationStep.Carver.field_13166)
	);
	public static final ChunkStatus field_12795 = register(
		"features",
		field_12790,
		8,
		true,
		ChunkStatus.class_2808.field_12808,
		(serverChunkManager, world, chunkGenerator, list, chunk) -> {
			Heightmap.method_16684(
				chunk, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE)
			);
			chunkGenerator.generateFeatures(new class_3233(world, list));
		}
	);
	public static final ChunkStatus field_12805 = register(
		"light", field_12795, 1, true, ChunkStatus.class_2808.field_12808, (chunkStatus, serverChunkManager, world, chunkGenerator, list, chunk) -> {
			chunk.method_12027(serverChunkManager);
			ChunkPos chunkPos = chunk.getPos();
			int i = chunkPos.x;
			int j = chunkPos.z;
			boolean bl = chunk.getStatus().isAfter(chunkStatus) && chunk.method_12038();
			ChunkSection[] chunkSections = chunk.getSectionArray();
			if (!chunk.getStatus().isAfter(chunkStatus)) {
				((class_2839)chunk).method_12308(chunkStatus);
			}

			LightingProvider lightingProvider = serverChunkManager.getLightingProvider();

			for (int k = 0; k < 16; k++) {
				ChunkSection chunkSection = chunkSections[k];
				boolean bl2 = chunkSection == WorldChunk.EMPTY_SECTION || chunkSection.isEmpty();
				if (!bl2) {
					lightingProvider.method_15551(i, k, j, false);
				}
			}

			if (!bl) {
				lightingProvider.method_15557(i, j, false);
				chunk.method_12018().forEach(blockPos -> lightingProvider.method_15560(blockPos, chunk.getLuminance(blockPos)));
			}

			return CompletableFuture.completedFuture(chunk);
		}
	);
	public static final ChunkStatus field_12786 = register(
		"spawn",
		field_12805,
		0,
		true,
		ChunkStatus.class_2808.field_12808,
		(serverChunkManager, world, chunkGenerator, list, chunk) -> chunkGenerator.populateEntities(new class_3233(world, list))
	);
	public static final ChunkStatus field_12800 = register(
		"heightmaps", field_12786, 0, true, ChunkStatus.class_2808.field_12808, (serverChunkManager, world, chunkGenerator, list, chunk) -> {
		}
	);
	public static final ChunkStatus field_12803 = register(
		"full",
		field_12800,
		0,
		true,
		ChunkStatus.class_2808.field_12807,
		(chunkStatus, serverChunkManager, world, chunkGenerator, list, chunk) -> serverChunkManager.method_14140((class_2839)chunk)
	);
	private static final List<ChunkStatus> ORDERED = ImmutableList.of(
		field_12803, field_12795, field_12790, field_16423, field_16423, field_16423, field_16423, field_16423, field_16423, field_16423, field_16423
	);
	private static final IntList STATUS_ORDER = SystemUtil.consume(new IntArrayList(createOrderedList().size()), intArrayList -> {
		int i = 0;

		for (int j = createOrderedList().size() - 1; j >= 0; j--) {
			while (i + 1 < ORDERED.size() && j <= ((ChunkStatus)ORDERED.get(i + 1)).getOrderId()) {
				i++;
			}

			intArrayList.add(0, i);
		}
	});
	private final String name;
	private final int orderId;
	private final ChunkStatus previous;
	private final ChunkStatus.class_2807 field_12792;
	private final int field_12802;
	private final ChunkStatus.class_2808 field_12787;
	private final boolean field_12793;

	private static ChunkStatus register(
		String string, @Nullable ChunkStatus chunkStatus, int i, boolean bl, ChunkStatus.class_2808 arg, ChunkStatus.class_3768 arg2
	) {
		return register(string, chunkStatus, i, bl, arg, (ChunkStatus.class_2807)arg2);
	}

	private static ChunkStatus register(
		String string, @Nullable ChunkStatus chunkStatus, int i, boolean bl, ChunkStatus.class_2808 arg, ChunkStatus.class_2807 arg2
	) {
		return Registry.register(Registry.CHUNK_STATUS, string, new ChunkStatus(string, chunkStatus, i, bl, arg, arg2));
	}

	public static List<ChunkStatus> createOrderedList() {
		List<ChunkStatus> list = Lists.<ChunkStatus>newArrayList();

		ChunkStatus chunkStatus;
		for (chunkStatus = field_12803; chunkStatus.getPrevious() != chunkStatus; chunkStatus = chunkStatus.getPrevious()) {
			list.add(chunkStatus);
		}

		list.add(chunkStatus);
		Collections.reverse(list);
		return list;
	}

	private static ChunkStatus method_16561() {
		return field_12805;
	}

	public static ChunkStatus getOrdered(int i) {
		if (i >= ORDERED.size()) {
			return field_12798;
		} else {
			return i < 0 ? field_12803 : (ChunkStatus)ORDERED.get(i);
		}
	}

	public static int getOrderedSize() {
		return ORDERED.size();
	}

	public static int method_12175(ChunkStatus chunkStatus) {
		return STATUS_ORDER.getInt(chunkStatus.getOrderId());
	}

	ChunkStatus(String string, @Nullable ChunkStatus chunkStatus, int i, boolean bl, ChunkStatus.class_2808 arg, ChunkStatus.class_2807 arg2) {
		this.name = string;
		this.previous = chunkStatus == null ? this : chunkStatus;
		this.field_12792 = arg2;
		this.field_12802 = i;
		this.field_12787 = arg;
		this.field_12793 = bl;
		this.orderId = chunkStatus == null ? 0 : chunkStatus.getOrderId() + 1;
	}

	public int getOrderId() {
		return this.orderId;
	}

	public String getName() {
		return this.name;
	}

	public ChunkStatus getPrevious() {
		return this.previous;
	}

	public CompletableFuture<Chunk> method_12154(ServerChunkManager serverChunkManager, ChunkGenerator<?> chunkGenerator, List<Chunk> list) {
		Chunk chunk = (Chunk)list.get(list.size() / 2);
		CompletableFuture<Chunk> completableFuture = this.field_12792.doWork(this, serverChunkManager, serverChunkManager.getWorld(), chunkGenerator, list, chunk);
		return this.field_12787 == ChunkStatus.class_2808.field_12808 ? completableFuture.thenApply(chunkx -> {
			((class_2839)chunkx).method_12308(this);
			return chunkx;
		}) : completableFuture;
	}

	public int method_12152() {
		return this.field_12802;
	}

	public ChunkStatus.class_2808 method_12164() {
		return this.field_12787;
	}

	public static ChunkStatus get(String string) {
		return Registry.CHUNK_STATUS.get(Identifier.create(string));
	}

	public boolean method_12160() {
		return this.field_12793;
	}

	public boolean isAfter(ChunkStatus chunkStatus) {
		return this.getOrderId() >= chunkStatus.getOrderId();
	}

	interface class_2807 {
		CompletableFuture<Chunk> doWork(
			ChunkStatus chunkStatus, ServerChunkManager serverChunkManager, World world, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk
		);
	}

	public static enum class_2808 {
		field_12808,
		field_12807;
	}

	interface class_3768 extends ChunkStatus.class_2807 {
		@Override
		default CompletableFuture<Chunk> doWork(
			ChunkStatus chunkStatus, ServerChunkManager serverChunkManager, World world, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk
		) {
			if (!chunk.getStatus().isAfter(chunkStatus)) {
				this.doWork(serverChunkManager, world, chunkGenerator, list, chunk);
			}

			return CompletableFuture.completedFuture(chunk);
		}

		void doWork(ServerChunkManager serverChunkManager, World world, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk);
	}
}
