package net.minecraft.data.client.model;

import javax.annotation.Nullable;

public final class TextureKey {
	public static final TextureKey ALL = method_27043("all");
	public static final TextureKey TEXTURE = method_27044("texture", ALL);
	public static final TextureKey PARTICLE = method_27044("particle", TEXTURE);
	public static final TextureKey END = method_27044("end", ALL);
	public static final TextureKey BOTTOM = method_27044("bottom", END);
	public static final TextureKey TOP = method_27044("top", END);
	public static final TextureKey FRONT = method_27044("front", ALL);
	public static final TextureKey BACK = method_27044("back", ALL);
	public static final TextureKey SIDE = method_27044("side", ALL);
	public static final TextureKey NORTH = method_27044("north", SIDE);
	public static final TextureKey SOUTH = method_27044("south", SIDE);
	public static final TextureKey EAST = method_27044("east", SIDE);
	public static final TextureKey WEST = method_27044("west", SIDE);
	public static final TextureKey UP = method_27043("up");
	public static final TextureKey DOWN = method_27043("down");
	public static final TextureKey CROSS = method_27043("cross");
	public static final TextureKey PLANT = method_27043("plant");
	public static final TextureKey WALL = method_27044("wall", ALL);
	public static final TextureKey RAIL = method_27043("rail");
	public static final TextureKey WOOL = method_27043("wool");
	public static final TextureKey PATTERN = method_27043("pattern");
	public static final TextureKey PANE = method_27043("pane");
	public static final TextureKey EDGE = method_27043("edge");
	public static final TextureKey FAN = method_27043("fan");
	public static final TextureKey STEM = method_27043("stem");
	public static final TextureKey UPPERSTEM = method_27043("upperstem");
	public static final TextureKey CROP = method_27043("crop");
	public static final TextureKey DIRT = method_27043("dirt");
	public static final TextureKey FIRE = method_27043("fire");
	public static final TextureKey LANTERN = method_27043("lantern");
	public static final TextureKey PLATFORM = method_27043("platform");
	public static final TextureKey UNSTICKY = method_27043("unsticky");
	public static final TextureKey TORCH = method_27043("torch");
	public static final TextureKey LAYER0 = method_27043("layer0");
	public static final TextureKey LIT_LOG = method_27043("lit_log");
	private final String name;
	@Nullable
	private final TextureKey parent;

	private static TextureKey method_27043(String string) {
		return new TextureKey(string, null);
	}

	private static TextureKey method_27044(String string, TextureKey textureKey) {
		return new TextureKey(string, textureKey);
	}

	private TextureKey(String string, @Nullable TextureKey textureKey) {
		this.name = string;
		this.parent = textureKey;
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
