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
	private static final float field_33589 = 4.0F;
	private static final float field_33590 = 0.08F;
	private static final float field_33591 = 0.4F;
	private static final float field_33592 = 0.7F;
	private static final float field_33662 = 0.1F;
	private static final float field_33663 = 0.3F;
	private static final float field_33664 = 0.6F;
	private static final float field_33665 = 0.01F;
	private static final float field_33666 = -0.3F;
	private final int field_33595;
	private final int field_33596;
	private final BlockState defaultState;
	private final DoublePerlinNoiseSampler field_33598;
	private final DoublePerlinNoiseSampler field_33599;
	private final DoublePerlinNoiseSampler field_33600;
	private final DoublePerlinNoiseSampler field_33667;
	private final int field_33601;
	private final int field_33602;

	public OreVeinGenerator(long seed, BlockState defaultState, int i, int j, int k) {
		Random random = new Random(seed);
		this.defaultState = defaultState;
		this.field_33598 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, 1.0);
		this.field_33599 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0);
		this.field_33600 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, 1.0);
		this.field_33667 = DoublePerlinNoiseSampler.create(new SimpleRandom(0L), -5, 1.0);
		this.field_33601 = i;
		this.field_33602 = j;
		this.field_33595 = Stream.of(OreVeinGenerator.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(k);
		this.field_33596 = Stream.of(OreVeinGenerator.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(k);
	}

	public void method_36401(double[] ds, int i, int j, int k, int l) {
		this.method_36402(ds, i, j, this.field_33598, 1.0, k, l);
	}

	public void method_36404(double[] ds, int i, int j, int k, int l) {
		this.method_36402(ds, i, j, this.field_33599, 4.0, k, l);
	}

	public void method_36405(double[] ds, int i, int j, int k, int l) {
		this.method_36402(ds, i, j, this.field_33600, 4.0, k, l);
	}

	public void method_36402(double[] ds, int i, int j, DoublePerlinNoiseSampler doublePerlinNoiseSampler, double d, int k, int l) {
		for (int m = 0; m < l; m++) {
			int n = m + k;
			int o = i * this.field_33601;
			int p = n * this.field_33602;
			int q = j * this.field_33601;
			double e;
			if (p >= this.field_33596 && p <= this.field_33595) {
				e = doublePerlinNoiseSampler.sample((double)o * d, (double)p * d, (double)q * d);
			} else {
				e = 0.0;
			}

			ds[m] = e;
		}
	}

	public BlockState sample(WorldGenRandom random, int x, int y, int z, double d, double e, double f) {
		BlockState blockState = this.defaultState;
		OreVeinGenerator.VeinType veinType = this.getVeinType(d);
		if (veinType != null && y >= veinType.minY && y <= veinType.maxY) {
			if (random.nextFloat() > 0.7F) {
				return blockState;
			} else if (this.method_36398(e, f)) {
				double g = MathHelper.clampedLerpFromProgress(Math.abs(d), 0.4F, 0.6F, 0.1F, 0.3F);
				if ((double)random.nextFloat() < g && this.field_33667.sample((double)x, (double)y, (double)z) > -0.3F) {
					return random.nextFloat() < 0.01F ? veinType.rawBlock : veinType.ore;
				} else {
					return veinType.stone;
				}
			} else {
				return blockState;
			}
		} else {
			return blockState;
		}
	}

	private boolean method_36398(double d, double e) {
		double f = Math.abs(1.0 * d) - 0.08F;
		double g = Math.abs(1.0 * e) - 0.08F;
		return Math.max(f, g) < 0.0;
	}

	@Nullable
	private OreVeinGenerator.VeinType getVeinType(double d) {
		if (Math.abs(d) < 0.4F) {
			return null;
		} else {
			return d > 0.0 ? OreVeinGenerator.VeinType.COPPER : OreVeinGenerator.VeinType.IRON;
		}
	}

	static enum VeinType {
		COPPER(Blocks.COPPER_ORE.getDefaultState(), Blocks.RAW_COPPER_BLOCK.getDefaultState(), Blocks.GRANITE.getDefaultState(), 0, 50),
		IRON(Blocks.DEEPSLATE_IRON_ORE.getDefaultState(), Blocks.RAW_IRON_BLOCK.getDefaultState(), Blocks.TUFF.getDefaultState(), -60, -8);

		private final BlockState ore;
		private final BlockState rawBlock;
		private final BlockState stone;
		private final int minY;
		private final int maxY;

		private VeinType(BlockState ore, BlockState rawBlock, BlockState stone, int minY, int maxY) {
			this.ore = ore;
			this.rawBlock = rawBlock;
			this.stone = stone;
			this.minY = minY;
			this.maxY = maxY;
		}
	}
}
