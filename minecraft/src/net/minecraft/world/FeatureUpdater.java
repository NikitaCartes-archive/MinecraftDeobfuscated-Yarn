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
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;

public class FeatureUpdater {
	private static final Map<String, String> OLD_TO_NEW = Util.make(Maps.<String, String>newHashMap(), map -> {
		map.put("Village", "Village");
		map.put("Mineshaft", "Mineshaft");
		map.put("Mansion", "Mansion");
		map.put("Igloo", "Temple");
		map.put("Desert_Pyramid", "Temple");
		map.put("Jungle_Pyramid", "Temple");
		map.put("Swamp_Hut", "Temple");
		map.put("Stronghold", "Stronghold");
		map.put("Monument", "Monument");
		map.put("Fortress", "Fortress");
		map.put("EndCity", "EndCity");
	});
	private static final Map<String, String> ANCIENT_TO_OLD = Util.make(Maps.<String, String>newHashMap(), map -> {
		map.put("Iglu", "Igloo");
		map.put("TeDP", "Desert_Pyramid");
		map.put("TeJP", "Jungle_Pyramid");
		map.put("TeSH", "Swamp_Hut");
	});
	private static final Set<String> field_37194 = Set.of(
		"pillager_outpost",
		"mineshaft",
		"mansion",
		"jungle_pyramid",
		"desert_pyramid",
		"igloo",
		"ruined_portal",
		"shipwreck",
		"swamp_hut",
		"stronghold",
		"monument",
		"ocean_ruin",
		"fortress",
		"endcity",
		"buried_treasure",
		"village",
		"nether_fossil",
		"bastion_remnant"
	);
	private final boolean needsUpdate;
	private final Map<String, Long2ObjectMap<NbtCompound>> featureIdToChunkNbt = Maps.<String, Long2ObjectMap<NbtCompound>>newHashMap();
	private final Map<String, ChunkUpdateState> updateStates = Maps.<String, ChunkUpdateState>newHashMap();
	private final List<String> field_17658;
	private final List<String> field_17659;

	public FeatureUpdater(@Nullable PersistentStateManager persistentStateManager, List<String> list, List<String> list2) {
		this.field_17658 = list;
		this.field_17659 = list2;
		this.init(persistentStateManager);
		boolean bl = false;

		for (String string : this.field_17659) {
			bl |= this.featureIdToChunkNbt.get(string) != null;
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

	public NbtCompound getUpdatedReferences(NbtCompound nbt) {
		NbtCompound nbtCompound = nbt.getCompound("Level");
		ChunkPos chunkPos = new ChunkPos(nbtCompound.getInt("xPos"), nbtCompound.getInt("zPos"));
		if (this.needsUpdate(chunkPos.x, chunkPos.z)) {
			nbt = this.getUpdatedStarts(nbt, chunkPos);
		}

		NbtCompound nbtCompound2 = nbtCompound.getCompound("Structures");
		NbtCompound nbtCompound3 = nbtCompound2.getCompound("References");

		for (String string : this.field_17659) {
			boolean bl = field_37194.contains(string.toLowerCase(Locale.ROOT));
			if (!nbtCompound3.contains(string, NbtElement.LONG_ARRAY_TYPE) && bl) {
				int i = 8;
				LongList longList = new LongArrayList();

				for (int j = chunkPos.x - 8; j <= chunkPos.x + 8; j++) {
					for (int k = chunkPos.z - 8; k <= chunkPos.z + 8; k++) {
						if (this.needsUpdate(j, k, string)) {
							longList.add(ChunkPos.toLong(j, k));
						}
					}
				}

				nbtCompound3.putLongArray(string, longList);
			}
		}

		nbtCompound2.put("References", nbtCompound3);
		nbtCompound.put("Structures", nbtCompound2);
		nbt.put("Level", nbtCompound);
		return nbt;
	}

	private boolean needsUpdate(int chunkX, int chunkZ, String id) {
		return !this.needsUpdate
			? false
			: this.featureIdToChunkNbt.get(id) != null && ((ChunkUpdateState)this.updateStates.get(OLD_TO_NEW.get(id))).contains(ChunkPos.toLong(chunkX, chunkZ));
	}

	private boolean needsUpdate(int chunkX, int chunkZ) {
		if (!this.needsUpdate) {
			return false;
		} else {
			for (String string : this.field_17659) {
				if (this.featureIdToChunkNbt.get(string) != null
					&& ((ChunkUpdateState)this.updateStates.get(OLD_TO_NEW.get(string))).isRemaining(ChunkPos.toLong(chunkX, chunkZ))) {
					return true;
				}
			}

			return false;
		}
	}

	private NbtCompound getUpdatedStarts(NbtCompound nbt, ChunkPos pos) {
		NbtCompound nbtCompound = nbt.getCompound("Level");
		NbtCompound nbtCompound2 = nbtCompound.getCompound("Structures");
		NbtCompound nbtCompound3 = nbtCompound2.getCompound("Starts");

		for (String string : this.field_17659) {
			Long2ObjectMap<NbtCompound> long2ObjectMap = (Long2ObjectMap<NbtCompound>)this.featureIdToChunkNbt.get(string);
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
		nbt.put("Level", nbtCompound);
		return nbt;
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
					NbtList nbtList = nbtCompound2.getList("Children", NbtElement.COMPOUND_TYPE);
					if (!nbtList.isEmpty()) {
						String string3 = nbtList.getCompound(0).getString("id");
						String string4 = (String)ANCIENT_TO_OLD.get(string3);
						if (string4 != null) {
							nbtCompound2.putString("id", string4);
						}
					}

					String string3 = nbtCompound2.getString("id");
					((Long2ObjectMap)this.featureIdToChunkNbt.computeIfAbsent(string3, stringx -> new Long2ObjectOpenHashMap())).put(l, nbtCompound2);
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

	public static FeatureUpdater create(RegistryKey<World> world, @Nullable PersistentStateManager persistentStateManager) {
		if (world == World.OVERWORLD) {
			return new FeatureUpdater(
				persistentStateManager,
				ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"),
				ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument")
			);
		} else if (world == World.NETHER) {
			List<String> list = ImmutableList.of("Fortress");
			return new FeatureUpdater(persistentStateManager, list, list);
		} else if (world == World.END) {
			List<String> list = ImmutableList.of("EndCity");
			return new FeatureUpdater(persistentStateManager, list, list);
		} else {
			throw new RuntimeException(String.format(Locale.ROOT, "Unknown dimension type : %s", world));
		}
	}
}
