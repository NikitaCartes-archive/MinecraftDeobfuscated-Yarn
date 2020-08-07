package net.minecraft.data.client.model;

import javax.annotation.Nullable;

public final class TextureKey {
	public static final TextureKey field_23010 = method_27043("all");
	public static final TextureKey field_23011 = method_27044("texture", field_23010);
	public static final TextureKey field_23012 = method_27044("particle", field_23011);
	public static final TextureKey field_23013 = method_27044("end", field_23010);
	public static final TextureKey field_23014 = method_27044("bottom", field_23013);
	public static final TextureKey field_23015 = method_27044("top", field_23013);
	public static final TextureKey field_23016 = method_27044("front", field_23010);
	public static final TextureKey field_23017 = method_27044("back", field_23010);
	public static final TextureKey field_23018 = method_27044("side", field_23010);
	public static final TextureKey field_23019 = method_27044("north", field_23018);
	public static final TextureKey field_23020 = method_27044("south", field_23018);
	public static final TextureKey field_23021 = method_27044("east", field_23018);
	public static final TextureKey field_23022 = method_27044("west", field_23018);
	public static final TextureKey field_23023 = method_27043("up");
	public static final TextureKey field_23024 = method_27043("down");
	public static final TextureKey field_23025 = method_27043("cross");
	public static final TextureKey field_23026 = method_27043("plant");
	public static final TextureKey field_23027 = method_27044("wall", field_23010);
	public static final TextureKey field_23028 = method_27043("rail");
	public static final TextureKey field_23029 = method_27043("wool");
	public static final TextureKey field_23030 = method_27043("pattern");
	public static final TextureKey field_23031 = method_27043("pane");
	public static final TextureKey field_23032 = method_27043("edge");
	public static final TextureKey field_23033 = method_27043("fan");
	public static final TextureKey field_23034 = method_27043("stem");
	public static final TextureKey field_23035 = method_27043("upperstem");
	public static final TextureKey field_22999 = method_27043("crop");
	public static final TextureKey field_23000 = method_27043("dirt");
	public static final TextureKey field_23001 = method_27043("fire");
	public static final TextureKey field_23002 = method_27043("lantern");
	public static final TextureKey field_23003 = method_27043("platform");
	public static final TextureKey field_23004 = method_27043("unsticky");
	public static final TextureKey field_23005 = method_27043("torch");
	public static final TextureKey field_23006 = method_27043("layer0");
	public static final TextureKey field_23958 = method_27043("lit_log");
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
