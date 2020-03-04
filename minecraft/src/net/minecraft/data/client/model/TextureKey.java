package net.minecraft.data.client.model;

import javax.annotation.Nullable;

public enum TextureKey {
	ALL("all"),
	TEXTURE("texture", ALL),
	PARTICLE("particle", TEXTURE),
	END("end", ALL),
	BOTTOM("bottom", END),
	TOP("top", END),
	FRONT("front", ALL),
	BACK("back", ALL),
	SIDE("side", ALL),
	NORTH("north", SIDE),
	SOUTH("south", SIDE),
	EAST("east", SIDE),
	WEST("west", SIDE),
	UP("up"),
	DOWN("down"),
	CROSS("cross"),
	PLANT("plant"),
	WALL("wall", ALL),
	RAIL("rail"),
	WOOL("wool"),
	PATTERN("pattern"),
	PANE("pane"),
	EDGE("edge"),
	FAN("fan"),
	STEM("stem"),
	UPPER_STEM("upperstem"),
	CROP("crop"),
	DIRT("dirt"),
	FIRE("fire"),
	LANTERN("lantern"),
	PLATFORM("platform"),
	UNSTICKY("unsticky"),
	TORCH("torch"),
	LAYER0("layer0");

	private final String name;
	@Nullable
	private final TextureKey parent;

	private TextureKey(String name) {
		this.name = name;
		this.parent = null;
	}

	private TextureKey(String name, TextureKey parent) {
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
}
