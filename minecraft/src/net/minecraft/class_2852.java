package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2852 implements class_2858 {
	private static final Logger field_13001 = LogManager.getLogger();
	private final Map<class_1923, class_2487> field_13000 = Maps.<class_1923, class_2487>newHashMap();
	private final File field_12999;
	private final DataFixer field_13002;
	private class_3360 field_13003;
	private boolean field_12998;

	public class_2852(File file, DataFixer dataFixer) {
		this.field_12999 = file;
		this.field_13002 = dataFixer;
	}

	@Nullable
	private class_2487 method_12389(class_1936 arg, int i, int j) throws IOException {
		return this.method_12398(arg.method_8597().method_12460(), arg.method_8646(), i, j);
	}

	@Nullable
	private class_2487 method_12398(class_2874 arg, @Nullable class_37 arg2, int i, int j) throws IOException {
		class_2487 lv = (class_2487)this.field_13000.get(new class_1923(i, j));
		if (lv != null) {
			return lv;
		} else {
			DataInputStream dataInputStream = class_2867.method_12439(this.field_12999, i, j);
			if (dataInputStream == null) {
				return null;
			} else {
				class_2487 lv2 = class_2507.method_10627(dataInputStream);
				dataInputStream.close();
				int k = lv2.method_10573("DataVersion", 99) ? lv2.method_10550("DataVersion") : -1;
				if (k < 1493) {
					lv2 = class_2512.method_10693(this.field_13002, DataFixTypes.CHUNK, lv2, k, 1493);
					if (lv2.method_10562("Level").method_10577("hasLegacyStructureData")) {
						this.method_12380(arg, arg2);
						lv2 = this.field_13003.method_14735(lv2);
					}
				}

				lv2 = class_2512.method_10688(this.field_13002, DataFixTypes.CHUNK, lv2, Math.max(1493, k));
				if (k < class_155.method_16673().getWorldVersion()) {
					lv2.method_10569("DataVersion", class_155.method_16673().getWorldVersion());
					this.method_12390(new class_1923(i, j), lv2);
				}

				return lv2;
			}
		}
	}

	public void method_12380(class_2874 arg, @Nullable class_37 arg2) {
		if (this.field_13003 == null) {
			this.field_13003 = class_3360.method_14745(arg, arg2);
		}
	}

	@Nullable
	@Override
	public class_2839 method_12411(class_1936 arg, int i, int j) {
		try {
			class_3485 lv = arg.method_8411().method_134();
			class_2794<?> lv2 = arg.method_8398().method_12129();
			class_1966 lv3 = lv2.method_12098();
			class_2487 lv4 = this.method_12389(arg, i, j);
			if (lv4 == null) {
				return null;
			} else {
				class_2806.class_2808 lv5 = this.method_12377(lv4);
				boolean bl = lv4.method_10573("Level", 10) && lv4.method_10562("Level").method_10573("Status", 8);
				if (!bl) {
					field_13001.error("Chunk file at {},{} is missing level data, skipping", i, j);
					return null;
				} else {
					class_2487 lv6 = lv4.method_10562("Level");
					int k = lv6.method_10550("xPos");
					int l = lv6.method_10550("zPos");
					if (k != i || l != j) {
						field_13001.error("Chunk file at {},{} is in the wrong location; relocating. (Expected {}, {}, got {}, {})", i, j, i, j, k, l);
					}

					class_1959[] lvs = new class_1959[256];
					class_2338.class_2339 lv7 = new class_2338.class_2339();
					if (lv6.method_10573("Biomes", 11)) {
						int[] is = lv6.method_10561("Biomes");

						for (int m = 0; m < is.length; m++) {
							lvs[m] = class_2378.field_11153.method_10200(is[m]);
							if (lvs[m] == null) {
								lvs[m] = lv3.method_8758(lv7.method_10103((m & 15) + (i << 4), 0, (m >> 4 & 15) + (j << 4)));
							}
						}
					} else {
						for (int n = 0; n < lvs.length; n++) {
							lvs[n] = lv3.method_8758(lv7.method_10103((n & 15) + (i << 4), 0, (n >> 4 & 15) + (j << 4)));
						}
					}

					class_2843 lv8 = lv6.method_10573("UpgradeData", 10) ? new class_2843(lv6.method_10562("UpgradeData")) : class_2843.field_12950;
					class_1923 lv9 = new class_1923(i, j);
					class_2850<class_2248> lv10 = new class_2850<>(
						argx -> argx == null || argx.method_9564().method_11588(),
						class_2378.field_11146::method_10221,
						class_2378.field_11146::method_10223,
						lv9,
						lv6.method_10554("ToBeTicked", 9)
					);
					class_2850<class_3611> lv11 = new class_2850<>(
						argx -> argx == null || argx == class_3612.field_15906,
						class_2378.field_11154::method_10221,
						class_2378.field_11154::method_10223,
						lv9,
						lv6.method_10554("LiquidsToBeTicked", 9)
					);
					boolean bl2 = lv6.method_10577("isLightOn");
					class_2499 lv12 = lv6.method_10554("Sections", 10);
					class_2826[] lvs2 = method_12384(arg, i, j, lv12, bl2);
					long o = lv6.method_10537("InhabitedTime");
					class_2791 lv13;
					if (lv5 == class_2806.class_2808.field_12807) {
						lv13 = new class_2818(arg.method_8410(), i, j, lvs, lv8, lv10, lv11, o, lvs2, arg2 -> method_12386(lv6, arg2));
					} else {
						class_2839 lv14 = new class_2839(i, j, lv8, lvs2, lv10, lv11);
						lv13 = lv14;
						lv14.method_12022(lvs);
						lv14.method_12028(o);
						lv14.method_12308(class_2806.method_12168(lv6.method_10558("Status")));
						if (!bl2 && lv14.method_12009().method_12165(class_2806.field_12805)) {
							for (class_2338 lv15 : class_2338.class_2339.method_10094(i << 4, 0, j << 4, (i + 1 << 4) - 1, 255, (j + 1 << 4) - 1)) {
								if (lv13.method_8320(lv15).method_11630() != 0) {
									lv14.method_12315(lv15);
								}
							}
						}
					}

					lv13.method_12020(bl2);
					class_2487 lv16 = lv6.method_10562("Heightmaps");
					EnumSet<class_2902.class_2903> enumSet = EnumSet.noneOf(class_2902.class_2903.class);

					for (class_2902.class_2903 lv17 : class_2902.class_2903.values()) {
						String string = lv17.method_12605();
						if (lv16.method_10573(string, 12)) {
							if (lv5 == class_2806.class_2808.field_12808 || lv17.method_16136()) {
								lv13.method_12037(lv17, lv16.method_10565(string));
							}
						} else if (lv5 == class_2806.class_2808.field_12807 && lv17.method_16136()) {
							enumSet.add(lv17);
						}
					}

					class_2902.method_16684(lv13, enumSet);
					class_2487 lv18 = lv6.method_10562("Structures");
					lv13.method_12034(this.method_12392(lv2, lv, lv3, lv18));
					lv13.method_12183(this.method_12387(lv18));
					if (lv6.method_10577("shouldSave")) {
						lv13.method_12008(true);
					}

					class_2499 lv19 = lv6.method_10554("PostProcessing", 9);

					for (int p = 0; p < lv19.size(); p++) {
						class_2499 lv20 = lv19.method_10603(p);

						for (int q = 0; q < lv20.size(); q++) {
							lv13.method_12029(lv20.method_10609(q), p);
						}
					}

					if (lv5 == class_2806.class_2808.field_12807) {
						return new class_2821((class_2818)lv13);
					} else {
						class_2839 lv21 = (class_2839)lv13;
						class_2499 lv20 = lv6.method_10554("Entities", 10);

						for (int q = 0; q < lv20.size(); q++) {
							lv21.method_12302(lv20.method_10602(q));
						}

						class_2499 lv22 = lv6.method_10554("TileEntities", 10);

						for (int r = 0; r < lv22.size(); r++) {
							class_2487 lv23 = lv22.method_10602(r);
							lv13.method_12042(lv23);
						}

						class_2499 lv24 = lv6.method_10554("Lights", 9);

						for (int s = 0; s < lv24.size(); s++) {
							class_2499 lv25 = lv24.method_10603(s);

							for (int t = 0; t < lv25.size(); t++) {
								lv21.method_12304(lv25.method_10609(t), s);
							}
						}

						class_2487 lv23 = lv6.method_10562("CarvingMasks");

						for (String string2 : lv23.method_10541()) {
							class_2893.class_2894 lv26 = class_2893.class_2894.valueOf(string2);
							lv21.method_12307(lv26, BitSet.valueOf(lv23.method_10547(string2)));
						}

						return lv21;
					}
				}
			}
		} catch (class_148 var37) {
			throw var37;
		} catch (Exception var38) {
			field_13001.error("Couldn't load chunk", (Throwable)var38);
			return null;
		}
	}

	@Override
	public void method_12410(class_1937 arg, class_2791 arg2) throws IOException, class_1939 {
		arg.method_8468();

		try {
			class_2487 lv = new class_2487();
			class_2487 lv2 = new class_2487();
			lv.method_10569("DataVersion", class_155.method_16673().getWorldVersion());
			class_1923 lv3 = arg2.method_12004();
			lv.method_10566("Level", lv2);
			if (arg2.method_12009().method_12164() != class_2806.class_2808.field_12807) {
				class_2487 lv4 = this.method_12389(arg, lv3.field_9181, lv3.field_9180);
				if (lv4 != null && this.method_12377(lv4) == class_2806.class_2808.field_12807) {
					return;
				}

				if (arg2.method_12009() == class_2806.field_12798 && arg2.method_12016().values().stream().noneMatch(class_3449::method_16657)) {
					return;
				}
			}

			int i = lv3.field_9181;
			int j = lv3.field_9180;
			lv2.method_10569("xPos", i);
			lv2.method_10569("zPos", j);
			lv2.method_10544("LastUpdate", arg.method_8510());
			lv2.method_10544("InhabitedTime", arg2.method_12033());
			lv2.method_10582("Status", arg2.method_12009().method_12172());
			class_2843 lv5 = arg2.method_12003();
			if (!lv5.method_12349()) {
				lv2.method_10566("UpgradeData", lv5.method_12350());
			}

			class_2826[] lvs = arg2.method_12006();
			class_2499 lv6 = this.method_12395(arg, i, j, lvs);
			lv2.method_10566("Sections", lv6);
			if (arg2.method_12038()) {
				lv2.method_10556("isLightOn", true);
			}

			class_1959[] lvs2 = arg2.method_12036();
			int[] is = lvs2 != null ? new int[lvs2.length] : new int[0];
			if (lvs2 != null) {
				for (int k = 0; k < lvs2.length; k++) {
					is[k] = class_2378.field_11153.method_10249(lvs2[k]);
				}
			}

			lv2.method_10539("Biomes", is);
			class_2499 lv7 = new class_2499();

			for (class_2338 lv8 : arg2.method_12021()) {
				class_2586 lv9 = arg2.method_8321(lv8);
				if (lv9 != null) {
					class_2487 lv10 = new class_2487();
					lv9.method_11007(lv10);
					if (arg2.method_12009().method_12164() == class_2806.class_2808.field_12807) {
						lv10.method_10556("keepPacked", false);
					}

					lv7.method_10606(lv10);
				} else {
					class_2487 lv10 = arg2.method_12024(lv8);
					if (lv10 != null) {
						if (arg2.method_12009().method_12164() == class_2806.class_2808.field_12807) {
							lv10.method_10556("keepPacked", true);
						}

						lv7.method_10606(lv10);
					}
				}
			}

			lv2.method_10566("TileEntities", lv7);
			class_2499 lv11 = new class_2499();
			if (arg2.method_12009().method_12164() == class_2806.class_2808.field_12807) {
				class_2818 lv12 = (class_2818)arg2;
				lv12.method_12232(false);

				for (int l = 0; l < lv12.method_12215().length; l++) {
					for (class_1297 lv13 : lv12.method_12215()[l]) {
						class_2487 lv14 = new class_2487();
						if (lv13.method_5662(lv14)) {
							lv12.method_12232(true);
							lv11.method_10606(lv14);
						}
					}
				}
			} else {
				class_2839 lv15 = (class_2839)arg2;
				lv11.addAll(lv15.method_12295());

				for (class_2338 lv16 : arg2.method_12021()) {
					class_2586 lv17 = arg2.method_8321(lv16);
					if (lv17 != null) {
						class_2487 lv14 = new class_2487();
						lv17.method_11007(lv14);
						lv7.method_10606(lv14);
					} else {
						lv7.method_10606(arg2.method_12024(lv16));
					}
				}

				lv2.method_10566("Lights", method_12393(lv15.method_12296()));
				class_2487 lv18 = new class_2487();

				for (class_2893.class_2894 lv19 : class_2893.class_2894.values()) {
					lv18.method_10570(lv19.toString(), arg2.method_12025(lv19).toByteArray());
				}

				lv2.method_10566("CarvingMasks", lv18);
			}

			lv2.method_10566("Entities", lv11);
			if (arg.method_8397() instanceof class_1949) {
				lv2.method_10566("TileTicks", ((class_1949)arg.method_8397()).method_8669(lv3));
			}

			if (arg2.method_12013() instanceof class_2850) {
				lv2.method_10566("ToBeTicked", ((class_2850)arg2.method_12013()).method_12367());
			}

			if (arg.method_8405() instanceof class_1949) {
				lv2.method_10566("LiquidTicks", ((class_1949)arg.method_8405()).method_8669(lv3));
			}

			if (arg2.method_12014() instanceof class_2850) {
				lv2.method_10566("LiquidsToBeTicked", ((class_2850)arg2.method_12014()).method_12367());
			}

			lv2.method_10566("PostProcessing", method_12393(arg2.method_12012()));
			class_2487 lv20 = new class_2487();

			for (Entry<class_2902.class_2903, class_2902> entry : arg2.method_12011()) {
				if (arg2.method_12009().method_12164() == class_2806.class_2808.field_12808 || ((class_2902.class_2903)entry.getKey()).method_16136()) {
					lv20.method_10566(((class_2902.class_2903)entry.getKey()).method_12605(), new class_2501(((class_2902)entry.getValue()).method_12598()));
				}
			}

			lv2.method_10566("Heightmaps", lv20);
			lv2.method_10566("Structures", this.method_12385(i, j, arg2.method_12016(), arg2.method_12179()));
			this.method_12390(lv3, lv);
		} catch (Exception var21) {
			field_13001.error("Failed to save chunk", (Throwable)var21);
		}
	}

	private void method_12390(class_1923 arg, class_2487 arg2) {
		this.field_13000.put(arg, arg2);
	}

	@Override
	public boolean method_12412() {
		Iterator<Entry<class_1923, class_2487>> iterator = this.field_13000.entrySet().iterator();
		if (!iterator.hasNext()) {
			if (this.field_12998) {
				field_13001.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", this.field_12999.getName());
			}

			return false;
		} else {
			Entry<class_1923, class_2487> entry = (Entry<class_1923, class_2487>)iterator.next();
			iterator.remove();
			class_1923 lv = (class_1923)entry.getKey();
			class_2487 lv2 = (class_2487)entry.getValue();
			if (lv2 == null) {
				return true;
			} else {
				try {
					DataOutputStream dataOutputStream = class_2867.method_12437(this.field_12999, lv.field_9181, lv.field_9180);
					class_2507.method_10628(lv2, dataOutputStream);
					dataOutputStream.close();
					if (this.field_13003 != null) {
						this.field_13003.method_14744(lv.method_8324());
					}
				} catch (Exception var6) {
					field_13001.error("Failed to save chunk", (Throwable)var6);
				}

				return true;
			}
		}
	}

	private class_2806.class_2808 method_12377(@Nullable class_2487 arg) {
		if (arg != null) {
			class_2806 lv = class_2806.method_12168(arg.method_10562("Level").method_10558("Status"));
			if (lv != null) {
				return lv.method_12164();
			}
		}

		return class_2806.class_2808.field_12808;
	}

	@Override
	public void method_12413() {
		try {
			this.field_12998 = true;

			while (this.method_12412()) {
			}
		} finally {
			this.field_12998 = false;
		}
	}

	public static void method_12386(class_2487 arg, class_2818 arg2) {
		class_2499 lv = arg.method_10554("Entities", 10);
		class_1937 lv2 = arg2.method_12200();

		for (int i = 0; i < lv.size(); i++) {
			class_2487 lv3 = lv.method_10602(i);
			method_12383(lv3, lv2, arg2);
			arg2.method_12232(true);
		}

		class_2499 lv4 = arg.method_10554("TileEntities", 10);

		for (int j = 0; j < lv4.size(); j++) {
			class_2487 lv5 = lv4.method_10602(j);
			boolean bl = lv5.method_10577("keepPacked");
			if (bl) {
				arg2.method_12042(lv5);
			} else {
				class_2586 lv6 = class_2586.method_11005(lv5);
				if (lv6 != null) {
					arg2.method_12216(lv6);
				}
			}
		}

		if (arg.method_10573("TileTicks", 9) && lv2.method_8397() instanceof class_1949) {
			((class_1949)lv2.method_8397()).method_8665(arg.method_10554("TileTicks", 10));
		}

		if (arg.method_10573("LiquidTicks", 9) && lv2.method_8405() instanceof class_1949) {
			((class_1949)lv2.method_8405()).method_8665(arg.method_10554("LiquidTicks", 10));
		}
	}

	private class_2499 method_12395(class_1937 arg, int i, int j, class_2826[] args) {
		class_2499 lv = new class_2499();
		class_3568 lv2 = arg.method_8398().method_12130();

		for (int k = -1; k < 17; k++) {
			int l = k;
			class_2826 lv3 = (class_2826)Arrays.stream(args).filter(argx -> argx != null && argx.method_12259() >> 4 == l).findFirst().orElse(class_2818.field_12852);
			class_2804 lv4 = lv2.method_15562(class_1944.field_9282).method_15544(i, l, j);
			class_2804 lv5 = lv2.method_15562(class_1944.field_9284).method_15544(i, l, j);
			if (lv3 != class_2818.field_12852 || lv4 != null || lv5 != null) {
				class_2487 lv6 = new class_2487();
				lv6.method_10567("Y", (byte)(l & 0xFF));
				if (lv3 != class_2818.field_12852) {
					lv3.method_12265().method_12330(lv6, "Palette", "BlockStates");
				}

				if (lv4 != null && !lv4.method_12146()) {
					lv6.method_10570("BlockLight", lv4.method_12137());
				}

				if (lv5 != null && !lv5.method_12146()) {
					lv6.method_10570("SkyLight", lv5.method_12137());
				}

				lv.method_10606(lv6);
			}
		}

		return lv;
	}

	private static class_2826[] method_12384(class_1936 arg, int i, int j, class_2499 arg2, boolean bl) {
		int k = 16;
		class_2826[] lvs = new class_2826[16];
		boolean bl2 = arg.method_8597().method_12451();
		class_2802 lv = arg.method_8398();

		for (int l = 0; l < arg2.size(); l++) {
			class_2487 lv2 = arg2.method_10602(l);
			int m = lv2.method_10571("Y");
			if (lv2.method_10573("Palette", 9) && lv2.method_10573("BlockStates", 12)) {
				class_2826 lv3 = new class_2826(m << 4);
				lv3.method_12265().method_12329(lv2.method_10554("Palette", 10), lv2.method_10565("BlockStates"));
				lv3.method_12253();
				lvs[m] = lv3;
			}

			if (bl) {
				if (lv2.method_10573("BlockLight", 7)) {
					lv.method_12130().method_15558(class_1944.field_9282, i, m, j, new class_2804(lv2.method_10547("BlockLight")));
				}

				if (bl2 && lv2.method_10573("SkyLight", 7)) {
					lv.method_12130().method_15558(class_1944.field_9284, i, m, j, new class_2804(lv2.method_10547("SkyLight")));
				}
			}
		}

		return lvs;
	}

	private class_2487 method_12385(int i, int j, Map<String, class_3449> map, Map<String, LongSet> map2) {
		class_2487 lv = new class_2487();
		class_2487 lv2 = new class_2487();

		for (Entry<String, class_3449> entry : map.entrySet()) {
			lv2.method_10566((String)entry.getKey(), ((class_3449)entry.getValue()).method_14972(i, j));
		}

		lv.method_10566("Starts", lv2);
		class_2487 lv3 = new class_2487();

		for (Entry<String, LongSet> entry2 : map2.entrySet()) {
			lv3.method_10566((String)entry2.getKey(), new class_2501((LongSet)entry2.getValue()));
		}

		lv.method_10566("References", lv3);
		return lv;
	}

	private Map<String, class_3449> method_12392(class_2794<?> arg, class_3485 arg2, class_1966 arg3, class_2487 arg4) {
		Map<String, class_3449> map = Maps.<String, class_3449>newHashMap();
		class_2487 lv = arg4.method_10562("Starts");

		for (String string : lv.method_10541()) {
			map.put(string, class_3420.method_14842(arg, arg2, arg3, lv.method_10562(string)));
		}

		return map;
	}

	private Map<String, LongSet> method_12387(class_2487 arg) {
		Map<String, LongSet> map = Maps.<String, LongSet>newHashMap();
		class_2487 lv = arg.method_10562("References");

		for (String string : lv.method_10541()) {
			map.put(string, new LongOpenHashSet(lv.method_10565(string)));
		}

		return map;
	}

	public static class_2499 method_12393(ShortList[] shortLists) {
		class_2499 lv = new class_2499();

		for (ShortList shortList : shortLists) {
			class_2499 lv2 = new class_2499();
			if (shortList != null) {
				for (Short short_ : shortList) {
					lv2.method_10606(new class_2516(short_));
				}
			}

			lv.method_10606(lv2);
		}

		return lv;
	}

	@Nullable
	private static class_1297 method_12382(class_2487 arg, class_1937 arg2, Function<class_1297, class_1297> function) {
		class_1297 lv = method_12379(arg, arg2);
		if (lv == null) {
			return null;
		} else {
			lv = (class_1297)function.apply(lv);
			if (lv != null && arg.method_10573("Passengers", 9)) {
				class_2499 lv2 = arg.method_10554("Passengers", 10);

				for (int i = 0; i < lv2.size(); i++) {
					class_1297 lv3 = method_12382(lv2.method_10602(i), arg2, function);
					if (lv3 != null) {
						lv3.method_5873(lv, true);
					}
				}
			}

			return lv;
		}
	}

	@Nullable
	public static class_1297 method_12383(class_2487 arg, class_1937 arg2, class_2818 arg3) {
		return method_12382(arg, arg2, arg2x -> {
			arg3.method_12002(arg2x);
			return arg2x;
		});
	}

	@Nullable
	public static class_1297 method_12399(class_2487 arg, class_1937 arg2, double d, double e, double f, boolean bl) {
		return method_12382(arg, arg2, arg2x -> {
			arg2x.method_5808(d, e, f, arg2x.field_6031, arg2x.field_5965);
			return bl && !arg2.method_8649(arg2x) ? null : arg2x;
		});
	}

	@Nullable
	public static class_1297 method_12378(class_2487 arg, class_1937 arg2, boolean bl) {
		return method_12382(arg, arg2, arg2x -> bl && !arg2.method_8649(arg2x) ? null : arg2x);
	}

	@Nullable
	protected static class_1297 method_12379(class_2487 arg, class_1937 arg2) {
		try {
			return class_1299.method_5892(arg, arg2);
		} catch (RuntimeException var3) {
			field_13001.warn("Exception loading entity: ", (Throwable)var3);
			return null;
		}
	}

	public static void method_12394(class_1297 arg, class_1936 arg2) {
		if (arg2.method_8649(arg) && arg.method_5782()) {
			for (class_1297 lv : arg.method_5685()) {
				method_12394(lv, arg2);
			}
		}
	}

	public boolean method_12375(class_1923 arg, class_2874 arg2, class_37 arg3) {
		boolean bl = false;

		try {
			this.method_12398(arg2, arg3, arg.field_9181, arg.field_9180);

			while (this.method_12412()) {
				bl = true;
			}
		} catch (IOException var6) {
		}

		return bl;
	}

	@Override
	public void close() {
		while (this.method_12412()) {
		}
	}
}
