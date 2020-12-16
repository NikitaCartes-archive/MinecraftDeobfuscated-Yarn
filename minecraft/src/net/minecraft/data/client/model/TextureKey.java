package net.minecraft.data.client.model;

import javax.annotation.Nullable;

public final class TextureKey {
	public static final TextureKey ALL = of("all");
	public static final TextureKey TEXTURE = of("texture", ALL);
	public static final TextureKey PARTICLE = of("particle", TEXTURE);
	public static final TextureKey END = of("end", ALL);
	public static final TextureKey BOTTOM = of("bottom", END);
	public static final TextureKey TOP = of("top", END);
	public static final TextureKey FRONT = of("front", ALL);
	public static final TextureKey BACK = of("back", ALL);
	public static final TextureKey SIDE = of("side", ALL);
	public static final TextureKey NORTH = of("north", SIDE);
	public static final TextureKey SOUTH = of("south", SIDE);
	public static final TextureKey EAST = of("east", SIDE);
	public static final TextureKey WEST = of("west", SIDE);
	public static final TextureKey UP = of("up");
	public static final TextureKey DOWN = of("down");
	public static final TextureKey CROSS = of("cross");
	public static final TextureKey PLANT = of("plant");
	public static final TextureKey WALL = of("wall", ALL);
	public static final TextureKey RAIL = of("rail");
	public static final TextureKey WOOL = of("wool");
	public static final TextureKey PATTERN = of("pattern");
	public static final TextureKey PANE = of("pane");
	public static final TextureKey EDGE = of("edge");
	public static final TextureKey FAN = of("fan");
	public static final TextureKey STEM = of("stem");
	public static final TextureKey UPPERSTEM = of("upperstem");
	public static final TextureKey CROP = of("crop");
	public static final TextureKey DIRT = of("dirt");
	public static final TextureKey FIRE = of("fire");
	public static final TextureKey LANTERN = of("lantern");
	public static final TextureKey PLATFORM = of("platform");
	public static final TextureKey UNSTICKY = of("unsticky");
	public static final TextureKey TORCH = of("torch");
	public static final TextureKey LAYER0 = of("layer0");
	public static final TextureKey LIT_LOG = of("lit_log");
	public static final TextureKey CANDLE = of("candle");
	public static final TextureKey INSIDE = of("inside");
	public static final TextureKey CONTENT = of("content");
	private final String name;
	@Nullable
	private final TextureKey parent;

	private static TextureKey of(String name) {
		return new TextureKey(name, null);
	}

	private static TextureKey of(String name, TextureKey parent) {
		return new TextureKey(name, parent);
	}

	private TextureKey(String name, @Nullable TextureKey parent) {
		this.name = name;
		this.parent = parent;
	}

	public String getName() {
		return this.name;
	}

	@Nullable
	public TextureKey getParent() {
		return this.parent;
	}

	public String toString() {
		return "#" + this.name;
	}
}
