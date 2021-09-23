package net.minecraft.world.gen;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.class_6568;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.TerrainNoisePoint;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.BlockPosRandomDeriver;

/**
 * Samples noise values for use in chunk generation.
 */
public class NoiseColumnSampler implements MultiNoiseUtil.MultiNoiseSampler {
	private static final float field_34658 = 1.0F;
	private static final float field_34668 = 0.08F;
	private static final float field_34669 = 0.4F;
	private static final double field_34670 = 1.5;
	private static final int field_34671 = 20;
	private static final double field_34672 = 0.2;
	private static final float field_34673 = 0.7F;
	private static final float field_34674 = 0.1F;
	private static final float field_34675 = 0.3F;
	private static final float field_34676 = 0.6F;
	private static final float field_34677 = 0.02F;
	private static final float field_34678 = -0.3F;
	private static final double field_34679 = 1.5;
	private final int verticalNoiseResolution;
	private final int noiseSizeY;
	private final GenerationShapeConfig config;
	private final class_6568.ColumnSampler noise;
	@Nullable
	private final SimplexNoiseSampler islandNoise;
	private final DoublePerlinNoiseSampler field_34681;
	private final double densityFactor;
	private final double densityOffset;
	private final int field_34682;
	private final DoublePerlinNoiseSampler field_34683;
	private final DoublePerlinNoiseSampler field_34684;
	private final DoublePerlinNoiseSampler field_34685;
	private final BlockPosRandomDeriver field_34686;
	private final NoiseSampler field_34632;
	private final DoublePerlinNoiseSampler field_34633;
	private final DoublePerlinNoiseSampler field_34634;
	private final DoublePerlinNoiseSampler field_34635;
	private final DoublePerlinNoiseSampler field_34636;
	private final DoublePerlinNoiseSampler field_34637;
	private final DoublePerlinNoiseSampler field_34638;
	private final DoublePerlinNoiseSampler field_34639;
	private final DoublePerlinNoiseSampler field_34640;
	private final DoublePerlinNoiseSampler field_34641;
	private final DoublePerlinNoiseSampler field_34642;
	private final DoublePerlinNoiseSampler field_34643;
	private final DoublePerlinNoiseSampler field_34644;
	private final DoublePerlinNoiseSampler field_34645;
	private final DoublePerlinNoiseSampler field_34646;
	private final DoublePerlinNoiseSampler field_34647;
	private final DoublePerlinNoiseSampler field_34648;
	private final DoublePerlinNoiseSampler temperatureNoise;
	private final DoublePerlinNoiseSampler humidityNoise;
	private final DoublePerlinNoiseSampler continentalnessNoise;
	private final DoublePerlinNoiseSampler erosionNoise;
	private final DoublePerlinNoiseSampler weirdnessNoise;
	private final DoublePerlinNoiseSampler shiftNoise;
	private final VanillaTerrainParameters terrainParameters = new VanillaTerrainParameters();
	private final DoublePerlinNoiseSampler field_34656;
	private final class_6568.class_6571 field_34657;
	private final class_6568.class_6571 field_34659;
	private final class_6568.class_6571 field_34660;
	private final class_6568.class_6571 field_34661;
	private final BlockPosRandomDeriver field_34662;
	private final class_6568.class_6571 field_34663;
	private final class_6568.class_6571 field_34664;
	private final class_6568.class_6571 field_34665;
	private final class_6568.class_6571 field_34666;
	private final boolean field_34667;

