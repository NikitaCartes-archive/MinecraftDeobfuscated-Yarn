package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.VisibleForTesting;

public class ChunkStatus {
	public static final int field_35470 = 8;
	private static final EnumSet<Heightmap.Type> field_51904 = EnumSet.of(Heightmap.Type.OCEAN_FLOOR_WG, Heightmap.Type.WORLD_SURFACE_WG);
	public static final EnumSet<Heightmap.Type> field_51903 = EnumSet.of(
		Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE, Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES
	);
	public static final ChunkStatus EMPTY = method_60546("empty", null, field_51904, ChunkType.PROTOCHUNK);
	public static final ChunkStatus STRUCTURE_STARTS = method_60546("structure_starts", EMPTY, field_51904, ChunkType.PROTOCHUNK);
	public static final ChunkStatus STRUCTURE_REFERENCES = method_60546("structure_references", STRUCTURE_STARTS, field_51904, ChunkType.PROTOCHUNK);
	public static final ChunkStatus BIOMES = method_60546("biomes", STRUCTURE_REFERENCES, field_51904, ChunkType.PROTOCHUNK);
	public static final ChunkStatus NOISE = method_60546("noise", BIOMES, field_51904, ChunkType.PROTOCHUNK);
	public static final ChunkStatus SURFACE = method_60546("surface", NOISE, field_51904, ChunkType.PROTOCHUNK);
	public static final ChunkStatus CARVERS = method_60546("carvers", SURFACE, field_51903, ChunkType.PROTOCHUNK);
	public static final ChunkStatus FEATURES = method_60546("features", CARVERS, field_51903, ChunkType.PROTOCHUNK);
	public static final ChunkStatus INITIALIZE_LIGHT = method_60546("initialize_light", FEATURES, field_51903, ChunkType.PROTOCHUNK);
	public static final ChunkStatus LIGHT = method_60546("light", INITIALIZE_LIGHT, field_51903, ChunkType.PROTOCHUNK);
	public static final ChunkStatus SPAWN = method_60546("spawn", LIGHT, field_51903, ChunkType.PROTOCHUNK);
	public static final ChunkStatus FULL = method_60546("full", SPAWN, field_51903, ChunkType.LEVELCHUNK);
	private final int index;
	private final ChunkStatus previous;
	private final ChunkType chunkType;
	private final EnumSet<Heightmap.Type> heightMapTypes;

	private static ChunkStatus method_60546(String string, @Nullable ChunkStatus chunkStatus, EnumSet<Heightmap.Type> enumSet, ChunkType chunkType) {
		return Registry.register(Registries.CHUNK_STATUS, string, new ChunkStatus(chunkStatus, enumSet, chunkType));
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

	@VisibleForTesting
	protected ChunkStatus(@Nullable ChunkStatus previous, EnumSet<Heightmap.Type> enumSet, ChunkType chunkType) {
		this.previous = previous == null ? this : previous;
		this.chunkType = chunkType;
		this.heightMapTypes = enumSet;
		this.index = previous == null ? 0 : previous.getIndex() + 1;
	}

	public int getIndex() {
		return this.index;
	}

	public ChunkStatus getPrevious() {
		return this.previous;
	}

	public ChunkType getChunkType() {
		return this.chunkType;
	}

	public static ChunkStatus byId(String id) {
		return Registries.CHUNK_STATUS.get(Identifier.tryParse(id));
	}

	public EnumSet<Heightmap.Type> getHeightmapTypes() {
		return this.heightMapTypes;
	}

	public boolean isAtLeast(ChunkStatus chunkStatus) {
		return this.getIndex() >= chunkStatus.getIndex();
	}

	public boolean method_60547(ChunkStatus chunkStatus) {
		return this.getIndex() > chunkStatus.getIndex();
	}

	public boolean method_60548(ChunkStatus chunkStatus) {
		return this.getIndex() <= chunkStatus.getIndex();
	}

	public boolean method_60549(ChunkStatus chunkStatus) {
		return this.getIndex() < chunkStatus.getIndex();
	}

	public static ChunkStatus method_60545(ChunkStatus chunkStatus, ChunkStatus chunkStatus2) {
		return chunkStatus.method_60547(chunkStatus2) ? chunkStatus : chunkStatus2;
	}

	public String toString() {
		return this.method_60550();
	}

	public String method_60550() {
		return Registries.CHUNK_STATUS.getId(this).toString();
	}
}
