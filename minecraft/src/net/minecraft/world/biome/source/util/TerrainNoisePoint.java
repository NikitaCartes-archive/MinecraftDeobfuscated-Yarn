package net.minecraft.world.biome.source.util;

import java.lang.runtime.ObjectMethods;

public final class TerrainNoisePoint extends Record {
	private final double offset;
	private final double factor;
	private final double peaks;

	public TerrainNoisePoint(double d, double e, double f) {
		this.offset = d;
		this.factor = e;
		this.peaks = f;
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",TerrainNoisePoint,"offset;factor;peaks",TerrainNoisePoint::offset,TerrainNoisePoint::factor,TerrainNoisePoint::peaks>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",TerrainNoisePoint,"offset;factor;peaks",TerrainNoisePoint::offset,TerrainNoisePoint::factor,TerrainNoisePoint::peaks>(
			this
		);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",TerrainNoisePoint,"offset;factor;peaks",TerrainNoisePoint::offset,TerrainNoisePoint::factor,TerrainNoisePoint::peaks>(
			this, object
		);
	}

	public double offset() {
		return this.offset;
	}

	public double factor() {
		return this.factor;
	}

	public double peaks() {
		return this.peaks;
	}
}
