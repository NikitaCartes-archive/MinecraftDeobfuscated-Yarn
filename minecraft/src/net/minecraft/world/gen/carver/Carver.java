package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;

public abstract class Carver<C extends CarverConfig> {
	public static final Carver<ProbabilityConfig> CAVE = register("cave", new CaveCarver(ProbabilityConfig::deserialize, 256));
	public static final Carver<ProbabilityConfig> NETHER_CAVE = register("hell_cave", new NetherCaveCarver(ProbabilityConfig::deserialize));
	public static final Carver<ProbabilityConfig> RAVINE = register("canyon", new RavineCarver(ProbabilityConfig::deserialize));
	public static final Carver<ProbabilityConfig> UNDERWATER_RAVINE = register("underwater_canyon", new UnderwaterRavineCarver(ProbabilityConfig::deserialize));
	public static final Carver<ProbabilityConfig> UNDERWATER_CAVE = register("underwater_cave", new UnderwaterCaveCarver(ProbabilityConfig::deserialize));
	protected static final BlockState AIR = Blocks.field_10124.getDefaultState();
	protected static final BlockState CAVE_AIR = Blocks.field_10543.getDefaultState();
	protected static final FluidState WATER = Fluids.WATER.getDefaultState();
	protected static final FluidState LAVA = Fluids.LAVA.getDefaultState();
	protected Set<Block> field_13302 = ImmutableSet.of(
		Blocks.field_10340,
		Blocks.field_10474,
		Blocks.field_10508,
		Blocks.field_10115,
		Blocks.field_10566,
		Blocks.field_10253,
		Blocks.field_10520,
		Blocks.field_10219,
		Blocks.field_10415,
		Blocks.field_10611,
		Blocks.field_10184,
		Blocks.field_10015,
		Blocks.field_10325,
		Blocks.field_10143,
		Blocks.field_10014,
		Blocks.field_10444,
		Blocks.field_10349,
		Blocks.field_10590,
		Blocks.field_10235,
		Blocks.field_10570,
		Blocks.field_10409,
		Blocks.field_10123,
		Blocks.field_10526,
		Blocks.field_10328,
		Blocks.field_10626,
		Blocks.field_9979,
		Blocks.field_10344,
		Blocks.field_10402,
		Blocks.field_10477,
		Blocks.field_10225
	);
	protected Set<Fluid> field_13298 = ImmutableSet.of(Fluids.WATER);
	private final Function<Dynamic<?>, ? extends C> field_13299;
	protected final int field_16653;

	private static <C extends CarverConfig, F extends Carver<C>> F register(String string, F carver) {
		return Registry.register(Registry.CARVER, string, carver);
	}

	public Carver(Function<Dynamic<?>, ? extends C> function, int i) {
		this.field_13299 = function;
		this.field_16653 = i;
	}

	public int method_12710() {
		return 4;
	}

	protected boolean method_16580(Chunk chunk, long l, int i, int j, int k, double d, double e, double f, double g, double h, BitSet bitSet) {
		Random random = new Random(l + (long)j + (long)k);
		double m = (double)(j * 16 + 8);
		double n = (double)(k * 16 + 8);
		if (!(d < m - 16.0 - g * 2.0) && !(f < n - 16.0 - g * 2.0) && !(d > m + 16.0 + g * 2.0) && !(f > n + 16.0 + g * 2.0)) {
			int o = Math.max(MathHelper.floor(d - g) - j * 16 - 1, 0);
			int p = Math.min(MathHelper.floor(d + g) - j * 16 + 1, 16);
			int q = Math.max(MathHelper.floor(e - h) - 1, 1);
			int r = Math.min(MathHelper.floor(e + h) + 1, this.field_16653 - 8);
			int s = Math.max(MathHelper.floor(f - g) - k * 16 - 1, 0);
			int t = Math.min(MathHelper.floor(f + g) - k * 16 + 1, 16);
			if (this.method_12711(chunk, j, k, o, p, q, r, s, t)) {
				return false;
			} else {
				boolean bl = false;
				BlockPos.Mutable mutable = new BlockPos.Mutable();
				BlockPos.Mutable mutable2 = new BlockPos.Mutable();
				BlockPos.Mutable mutable3 = new BlockPos.Mutable();

				for (int u = o; u < p; u++) {
					int v = u + j * 16;
					double w = ((double)v + 0.5 - d) / g;

					for (int x = s; x < t; x++) {
						int y = x + k * 16;
						double z = ((double)y + 0.5 - f) / g;
						if (!(w * w + z * z >= 1.0)) {
							AtomicBoolean atomicBoolean = new AtomicBoolean(false);

							for (int aa = r; aa > q; aa--) {
								double ab = ((double)aa - 0.5 - e) / h;
								if (!this.method_16582(w, ab, z, aa)) {
									bl |= this.method_16581(chunk, bitSet, random, mutable, mutable2, mutable3, i, j, k, v, y, u, aa, x, atomicBoolean);
								}
							}
						}
					}
				}

				return bl;
			}
		} else {
			return false;
		}
	}

