package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;

public class UnderwaterCaveCarver extends CaveCarver {
	public UnderwaterCaveCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> function) {
		super(function, 256);
		this.alwaysCarvableBlocks = ImmutableSet.of(
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
			Blocks.SAND,
			Blocks.GRAVEL,
			Blocks.WATER,
			Blocks.LAVA,
			Blocks.OBSIDIAN,
			Blocks.AIR,
			Blocks.CAVE_AIR,
			Blocks.PACKED_ICE
		);
	}

	@Override
	protected boolean isRegionUncarvable(Chunk chunk, int i, int j, int k, int l, int m, int n, int o, int p) {
		return false;
	}

	@Override
	protected boolean carveAtPoint(
		Chunk chunk,
		Function<BlockPos, Biome> function,
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
		return carveAtPoint(this, chunk, bitSet, random, mutable, i, j, k, l, m, n, o, p);
	}

	protected static boolean carveAtPoint(
		Carver<?> carver, Chunk chunk, BitSet bitSet, Random random, BlockPos.Mutable mutable, int i, int j, int k, int l, int m, int n, int o, int p
	) {
		if (o >= i) {
			return false;
		} else {
			int q = n | p << 4 | o << 8;
			if (bitSet.get(q)) {
				return false;
			} else {
				bitSet.set(q);
				mutable.set(l, o, m);
				BlockState blockState = chunk.getBlockState(mutable);
				if (!carver.canAlwaysCarveBlock(blockState)) {
					return false;
				} else if (o == 10) {
					float f = random.nextFloat();
					if ((double)f < 0.25) {
						chunk.setBlockState(mutable, Blocks.MAGMA_BLOCK.getDefaultState(), false);
						chunk.getBlockTickScheduler().schedule(mutable, Blocks.MAGMA_BLOCK, 0);
					} else {
						chunk.setBlockState(mutable, Blocks.OBSIDIAN.getDefaultState(), false);
					}

					return true;
				} else if (o < 10) {
					chunk.setBlockState(mutable, Blocks.LAVA.getDefaultState(), false);
					return false;
				} else {
					boolean bl = false;

					for (Direction direction : Direction.Type.HORIZONTAL) {
						int r = l + direction.getOffsetX();
						int s = m + direction.getOffsetZ();
						if (r >> 4 != j || s >> 4 != k || chunk.getBlockState(mutable.set(r, o, s)).isAir()) {
							chunk.setBlockState(mutable, WATER.getBlockState(), false);
							chunk.getFluidTickScheduler().schedule(mutable, WATER.getFluid(), 0);
							bl = true;
							break;
						}
					}

					mutable.set(l, o, m);
					if (!bl) {
						chunk.setBlockState(mutable, WATER.getBlockState(), false);
						return true;
					} else {
						return true;
					}
				}
			}
		}
	}
}
