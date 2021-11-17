package net.minecraft.util.math.noise;

import com.google.common.annotations.VisibleForTesting;
import java.util.stream.IntStream;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.random.AbstractRandom;

public class InterpolatedNoiseSampler implements ChunkNoiseSampler.ColumnSampler {
	private final OctavePerlinNoiseSampler lowerInterpolatedNoise;
	private final OctavePerlinNoiseSampler upperInterpolatedNoise;
	private final OctavePerlinNoiseSampler interpolationNoise;
	private final double xzScale;
	private final double yScale;
	private final double xzMainScale;
	private final double yMainScale;
	private final int cellWidth;
	private final int cellHeight;

	private InterpolatedNoiseSampler(
		OctavePerlinNoiseSampler lowerInterpolatedNoise,
		OctavePerlinNoiseSampler upperInterpolatedNoise,
		OctavePerlinNoiseSampler interpolationNoise,
		NoiseSamplingConfig config,
		int cellWidth,
		int cellHeight
	) {
		this.lowerInterpolatedNoise = lowerInterpolatedNoise;
		this.upperInterpolatedNoise = upperInterpolatedNoise;
		this.interpolationNoise = interpolationNoise;
		this.xzScale = 684.412 * config.getXZScale();
		this.yScale = 684.412 * config.getYScale();
		this.xzMainScale = this.xzScale / config.getXZFactor();
		this.yMainScale = this.yScale / config.getYFactor();
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
	}

	public InterpolatedNoiseSampler(AbstractRandom random, NoiseSamplingConfig config, int cellWidth, int cellHeight) {
		this(
			OctavePerlinNoiseSampler.createLegacy(random, IntStream.rangeClosed(-15, 0)),
			OctavePerlinNoiseSampler.createLegacy(random, IntStream.rangeClosed(-15, 0)),
			OctavePerlinNoiseSampler.createLegacy(random, IntStream.rangeClosed(-7, 0)),
			config,
			cellWidth,
			cellHeight
		);
	}

	@Override
	public double calculateNoise(int i, int j, int k) {
		int l = Math.floorDiv(i, this.cellWidth);
		int m = Math.floorDiv(j, this.cellHeight);
		int n = Math.floorDiv(k, this.cellWidth);
		double d = 0.0;
		double e = 0.0;
		double f = 0.0;
		boolean bl = true;
		double g = 1.0;

		for (int o = 0; o < 8; o++) {
			PerlinNoiseSampler perlinNoiseSampler = this.interpolationNoise.getOctave(o);
			if (perlinNoiseSampler != null) {
				f += perlinNoiseSampler.sample(
						OctavePerlinNoiseSampler.maintainPrecision((double)l * this.xzMainScale * g),
						OctavePerlinNoiseSampler.maintainPrecision((double)m * this.yMainScale * g),
						OctavePerlinNoiseSampler.maintainPrecision((double)n * this.xzMainScale * g),
						this.yMainScale * g,
						(double)m * this.yMainScale * g
					)
					/ g;
			}

			g /= 2.0;
		}

		double h = (f / 10.0 + 1.0) / 2.0;
		boolean bl2 = h >= 1.0;
		boolean bl3 = h <= 0.0;
		g = 1.0;

		for (int p = 0; p < 16; p++) {
			double q = OctavePerlinNoiseSampler.maintainPrecision((double)l * this.xzScale * g);
			double r = OctavePerlinNoiseSampler.maintainPrecision((double)m * this.yScale * g);
			double s = OctavePerlinNoiseSampler.maintainPrecision((double)n * this.xzScale * g);
			double t = this.yScale * g;
			if (!bl2) {
				PerlinNoiseSampler perlinNoiseSampler2 = this.lowerInterpolatedNoise.getOctave(p);
				if (perlinNoiseSampler2 != null) {
					d += perlinNoiseSampler2.sample(q, r, s, t, (double)m * t) / g;
				}
			}

			if (!bl3) {
				PerlinNoiseSampler perlinNoiseSampler2 = this.upperInterpolatedNoise.getOctave(p);
				if (perlinNoiseSampler2 != null) {
					e += perlinNoiseSampler2.sample(q, r, s, t, (double)m * t) / g;
				}
			}

			g /= 2.0;
		}

		return MathHelper.clampedLerp(d / 512.0, e / 512.0, h) / 128.0;
	}

	@VisibleForTesting
	public void addDebugInfo(StringBuilder info) {
		info.append("BlendedNoise{minLimitNoise=");
		this.lowerInterpolatedNoise.addDebugInfo(info);
		info.append(", maxLimitNoise=");
		this.upperInterpolatedNoise.addDebugInfo(info);
		info.append(", mainNoise=");
		this.interpolationNoise.addDebugInfo(info);
		info.append(
				String.format(
					", xzScale=%.3f, yScale=%.3f, xzMainScale=%.3f, yMainScale=%.3f, cellWidth=%d, cellHeight=%d",
					this.xzScale,
					this.yScale,
					this.xzMainScale,
					this.yMainScale,
					this.cellWidth,
					this.cellHeight
				)
			)
			.append('}');
	}
}
