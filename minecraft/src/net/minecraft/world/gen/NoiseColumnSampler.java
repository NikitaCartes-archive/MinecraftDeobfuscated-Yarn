package net.minecraft.world.gen;

import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;

/**
 * Samples noise values for use in chunk generation.
 */
public class NoiseColumnSampler {
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
	@Nullable
	private final NoiseCaveSampler noiseCaveSampler;

	public NoiseColumnSampler(
		BiomeSource biomeSource,
		int horizontalNoiseResolution,
		int verticalNoiseResolution,
		int noiseSizeY,
		GenerationShapeConfig config,
		InterpolatedNoiseSampler noise,
		@Nullable SimplexNoiseSampler islandNoise,
		OctavePerlinNoiseSampler densityNoise,
		@Nullable NoiseCaveSampler noiseCaveSampler
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
		this.noiseCaveSampler = noiseCaveSampler;
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
			float f = 0.0F;
			float g = 0.0F;
			float h = 0.0F;
			int i = 2;
			int j = seaLevel;
			float k = this.biomeSource.getBiomeForNoiseGen(x, seaLevel, z).getDepth();

			for (int l = -2; l <= 2; l++) {
				for (int m = -2; m <= 2; m++) {
					Biome biome = this.biomeSource.getBiomeForNoiseGen(x + l, j, z + m);
					float n = biome.getDepth();
					float o = biome.getScale();
					float p;
					float q;
					if (config.isAmplified() && n > 0.0F) {
						p = 1.0F + n * 2.0F;
						q = 1.0F + o * 4.0F;
					} else {
						p = n;
						q = o;
					}

					float r = n > k ? 0.5F : 1.0F;
					float s = r * BIOME_WEIGHT_TABLE[l + 2 + (m + 2) * 5] / (p + 2.0F);
					f += q * s;
					g += p * s;
					h += s;
				}
			}

			float t = g / h;
			float u = f / h;
			double v = (double)(t * 0.5F - 0.125F);
			double w = (double)(u * 0.9F + 0.1F);
			d = v * 0.265625;
			e = 96.0 / w;
		}

		double y = 684.412 * config.getSampling().getXZScale();
		double aa = 684.412 * config.getSampling().getYScale();
		double ab = y / config.getSampling().getXZFactor();
		double ac = aa / config.getSampling().getYFactor();
		double v = config.hasRandomDensityOffset() ? this.getDensityNoise(x, z) : 0.0;

		for (int ad = 0; ad <= noiseSizeY; ad++) {
			int ae = ad + minY;
			double af = this.noise.sample(x, ae, z, y, aa, ab, ac);
			double ag = this.getOffset(ae, d, e, v) + af;
			ag = this.sampleNoiseCaves(x * this.horizontalNoiseResolution, ae * this.verticalNoiseResolution, z * this.horizontalNoiseResolution, ag);
			ag = this.applySlides(ag, ae);
			buffer[ad] = ag;
		}
	}

	private double sampleNoiseCaves(int x, int y, int z, double noise) {
		return this.noiseCaveSampler != null ? this.noiseCaveSampler.sample(x, y, z, noise) : noise;
	}

	/**
	 * Calculates an offset for the noise.
	 * <p>For example in the overworld, this makes lower y values solid while making higher y values air.</p>
	 */
	private double getOffset(int y, double depth, double scale, double randomDensityOffset) {
		double d = 1.0 - (double)y * 2.0 / 32.0 + randomDensityOffset;
		double e = d * this.densityFactor + this.densityOffset;
		double f = (e + depth) * scale;
		return f * (double)(f > 0.0 ? 4 : 1);
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
}
