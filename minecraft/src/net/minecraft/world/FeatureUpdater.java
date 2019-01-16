package net.minecraft.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class FeatureUpdater {
	private static final Logger LOGGER = LogManager.getLogger();
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

	public FeatureUpdater(@Nullable PersistentStateManager persistentStateManager) {
		this.init(persistentStateManager);
		boolean bl = false;

		for (String string : this.getOldNames()) {
			bl |= this.featureIdToChunkTag.get(string) != null;
		}

		this.needsUpdate = bl;
	}

	public void markResolved(long l) {
		for (String string : this.getNewNames()) {
			ChunkUpdateState chunkUpdateState = (ChunkUpdateState)this.updateStates.get(string);
			if (chunkUpdateState != null && chunkUpdateState.isRemaing(l)) {
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

		for (String string : this.getOldNames()) {
			StructureFeature<?> structureFeature = (StructureFeature<?>)Feature.STRUCTURES.get(string.toLowerCase(Locale.ROOT));
			if (!compoundTag4.containsKey(string, 12) && structureFeature != null) {
				int i = structureFeature.method_14021();
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

	protected abstract String[] getNewNames();

	protected abstract String[] getOldNames();

	private boolean needsUpdate(int i, int j, String string) {
		return !this.needsUpdate
			? false
			: this.featureIdToChunkTag.get(string) != null && ((ChunkUpdateState)this.updateStates.get(OLD_TO_NEW.get(string))).contains(ChunkPos.toLong(i, j));
	}

	private boolean needsUpdate(int i, int j) {
		if (!this.needsUpdate) {
			return false;
		} else {
			for (String string : this.getOldNames()) {
				if (this.featureIdToChunkTag.get(string) != null && ((ChunkUpdateState)this.updateStates.get(OLD_TO_NEW.get(string))).isRemaing(ChunkPos.toLong(i, j))) {
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

		for (String string : this.getOldNames()) {
			Long2ObjectMap<CompoundTag> long2ObjectMap = (Long2ObjectMap<CompoundTag>)this.featureIdToChunkTag.get(string);
			if (long2ObjectMap != null) {
				long l = chunkPos.toLong();
				if (((ChunkUpdateState)this.updateStates.get(OLD_TO_NEW.get(string))).isRemaing(l)) {
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
			for (String string : this.getNewNames()) {
				CompoundTag compoundTag = new CompoundTag();

				try {
					compoundTag = persistentStateManager.update(string, 1493).getCompound("data").getCompound("Features");
					if (compoundTag.isEmpty()) {
						continue;
					}
				} catch (IOException var15) {
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
				ChunkUpdateState chunkUpdateState = persistentStateManager.get(DimensionType.field_13072, ChunkUpdateState::new, string5);
				if (chunkUpdateState != null && !chunkUpdateState.getAll().isEmpty()) {
					this.updateStates.put(string, chunkUpdateState);
				} else {
					ChunkUpdateState chunkUpdateState2 = new ChunkUpdateState(string5);
					this.updateStates.put(string, chunkUpdateState2);

					for (String string6 : compoundTag.getKeys()) {
						CompoundTag compoundTag3 = compoundTag.getCompound(string6);
						chunkUpdateState2.add(ChunkPos.toLong(compoundTag3.getInt("ChunkX"), compoundTag3.getInt("ChunkZ")));
					}

					persistentStateManager.set(DimensionType.field_13072, string5, chunkUpdateState2);
					chunkUpdateState2.markDirty();
				}
			}
		}
	}

	public static FeatureUpdater create(DimensionType dimensionType, @Nullable PersistentStateManager persistentStateManager) {
		if (dimensionType == DimensionType.field_13072) {
			return new FeatureUpdater.Overworld(persistentStateManager);
		} else if (dimensionType == DimensionType.field_13076) {
			return new FeatureUpdater.TheNether(persistentStateManager);
		} else if (dimensionType == DimensionType.field_13078) {
			return new FeatureUpdater.TheEnd(persistentStateManager);
		} else {
			throw new RuntimeException(String.format("Unknown dimension type : %s", dimensionType));
		}
	}

	public static class Overworld extends FeatureUpdater {
		private static final String[] NEW_NAMES = new String[]{"Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"};
		private static final String[] OLD_NAMES = new String[]{
			"Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"
		};

		public Overworld(@Nullable PersistentStateManager persistentStateManager) {
			super(persistentStateManager);
		}

		@Override
		protected String[] getNewNames() {
			return NEW_NAMES;
		}

		@Override
		protected String[] getOldNames() {
			return OLD_NAMES;
		}
	}

	public static class TheEnd extends FeatureUpdater {
		private static final String[] NAMES = new String[]{"EndCity"};

		public TheEnd(@Nullable PersistentStateManager persistentStateManager) {
			super(persistentStateManager);
		}

		@Override
		protected String[] getNewNames() {
			return NAMES;
		}

		@Override
		protected String[] getOldNames() {
			return NAMES;
		}
	}

	public static class TheNether extends FeatureUpdater {
		private static final String[] NAMES = new String[]{"Fortress"};

		public TheNether(@Nullable PersistentStateManager persistentStateManager) {
			super(persistentStateManager);
		}

		@Override
		protected String[] getNewNames() {
			return NAMES;
		}

		@Override
		protected String[] getOldNames() {
			return NAMES;
		}
	}
}
