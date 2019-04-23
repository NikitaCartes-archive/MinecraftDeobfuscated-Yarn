package net.minecraft.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MaterialColor;

public enum DyeColor implements SnakeCaseIdentifiable {
	field_7952(0, "white", 16383998, MaterialColor.WHITE, 15790320, 16777215),
	field_7946(1, "orange", 16351261, MaterialColor.ORANGE, 15435844, 16738335),
	field_7958(2, "magenta", 13061821, MaterialColor.MAGENTA, 12801229, 16711935),
	field_7951(3, "light_blue", 3847130, MaterialColor.LIGHT_BLUE, 6719955, 10141901),
	field_7947(4, "yellow", 16701501, MaterialColor.YELLOW, 14602026, 16776960),
	field_7961(5, "lime", 8439583, MaterialColor.LIME, 4312372, 12582656),
	field_7954(6, "pink", 15961002, MaterialColor.PINK, 14188952, 16738740),
	field_7944(7, "gray", 4673362, MaterialColor.GRAY, 4408131, 8421504),
	field_7967(8, "light_gray", 10329495, MaterialColor.LIGHT_GRAY, 11250603, 13882323),
	field_7955(9, "cyan", 1481884, MaterialColor.CYAN, 2651799, 65535),
	field_7945(10, "purple", 8991416, MaterialColor.PURPLE, 8073150, 10494192),
	field_7966(11, "blue", 3949738, MaterialColor.BLUE, 2437522, 255),
	field_7957(12, "brown", 8606770, MaterialColor.BROWN, 5320730, 9127187),
	field_7942(13, "green", 6192150, MaterialColor.GREEN, 3887386, 65280),
	field_7964(14, "red", 11546150, MaterialColor.RED, 11743532, 16711680),
	field_7963(15, "black", 1908001, MaterialColor.BLACK, 1973019, 0);

	private static final DyeColor[] VALUES = (DyeColor[])Arrays.stream(values()).sorted(Comparator.comparingInt(DyeColor::getId)).toArray(DyeColor[]::new);
	private static final Int2ObjectOpenHashMap<DyeColor> BY_FIREWORK_COLOR = new Int2ObjectOpenHashMap<>(
		(Map<? extends Integer, ? extends DyeColor>)Arrays.stream(values()).collect(Collectors.toMap(dyeColor -> dyeColor.fireworkColor, dyeColor -> dyeColor))
	);
	private final int id;
	private final String name;
	private final MaterialColor materialColor;
	private final int color;
	private final int colorSwapped;
	private final float[] colorComponents;
	private final int fireworkColor;
	private final int signColor;

	private DyeColor(int j, String string2, int k, MaterialColor materialColor, int l, int m) {
		this.id = j;
		this.name = string2;
		this.color = k;
		this.materialColor = materialColor;
		this.signColor = m;
		int n = (k & 0xFF0000) >> 16;
		int o = (k & 0xFF00) >> 8;
		int p = (k & 0xFF) >> 0;
		this.colorSwapped = p << 16 | o << 8 | n << 0;
		this.colorComponents = new float[]{(float)n / 255.0F, (float)o / 255.0F, (float)p / 255.0F};
		this.fireworkColor = l;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	@Environment(EnvType.CLIENT)
	public int getColorSwapped() {
		return this.colorSwapped;
	}

	public float[] getColorComponents() {
		return this.colorComponents;
	}

	public MaterialColor getMaterialColor() {
		return this.materialColor;
	}

	public int getFireworkColor() {
		return this.fireworkColor;
	}

	@Environment(EnvType.CLIENT)
	public int getSignColor() {
		return this.signColor;
	}

	public static DyeColor byId(int i) {
		if (i < 0 || i >= VALUES.length) {
			i = 0;
		}

		return VALUES[i];
	}

	public static DyeColor byName(String string, DyeColor dyeColor) {
		for (DyeColor dyeColor2 : values()) {
			if (dyeColor2.name.equals(string)) {
				return dyeColor2;
			}
		}

		return dyeColor;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static DyeColor byFireworkColor(int i) {
		return BY_FIREWORK_COLOR.get(i);
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String toSnakeCase() {
		return this.name;
	}
}
