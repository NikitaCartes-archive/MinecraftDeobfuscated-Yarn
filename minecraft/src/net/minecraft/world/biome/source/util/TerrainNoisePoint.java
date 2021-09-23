package net.minecraft.world.biome.source.util;

public record TerrainNoisePoint() {
	private final double offset;
	private final double factor;
	private final double peaks;

	public TerrainNoisePoint(double d, double e, double f) {
		this.offset = d;
		this.factor = e;
		this.peaks = f;
	}
}