	protected boolean method_16581(
		Chunk chunk,
		BitSet bitSet,
		Random random,
		BlockPos.Mutable mutable,
		BlockPos.Mutable mutable2,
		BlockPos.Mutable mutable3,
		int i,
		int j,
		int k,
		int l,
		int m,
		int n,
		int o,
		int p,
		AtomicBoolean atomicBoolean
	) {
		int q = n | p << 4 | o << 8;
		if (bitSet.get(q)) {
			return false;
		} else {
			bitSet.set(q);
			mutable.set(l, o, m);
			BlockState blockState = chunk.getBlockState(mutable);
			BlockState blockState2 = chunk.getBlockState(mutable2.set(mutable).setOffset(Direction.UP));
			if (blockState.getBlock() == Blocks.field_10219 || blockState.getBlock() == Blocks.field_10402) {
				atomicBoolean.set(true);
			}

			if (!this.method_12703(blockState, blockState2)) {
				return false;
			} else {
				if (o < 11) {
					chunk.setBlockState(mutable, LAVA.getBlockState(), false);
				} else {
					chunk.setBlockState(mutable, CAVE_AIR, false);
					if (atomicBoolean.get()) {
						mutable3.set(mutable).setOffset(Direction.DOWN);
						if (chunk.getBlockState(mutable3).getBlock() == Blocks.field_10566) {
							chunk.setBlockState(mutable3, chunk.getBiome(mutable).getSurfaceConfig().getTopMaterial(), false);
						}
					}
				}

				return true;
			}
		}
	}

	public abstract boolean carve(Chunk chunk, Random random, int i, int j, int k, int l, int m, BitSet bitSet, C carverConfig);

	public abstract boolean shouldCarve(Random random, int i, int j, C carverConfig);

	protected boolean method_12709(BlockState blockState) {
		return this.field_13302.contains(blockState.getBlock());
	}

	protected boolean method_12703(BlockState blockState, BlockState blockState2) {
		Block block = blockState.getBlock();
		return this.method_12709(blockState)
			|| (block == Blocks.field_10102 || block == Blocks.field_10255) && !blockState2.getFluidState().matches(FluidTags.field_15517);
	}

	protected boolean method_12711(Chunk chunk, int i, int j, int k, int l, int m, int n, int o, int p) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int q = k; q < l; q++) {
			for (int r = o; r < p; r++) {
				for (int s = m - 1; s <= n + 1; s++) {
					if (this.field_13298.contains(chunk.getFluidState(mutable.set(q + i * 16, s, r + j * 16)).getFluid())) {
						return true;
					}

					if (s != n + 1 && !this.method_12706(k, l, o, p, q, r)) {
						s = n;
					}
				}
			}
		}

		return false;
	}

	private boolean method_12706(int i, int j, int k, int l, int m, int n) {
		return m == i || m == j - 1 || n == k || n == l - 1;
	}

	protected boolean method_12707(int i, int j, double d, double e, int k, int l, float f) {
		double g = (double)(i * 16 + 8);
		double h = (double)(j * 16 + 8);
		double m = d - g;
		double n = e - h;
		double o = (double)(l - k);
		double p = (double)(f + 2.0F + 16.0F);
		return m * m + n * n - o * o <= p * p;
	}

	protected abstract boolean method_16582(double d, double e, double f, int i);
}
