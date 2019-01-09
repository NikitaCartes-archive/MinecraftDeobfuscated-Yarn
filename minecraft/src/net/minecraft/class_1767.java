package net.minecraft;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum class_1767 implements class_3542 {
	field_7952(0, "white", 16383998, class_3620.field_16022, 15790320, 16777215),
	field_7946(1, "orange", 16351261, class_3620.field_15987, 15435844, 16738335),
	field_7958(2, "magenta", 13061821, class_3620.field_15998, 12801229, 16711935),
	field_7951(3, "light_blue", 3847130, class_3620.field_16024, 6719955, 10141901),
	field_7947(4, "yellow", 16701501, class_3620.field_16010, 14602026, 16776960),
	field_7961(5, "lime", 8439583, class_3620.field_15997, 4312372, 12582656),
	field_7954(6, "pink", 15961002, class_3620.field_16030, 14188952, 16738740),
	field_7944(7, "gray", 4673362, class_3620.field_15978, 4408131, 8421504),
	field_7967(8, "light_gray", 10329495, class_3620.field_15993, 11250603, 13882323),
	field_7955(9, "cyan", 1481884, class_3620.field_16026, 2651799, 65535),
	field_7945(10, "purple", 8991416, class_3620.field_16014, 8073150, 10494192),
	field_7966(11, "blue", 3949738, class_3620.field_15984, 2437522, 255),
	field_7957(12, "brown", 8606770, class_3620.field_15977, 5320730, 9127187),
	field_7942(13, "green", 6192150, class_3620.field_15995, 3887386, 65280),
	field_7964(14, "red", 11546150, class_3620.field_16020, 11743532, 16711680),
	field_7963(15, "black", 1908001, class_3620.field_16009, 1973019, 0);

	private static final class_1767[] field_7959 = (class_1767[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(class_1767::method_7789))
		.toArray(class_1767[]::new);
	private static final Int2ObjectOpenHashMap<class_1767> field_7950 = new Int2ObjectOpenHashMap<>(
		(Map<? extends Integer, ? extends class_1767>)Arrays.stream(values()).collect(Collectors.toMap(arg -> arg.field_7960, arg -> arg))
	);
	private final int field_7965;
	private final String field_7948;
	private final class_3620 field_7956;
	private final int field_7949;
	private final int field_7962;
	private final float[] field_7943;
	private final int field_7960;
	private final int field_16537;

	private class_1767(int j, String string2, int k, class_3620 arg, int l, int m) {
		this.field_7965 = j;
		this.field_7948 = string2;
		this.field_7949 = k;
		this.field_7956 = arg;
		this.field_16537 = m;
		int n = (k & 0xFF0000) >> 16;
		int o = (k & 0xFF00) >> 8;
		int p = (k & 0xFF) >> 0;
		this.field_7962 = p << 16 | o << 8 | n << 0;
		this.field_7943 = new float[]{(float)n / 255.0F, (float)o / 255.0F, (float)p / 255.0F};
		this.field_7960 = l;
	}

	public int method_7789() {
		return this.field_7965;
	}

	public String method_7792() {
		return this.field_7948;
	}

	@Environment(EnvType.CLIENT)
	public int method_7788() {
		return this.field_7962;
	}

	public float[] method_7787() {
		return this.field_7943;
	}

	public class_3620 method_7794() {
		return this.field_7956;
	}

	public int method_7790() {
		return this.field_7960;
	}

	@Environment(EnvType.CLIENT)
	public int method_16357() {
		return this.field_16537;
	}

	public static class_1767 method_7791(int i) {
		if (i < 0 || i >= field_7959.length) {
			i = 0;
		}

		return field_7959[i];
	}

	public static class_1767 method_7793(String string, class_1767 arg) {
		for (class_1767 lv : values()) {
			if (lv.field_7948.equals(string)) {
				return lv;
			}
		}

		return arg;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static class_1767 method_7786(int i) {
		return field_7950.get(i);
	}

	public String toString() {
		return this.field_7948;
	}

	@Override
	public String method_15434() {
		return this.field_7948;
	}
}
