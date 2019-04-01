package net.minecraft;

public class class_2864 {
	public static class_2864.class_2865 method_12433(class_2487 arg) {
		int i = arg.method_10550("xPos");
		int j = arg.method_10550("zPos");
		class_2864.class_2865 lv = new class_2864.class_2865(i, j);
		lv.field_13048 = arg.method_10547("Blocks");
		lv.field_13044 = new class_2832(arg.method_10547("Data"), 7);
		lv.field_13039 = new class_2832(arg.method_10547("SkyLight"), 7);
		lv.field_13038 = new class_2832(arg.method_10547("BlockLight"), 7);
		lv.field_13045 = arg.method_10547("HeightMap");
		lv.field_13042 = arg.method_10577("TerrainPopulated");
		lv.field_13037 = arg.method_10554("Entities", 10);
		lv.field_13040 = arg.method_10554("TileEntities", 10);
		lv.field_13041 = arg.method_10554("TileTicks", 10);

		try {
			lv.field_13043 = arg.method_10537("LastUpdate");
		} catch (ClassCastException var5) {
			lv.field_13043 = (long)arg.method_10550("LastUpdate");
		}

		return lv;
	}

	public static void method_12432(class_2864.class_2865 arg, class_2487 arg2, class_1966 arg3) {
		arg2.method_10569("xPos", arg.field_13047);
		arg2.method_10569("zPos", arg.field_13046);
		arg2.method_10544("LastUpdate", arg.field_13043);
		int[] is = new int[arg.field_13045.length];

		for (int i = 0; i < arg.field_13045.length; i++) {
			is[i] = arg.field_13045[i];
		}

		arg2.method_10539("HeightMap", is);
		arg2.method_10556("TerrainPopulated", arg.field_13042);
		class_2499 lv = new class_2499();

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
				class_2804 lv2 = new class_2804();
				class_2804 lv3 = new class_2804();
				class_2804 lv4 = new class_2804();

				for (int o = 0; o < 16; o++) {
					for (int p = 0; p < 16; p++) {
						for (int q = 0; q < 16; q++) {
							int r = o << 11 | q << 7 | p + (j << 4);
							int s = arg.field_13048[r];
							bs[p << 8 | q << 4 | o] = (byte)(s & 0xFF);
							lv2.method_12145(o, p, q, arg.field_13044.method_12275(o, p + (j << 4), q));
							lv3.method_12145(o, p, q, arg.field_13039.method_12275(o, p + (j << 4), q));
							lv4.method_12145(o, p, q, arg.field_13038.method_12275(o, p + (j << 4), q));
						}
					}
				}

				class_2487 lv5 = new class_2487();
				lv5.method_10567("Y", (byte)(j & 0xFF));
				lv5.method_10570("Blocks", bs);
				lv5.method_10570("Data", lv2.method_12137());
				lv5.method_10570("SkyLight", lv3.method_12137());
				lv5.method_10570("BlockLight", lv4.method_12137());
				lv.add(lv5);
			}
		}

		arg2.method_10566("Sections", lv);
		byte[] cs = new byte[256];
		class_2338.class_2339 lv6 = new class_2338.class_2339();

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				lv6.method_10103(arg.field_13047 << 4 | k, 0, arg.field_13046 << 4 | l);
				cs[l << 4 | k] = (byte)(class_2378.field_11153.method_10249(arg3.method_8758(lv6)) & 0xFF);
			}
		}

		arg2.method_10570("Biomes", cs);
		arg2.method_10566("Entities", arg.field_13037);
		arg2.method_10566("TileEntities", arg.field_13040);
		if (arg.field_13041 != null) {
			arg2.method_10566("TileTicks", arg.field_13041);
		}

		arg2.method_10556("convertedFromAlphaFormat", true);
	}

	public static class class_2865 {
		public long field_13043;
		public boolean field_13042;
		public byte[] field_13045;
		public class_2832 field_13038;
		public class_2832 field_13039;
		public class_2832 field_13044;
		public byte[] field_13048;
		public class_2499 field_13037;
		public class_2499 field_13040;
		public class_2499 field_13041;
		public final int field_13047;
		public final int field_13046;

		public class_2865(int i, int j) {
			this.field_13047 = i;
			this.field_13046 = j;
		}
	}
}
