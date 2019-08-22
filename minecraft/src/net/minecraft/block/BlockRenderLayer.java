package net.minecraft.block;

public enum BlockRenderLayer {
	SOLID("Solid"),
	CUTOUT_MIPPED("Mipped Cutout"),
	CUTOUT("Cutout"),
	TRANSLUCENT("Translucent");

	private final String name;

	private BlockRenderLayer(String string2) {
		this.name = string2;
	}

	public String toString() {
		return this.name;
	}
}
