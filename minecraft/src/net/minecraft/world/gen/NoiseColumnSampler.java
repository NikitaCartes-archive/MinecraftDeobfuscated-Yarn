package net.minecraft.world.gen;

import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.WeightSampler;

/**
 * Samples noise values for use in chunk generation.
 */
public class NoiseColumnSampler {
	private static final int field_31470 = 32;
	/**
	 * Table of weights used to weight faraway biomes less than nearby biomes.
	 */
	private static final float[] BIOME_WEIGHT_TABLE = Util.make(new float[25], array -> {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
				array[i + 2 + (j + 2) * 5] = f;
			}
		}
	});
	private final BiomeSource biomeSource;
	private final int horizontalNoiseResolution;
	private final int verticalNoiseResolution;
	private final int noiseSizeY;
	private final GenerationShapeConfig config;
	private final InterpolatedNoiseSampler noise;
	@Nullable
	private final SimplexNoiseSampler islandNoise;
	private final OctavePerlinNoiseSampler densityNoise;
	private final double topSlideTarget;
	private final double topSlideSize;
	private final double topSlideOffset;
	private final double bottomSlideTarget;
	private final double bottomSlideSize;
	private final double bottomSlideOffset;
	private final double densityFactor;
	private final double densityOffset;
	private final WeightSampler weightSampler;

	public NoiseColumnSampler(
		BiomeSource biomeSource,
		int horizontalNoiseResolution,
		int verticalNoiseResolution,
		int noiseSizeY,
		GenerationShapeConfig config,
		InterpolatedNoiseSampler noise,
		@Nullable SimplexNoiseSampler islandNoise,
		OctavePerlinNoiseSampler densityNoise,
		WeightSampler weightSampler
	) {
		this.horizontalNoiseResolution = horizontalNoiseResolution;
		this.verticalNoiseResolution = verticalNoiseResolution;
		this.biomeSource = biomeSource;
		this.noiseSizeY = noiseSizeY;
		this.config = config;
		this.noise = noise;
		this.islandNoise = islandNoise;
		this.densityNoise = densityNoise;
		this.topSlideTarget = (double)config.getTopSlide().getTarget();
		this.topSlideSize = (double)config.getTopSlide().getSize();
		this.topSlideOffset = (double)config.getTopSlide().getOffset();
		this.bottomSlideTarget = (double)config.getBottomSlide().getTarget();
		this.bottomSlideSize = (double)config.getBottomSlide().getSize();
		this.bottomSlideOffset = (double)config.getBottomSlide().getOffset();
		this.densityFactor = config.getDensityFactor();
		this.densityOffset = config.getDensityOffset();
		this.weightSampler = weightSampler;
	}

	/**
	 * Samples the noise for the given column and stores it in the buffer parameter.
	 */
	public void sampleNoiseColumn(double[] buffer, int x, int z, GenerationShapeConfig config, int seaLevel, int minY, int noiseSizeY) {
		double d;
		double e;
		if (this.islandNoise != null) {
			d = (double)(TheEndBiomeSource.getNoiseAt(this.islandNoise, x, z) - 8.0F);
			if (d > 0.0) {
				e = 0.25;
			} else {
				e = 1.0;
			}
		} else {
			int i = x * this.horizontalNoiseResolution >> 2;
			int j = z * this.horizontalNoiseResolution >> 2;
			BiomeSource.class_6482 lv = this.biomeSource.method_37845(i, j);
			d = lv.field_34300;
			e = lv.field_34301;
		}

		double f = 684.412 * config.getSampling().getXZScale();
		double g = 684.412 * config.getSampling().getYScale();
		double h = f / config.getSampling().getXZFactor();
		double k = g / config.getSampling().getYFactor();

		for (int l = 0; l <= noiseSizeY; l++) {
			int m = l + minY;
			double n = (double)(m * this.verticalNoiseResolution);
			double o = this.noise.sample(x, m, z, f, g, h, k);
			double p = this.getOffset(n, d, e, 0.0) + o;
			p = this.weightSampler.sample(p, x * this.horizontalNoiseResolution, m * this.verticalNoiseResolution, z * this.horizontalNoiseResolution);
			p = this.applySlides(p, m);
			buffer[l] = p;
		}
	}

	/**
	 * Calculates an offset for the noise.
	 * <p>For example in the overworld, this makes lower y values solid while making higher y values air.
	 */
	private double getOffset(double verticalNoiseResolution, double offset, double factor, double d) {
		double e = getDepth(this.densityFactor, this.densityOffset, verticalNoiseResolution, d);
		double f = (e + offset) * factor;
		return f * (double)(f > 0.0 ? 4 : 1);
	}

	public static double getDepth(double densityFactor, double densityOffset, double height) {
		return getDepth(densityFactor, densityOffset, height, 0.0);
	}

	public static double getDepth(double densityFactor, double densityOffset, double height, double offset) {
		double d = 1.0 - height / 128.0 + offset;
		return d * densityFactor + densityOffset;
	}

	/**
	 * Interpolates the noise at the top and bottom of the world.
	 */
	private double applySlides(double noise, int y) {
		int i = MathHelper.floorDiv(this.config.getMinimumY(), this.verticalNoiseResolution);
		int j = y - i;
		if (this.topSlideSize > 0.0) {
			double d = ((double)(this.noiseSizeY - j) - this.topSlideOffset) / this.topSlideSize;
			noise = MathHelper.clampedLerp(this.topSlideTarget, noise, d);
		}

		if (this.bottomSlideSize > 0.0) {
			double d = ((double)j - this.bottomSlideOffset) / this.bottomSlideSize;
			noise = MathHelper.clampedLerp(this.bottomSlideTarget, noise, d);
		}

		return noise;
	}

	/**
	 * Applies a random change to the density to subtly vary the height of the terrain.
	 */
	private double getDensityNoise(int x, int z) {
		double d = this.densityNoise.sample((double)(x * 200), 10.0, (double)(z * 200), 1.0, 0.0, true);
		double e;
		if (d < 0.0) {
			e = -d * 0.3;
		} else {
			e = d;
		}

		double f = e * 24.575625 - 2.0;
		return f < 0.0 ? f * 0.009486607142857142 : Math.min(f, 1.0) * 0.006640625;
	}

	public NoiseColumnSampler.class_6483 method_37849(int i, int j) {
		int k = Math.floorDiv(i, this.horizontalNoiseResolution);
		int l = Math.floorDiv(j, this.horizontalNoiseResolution);
		int m = MathHelper.floorDiv(this.config.getMinimumY(), this.verticalNoiseResolution);
		int n = MathHelper.floorDiv(this.config.getHeight(), this.verticalNoiseResolution);
		int o = 2;
		int p = 1;
		boolean bl = false;
		int q = Integer.MAX_VALUE;

		for (int r = k - 2; r <= k + 2; r += 2) {
			for (int s = l - 2; s <= l + 2; s += 2) {
				int t = r * this.horizontalNoiseResolution >> 2;
				int u = s * this.horizontalNoiseResolution >> 2;
				BiomeSource.class_6482 lv = this.biomeSource.method_37845(t, u);
				double d = lv.field_34300;
				double e = lv.field_34301;
				if (lv.field_34302) {
					bl = true;
				}

				for (int v = m; v <= m + n; v++) {
					int w = v - m;
					double f = (double)(v * this.verticalNoiseResolution);
					double g = -70.0;
					double h = this.getOffset(f, d, e, 0.0) + -70.0;
					double x = this.applySlides(h, w);
					if (this.method_37763(x)) {
						q = Math.min(v * this.verticalNoiseResolution, q);
						break;
					}
				}
			}
		}

		return new NoiseColumnSampler.class_6483(q, bl);
	}

	public int method_37766(int i, int j) {
		return this.method_37849(i, j).field_34307;
	}

	private boolean method_37763(double d) {
		return d < 50.0;
	}

	public static class class_6483 {
		public final int field_34307;
		public final boolean field_34308;

		class_6483(int i, boolean bl) {
			this.field_34307 = i;
			this.field_34308 = bl;
		}
	}
}
