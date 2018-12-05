package net.minecraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSource;

public class class_2864 {
	public static class_2864.class_2865 method_12433(CompoundTag compoundTag) {
		int i = compoundTag.getInt("xPos");
		int j = compoundTag.getInt("zPos");
		class_2864.class_2865 lv = new class_2864.class_2865(i, j);
		lv.field_13048 = compoundTag.getByteArray("Blocks");
		lv.field_13044 = new class_2832(compoundTag.getByteArray("Data"), 7);
		lv.field_13039 = new class_2832(compoundTag.getByteArray("SkyLight"), 7);
		lv.field_13038 = new class_2832(compoundTag.getByteArray("BlockLight"), 7);
		lv.field_13045 = compoundTag.getByteArray("HeightMap");
		lv.field_13042 = compoundTag.getBoolean("TerrainPopulated");
		lv.field_13037 = compoundTag.getList("Entities", 10);
		lv.field_13040 = compoundTag.getList("TileEntities", 10);
		lv.field_13041 = compoundTag.getList("TileTicks", 10);

		try {
			lv.field_13043 = compoundTag.getLong("LastUpdate");
		} catch (ClassCastException var5) {
			lv.field_13043 = (long)compoundTag.getInt("LastUpdate");
		}

		return lv;
	}

	public static void method_12432(class_2864.class_2865 arg, CompoundTag compoundTag, BiomeSource biomeSource) {
		compoundTag.putInt("xPos", arg.field_13047);
		compoundTag.putInt("zPos", arg.field_13046);
		compoundTag.putLong("LastUpdate", arg.field_13043);
		int[] is = new int[arg.field_13045.length];

		for (int i = 0; i < arg.field_13045.length; i++) {
			is[i] = arg.field_13045[i];
		}

		compoundTag.putIntArray("HeightMap", is);
		compoundTag.putBoolean("TerrainPopulated", arg.field_13042);
		ListTag listTag = new ListTag();

		for (int j = 0; j < 8; j++) {
			boolean bl = true;

			for (int k = 0; k < 16 && bl; k++) {
				for (int l = 0; l < 16 && bl; l++) {
					for (int m = 0; m < 16; m++) {
						int n = k << 11 | m << 7 | l + (j << 4);
						int o = arg.field_13048[n];
						if (o != 0) {
							bl = false;
							break;
						}
					}
				}
			}

			if (!bl) {
				byte[] bs = new byte[4096];
				class_2804 lv = new class_2804();
				class_2804 lv2 = new class_2804();
				class_2804 lv3 = new class_2804();

				for (int o = 0; o < 16; o++) {
					for (int p = 0; p < 16; p++) {
						for (int q = 0; q < 16; q++) {
							int r = o << 11 | q << 7 | p + (j << 4);
							int s = arg.field_13048[r];
							bs[p << 8 | q << 4 | o] = (byte)(s & 0xFF);
							lv.method_12145(o, p, q, arg.field_13044.method_12275(o, p + (j << 4), q));
							lv2.method_12145(o, p, q, arg.field_13039.method_12275(o, p + (j << 4), q));
							lv3.method_12145(o, p, q, arg.field_13038.method_12275(o, p + (j << 4), q));
						}
					}
				}

				CompoundTag compoundTag2 = new CompoundTag();
				compoundTag2.putByte("Y", (byte)(j & 0xFF));
				compoundTag2.putByteArray("Blocks", bs);
				compoundTag2.putByteArray("Data", lv.method_12137());
				compoundTag2.putByteArray("SkyLight", lv2.method_12137());
				compoundTag2.putByteArray("BlockLight", lv3.method_12137());
				listTag.add((Tag)compoundTag2);
			}
		}

		compoundTag.put("Sections", listTag);
		byte[] cs = new byte[256];
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				mutable.set(arg.field_13047 << 4 | k, 0, arg.field_13046 << 4 | l);
				cs[l << 4 | k] = (byte)(Registry.BIOME.getRawId(biomeSource.method_8758(mutable)) & 0xFF);
			}
		}

		compoundTag.putByteArray("Biomes", cs);
		compoundTag.put("Entities", arg.field_13037);
		compoundTag.put("TileEntities", arg.field_13040);
		if (arg.field_13041 != null) {
			compoundTag.put("TileTicks", arg.field_13041);
		}

		compoundTag.putBoolean("convertedFromAlphaFormat", true);
	}

	public static class class_2865 {
		public long field_13043;
		public boolean field_13042;
		public byte[] field_13045;
		public class_2832 field_13038;
		public class_2832 field_13039;
		public class_2832 field_13044;
		public byte[] field_13048;
		public ListTag field_13037;
		public ListTag field_13040;
		public ListTag field_13041;
		public final int field_13047;
		public final int field_13046;

		public class_2865(int i, int j) {
			this.field_13047 = i;
			this.field_13046 = j;
		}
	}
}
