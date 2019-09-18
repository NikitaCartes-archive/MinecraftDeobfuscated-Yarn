package net.minecraft.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public class FeatureUpdater {
	private static final Map<String, String> OLD_TO_NEW = SystemUtil.consume(Maps.<String, String>newHashMap(), hashMap -> {
		hashMap.put("Village", "Village");
		hashMap.put("Mineshaft", "Mineshaft");
		hashMap.put("Mansion", "Mansion");
		hashMap.put("Igloo", "Temple");
		hashMap.put("Desert_Pyramid", "Temple");
		hashMap.put("Jungle_Pyramid", "Temple");
		hashMap.put("Swamp_Hut", "Temple");
		hashMap.put("Stronghold", "Stronghold");
		hashMap.put("Monument", "Monument");
		hashMap.put("Fortress", "Fortress");
		hashMap.put("EndCity", "EndCity");
	});
	private static final Map<String, String> ANCIENT_TO_OLD = SystemUtil.consume(Maps.<String, String>newHashMap(), hashMap -> {
		hashMap.put("Iglu", "Igloo");
		hashMap.put("TeDP", "Desert_Pyramid");
		hashMap.put("TeJP", "Jungle_Pyramid");
		hashMap.put("TeSH", "Swamp_Hut");
	});
	private final boolean needsUpdate;
	private final Map<String, Long2ObjectMap<CompoundTag>> featureIdToChunkTag = Maps.<String, Long2ObjectMap<CompoundTag>>newHashMap();
	private final Map<String, ChunkUpdateState> updateStates = Maps.<String, ChunkUpdateState>newHashMap();
	private final List<String> field_17658;
	private final List<String> field_17659;

	public FeatureUpdater(@Nullable PersistentStateManager persistentStateManager, List<String> list, List<String> list2) {
		this.field_17658 = list;
		this.field_17659 = list2;
		this.init(persistentStateManager);
		boolean bl = false;

		for (String string : this.field_17659) {
			bl |= this.featureIdToChunkTag.get(string) != null;
		}

		this.needsUpdate = bl;
	}

	public void markResolved(long l) {
		for (String string : this.field_17658) {
			ChunkUpdateState chunkUpdateState = (ChunkUpdateState)this.updateStates.get(string);
			if (chunkUpdateState != null && chunkUpdateState.isRemaining(l)) {
				chunkUpdateState.markResolved(l);
				chunkUpdateState.markDirty();
			}
		}
	}

	public CompoundTag getUpdatedReferences(CompoundTag compoundTag) {
		CompoundTag compoundTag2 = compoundTag.getCompound("Level");
		ChunkPos chunkPos = new ChunkPos(compoundTag2.getInt("xPos"), compoundTag2.getInt("zPos"));
		if (this.needsUpdate(chunkPos.x, chunkPos.z)) {
			compoundTag = this.getUpdatedStarts(compoundTag, chunkPos);
		}

		CompoundTag compoundTag3 = compoundTag2.getCompound("Structures");
		CompoundTag compoundTag4 = compoundTag3.getCompound("References");

		for (String string : this.field_17659) {
			StructureFeature<?> structureFeature = (StructureFeature<?>)Feature.STRUCTURES.get(string.toLowerCase(Locale.ROOT));
			if (!compoundTag4.containsKey(string, 12) && structureFeature != null) {
				int i = structureFeature.getRadius();
				LongList longList = new LongArrayList();

				for (int j = chunkPos.x - i; j <= chunkPos.x + i; j++) {
					for (int k = chunkPos.z - i; k <= chunkPos.z + i; k++) {
						if (this.needsUpdate(j, k, string)) {
							longList.add(ChunkPos.toLong(j, k));
						}
					}
				}

				compoundTag4.putLongArray(string, longList);
			}
		}

		compoundTag3.put("References", compoundTag4);
		compoundTag2.put("Structures", compoundTag3);
		compoundTag.put("Level", compoundTag2);
		return compoundTag;
	}

	private boolean needsUpdate(int i, int j, String string) {
		return !this.needsUpdate
			? false
			: this.featureIdToChunkTag.get(string) != null && ((ChunkUpdateState)this.updateStates.get(OLD_TO_NEW.get(string))).contains(ChunkPos.toLong(i, j));
	}

	private boolean needsUpdate(int i, int j) {
		if (!this.needsUpdate) {
			return false;
		} else {
			for (String string : this.field_17659) {
				if (this.featureIdToChunkTag.get(string) != null && ((ChunkUpdateState)this.updateStates.get(OLD_TO_NEW.get(string))).isRemaining(ChunkPos.toLong(i, j))) {
					return true;
				}
			}

			return false;
		}
	}

	private CompoundTag getUpdatedStarts(CompoundTag compoundTag, ChunkPos chunkPos) {
		CompoundTag compoundTag2 = compoundTag.getCompound("Level");
		CompoundTag compoundTag3 = compoundTag2.getCompound("Structures");
		CompoundTag compoundTag4 = compoundTag3.getCompound("Starts");

		for (String string : this.field_17659) {
			Long2ObjectMap<CompoundTag> long2ObjectMap = (Long2ObjectMap<CompoundTag>)this.featureIdToChunkTag.get(string);
			if (long2ObjectMap != null) {
				long l = chunkPos.toLong();
				if (((ChunkUpdateState)this.updateStates.get(OLD_TO_NEW.get(string))).isRemaining(l)) {
					CompoundTag compoundTag5 = long2ObjectMap.get(l);
					if (compoundTag5 != null) {
						compoundTag4.put(string, compoundTag5);
					}
				}
			}
		}

		compoundTag3.put("Starts", compoundTag4);
		compoundTag2.put("Structures", compoundTag3);
		compoundTag.put("Level", compoundTag2);
		return compoundTag;
	}

	private void init(@Nullable PersistentStateManager persistentStateManager) {
		if (persistentStateManager != null) {
			for (String string : this.field_17658) {
				CompoundTag compoundTag = new CompoundTag();

				try {
					compoundTag = persistentStateManager.readTag(string, 1493).getCompound("data").getCompound("Features");
					if (compoundTag.isEmpty()) {
						continue;
					}
				} catch (IOException var13) {
				}

				for (String string2 : compoundTag.getKeys()) {
					CompoundTag compoundTag2 = compoundTag.getCompound(string2);
					long l = ChunkPos.toLong(compoundTag2.getInt("ChunkX"), compoundTag2.getInt("ChunkZ"));
					ListTag listTag = compoundTag2.getList("Children", 10);
					if (!listTag.isEmpty()) {
						String string3 = listTag.getCompoundTag(0).getString("id");
						String string4 = (String)ANCIENT_TO_OLD.get(string3);
						if (string4 != null) {
							compoundTag2.putString("id", string4);
						}
					}

					String string3 = compoundTag2.getString("id");
					((Long2ObjectMap)this.featureIdToChunkTag.computeIfAbsent(string3, stringx -> new Long2ObjectOpenHashMap())).put(l, compoundTag2);
				}

				String string5 = string + "_index";
				ChunkUpdateState chunkUpdateState = persistentStateManager.getOrCreate(() -> new ChunkUpdateState(string5), string5);
				if (!chunkUpdateState.getAll().isEmpty()) {
					this.updateStates.put(string, chunkUpdateState);
				} else {
					ChunkUpdateState chunkUpdateState2 = new ChunkUpdateState(string5);
					this.updateStates.put(string, chunkUpdateState2);

					for (String string6 : compoundTag.getKeys()) {
						CompoundTag compoundTag3 = compoundTag.getCompound(string6);
						chunkUpdateState2.add(ChunkPos.toLong(compoundTag3.getInt("ChunkX"), compoundTag3.getInt("ChunkZ")));
					}

					chunkUpdateState2.markDirty();
				}
			}
		}
	}

	public static FeatureUpdater create(DimensionType dimensionType, @Nullable PersistentStateManager persistentStateManager) {
		if (dimensionType == DimensionType.OVERWORLD) {
			return new FeatureUpdater(
				persistentStateManager,
				ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"),
				ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument")
			);
		} else if (dimensionType == DimensionType.THE_NETHER) {
			List<String> list = ImmutableList.of("Fortress");
			return new FeatureUpdater(persistentStateManager, list, list);
		} else if (dimensionType == DimensionType.THE_END) {
			List<String> list = ImmutableList.of("EndCity");
			return new FeatureUpdater(persistentStateManager, list, list);
		} else {
			throw new RuntimeException(String.format("Unknown dimension type : %s", dimensionType));
		}
	}
}
