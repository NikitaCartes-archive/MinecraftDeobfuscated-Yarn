package net.minecraft.block;

public enum BlockRenderLayer {
	SOLID("Solid"),
	MIPPED_CUTOUT("Mipped Cutout"),
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
