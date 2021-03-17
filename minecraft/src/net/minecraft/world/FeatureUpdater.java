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
import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.StructureFeature;

public class FeatureUpdater {
	private static final Map<String, String> OLD_TO_NEW = Util.make(Maps.<String, String>newHashMap(), hashMap -> {
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
	private static final Map<String, String> ANCIENT_TO_OLD = Util.make(Maps.<String, String>newHashMap(), hashMap -> {
		hashMap.put("Iglu", "Igloo");
		hashMap.put("TeDP", "Desert_Pyramid");
		hashMap.put("TeJP", "Jungle_Pyramid");
		hashMap.put("TeSH", "Swamp_Hut");
	});
	private final boolean needsUpdate;
	private final Map<String, Long2ObjectMap<NbtCompound>> featureIdToChunkTag = Maps.<String, Long2ObjectMap<NbtCompound>>newHashMap();
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

	public NbtCompound getUpdatedReferences(NbtCompound nbtCompound) {
		NbtCompound nbtCompound2 = nbtCompound.getCompound("Level");
		ChunkPos chunkPos = new ChunkPos(nbtCompound2.getInt("xPos"), nbtCompound2.getInt("zPos"));
		if (this.needsUpdate(chunkPos.x, chunkPos.z)) {
			nbtCompound = this.getUpdatedStarts(nbtCompound, chunkPos);
		}

		NbtCompound nbtCompound3 = nbtCompound2.getCompound("Structures");
		NbtCompound nbtCompound4 = nbtCompound3.getCompound("References");

		for (String string : this.field_17659) {
			StructureFeature<?> structureFeature = (StructureFeature<?>)StructureFeature.STRUCTURES.get(string.toLowerCase(Locale.ROOT));
			if (!nbtCompound4.contains(string, NbtTypeIds.LONG_ARRAY) && structureFeature != null) {
				int i = 8;
				LongList longList = new LongArrayList();

				for (int j = chunkPos.x - 8; j <= chunkPos.x + 8; j++) {
					for (int k = chunkPos.z - 8; k <= chunkPos.z + 8; k++) {
						if (this.needsUpdate(j, k, string)) {
							longList.add(ChunkPos.toLong(j, k));
						}
					}
				}

				nbtCompound4.putLongArray(string, longList);
			}
		}

		nbtCompound3.put("References", nbtCompound4);
		nbtCompound2.put("Structures", nbtCompound3);
		nbtCompound.put("Level", nbtCompound2);
		return nbtCompound;
	}

	private boolean needsUpdate(int chunkX, int chunkZ, String id) {
		return !this.needsUpdate
			? false
			: this.featureIdToChunkTag.get(id) != null && ((ChunkUpdateState)this.updateStates.get(OLD_TO_NEW.get(id))).contains(ChunkPos.toLong(chunkX, chunkZ));
	}

	private boolean needsUpdate(int chunkX, int chunkZ) {
		if (!this.needsUpdate) {
			return false;
		} else {
			for (String string : this.field_17659) {
				if (this.featureIdToChunkTag.get(string) != null
					&& ((ChunkUpdateState)this.updateStates.get(OLD_TO_NEW.get(string))).isRemaining(ChunkPos.toLong(chunkX, chunkZ))) {
					return true;
				}
			}

			return false;
		}
	}

	private NbtCompound getUpdatedStarts(NbtCompound tag, ChunkPos pos) {
		NbtCompound nbtCompound = tag.getCompound("Level");
		NbtCompound nbtCompound2 = nbtCompound.getCompound("Structures");
		NbtCompound nbtCompound3 = nbtCompound2.getCompound("Starts");

		for (String string : this.field_17659) {
			Long2ObjectMap<NbtCompound> long2ObjectMap = (Long2ObjectMap<NbtCompound>)this.featureIdToChunkTag.get(string);
			if (long2ObjectMap != null) {
				long l = pos.toLong();
				if (((ChunkUpdateState)this.updateStates.get(OLD_TO_NEW.get(string))).isRemaining(l)) {
					NbtCompound nbtCompound4 = long2ObjectMap.get(l);
					if (nbtCompound4 != null) {
						nbtCompound3.put(string, nbtCompound4);
					}
				}
			}
		}

		nbtCompound2.put("Starts", nbtCompound3);
		nbtCompound.put("Structures", nbtCompound2);
		tag.put("Level", nbtCompound);
		return tag;
	}

	private void init(@Nullable PersistentStateManager persistentStateManager) {
		if (persistentStateManager != null) {
			for (String string : this.field_17658) {
				NbtCompound nbtCompound = new NbtCompound();

				try {
					nbtCompound = persistentStateManager.readNbt(string, 1493).getCompound("data").getCompound("Features");
					if (nbtCompound.isEmpty()) {
						continue;
					}
				} catch (IOException var13) {
				}

				for (String string2 : nbtCompound.getKeys()) {
					NbtCompound nbtCompound2 = nbtCompound.getCompound(string2);
					long l = ChunkPos.toLong(nbtCompound2.getInt("ChunkX"), nbtCompound2.getInt("ChunkZ"));
					NbtList nbtList = nbtCompound2.getList("Children", NbtTypeIds.COMPOUND);
					if (!nbtList.isEmpty()) {
						String string3 = nbtList.getCompound(0).getString("id");
						String string4 = (String)ANCIENT_TO_OLD.get(string3);
						if (string4 != null) {
							nbtCompound2.putString("id", string4);
						}
					}

					String string3 = nbtCompound2.getString("id");
					((Long2ObjectMap)this.featureIdToChunkTag.computeIfAbsent(string3, stringx -> new Long2ObjectOpenHashMap())).put(l, nbtCompound2);
				}

				String string5 = string + "_index";
				ChunkUpdateState chunkUpdateState = persistentStateManager.getOrCreate(ChunkUpdateState::fromNbt, ChunkUpdateState::new, string5);
				if (!chunkUpdateState.getAll().isEmpty()) {
					this.updateStates.put(string, chunkUpdateState);
				} else {
					ChunkUpdateState chunkUpdateState2 = new ChunkUpdateState();
					this.updateStates.put(string, chunkUpdateState2);

					for (String string6 : nbtCompound.getKeys()) {
						NbtCompound nbtCompound3 = nbtCompound.getCompound(string6);
						chunkUpdateState2.add(ChunkPos.toLong(nbtCompound3.getInt("ChunkX"), nbtCompound3.getInt("ChunkZ")));
					}

					chunkUpdateState2.markDirty();
				}
			}
		}
	}

	public static FeatureUpdater create(RegistryKey<World> registryKey, @Nullable PersistentStateManager persistentStateManager) {
		if (registryKey == World.OVERWORLD) {
			return new FeatureUpdater(
				persistentStateManager,
				ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"),
				ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument")
			);
		} else if (registryKey == World.NETHER) {
			List<String> list = ImmutableList.of("Fortress");
			return new FeatureUpdater(persistentStateManager, list, list);
		} else if (registryKey == World.END) {
			List<String> list = ImmutableList.of("EndCity");
			return new FeatureUpdater(persistentStateManager, list, list);
		} else {
			throw new RuntimeException(String.format("Unknown dimension type : %s", registryKey));
		}
	}
}
