package net.minecraft.block;

public final class Material {
	/**
	 * Material for plants such as flowers and crops
	 */
	public static final Material PLANT = new Material(MapColor.DARK_GREEN, false);
	public static final Material LIGHT_PASSES_THROUGH = new Material(MapColor.CLEAR, false);
	public static final Material BLOCKS_LIGHT = new Material(MapColor.CLEAR, true);
	private final MapColor color;
	private final boolean blocksLight;

	public Material(MapColor color, boolean blocksLight) {
		this.color = color;
		this.blocksLight = blocksLight;
	}

	public boolean blocksLight() {
		return this.blocksLight;
	}

	public MapColor getColor() {
		return this.color;
	}
}