	public NoiseColumnSampler(
		int i, int horizontalNoiseResolution, int verticalNoiseResolution, GenerationShapeConfig config, MultiNoiseParameters noiseParameters, boolean bl, long seed
	) {
		this.verticalNoiseResolution = horizontalNoiseResolution;
		this.noiseSizeY = verticalNoiseResolution;
		this.config = config;
		this.densityFactor = config.getDensityFactor();
		this.densityOffset = config.getDensityOffset();
		int j = config.getMinimumY();
		this.field_34682 = MathHelper.floorDiv(j, horizontalNoiseResolution);
		AbstractRandom abstractRandom = new AtomicSimpleRandom(seed);
		AbstractRandom abstractRandom2 = new AtomicSimpleRandom(seed);
		AbstractRandom abstractRandom3 = config.method_38413() ? abstractRandom2 : abstractRandom.derive();
		this.noise = new InterpolatedNoiseSampler(abstractRandom3, config.getSampling(), i, horizontalNoiseResolution);
		this.field_34632 = (NoiseSampler)(config.hasSimplexSurfaceNoise()
			? new OctaveSimplexNoiseSampler(abstractRandom2, IntStream.rangeClosed(-3, 0))
			: new OctavePerlinNoiseSampler(abstractRandom2, IntStream.rangeClosed(-3, 0)));
		if (config.hasIslandNoiseOverride()) {
			AbstractRandom abstractRandom4 = new AtomicSimpleRandom(seed);
			abstractRandom4.skip(17292);
			this.islandNoise = new SimplexNoiseSampler(abstractRandom4);
		} else {
			this.islandNoise = null;
		}

		AbstractRandom abstractRandom4 = abstractRandom.derive();
		this.field_34683 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -3, 1.0);
		this.field_34684 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -3, 0.2, 2.0, 1.0);
		this.field_34685 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -1, 1.0, 0.0);
		this.field_34686 = abstractRandom4.createBlockPosRandomDeriver();
		abstractRandom4 = abstractRandom.derive();
		this.field_34634 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -7, 1.0, 1.0);
		this.field_34635 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -8, 1.0);
		this.field_34636 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -8, 1.0);
		this.field_34637 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -7, 1.0);
		this.field_34638 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -8, 1.0);
		this.field_34639 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -11, 1.0);
		this.field_34640 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -11, 1.0);
		this.field_34641 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -7, 1.0);
		this.field_34642 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -7, 1.0);
		this.field_34643 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -11, 1.0);
		this.field_34644 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -8, 1.0);
		this.field_34645 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -5, 1.0);
		this.field_34646 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -8, 1.0);
		this.field_34647 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -7, 0.4, 0.5, 1.0);
		this.field_34633 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -8, 1.0);
		this.field_34648 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -8, 0.5, 1.0, 2.0, 1.0, 2.0, 1.0, 0.0, 2.0, 0.0);
		this.field_34667 = bl;
		this.temperatureNoise = DoublePerlinNoiseSampler.create(new AtomicSimpleRandom(seed), noiseParameters.temperature());
		this.humidityNoise = DoublePerlinNoiseSampler.create(new AtomicSimpleRandom(seed + 1L), noiseParameters.humidity());
		this.continentalnessNoise = DoublePerlinNoiseSampler.create(new AtomicSimpleRandom(seed + 2L), noiseParameters.continentalness());
		this.erosionNoise = DoublePerlinNoiseSampler.create(new AtomicSimpleRandom(seed + 3L), noiseParameters.erosion());
		this.weirdnessNoise = DoublePerlinNoiseSampler.create(new AtomicSimpleRandom(seed + 4L), noiseParameters.weirdness());
		this.shiftNoise = DoublePerlinNoiseSampler.create(new AtomicSimpleRandom(seed + 5L), noiseParameters.shift());
		this.field_34657 = arg -> arg.method_38344(
				(ix, jx, kx) -> this.method_38380(ix, jx, kx, arg.method_38360(BiomeCoords.fromBlock(ix), BiomeCoords.fromBlock(kx)))
			);
		int k = Stream.of(NoiseColumnSampler.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(j);
		int l = Stream.of(NoiseColumnSampler.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(j);
		float f = 4.0F;
		AbstractRandom abstractRandom5 = abstractRandom.derive();
		this.field_34659 = method_38379(k, l, 0, 1.5, abstractRandom5.derive(), -8, 1.0);
		this.field_34660 = method_38379(k, l, 0, 4.0, abstractRandom5.derive(), -7, 1.0);
		this.field_34661 = method_38379(k, l, 0, 4.0, abstractRandom5.derive(), -7, 1.0);
		this.field_34656 = DoublePerlinNoiseSampler.create(abstractRandom5.derive(), -5, 1.0);
		this.field_34662 = abstractRandom5.createBlockPosRandomDeriver();
		double d = 2.6666666666666665;
		AbstractRandom abstractRandom6 = abstractRandom.derive();
		int m = j + 4;
		int n = j + config.getHeight();
		this.field_34663 = method_38379(m, n, -1, 1.0, abstractRandom6.derive(), -8, 1.0);
		this.field_34664 = method_38379(m, n, 0, 1.0, abstractRandom6.derive(), -8, 1.0);
		this.field_34665 = method_38379(m, n, 0, 2.6666666666666665, abstractRandom6.derive(), -7, 1.0);
		this.field_34666 = method_38379(m, n, 0, 2.6666666666666665, abstractRandom6.derive(), -7, 1.0);
		this.field_34681 = DoublePerlinNoiseSampler.create(
			abstractRandom.derive(), -16, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0
		);
	}

	private static class_6568.class_6571 method_38379(int i, int j, int k, double d, AbstractRandom abstractRandom, int l, double... ds) {
		DoublePerlinNoiseSampler doublePerlinNoiseSampler = DoublePerlinNoiseSampler.create(abstractRandom, l, ds);
		class_6568.ColumnSampler columnSampler = (lx, m, n) -> m <= j && m >= i
				? doublePerlinNoiseSampler.sample((double)lx * d, (double)m * d, (double)n * d)
				: (double)k;
		return arg -> arg.method_38344(columnSampler);
	}

	private double method_38380(int i, int j, int k, TerrainNoisePoint terrainNoisePoint) {
		double d = this.noise.calculateNoise(i, j, k);
		boolean bl = !this.field_34667;
		return this.method_38381(i, j, k, terrainNoisePoint, d, bl);
	}

	private double method_38381(int i, int j, int k, TerrainNoisePoint terrainNoisePoint, double d, boolean bl) {
		double e;
		if (this.densityFactor == 0.0 && this.densityOffset == -0.030078125) {
			e = 0.0;
		} else {
			double f = this.method_38409(terrainNoisePoint.peaks(), (double)i, (double)k);
			double g = this.getDepth((double)j);
			double h = (g + terrainNoisePoint.offset() + f) * terrainNoisePoint.factor();
			e = h * (double)(h > 0.0 ? 4 : 1);
		}

		double f = e + d;
		double g = 1.5625;
		double l;
		double m;
		double h;
		if (!bl && !(f < -64.0)) {
			double n = f - 1.5625;
			boolean bl2 = n < 0.0;
			double o = this.method_38398(i, j, k);
			double p = this.method_38411(i, j, k);
			double q = this.method_38408(i, j, k);
			double r = Math.min(o, q + p);
			if (bl2) {
				h = f;
				l = r * 5.0;
				m = -64.0;
			} else {
				double s = this.method_38405(i, j, k);
				if (s > 64.0) {
					h = 64.0;
				} else {
					double t = this.field_34648.sample((double)i, (double)j / 1.5, (double)k);
					double u = MathHelper.clamp(t + 0.27, -1.0, 1.0);
					double v = n * 1.28;
					double w = u + MathHelper.clampedLerp(0.5, 0.0, v);
					h = w + s;
				}

				double t = this.method_38410(i, j, k);
				l = Math.min(r, t + p);
				m = this.method_38402(i, j, k);
			}
		} else {
			h = f;
			l = 64.0;
			m = -64.0;
		}

		double n = Math.max(Math.min(h, l), m);
		n = this.applySlides(n, j / this.verticalNoiseResolution);
		return MathHelper.clamp(n, -64.0, 64.0);
	}

	private double method_38409(double d, double e, double f) {
		if (d == 0.0) {
			return 0.0;
		} else {
			float g = 1500.0F;
			double h = this.field_34681.sample(e * 1500.0, 0.0, f * 1500.0);
			return h > 0.0 ? d * h : d / 2.0 * h;
		}
	}

	private double getDepth(double d) {
		double e = 1.0 - d / 128.0;
		return e * this.densityFactor + this.densityOffset;
	}

	/**
	 * Interpolates the noise at the top and bottom of the world.
	 */
	private double applySlides(double noise, int y) {
		int i = y - this.field_34682;
		noise = this.config.getTopSlide().method_38414(noise, this.noiseSizeY - i);
		return this.config.getBottomSlide().method_38414(noise, i);
	}

	public class_6568.class_6569 method_38390(class_6568 arg, class_6568.ColumnSampler columnSampler, boolean bl) {
		class_6568.class_6573 lv = this.field_34657.instantiate(arg);
		class_6568.class_6573 lv2 = bl ? this.field_34663.instantiate(arg) : () -> -1.0;
		class_6568.class_6573 lv3 = bl ? this.field_34664.instantiate(arg) : () -> 0.0;
		class_6568.class_6573 lv4 = bl ? this.field_34665.instantiate(arg) : () -> 0.0;
		class_6568.class_6573 lv5 = bl ? this.field_34666.instantiate(arg) : () -> 0.0;
		return (i, j, k) -> {
			double d = lv.sample();
			double e = MathHelper.clamp(d * 0.64, -1.0, 1.0);
			e = e / 2.0 - e * e * e / 24.0;
			if (lv2.sample() >= 0.0) {
				double f = 0.05;
				double g = 0.1;
				double h = MathHelper.clampedLerpFromProgress(lv3.sample(), -1.0, 1.0, 0.05, 0.1);
				double l = Math.abs(1.5 * lv4.sample()) - h;
				double m = Math.abs(1.5 * lv5.sample()) - h;
				e = Math.min(e, Math.max(l, m));
			}

			e += columnSampler.calculateNoise(i, j, k);
			return arg.method_38354().apply(i, j, k, d, e);
		};
	}

	public class_6568.class_6569 method_38391(class_6568 arg, boolean bl) {
		if (!bl) {
			return (i, j, k) -> null;
		} else {
			class_6568.class_6573 lv = this.field_34659.instantiate(arg);
			class_6568.class_6573 lv2 = this.field_34660.instantiate(arg);
			class_6568.class_6573 lv3 = this.field_34661.instantiate(arg);
			BlockState blockState = null;
			return (i, j, k) -> {
				AbstractRandom abstractRandom = this.field_34662.createRandom(i, j, k);
				double d = lv.sample();
				NoiseColumnSampler.VeinType veinType = this.method_38397(d, j);
				if (veinType == null) {
					return blockState;
				} else if (abstractRandom.nextFloat() > 0.7F) {
					return blockState;
				} else if (this.method_38374(lv2.sample(), lv3.sample())) {
					double e = MathHelper.clampedLerpFromProgress(Math.abs(d), 0.4F, 0.6F, 0.1F, 0.3F);
					if ((double)abstractRandom.nextFloat() < e && this.field_34656.sample((double)i, (double)j, (double)k) > -0.3F) {
						return abstractRandom.nextFloat() < 0.02F ? veinType.rawBlock : veinType.ore;
					} else {
						return veinType.stone;
					}
				} else {
					return blockState;
				}
			};
		}
	}

	public int method_38383(int i, int j, TerrainNoisePoint terrainNoisePoint) {
		for (int k = this.field_34682 + this.noiseSizeY; k >= this.field_34682; k--) {
			int l = k * this.verticalNoiseResolution;
			double d = -0.703125;
			double e = this.method_38381(i, l, j, terrainNoisePoint, -0.703125, true);
			if (e > 0.390625) {
				return l;
			}
		}

		return Integer.MAX_VALUE;
	}

	public AquiferSampler method_38389(class_6568 arg, int i, int j, int k, int l, AquiferSampler.class_6565 arg2, boolean bl) {
		if (!bl) {
			return AquiferSampler.seaLevel(arg2);
		} else {
			int m = ChunkSectionPos.getSectionCoord(i);
			int n = ChunkSectionPos.getSectionCoord(j);
			return AquiferSampler.aquifer(
				arg,
				new ChunkPos(m, n),
				this.field_34683,
				this.field_34684,
				this.field_34685,
				this.field_34686,
				this,
				k * this.verticalNoiseResolution,
				l * this.verticalNoiseResolution,
				arg2
			);
		}
	}

	public NoiseSampler method_38372() {
		return this.field_34632;
	}

	@Override
	public MultiNoiseUtil.NoiseValuePoint sample(int i, int j, int k) {
		double d = (double)i + this.method_38377(i, 0, k);
		double e = (double)k + this.method_38377(k, i, 0);
		float f = (float)this.method_38401(d, 0.0, e);
		float g = (float)this.method_38404(d, 0.0, e);
		float h = (float)this.method_38407(d, 0.0, e);
		double l = (double)this.terrainParameters.getOffset(this.terrainParameters.createNoisePoint(f, g, h));
		return this.method_38378(i, j, k, d, e, f, g, h, l);
	}

	public MultiNoiseUtil.NoiseValuePoint method_38378(int i, int j, int k, double d, double e, float f, float g, float h, double l) {
		double m = (double)j + this.method_38377(j, k, i);
		double n = this.getDepth((double)BiomeCoords.toBlock(j)) + l;
		return MultiNoiseUtil.createNoiseValuePoint((float)this.method_38375(d, m, e), (float)this.method_38396(d, m, e), f, g, (float)n, h);
	}

	public TerrainNoisePoint createTerrainNoisePoint(int x, int z, float continentalness, float weirdness, float erosion) {
		if (this.islandNoise != null) {
			double d = (double)(TheEndBiomeSource.getNoiseAt(this.islandNoise, x / 8, z / 8) - 8.0F);
			double e;
			if (d > 0.0) {
				e = 0.001953125;
			} else {
				e = 0.0078125;
			}

			return new TerrainNoisePoint(d, e, 0.0);
		} else {
			VanillaTerrainParameters.NoisePoint noisePoint = this.terrainParameters.createNoisePoint(continentalness, erosion, weirdness);
			return new TerrainNoisePoint(
				(double)this.terrainParameters.getOffset(noisePoint),
				(double)this.terrainParameters.getFactor(noisePoint),
				(double)this.terrainParameters.getPeak(noisePoint)
			);
		}
	}

	public double method_38377(int i, int j, int k) {
		return this.shiftNoise.sample((double)i, (double)j, (double)k) * 4.0;
	}

	public double method_38375(double d, double e, double f) {
		return this.temperatureNoise.sample(d, e, f);
	}

	public double method_38396(double d, double e, double f) {
		return this.humidityNoise.sample(d, e, f);
	}

	public double method_38401(double d, double e, double f) {
		if (SharedConstants.DEBUG_BIOME_SOURCE) {
			if (SharedConstants.method_37896((int)d * 4, (int)f * 4)) {
				return -1.0;
			} else {
				double g = MathHelper.fractionalPart(d / 2048.0) * 2.0 - 1.0;
				return g * g * (double)(g < 0.0 ? -1 : 1);
			}
		} else if (SharedConstants.field_34372) {
			double g = d * 0.005;
			return Math.sin(g + 0.5 * Math.sin(g));
		} else {
			return this.continentalnessNoise.sample(d, e, f);
		}
	}

	public double method_38404(double d, double e, double f) {
		if (SharedConstants.DEBUG_BIOME_SOURCE) {
			if (SharedConstants.method_37896((int)d * 4, (int)f * 4)) {
				return -1.0;
			} else {
				double g = MathHelper.fractionalPart(f / 256.0) * 2.0 - 1.0;
				return g * g * (double)(g < 0.0 ? -1 : 1);
			}
		} else if (SharedConstants.field_34372) {
			double g = f * 0.005;
			return Math.sin(g + 0.5 * Math.sin(g));
		} else {
			return this.erosionNoise.sample(d, e, f);
		}
	}

	public double method_38407(double d, double e, double f) {
		return this.weirdnessNoise.sample(d, e, f);
	}

	private double method_38398(int i, int j, int k) {
		double d = 0.75;
		double e = 0.5;
		double f = 0.37;
		double g = this.field_34647.sample((double)i * 0.75, (double)j * 0.5, (double)k * 0.75) + 0.37;
		int l = -10;
		double h = (double)(j - -10) / 40.0;
		double m = 0.3;
		return g + MathHelper.clampedLerp(0.3, 0.0, h);
	}

	private double method_38402(int i, int j, int k) {
		double d = 0.0;
		double e = 2.0;
		double f = NoiseHelper.lerpFromProgress(this.field_34635, (double)i, (double)j, (double)k, 0.0, 2.0);
		double g = 0.0;
		double h = 1.1;
		double l = NoiseHelper.lerpFromProgress(this.field_34636, (double)i, (double)j, (double)k, 0.0, 1.1);
		l = Math.pow(l, 3.0);
		double m = 25.0;
		double n = 0.3;
		double o = this.field_34634.sample((double)i * 25.0, (double)j * 0.3, (double)k * 25.0);
		o = l * (o * 2.0 - f);
		return o > 0.03 ? o : Double.NEGATIVE_INFINITY;
	}

	private double method_38405(int i, int j, int k) {
		double d = this.field_34633.sample((double)i, (double)(j * 8), (double)k);
		return MathHelper.square(d) * 4.0;
	}

	private double method_38408(int i, int j, int k) {
		double d = this.field_34643.sample((double)(i * 2), (double)j, (double)(k * 2));
		double e = NoiseColumnSampler.CaveScaler.scaleTunnels(d);
		double f = 0.065;
		double g = 0.088;
		double h = NoiseHelper.lerpFromProgress(this.field_34644, (double)i, (double)j, (double)k, 0.065, 0.088);
		double l = method_38393(this.field_34641, (double)i, (double)j, (double)k, e);
		double m = Math.abs(e * l) - h;
		double n = method_38393(this.field_34642, (double)i, (double)j, (double)k, e);
		double o = Math.abs(e * n) - h;
		return method_38395(Math.max(m, o));
	}

	private double method_38410(int i, int j, int k) {
		double d = this.field_34639.sample((double)(i * 2), (double)j, (double)(k * 2));
		double e = NoiseColumnSampler.CaveScaler.scaleCaves(d);
		double f = 0.6;
		double g = 1.3;
		double h = NoiseHelper.lerpFromProgress(this.field_34640, (double)(i * 2), (double)j, (double)(k * 2), 0.6, 1.3);
		double l = method_38393(this.field_34637, (double)i, (double)j, (double)k, e);
		double m = 0.083;
		double n = Math.abs(e * l) - 0.083 * h;
		int o = this.field_34682;
		int p = 8;
		double q = NoiseHelper.lerpFromProgress(this.field_34638, (double)i, 0.0, (double)k, (double)o, 8.0);
		double r = Math.abs(q - (double)j / 8.0) - 1.0 * h;
		r = r * r * r;
		return method_38395(Math.max(r, n));
	}

	private double method_38411(int i, int j, int k) {
		double d = NoiseHelper.lerpFromProgress(this.field_34646, (double)i, (double)j, (double)k, 0.0, 0.1);
		return (0.4 - Math.abs(this.field_34645.sample((double)i, (double)j, (double)k))) * d;
	}

	private static double method_38395(double d) {
		return MathHelper.clamp(d, -1.0, 1.0);
	}

	private static double method_38393(DoublePerlinNoiseSampler doublePerlinNoiseSampler, double d, double e, double f, double g) {
		return doublePerlinNoiseSampler.sample(d / g, e / g, f / g);
	}

	private boolean method_38374(double d, double e) {
		double f = Math.abs(1.0 * d) - 0.08F;
		double g = Math.abs(1.0 * e) - 0.08F;
		return Math.max(f, g) < 0.0;
	}

	@Nullable
	private NoiseColumnSampler.VeinType method_38397(double d, int i) {
		NoiseColumnSampler.VeinType veinType = d > 0.0 ? NoiseColumnSampler.VeinType.COPPER : NoiseColumnSampler.VeinType.IRON;
		int j = veinType.maxY - i;
		int k = i - veinType.minY;
		if (k >= 0 && j >= 0) {
			int l = Math.min(j, k);
			double e = MathHelper.clampedLerpFromProgress((double)l, 0.0, 20.0, -0.2, 0.0);
			return Math.abs(d) + e < 0.4F ? null : veinType;
		} else {
			return null;
		}
	}

	static final class CaveScaler {
		private CaveScaler() {
		}

		static double scaleCaves(double value) {
			if (value < -0.75) {
				return 0.5;
			} else if (value < -0.5) {
				return 0.75;
			} else if (value < 0.5) {
				return 1.0;
			} else {
				return value < 0.75 ? 2.0 : 3.0;
			}
		}

		static double scaleTunnels(double value) {
			if (value < -0.5) {
				return 0.75;
			} else if (value < 0.0) {
				return 1.0;
			} else {
				return value < 0.5 ? 1.5 : 2.0;
			}
		}
	}

	static enum VeinType {
		COPPER(Blocks.COPPER_ORE.getDefaultState(), Blocks.RAW_COPPER_BLOCK.getDefaultState(), Blocks.GRANITE.getDefaultState(), 0, 50),
		IRON(Blocks.DEEPSLATE_IRON_ORE.getDefaultState(), Blocks.RAW_IRON_BLOCK.getDefaultState(), Blocks.TUFF.getDefaultState(), -60, -8);

		final BlockState ore;
		final BlockState rawBlock;
		final BlockState stone;
		final int minY;
		final int maxY;

		private VeinType(BlockState ore, BlockState rawBlock, BlockState stone, int minY, int maxY) {
			this.ore = ore;
			this.rawBlock = rawBlock;
			this.stone = stone;
			this.minY = minY;
			this.maxY = maxY;
		}
	}
}
