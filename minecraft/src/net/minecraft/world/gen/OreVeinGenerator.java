package net.minecraft.world.gen;

import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;

public class OreVeinGenerator {
	private static final float field_33588 = 1.0F;
	private static final float ORE_PLACEMENT_NOISE_SCALE = 4.0F;
	private static final float ORE_PLACEMENT_NOISE_THRESHOLD = 0.08F;
	private static final float FREQUENCY_NOISE_THRESHOLD = 0.5F;
	private static final double ORE_FREQUENCY_NOISE_SCALE = 1.5;
	private static final int field_33695 = 20;
	private static final double field_33696 = 0.2;
	private static final float GENERATION_CHANCE = 0.7F;
	private static final float ORE_CHANCE_START = 0.1F;
	private static final float ORE_CHANCE_END = 0.3F;
	private static final float MAX_ORE_CHANCE_NOISE_THRESHOLD = 0.6F;
	private static final float RAW_ORE_CHANCE = 0.02F;
	private static final float ORE_CHANCE_THRESHOLD = -0.3F;
	private final int maxY;
	private final int minY;
	private final BlockState defaultState;
	private final DoublePerlinNoiseSampler oreFrequencyNoiseSampler;
	private final DoublePerlinNoiseSampler firstOrePlacementNoiseSampler;
	private final DoublePerlinNoiseSampler secondOrePlacementNoiseSampler;
	private final DoublePerlinNoiseSampler oreChanceNoiseSampler;
	private final int horizontalNoiseResolution;
	private final int verticalNoiseResolution;

	public OreVeinGenerator(long seed, BlockState defaultState, int horizontalNoiseResolution, int verticalNoiseResolution, int minY) {
		Random random = new Random(seed);
		this.defaultState = defaultState;
		this.oreFrequencyNoiseSampler = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.firstOrePlacementNoiseSampler = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0);
		this.secondOrePlacementNoiseSampler = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0);
		this.oreChanceNoiseSampler = DoublePerlinNoiseSampler.create(new SimpleRandom(0L), -5, 1.0);
		this.horizontalNoiseResolution = horizontalNoiseResolution;
		this.verticalNoiseResolution = verticalNoiseResolution;
		this.maxY = Stream.of(OreVeinGenerator.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(minY);
		this.minY = Stream.of(OreVeinGenerator.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(minY);
	}

	public void sampleOreFrequencyNoise(double[] buffer, int x, int z, int minY, int noiseSizeY) {
		this.sample(buffer, x, z, this.oreFrequencyNoiseSampler, 1.5, minY, noiseSizeY);
	}

	public void sampleFirstOrePlacementNoise(double[] buffer, int x, int z, int minY, int noiseSizeY) {
		this.sample(buffer, x, z, this.firstOrePlacementNoiseSampler, 4.0, minY, noiseSizeY);
	}

	public void sampleSecondOrePlacementNoise(double[] buffer, int x, int z, int minY, int noiseSizeY) {
		this.sample(buffer, x, z, this.secondOrePlacementNoiseSampler, 4.0, minY, noiseSizeY);
	}

	public void sample(double[] buffer, int x, int z, DoublePerlinNoiseSampler sampler, double scale, int minY, int noiseSizeY) {
		for (int i = 0; i < noiseSizeY; i++) {
			int j = i + minY;
			int k = x * this.horizontalNoiseResolution;
			int l = j * this.verticalNoiseResolution;
			int m = z * this.horizontalNoiseResolution;
			double d;
			if (l >= this.minY && l <= this.maxY) {
				d = sampler.sample((double)k * scale, (double)l * scale, (double)m * scale);
			} else {
				d = 0.0;
			}

			buffer[i] = d;
		}
	}

	public BlockState sample(WorldGenRandom random, int x, int y, int z, double oreFrequencyNoise, double firstOrePlacementNoise, double secondOrePlacementNoise) {
		BlockState blockState = this.defaultState;
		OreVeinGenerator.VeinType veinType = this.getVeinType(oreFrequencyNoise, y);
		if (veinType == null) {
			return blockState;
		} else if (random.nextFloat() > 0.7F) {
			return blockState;
		} else if (this.shouldPlaceOreBlock(firstOrePlacementNoise, secondOrePlacementNoise)) {
			double d = MathHelper.clampedLerpFromProgress(Math.abs(oreFrequencyNoise), 0.5, 0.6F, 0.1F, 0.3F);
			if ((double)random.nextFloat() < d && this.oreChanceNoiseSampler.sample((double)x, (double)y, (double)z) > -0.3F) {
				return random.nextFloat() < 0.02F ? veinType.rawBlock : veinType.ore;
			} else {
				return veinType.stone;
			}
		} else {
			return blockState;
		}
	}

	private boolean shouldPlaceOreBlock(double firstOrePlacementNoise, double secondOrePlacementNoise) {
		double d = Math.abs(1.0 * firstOrePlacementNoise) - 0.08F;
		double e = Math.abs(1.0 * secondOrePlacementNoise) - 0.08F;
		return Math.max(d, e) < 0.0;
	}

	@Nullable
	private OreVeinGenerator.VeinType getVeinType(double oreFrequencyNoise, int y) {
		OreVeinGenerator.VeinType veinType = oreFrequencyNoise > 0.0 ? OreVeinGenerator.VeinType.COPPER : OreVeinGenerator.VeinType.IRON;
		int i = veinType.maxY - y;
		int j = y - veinType.minY;
		if (j >= 0 && i >= 0) {
			int k = Math.min(i, j);
			double d = MathHelper.clampedLerpFromProgress((double)k, 0.0, 20.0, -0.2, 0.0);
			return Math.abs(oreFrequencyNoise) + d < 0.5 ? null : veinType;
		} else {
			return null;
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
