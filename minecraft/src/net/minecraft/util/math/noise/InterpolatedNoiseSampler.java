package net.minecraft.util.math.noise;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.IntStream;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public class InterpolatedNoiseSampler implements DensityFunction.class_6913 {
	private static final Codec<Double> field_38269 = Codec.doubleRange(0.001, 1000.0);
	private static final MapCodec<InterpolatedNoiseSampler> field_38270 = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					field_38269.fieldOf("xz_scale").forGetter(interpolatedNoiseSampler -> interpolatedNoiseSampler.xzScale),
					field_38269.fieldOf("y_scale").forGetter(interpolatedNoiseSampler -> interpolatedNoiseSampler.yScale),
					field_38269.fieldOf("xz_factor").forGetter(interpolatedNoiseSampler -> interpolatedNoiseSampler.field_38273),
					field_38269.fieldOf("y_factor").forGetter(interpolatedNoiseSampler -> interpolatedNoiseSampler.field_38274),
					Codec.doubleRange(1.0, 8.0).fieldOf("smear_scale_multiplier").forGetter(interpolatedNoiseSampler -> interpolatedNoiseSampler.field_38275)
				)
				.apply(instance, InterpolatedNoiseSampler::method_42384)
	);
	public static final CodecHolder<InterpolatedNoiseSampler> CODEC = CodecHolder.of(field_38270);
	private final OctavePerlinNoiseSampler lowerInterpolatedNoise;
	private final OctavePerlinNoiseSampler upperInterpolatedNoise;
	private final OctavePerlinNoiseSampler interpolationNoise;
	private final double field_38271;
	private final double field_38272;
	private final double field_38273;
	private final double field_38274;
	private final double field_38275;
	private final double field_36630;
	private final double xzScale;
	private final double yScale;

	public static InterpolatedNoiseSampler method_42384(double d, double e, double f, double g, double h) {
		return new InterpolatedNoiseSampler(new Xoroshiro128PlusPlusRandom(0L), d, e, f, g, h);
	}

	private InterpolatedNoiseSampler(
		OctavePerlinNoiseSampler lowerInterpolatedNoise,
		OctavePerlinNoiseSampler upperInterpolatedNoise,
		OctavePerlinNoiseSampler interpolationNoise,
		double d,
		double e,
		double f,
		double g,
		double h
	) {
		this.lowerInterpolatedNoise = lowerInterpolatedNoise;
		this.upperInterpolatedNoise = upperInterpolatedNoise;
		this.interpolationNoise = interpolationNoise;
		this.xzScale = d;
		this.yScale = e;
		this.field_38273 = f;
		this.field_38274 = g;
		this.field_38275 = h;
		this.field_38271 = 684.412 * this.xzScale;
		this.field_38272 = 684.412 * this.yScale;
		this.field_36630 = lowerInterpolatedNoise.method_40556(this.field_38272);
	}

	@VisibleForTesting
	public InterpolatedNoiseSampler(Random random, double d, double e, double f, double g, double h) {
		this(
			OctavePerlinNoiseSampler.createLegacy(random, IntStream.rangeClosed(-15, 0)),
			OctavePerlinNoiseSampler.createLegacy(random, IntStream.rangeClosed(-15, 0)),
			OctavePerlinNoiseSampler.createLegacy(random, IntStream.rangeClosed(-7, 0)),
			d,
			e,
			f,
			g,
			h
		);
	}

	public InterpolatedNoiseSampler method_42386(Random random) {
		return new InterpolatedNoiseSampler(random, this.xzScale, this.yScale, this.field_38273, this.field_38274, this.field_38275);
	}

	@Override
	public double sample(DensityFunction.NoisePos pos) {
		double d = (double)pos.blockX() * this.field_38271;
		double e = (double)pos.blockY() * this.field_38272;
		double f = (double)pos.blockZ() * this.field_38271;
		double g = d / this.field_38273;
		double h = e / this.field_38274;
		double i = f / this.field_38273;
		double j = this.field_38272 * this.field_38275;
		double k = j / this.field_38274;
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
		return this.field_36630;
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
					", xzScale=%.3f, yScale=%.3f, xzMainScale=%.3f, yMainScale=%.3f, cellWidth=4, cellHeight=8", 684.412, 684.412, 8.555150000000001, 4.277575000000001
				)
			)
			.append('}');
	}

	@Override
	public CodecHolder<? extends DensityFunction> getCodec() {
		return CODEC;
	}
}
