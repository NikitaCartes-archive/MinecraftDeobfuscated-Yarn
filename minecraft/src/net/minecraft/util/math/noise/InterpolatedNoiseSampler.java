package net.minecraft.util.math.noise;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.stream.IntStream;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public class InterpolatedNoiseSampler implements DensityFunction.Base {
	private static final Codec<Double> SCALE_AND_FACTOR_RANGE = Codec.doubleRange(0.001, 1000.0);
	private static final MapCodec<InterpolatedNoiseSampler> MAP_CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					SCALE_AND_FACTOR_RANGE.fieldOf("xz_scale").forGetter(interpolatedNoiseSampler -> interpolatedNoiseSampler.xzScale),
					SCALE_AND_FACTOR_RANGE.fieldOf("y_scale").forGetter(interpolatedNoiseSampler -> interpolatedNoiseSampler.yScale),
					SCALE_AND_FACTOR_RANGE.fieldOf("xz_factor").forGetter(interpolatedNoiseSampler -> interpolatedNoiseSampler.xzFactor),
					SCALE_AND_FACTOR_RANGE.fieldOf("y_factor").forGetter(interpolatedNoiseSampler -> interpolatedNoiseSampler.yFactor),
					Codec.doubleRange(1.0, 8.0).fieldOf("smear_scale_multiplier").forGetter(interpolatedNoiseSampler -> interpolatedNoiseSampler.smearScaleMultiplier)
				)
				.apply(instance, InterpolatedNoiseSampler::createBase3dNoiseFunction)
	);
	public static final CodecHolder<InterpolatedNoiseSampler> CODEC = CodecHolder.of(MAP_CODEC);
	private final OctavePerlinNoiseSampler lowerInterpolatedNoise;
	private final OctavePerlinNoiseSampler upperInterpolatedNoise;
	private final OctavePerlinNoiseSampler interpolationNoise;
	private final double scaledXzScale;
	private final double scaledYScale;
	private final double xzFactor;
	private final double yFactor;
	private final double smearScaleMultiplier;
	private final double maxValue;
	private final double xzScale;
	private final double yScale;

	public static InterpolatedNoiseSampler createBase3dNoiseFunction(double xzScale, double yScale, double xzFactor, double yFactor, double smearScaleMultiplier) {
		return new InterpolatedNoiseSampler(new Xoroshiro128PlusPlusRandom(0L), xzScale, yScale, xzFactor, yFactor, smearScaleMultiplier);
	}

	private InterpolatedNoiseSampler(
		OctavePerlinNoiseSampler lowerInterpolatedNoise,
		OctavePerlinNoiseSampler upperInterpolatedNoise,
		OctavePerlinNoiseSampler interpolationNoise,
		double xzScale,
		double yScale,
		double xzFactor,
		double yFactor,
		double smearScaleMultiplier
	) {
		this.lowerInterpolatedNoise = lowerInterpolatedNoise;
		this.upperInterpolatedNoise = upperInterpolatedNoise;
		this.interpolationNoise = interpolationNoise;
		this.xzScale = xzScale;
		this.yScale = yScale;
		this.xzFactor = xzFactor;
		this.yFactor = yFactor;
		this.smearScaleMultiplier = smearScaleMultiplier;
		this.scaledXzScale = 684.412 * this.xzScale;
		this.scaledYScale = 684.412 * this.yScale;
		this.maxValue = lowerInterpolatedNoise.method_40556(this.scaledYScale);
	}

	@VisibleForTesting
	public InterpolatedNoiseSampler(Random random, double xzScale, double yScale, double xzFactor, double yFactor, double smearScaleMultiplier) {
		this(
			OctavePerlinNoiseSampler.createLegacy(random, IntStream.rangeClosed(-15, 0)),
			OctavePerlinNoiseSampler.createLegacy(random, IntStream.rangeClosed(-15, 0)),
			OctavePerlinNoiseSampler.createLegacy(random, IntStream.rangeClosed(-7, 0)),
			xzScale,
			yScale,
			xzFactor,
			yFactor,
			smearScaleMultiplier
		);
	}

	public InterpolatedNoiseSampler copyWithRandom(Random random) {
		return new InterpolatedNoiseSampler(random, this.xzScale, this.yScale, this.xzFactor, this.yFactor, this.smearScaleMultiplier);
	}

	@Override
	public double sample(DensityFunction.NoisePos pos) {
		double d = (double)pos.blockX() * this.scaledXzScale;
		double e = (double)pos.blockY() * this.scaledYScale;
		double f = (double)pos.blockZ() * this.scaledXzScale;
		double g = d / this.xzFactor;
		double h = e / this.yFactor;
		double i = f / this.xzFactor;
		double j = this.scaledYScale * this.smearScaleMultiplier;
		double k = j / this.yFactor;
		double l = 0.0;
		double m = 0.0;
		double n = 0.0;
		boolean bl = true;
		double o = 1.0;

		for (int p = 0; p < 8; p++) {
			PerlinNoiseSampler perlinNoiseSampler = this.interpolationNoise.getOctave(p);
			if (perlinNoiseSampler != null) {
				n += perlinNoiseSampler.sample(
						OctavePerlinNoiseSampler.maintainPrecision(g * o),
						OctavePerlinNoiseSampler.maintainPrecision(h * o),
						OctavePerlinNoiseSampler.maintainPrecision(i * o),
						k * o,
						h * o
					)
					/ o;
			}

			o /= 2.0;
		}

		double q = (n / 10.0 + 1.0) / 2.0;
		boolean bl2 = q >= 1.0;
		boolean bl3 = q <= 0.0;
		o = 1.0;

		for (int r = 0; r < 16; r++) {
			double s = OctavePerlinNoiseSampler.maintainPrecision(d * o);
			double t = OctavePerlinNoiseSampler.maintainPrecision(e * o);
			double u = OctavePerlinNoiseSampler.maintainPrecision(f * o);
			double v = j * o;
			if (!bl2) {
				PerlinNoiseSampler perlinNoiseSampler2 = this.lowerInterpolatedNoise.getOctave(r);
				if (perlinNoiseSampler2 != null) {
					l += perlinNoiseSampler2.sample(s, t, u, v, e * o) / o;
				}
			}

			if (!bl3) {
				PerlinNoiseSampler perlinNoiseSampler2 = this.upperInterpolatedNoise.getOctave(r);
				if (perlinNoiseSampler2 != null) {
					m += perlinNoiseSampler2.sample(s, t, u, v, e * o) / o;
				}
			}

			o /= 2.0;
		}

		return MathHelper.clampedLerp(l / 512.0, m / 512.0, q) / 128.0;
	}

	@Override
	public double minValue() {
		return -this.maxValue();
	}

	@Override
	public double maxValue() {
		return this.maxValue;
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
					Locale.ROOT,
					", xzScale=%.3f, yScale=%.3f, xzMainScale=%.3f, yMainScale=%.3f, cellWidth=4, cellHeight=8",
					684.412,
					684.412,
					8.555150000000001,
					4.277575000000001
				)
			)
			.append('}');
	}

	@Override
	public CodecHolder<? extends DensityFunction> getCodecHolder() {
		return CODEC;
	}
}
