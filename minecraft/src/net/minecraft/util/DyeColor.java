package net.minecraft.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.MapColor;

public enum DyeColor implements StringIdentifiable {
	WHITE(0, "white", 16383998, MapColor.WHITE, 15790320, 16777215),
	ORANGE(1, "orange", 16351261, MapColor.ORANGE, 15435844, 16738335),
	MAGENTA(2, "magenta", 13061821, MapColor.MAGENTA, 12801229, 16711935),
	LIGHT_BLUE(3, "light_blue", 3847130, MapColor.LIGHT_BLUE, 6719955, 10141901),
	YELLOW(4, "yellow", 16701501, MapColor.YELLOW, 14602026, 16776960),
	LIME(5, "lime", 8439583, MapColor.LIME, 4312372, 12582656),
	PINK(6, "pink", 15961002, MapColor.PINK, 14188952, 16738740),
	GRAY(7, "gray", 4673362, MapColor.GRAY, 4408131, 8421504),
	LIGHT_GRAY(8, "light_gray", 10329495, MapColor.LIGHT_GRAY, 11250603, 13882323),
	CYAN(9, "cyan", 1481884, MapColor.CYAN, 2651799, 65535),
	PURPLE(10, "purple", 8991416, MapColor.PURPLE, 8073150, 10494192),
	BLUE(11, "blue", 3949738, MapColor.BLUE, 2437522, 255),
	BROWN(12, "brown", 8606770, MapColor.BROWN, 5320730, 9127187),
	GREEN(13, "green", 6192150, MapColor.GREEN, 3887386, 65280),
	RED(14, "red", 11546150, MapColor.RED, 11743532, 16711680),
	BLACK(15, "black", 1908001, MapColor.BLACK, 1973019, 0);

	private static final DyeColor[] VALUES = (DyeColor[])Arrays.stream(values()).sorted(Comparator.comparingInt(DyeColor::getId)).toArray(DyeColor[]::new);
	private static final Int2ObjectOpenHashMap<DyeColor> BY_FIREWORK_COLOR = new Int2ObjectOpenHashMap<>(
		(Map<? extends Integer, ? extends DyeColor>)Arrays.stream(values()).collect(Collectors.toMap(dyeColor -> dyeColor.fireworkColor, dyeColor -> dyeColor))
	);
	private final int id;
	private final String name;
	private final MapColor mapColor;
	private final float[] colorComponents;
	private final int fireworkColor;
	private final int signColor;

	private DyeColor(int woolId, String name, int color, MapColor mapColor, int fireworkColor, int signColor) {
		this.id = woolId;
		this.name = name;
		this.mapColor = mapColor;
		this.signColor = signColor;
		int j = (color & 0xFF0000) >> 16;
		int k = (color & 0xFF00) >> 8;
		int l = (color & 0xFF) >> 0;
		this.colorComponents = new float[]{(float)j / 255.0F, (float)k / 255.0F, (float)l / 255.0F};
		this.fireworkColor = fireworkColor;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Returns the red, blue and green components of this dye color.
	 * 
	 * @return an array composed of the red, blue and green floats
	 */
	public float[] getColorComponents() {
		return this.colorComponents;
	}

	public MapColor getMapColor() {
		return this.mapColor;
	}

	public int getFireworkColor() {
		return this.fireworkColor;
	}

	public int getSignColor() {
		return this.signColor;
	}

	public static DyeColor byId(int id) {
		if (id < 0 || id >= VALUES.length) {
			id = 0;
		}

		return VALUES[id];
	}

	public static DyeColor byName(String name, DyeColor defaultColor) {
		for (DyeColor dyeColor : values()) {
			if (dyeColor.name.equals(name)) {
				return dyeColor;
			}
		}

		return defaultColor;
	}

	@Nullable
	public static DyeColor byFireworkColor(int color) {
		return BY_FIREWORK_COLOR.get(color);
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
