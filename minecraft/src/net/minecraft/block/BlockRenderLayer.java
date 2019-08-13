package net.minecraft.block;

public enum BlockRenderLayer {
	field_9178("Solid"),
	CUTOUT_MIPPED("Mipped Cutout"),
	field_9174("Cutout"),
	field_9179("Translucent");

	private final String name;

	private BlockRenderLayer(String string2) {
		this.name = string2;
	}

	public String toString() {
		return this.name;
	}
}
