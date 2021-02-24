package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.class_5869;
import net.minecraft.class_5870;
import net.minecraft.class_5871;
import net.minecraft.class_5873;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class Carver<C extends class_5871> {
	public static final Carver<class_5871> CAVE = register("cave", new CaveCarver(class_5871.field_29054));
	public static final Carver<class_5871> NETHER_CAVE = register("nether_cave", new NetherCaveCarver(class_5871.field_29054));
	public static final Carver<class_5869> CANYON = register("canyon", new class_5870(class_5869.field_29041));
	protected static final BlockState AIR = Blocks.AIR.getDefaultState();
	protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
	protected static final FluidState WATER = Fluids.WATER.getDefaultState();
	protected static final FluidState LAVA = Fluids.LAVA.getDefaultState();
	protected Set<Block> alwaysCarvableBlocks = ImmutableSet.of(
		Blocks.STONE,
		Blocks.GRANITE,
		Blocks.DIORITE,
		Blocks.ANDESITE,
		Blocks.DIRT,
		Blocks.COARSE_DIRT,
		Blocks.PODZOL,
		Blocks.GRASS_BLOCK,
		Blocks.TERRACOTTA,
		Blocks.WHITE_TERRACOTTA,
		Blocks.ORANGE_TERRACOTTA,
		Blocks.MAGENTA_TERRACOTTA,
		Blocks.LIGHT_BLUE_TERRACOTTA,
		Blocks.YELLOW_TERRACOTTA,
		Blocks.LIME_TERRACOTTA,
		Blocks.PINK_TERRACOTTA,
		Blocks.GRAY_TERRACOTTA,
		Blocks.LIGHT_GRAY_TERRACOTTA,
		Blocks.CYAN_TERRACOTTA,
		Blocks.PURPLE_TERRACOTTA,
		Blocks.BLUE_TERRACOTTA,
		Blocks.BROWN_TERRACOTTA,
		Blocks.GREEN_TERRACOTTA,
		Blocks.RED_TERRACOTTA,
		Blocks.BLACK_TERRACOTTA,
		Blocks.SANDSTONE,
		Blocks.RED_SANDSTONE,
		Blocks.MYCELIUM,
		Blocks.SNOW,
		Blocks.PACKED_ICE,
		Blocks.DEEPSLATE
	);
	protected Set<Fluid> carvableFluids = ImmutableSet.of(Fluids.WATER);
	private final Codec<ConfiguredCarver<C>> codec;

	private static <C extends class_5871, F extends Carver<C>> F register(String name, F carver) {
		return Registry.register(Registry.CARVER, name, carver);
	}

	public Carver(Codec<C> configCodec) {
		this.codec = configCodec.fieldOf("config").<ConfiguredCarver<C>>xmap(this::configure, ConfiguredCarver::getConfig).codec();
	}

	public ConfiguredCarver<C> configure(C config) {
		return new ConfiguredCarver<>(this, config);
	}

	public Codec<ConfiguredCarver<C>> getCodec() {
		return this.codec;
	}

	public int getBranchFactor() {
		return 4;
	}

	protected boolean method_33978(
		class_5873 arg,
		C arg2,
		Chunk chunk,
		Function<BlockPos, Biome> function,
		long l,
		int i,
		double d,
		double e,
		double f,
		double g,
		double h,
		BitSet bitSet,
		Carver.class_5874 arg3
	) {
		ChunkPos chunkPos = chunk.getPos();
		int j = chunkPos.x;
		int k = chunkPos.z;
		Random random = new Random(l + (long)j + (long)k);
		double m = (double)chunkPos.method_33940();
		double n = (double)chunkPos.method_33942();
		double o = 16.0 + g * 2.0;
		if (!(Math.abs(d - m) > o) && !(Math.abs(f - n) > o)) {
			int p = chunkPos.getStartX();
			int q = chunkPos.getStartZ();
			int r = Math.max(MathHelper.floor(d - g) - p - 1, 0);
			int s = Math.min(MathHelper.floor(d + g) - p, 15);
			int t = Math.max(MathHelper.floor(e - h) - 1, arg.getMinY() + 1);
			int u = Math.min(MathHelper.floor(e + h) + 1, arg.getMinY() + arg.getMaxY() - 8);
			int v = Math.max(MathHelper.floor(f - g) - q - 1, 0);
			int w = Math.min(MathHelper.floor(f + g) - q, 15);
			if (this.method_33977(chunk, r, s, t, u, v, w)) {
				return false;
			} else {
				boolean bl = false;
				BlockPos.Mutable mutable = new BlockPos.Mutable();
				BlockPos.Mutable mutable2 = new BlockPos.Mutable();

				for (int x = r; x <= s; x++) {
					int y = chunkPos.method_33939(x);
					double z = ((double)y + 0.5 - d) / g;

					for (int aa = v; aa <= w; aa++) {
						int ab = chunkPos.method_33941(aa);
						double ac = ((double)ab + 0.5 - f) / g;
						if (!(z * z + ac * ac >= 1.0)) {
							MutableBoolean mutableBoolean = new MutableBoolean(false);

							for (int ad = u; ad > t; ad--) {
								double ae = ((double)ad - 0.5 - e) / h;
								if (!arg3.shouldSkip(arg, z, ae, ac, ad)) {
									int af = ad - arg.getMinY();
									int ag = x | aa << 4 | af << 8;
									if (!bitSet.get(ag) || method_33980(arg2)) {
										bitSet.set(ag);
										mutable.set(y, ad, ab);
										bl |= this.carveAtPoint(arg, arg2, chunk, function, bitSet, random, mutable, mutable2, i, mutableBoolean);
									}
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

	protected boolean carveAtPoint(
		class_5873 arg,
		C arg2,
		Chunk chunk,
		Function<BlockPos, Biome> function,
		BitSet bitSet,
		Random random,
		BlockPos.Mutable mutable,
		BlockPos.Mutable mutable2,
		int mainChunkX,
		MutableBoolean mutableBoolean
	) {
		BlockState blockState = chunk.getBlockState(mutable);
		BlockState blockState2 = chunk.getBlockState(mutable2.set(mutable, Direction.UP));
		if (blockState.isOf(Blocks.GRASS_BLOCK) || blockState.isOf(Blocks.MYCELIUM)) {
			mutableBoolean.setTrue();
		}

		if (!this.canCarveBlock(blockState, blockState2) && !method_33980(arg2)) {
			return false;
		} else {
			if (mutable.getY() < arg.getMinY() + 11 && !method_33980(arg2)) {
				chunk.setBlockState(mutable, LAVA.getBlockState(), false);
			} else {
				chunk.setBlockState(mutable, method_33979(arg2), false);
				if (mutableBoolean.isTrue()) {
					mutable2.set(mutable, Direction.DOWN);
					if (chunk.getBlockState(mutable2).isOf(Blocks.DIRT)) {
						chunk.setBlockState(mutable2, ((Biome)function.apply(mutable)).getGenerationSettings().getSurfaceConfig().getTopMaterial(), false);
					}
				}
			}

			return true;
		}
	}

	private static BlockState method_33979(class_5871 arg) {
		return method_33980(arg) ? arg.method_33969().method_33973() : CAVE_AIR;
	}

	public abstract boolean carve(
		class_5873 arg, C arg2, Chunk chunk, Function<BlockPos, Biome> function, Random random, int chunkZ, ChunkPos chunkPos, BitSet bitSet
	);

	public abstract boolean shouldCarve(C arg, Random random);

	protected boolean canAlwaysCarveBlock(BlockState state) {
		return this.alwaysCarvableBlocks.contains(state.getBlock());
	}

	protected boolean canCarveBlock(BlockState state, BlockState stateAbove) {
		return this.canAlwaysCarveBlock(state) || (state.isOf(Blocks.SAND) || state.isOf(Blocks.GRAVEL)) && !stateAbove.getFluidState().isIn(FluidTags.WATER);
	}

	protected boolean method_33977(Chunk chunk, int i, int j, int k, int l, int m, int n) {
		ChunkPos chunkPos = chunk.getPos();
		int o = chunkPos.getStartX();
		int p = chunkPos.getStartZ();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int q = i; q <= j; q++) {
			for (int r = m; r <= n; r++) {
				for (int s = k - 1; s <= l + 1; s++) {
					mutable.set(o + q, s, p + r);
					if (this.carvableFluids.contains(chunk.getFluidState(mutable).getFluid())) {
						return true;
					}

					if (s != l + 1 && !isOnBoundary(q, r, i, j, m, n)) {
						s = l;
					}
				}
			}
		}

		return false;
	}

	private static boolean isOnBoundary(int i, int j, int k, int l, int m, int n) {
		return i == k || i == l || j == m || j == n;
	}

	protected static boolean method_33976(ChunkPos chunkPos, double d, double e, int i, int j, float f) {
		double g = (double)chunkPos.method_33940();
		double h = (double)chunkPos.method_33942();
		double k = d - g;
		double l = e - h;
		double m = (double)(j - i);
		double n = (double)(f + 2.0F + 16.0F);
		return k * k + l * l - m * m <= n * n;
	}

	private static boolean method_33980(class_5871 arg) {
		return arg.method_33969().method_33970();
	}

	public interface class_5874 {
		boolean shouldSkip(class_5873 arg, double d, double e, double f, int i);
	}
}
