package net.minecraft;

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

public abstract class class_3360 {
	private static final Logger field_14437 = LogManager.getLogger();
	private static final Map<String, String> field_14435 = SystemUtil.consume(Maps.<String, String>newHashMap(), hashMap -> {
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
	private static final Map<String, String> field_14436 = SystemUtil.consume(Maps.<String, String>newHashMap(), hashMap -> {
		hashMap.put("Iglu", "Igloo");
		hashMap.put("TeDP", "Desert_Pyramid");
		hashMap.put("TeJP", "Jungle_Pyramid");
		hashMap.put("TeSH", "Swamp_Hut");
	});
	private final boolean field_14434;
	private final Map<String, Long2ObjectMap<CompoundTag>> field_14432 = Maps.<String, Long2ObjectMap<CompoundTag>>newHashMap();
	private final Map<String, class_3440> field_14433 = Maps.<String, class_3440>newHashMap();

	public class_3360(@Nullable class_37 arg) {
		this.method_14734(arg);
		boolean bl = false;

		for (String string : this.method_14743()) {
			bl |= this.field_14432.get(string) != null;
		}

		this.field_14434 = bl;
	}

	public void method_14744(long l) {
		for (String string : this.method_14740()) {
			class_3440 lv = (class_3440)this.field_14433.get(string);
			if (lv != null && lv.method_14894(l)) {
				lv.method_14895(l);
				lv.markDirty();
			}
		}
	}

	public CompoundTag method_14735(CompoundTag compoundTag) {
		CompoundTag compoundTag2 = compoundTag.getCompound("Level");
		ChunkPos chunkPos = new ChunkPos(compoundTag2.getInt("xPos"), compoundTag2.getInt("zPos"));
		if (this.method_14737(chunkPos.x, chunkPos.z)) {
			compoundTag = this.method_14741(compoundTag, chunkPos);
		}

		CompoundTag compoundTag3 = compoundTag2.getCompound("Structures");
		CompoundTag compoundTag4 = compoundTag3.getCompound("References");

		for (String string : this.method_14743()) {
			StructureFeature<?> structureFeature = (StructureFeature<?>)Feature.STRUCTURES.get(string.toLowerCase(Locale.ROOT));
			if (!compoundTag4.containsKey(string, 12) && structureFeature != null) {
				int i = structureFeature.method_14021();
				LongList longList = new LongArrayList();

				for (int j = chunkPos.x - i; j <= chunkPos.x + i; j++) {
					for (int k = chunkPos.z - i; k <= chunkPos.z + i; k++) {
						if (this.method_14738(j, k, string)) {
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

	protected abstract String[] method_14740();

	protected abstract String[] method_14743();

	private boolean method_14738(int i, int j, String string) {
		return !this.field_14434
			? false
			: this.field_14432.get(string) != null && ((class_3440)this.field_14433.get(field_14435.get(string))).method_14897(ChunkPos.toLong(i, j));
	}

	private boolean method_14737(int i, int j) {
		if (!this.field_14434) {
			return false;
		} else {
			for (String string : this.method_14743()) {
				if (this.field_14432.get(string) != null && ((class_3440)this.field_14433.get(field_14435.get(string))).method_14894(ChunkPos.toLong(i, j))) {
					return true;
				}
			}

			return false;
		}
	}

	private CompoundTag method_14741(CompoundTag compoundTag, ChunkPos chunkPos) {
		CompoundTag compoundTag2 = compoundTag.getCompound("Level");
		CompoundTag compoundTag3 = compoundTag2.getCompound("Structures");
		CompoundTag compoundTag4 = compoundTag3.getCompound("Starts");

		for (String string : this.method_14743()) {
			Long2ObjectMap<CompoundTag> long2ObjectMap = (Long2ObjectMap<CompoundTag>)this.field_14432.get(string);
			if (long2ObjectMap != null) {
				long l = chunkPos.toLong();
				if (((class_3440)this.field_14433.get(field_14435.get(string))).method_14894(l)) {
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

	private void method_14734(@Nullable class_37 arg) {
		if (arg != null) {
			for (String string : this.method_14740()) {
				CompoundTag compoundTag = new CompoundTag();

				try {
					compoundTag = arg.method_264(string, 1493).getCompound("data").getCompound("Features");
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
						String string4 = (String)field_14436.get(string3);
						if (string4 != null) {
							compoundTag2.putString("id", string4);
						}
					}

					String string3 = compoundTag2.getString("id");
					((Long2ObjectMap)this.field_14432.computeIfAbsent(string3, stringx -> new Long2ObjectOpenHashMap())).put(l, compoundTag2);
				}

				String string5 = string + "_index";
				class_3440 lv = arg.method_268(DimensionType.field_13072, class_3440::new, string5);
				if (lv != null && !lv.method_14898().isEmpty()) {
					this.field_14433.put(string, lv);
				} else {
					class_3440 lv2 = new class_3440(string5);
					this.field_14433.put(string, lv2);

					for (String string6 : compoundTag.getKeys()) {
						CompoundTag compoundTag3 = compoundTag.getCompound(string6);
						lv2.method_14896(ChunkPos.toLong(compoundTag3.getInt("ChunkX"), compoundTag3.getInt("ChunkZ")));
					}

					arg.method_267(DimensionType.field_13072, string5, lv2);
					lv2.markDirty();
				}
			}
		}
	}

	public static class_3360 method_14745(DimensionType dimensionType, @Nullable class_37 arg) {
		if (dimensionType == DimensionType.field_13072) {
			return new class_3360.class_3362(arg);
		} else if (dimensionType == DimensionType.field_13076) {
			return new class_3360.class_3361(arg);
		} else if (dimensionType == DimensionType.field_13078) {
			return new class_3360.class_3363(arg);
		} else {
			throw new RuntimeException(String.format("Unknown dimension type : %s", dimensionType));
		}
	}

	public static class class_3361 extends class_3360 {
		private static final String[] field_14438 = new String[]{"Fortress"};

		public class_3361(@Nullable class_37 arg) {
			super(arg);
		}

		@Override
		protected String[] method_14740() {
			return field_14438;
		}

		@Override
		protected String[] method_14743() {
			return field_14438;
		}
	}

	public static class class_3362 extends class_3360 {
		private static final String[] field_14440 = new String[]{"Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"};
		private static final String[] field_14439 = new String[]{
			"Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"
		};

		public class_3362(@Nullable class_37 arg) {
			super(arg);
		}

		@Override
		protected String[] method_14740() {
			return field_14440;
		}

		@Override
		protected String[] method_14743() {
			return field_14439;
		}
	}

	public static class class_3363 extends class_3360 {
		private static final String[] field_14441 = new String[]{"EndCity"};

		public class_3363(@Nullable class_37 arg) {
			super(arg);
		}

		@Override
		protected String[] method_14740() {
			return field_14441;
		}

		@Override
		protected String[] method_14743() {
			return field_14441;
		}
	}
}
