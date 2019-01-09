package net.minecraft;

import java.util.Locale;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3420 {
	private static final Logger field_15262 = LogManager.getLogger();
	public static final class_3195<?> field_16709 = method_14846("Mineshaft", class_3031.field_13547);
	public static final class_3195<?> field_16706 = method_14846("Pillager_Outpost", class_3031.field_16655);
	public static final class_3195<?> field_16707 = method_14846("Fortress", class_3031.field_13569);
	public static final class_3195<?> field_16697 = method_14846("Stronghold", class_3031.field_13565);
	public static final class_3195<?> field_16710 = method_14846("Jungle_Pyramid", class_3031.field_13586);
	public static final class_3195<?> field_16705 = method_14846("Ocean_Ruin", class_3031.field_13536);
	public static final class_3195<?> field_16700 = method_14846("Desert_Pyramid", class_3031.field_13515);
	public static final class_3195<?> field_16708 = method_14846("Igloo", class_3031.field_13527);
	public static final class_3195<?> field_16703 = method_14846("Swamp_Hut", class_3031.field_13520);
	public static final class_3195<?> field_16699 = method_14846("Monument", class_3031.field_13588);
	public static final class_3195<?> field_16701 = method_14846("EndCity", class_3031.field_13553);
	public static final class_3195<?> field_16704 = method_14846("Mansion", class_3031.field_13528);
	public static final class_3195<?> field_16711 = method_14846("Buried_Treasure", class_3031.field_13538);
	public static final class_3195<?> field_16702 = method_14846("Shipwreck", class_3031.field_13589);
	public static final class_3195<?> field_16698 = method_14846("Village", class_3031.field_13587);

	private static class_3195<?> method_14846(String string, class_3195<?> arg) {
		return class_2378.method_10226(class_2378.field_16644, string.toLowerCase(Locale.ROOT), arg);
	}

	public static void method_16651() {
	}

	@Nullable
	public static class_3449 method_14842(class_2794<?> arg, class_3485 arg2, class_1966 arg3, class_2487 arg4) {
		String string = arg4.method_10558("id");
		if ("INVALID".equals(string)) {
			return class_3449.field_16713;
		} else {
			class_3195<?> lv = class_2378.field_16644.method_10223(new class_2960(string.toLowerCase(Locale.ROOT)));
			if (lv == null) {
				field_15262.error("Unknown feature id: {}", string);
				return null;
			} else {
				int i = arg4.method_10550("ChunkX");
				int j = arg4.method_10550("ChunkZ");
				class_1959 lv2 = arg4.method_10545("biome")
					? class_2378.field_11153.method_10223(new class_2960(arg4.method_10558("biome")))
					: arg3.method_8758(new class_2338((i << 4) + 9, 0, (j << 4) + 9));
				class_3341 lv3 = arg4.method_10545("BB") ? new class_3341(arg4.method_10561("BB")) : class_3341.method_14665();
				class_2499 lv4 = arg4.method_10554("Children", 10);

				try {
					class_3449 lv5 = lv.method_14016().create(lv, i, j, lv2, lv3, 0, arg.method_12101());

					for (int k = 0; k < lv4.size(); k++) {
						class_2487 lv6 = lv4.method_10602(k);
						String string2 = lv6.method_10558("id");
						class_3773 lv7 = class_2378.field_16645.method_10223(new class_2960(string2.toLowerCase(Locale.ROOT)));
						if (lv7 == null) {
							field_15262.error("Unknown structure piece id: {}", string2);
						} else {
							try {
								class_3443 lv8 = lv7.load(arg2, lv6);
								lv5.field_15325.add(lv8);
							} catch (Exception var17) {
								field_15262.error("Exception loading structure piece with id {}", string2, var17);
							}
						}
					}

					return lv5;
				} catch (Exception var18) {
					field_15262.error("Failed Start with id {}", string, var18);
					return null;
				}
			}
		}
	}
}
