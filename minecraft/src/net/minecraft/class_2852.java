package net.minecraft;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2852 {
	private static final Logger field_13001 = LogManager.getLogger();

	public static class_2839 method_12395(class_3218 arg, class_3485 arg2, class_4153 arg3, class_1923 arg4, class_2487 arg5) {
		class_2794<?> lv = arg.method_14178().method_12129();
		class_1966 lv2 = lv.method_12098();
		class_2487 lv3 = arg5.method_10562("Level");
		class_1923 lv4 = new class_1923(lv3.method_10550("xPos"), lv3.method_10550("zPos"));
		if (!Objects.equals(arg4, lv4)) {
			field_13001.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", arg4, arg4, lv4);
		}

		class_1959[] lvs = new class_1959[256];
		class_2338.class_2339 lv5 = new class_2338.class_2339();
		if (lv3.method_10573("Biomes", 11)) {
			int[] is = lv3.method_10561("Biomes");

			for (int i = 0; i < is.length; i++) {
				lvs[i] = class_2378.field_11153.method_10200(is[i]);
				if (lvs[i] == null) {
					lvs[i] = lv2.method_8758(lv5.method_10103((i & 15) + arg4.method_8326(), 0, (i >> 4 & 15) + arg4.method_8328()));
				}
			}
		} else {
			for (int j = 0; j < lvs.length; j++) {
				lvs[j] = lv2.method_8758(lv5.method_10103((j & 15) + arg4.method_8326(), 0, (j >> 4 & 15) + arg4.method_8328()));
			}
		}

		class_2843 lv6 = lv3.method_10573("UpgradeData", 10) ? new class_2843(lv3.method_10562("UpgradeData")) : class_2843.field_12950;
		class_2850<class_2248> lv7 = new class_2850<>(argx -> argx == null || argx.method_9564().method_11588(), arg4, lv3.method_10554("ToBeTicked", 9));
		class_2850<class_3611> lv8 = new class_2850<>(argx -> argx == null || argx == class_3612.field_15906, arg4, lv3.method_10554("LiquidsToBeTicked", 9));
		boolean bl = lv3.method_10577("isLightOn");
		class_2499 lv9 = lv3.method_10554("Sections", 10);
		int k = 16;
		class_2826[] lvs2 = new class_2826[16];
		boolean bl2 = arg.method_8597().method_12451();
		class_2802 lv10 = arg.method_14178();
		class_3568 lv11 = lv10.method_12130();
		if (bl) {
			lv11.method_20601(arg4, true);
		}

		for (int l = 0; l < lv9.size(); l++) {
			class_2487 lv12 = lv9.method_10602(l);
			int m = lv12.method_10571("Y");
			if (lv12.method_10573("Palette", 9) && lv12.method_10573("BlockStates", 12)) {
				class_2826 lv13 = new class_2826(m << 4);
				lv13.method_12265().method_12329(lv12.method_10554("Palette", 10), lv12.method_10565("BlockStates"));
				lv13.method_12253();
				if (!lv13.method_12261()) {
					lvs2[m] = lv13;
				}

				arg3.method_19510(arg4, lv13);
			}

			if (bl) {
				if (lv12.method_10573("BlockLight", 7)) {
					lv11.method_15558(class_1944.field_9282, class_4076.method_18681(arg4, m), new class_2804(lv12.method_10547("BlockLight")));
				}

				if (bl2 && lv12.method_10573("SkyLight", 7)) {
					lv11.method_15558(class_1944.field_9284, class_4076.method_18681(arg4, m), new class_2804(lv12.method_10547("SkyLight")));
				}
			}
		}

		long n = lv3.method_10537("InhabitedTime");
		class_2806.class_2808 lv14 = method_12377(arg5);
		class_2791 lv17;
		if (lv14 == class_2806.class_2808.field_12807) {
			class_1951<class_2248> lv15;
			if (lv3.method_10573("TileTicks", 9)) {
				lv15 = class_4296.method_20512(lv3.method_10554("TileTicks", 10), class_2378.field_11146::method_10221, class_2378.field_11146::method_10223);
			} else {
				lv15 = lv7;
			}

			class_1951<class_3611> lv16;
			if (lv3.method_10573("LiquidTicks", 9)) {
				lv16 = class_4296.method_20512(lv3.method_10554("LiquidTicks", 10), class_2378.field_11154::method_10221, class_2378.field_11154::method_10223);
			} else {
				lv16 = lv8;
			}

			lv17 = new class_2818(arg.method_8410(), arg4, lvs, lv6, lv15, lv16, n, lvs2, arg2x -> method_12386(lv3, arg2x));
		} else {
			class_2839 lv18 = new class_2839(arg4, lv6, lvs2, lv7, lv8);
			lv17 = lv18;
			lv18.method_12022(lvs);
			lv18.method_12028(n);
			lv18.method_12308(class_2806.method_12168(lv3.method_10558("Status")));
			if (lv18.method_12009().method_12165(class_2806.field_12795)) {
				lv18.method_17032(lv11);
			}

			if (!bl && lv18.method_12009().method_12165(class_2806.field_12805)) {
				for (class_2338 lv19 : class_2338.method_10094(arg4.method_8326(), 0, arg4.method_8328(), arg4.method_8327(), 255, arg4.method_8329())) {
					if (lv17.method_8320(lv19).method_11630() != 0) {
						lv18.method_12315(lv19);
					}
				}
			}
		}

		lv17.method_12020(bl);
		class_2487 lv20 = lv3.method_10562("Heightmaps");
		EnumSet<class_2902.class_2903> enumSet = EnumSet.noneOf(class_2902.class_2903.class);

		for (class_2902.class_2903 lv21 : lv17.method_12009().method_12160()) {
			String string = lv21.method_12605();
			if (lv20.method_10573(string, 12)) {
				lv17.method_12037(lv21, lv20.method_10565(string));
			} else {
				enumSet.add(lv21);
			}
		}

		class_2902.method_16684(lv17, enumSet);
		class_2487 lv22 = lv3.method_10562("Structures");
		lv17.method_12034(method_12392(lv, arg2, lv2, lv22));
		lv17.method_12183(method_12387(lv22));
		if (lv3.method_10577("shouldSave")) {
			lv17.method_12008(true);
		}

		class_2499 lv23 = lv3.method_10554("PostProcessing", 9);

		for (int o = 0; o < lv23.size(); o++) {
			class_2499 lv24 = lv23.method_10603(o);

			for (int p = 0; p < lv24.size(); p++) {
				lv17.method_12029(lv24.method_10609(p), o);
			}
		}

		if (lv14 == class_2806.class_2808.field_12807) {
			return new class_2821((class_2818)lv17);
		} else {
			class_2839 lv25 = (class_2839)lv17;
			class_2499 lv24 = lv3.method_10554("Entities", 10);

			for (int p = 0; p < lv24.size(); p++) {
				lv25.method_12302(lv24.method_10602(p));
			}

			class_2499 lv26 = lv3.method_10554("TileEntities", 10);

			for (int q = 0; q < lv26.size(); q++) {
				class_2487 lv27 = lv26.method_10602(q);
				lv17.method_12042(lv27);
			}

			class_2499 lv28 = lv3.method_10554("Lights", 9);

			for (int r = 0; r < lv28.size(); r++) {
				class_2499 lv29 = lv28.method_10603(r);

				for (int s = 0; s < lv29.size(); s++) {
					lv25.method_12304(lv29.method_10609(s), r);
				}
			}

			class_2487 lv27 = lv3.method_10562("CarvingMasks");

			for (String string2 : lv27.method_10541()) {
				class_2893.class_2894 lv30 = class_2893.class_2894.valueOf(string2);
				lv25.method_12307(lv30, BitSet.valueOf(lv27.method_10547(string2)));
			}

			return lv25;
		}
	}

	public static class_2487 method_12410(class_3218 arg, class_2791 arg2) {
		class_1923 lv = arg2.method_12004();
		class_2487 lv2 = new class_2487();
		class_2487 lv3 = new class_2487();
		lv2.method_10569("DataVersion", class_155.method_16673().getWorldVersion());
		lv2.method_10566("Level", lv3);
		lv3.method_10569("xPos", lv.field_9181);
		lv3.method_10569("zPos", lv.field_9180);
		lv3.method_10544("LastUpdate", arg.method_8510());
		lv3.method_10544("InhabitedTime", arg2.method_12033());
		lv3.method_10582("Status", arg2.method_12009().method_12172());
		class_2843 lv4 = arg2.method_12003();
		if (!lv4.method_12349()) {
			lv3.method_10566("UpgradeData", lv4.method_12350());
		}

		class_2826[] lvs = arg2.method_12006();
		class_2499 lv5 = new class_2499();
		class_3568 lv6 = arg.method_14178().method_17293();
		boolean bl = arg2.method_12038();

		for (int i = -1; i < 17; i++) {
			int j = i;
			class_2826 lv7 = (class_2826)Arrays.stream(lvs).filter(argx -> argx != null && argx.method_12259() >> 4 == j).findFirst().orElse(class_2818.field_12852);
			class_2804 lv8 = lv6.method_15562(class_1944.field_9282).method_15544(class_4076.method_18681(lv, j));
			class_2804 lv9 = lv6.method_15562(class_1944.field_9284).method_15544(class_4076.method_18681(lv, j));
			if (lv7 != class_2818.field_12852 || lv8 != null || lv9 != null) {
				class_2487 lv10 = new class_2487();
				lv10.method_10567("Y", (byte)(j & 0xFF));
				if (lv7 != class_2818.field_12852) {
					lv7.method_12265().method_12330(lv10, "Palette", "BlockStates");
				}

				if (lv8 != null && !lv8.method_12146()) {
					lv10.method_10570("BlockLight", lv8.method_12137());
				}

				if (lv9 != null && !lv9.method_12146()) {
					lv10.method_10570("SkyLight", lv9.method_12137());
				}

				lv5.add(lv10);
			}
		}

		lv3.method_10566("Sections", lv5);
		if (bl) {
			lv3.method_10556("isLightOn", true);
		}

		class_1959[] lvs2 = arg2.method_12036();
		int[] is = lvs2 != null ? new int[lvs2.length] : new int[0];
		if (lvs2 != null) {
			for (int k = 0; k < lvs2.length; k++) {
				is[k] = class_2378.field_11153.method_10249(lvs2[k]);
			}
		}

		lv3.method_10539("Biomes", is);
		class_2499 lv11 = new class_2499();

		for (class_2338 lv12 : arg2.method_12021()) {
			class_2487 lv10x = arg2.method_20598(lv12);
			if (lv10x != null) {
				lv11.add(lv10x);
			}
		}

		lv3.method_10566("TileEntities", lv11);
		class_2499 lv13 = new class_2499();
		if (arg2.method_12009().method_12164() == class_2806.class_2808.field_12807) {
			class_2818 lv14 = (class_2818)arg2;
			lv14.method_12232(false);

			for (int l = 0; l < lv14.method_12215().length; l++) {
				for (class_1297 lv15 : lv14.method_12215()[l]) {
					class_2487 lv16 = new class_2487();
					if (lv15.method_5662(lv16)) {
						lv14.method_12232(true);
						lv13.add(lv16);
					}
				}
			}
		} else {
			class_2839 lv17 = (class_2839)arg2;
			lv13.addAll(lv17.method_12295());
			lv3.method_10566("Lights", method_12393(lv17.method_12296()));
			class_2487 lv10x = new class_2487();

			for (class_2893.class_2894 lv18 : class_2893.class_2894.values()) {
				lv10x.method_10570(lv18.toString(), arg2.method_12025(lv18).toByteArray());
			}

			lv3.method_10566("CarvingMasks", lv10x);
		}

		lv3.method_10566("Entities", lv13);
		class_1951<class_2248> lv19 = arg2.method_12013();
		if (lv19 instanceof class_2850) {
			lv3.method_10566("ToBeTicked", ((class_2850)lv19).method_12367());
		} else if (lv19 instanceof class_4296) {
			lv3.method_10566("TileTicks", ((class_4296)lv19).method_20463(arg.method_8510()));
		} else {
			lv3.method_10566("TileTicks", arg.method_14196().method_8669(lv));
		}

		class_1951<class_3611> lv20 = arg2.method_12014();
		if (lv20 instanceof class_2850) {
			lv3.method_10566("LiquidsToBeTicked", ((class_2850)lv20).method_12367());
		} else if (lv20 instanceof class_4296) {
			lv3.method_10566("LiquidTicks", ((class_4296)lv20).method_20463(arg.method_8510()));
		} else {
			lv3.method_10566("LiquidTicks", arg.method_14179().method_8669(lv));
		}

		lv3.method_10566("PostProcessing", method_12393(arg2.method_12012()));
		class_2487 lv21 = new class_2487();

		for (Entry<class_2902.class_2903, class_2902> entry : arg2.method_12011()) {
			if (arg2.method_12009().method_12160().contains(entry.getKey())) {
				lv21.method_10566(((class_2902.class_2903)entry.getKey()).method_12605(), new class_2501(((class_2902)entry.getValue()).method_12598()));
			}
		}

		lv3.method_10566("Heightmaps", lv21);
		lv3.method_10566("Structures", method_12385(lv, arg2.method_12016(), arg2.method_12179()));
		return lv2;
	}

	public static class_2806.class_2808 method_12377(@Nullable class_2487 arg) {
		if (arg != null) {
			class_2806 lv = class_2806.method_12168(arg.method_10562("Level").method_10558("Status"));
			if (lv != null) {
				return lv.method_12164();
			}
		}

		return class_2806.class_2808.field_12808;
	}

	private static void method_12386(class_2487 arg, class_2818 arg2) {
		class_2499 lv = arg.method_10554("Entities", 10);
		class_1937 lv2 = arg2.method_12200();

		for (int i = 0; i < lv.size(); i++) {
			class_2487 lv3 = lv.method_10602(i);
			class_1299.method_17842(lv3, lv2, arg2x -> {
				arg2.method_12002(arg2x);
				return arg2x;
			});
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
	}

	private static class_2487 method_12385(class_1923 arg, Map<String, class_3449> map, Map<String, LongSet> map2) {
		class_2487 lv = new class_2487();
		class_2487 lv2 = new class_2487();

		for (Entry<String, class_3449> entry : map.entrySet()) {
			lv2.method_10566((String)entry.getKey(), ((class_3449)entry.getValue()).method_14972(arg.field_9181, arg.field_9180));
		}

		lv.method_10566("Starts", lv2);
		class_2487 lv3 = new class_2487();

		for (Entry<String, LongSet> entry2 : map2.entrySet()) {
			lv3.method_10566((String)entry2.getKey(), new class_2501((LongSet)entry2.getValue()));
		}

		lv.method_10566("References", lv3);
		return lv;
	}

	private static Map<String, class_3449> method_12392(class_2794<?> arg, class_3485 arg2, class_1966 arg3, class_2487 arg4) {
		Map<String, class_3449> map = Maps.<String, class_3449>newHashMap();
		class_2487 lv = arg4.method_10562("Starts");

		for (String string : lv.method_10541()) {
			map.put(string, class_3420.method_14842(arg, arg2, arg3, lv.method_10562(string)));
		}

		return map;
	}

	private static Map<String, LongSet> method_12387(class_2487 arg) {
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
					lv2.add(new class_2516(short_));
				}
			}

			lv.add(lv2);
		}

		return lv;
	}
}
