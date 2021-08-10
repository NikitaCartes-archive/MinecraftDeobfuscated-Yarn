package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class DefaultSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	public DefaultSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	public void generate(
		Random random,
		Chunk chunk,
		Biome biome,
		int i,
		int j,
		int k,
		double d,
		BlockState blockState,
		BlockState blockState2,
		int l,
		int m,
		long n,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
		generate(
			random,
			chunk,
			biome,
			i,
			j,
			k,
			d,
			blockState,
			blockState2,
			ternarySurfaceConfig.getTopMaterial(),
			ternarySurfaceConfig.getUnderMaterial(),
			ternarySurfaceConfig.getUnderwaterMaterial(),
			m
		);
	}

	protected static void generate(
		Random random,
		Chunk chunk,
		Biome biome,
		int x,
		int z,
		int height,
		double noise,
		BlockState defaultBlock,
		BlockState defaultFluid,
		BlockState fluidBlock,
		BlockState topBlock,
		BlockState underBlock,
		int i
	) {
		int j = Integer.MIN_VALUE;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int k = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
		BlockState blockState = topBlock;
		int l = -1;

		for (int m = height; m >= i; m--) {
			mutable.set(x, m, z);
			BlockState blockState2 = chunk.getBlockState(mutable);
			if (blockState2.isAir()) {
				l = -1;
				j = Integer.MIN_VALUE;
			} else if (!blockState2.isOf(defaultBlock.getBlock())) {
				j = Math.max(m, j);
			} else if (l == -1) {
				l = k;
				BlockState blockState3;
				if (m >= j + 2) {
					blockState3 = fluidBlock;
				} else if (m >= j - 1) {
					blockState = topBlock;
					blockState3 = fluidBlock;
				} else if (m >= j - 4) {
					blockState = topBlock;
					blockState3 = topBlock;
				} else if (m >= j - (7 + k)) {
					blockState3 = blockState;
				} else {
					blockState = defaultBlock;
					blockState3 = underBlock;
				}

				chunk.setBlockState(mutable, method_37850(blockState3, chunk, mutable, j), false);
			} else if (l > 0) {
				l--;
				chunk.setBlockState(mutable, method_37850(blockState, chunk, mutable, j), false);
				if (l == 0 && blockState.isOf(Blocks.SAND) && k > 1) {
					l = random.nextInt(4) + Math.max(0, m - j);
					blockState = blockState.isOf(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
				}
			}
		}
	}

	private static BlockState method_37850(BlockState blockState, Chunk chunk, BlockPos blockPos, int i) {
		if (blockPos.getY() <= i && blockState.isOf(Blocks.GRASS_BLOCK)) {
			return Blocks.DIRT.getDefaultState();
		} else if (blockState.isOf(Blocks.SAND) && isPosAboveAirOrFluid(chunk, blockPos)) {
			return Blocks.SANDSTONE.getDefaultState();
		} else if (blockState.isOf(Blocks.RED_SAND) && isPosAboveAirOrFluid(chunk, blockPos)) {
			return Blocks.RED_SANDSTONE.getDefaultState();
		} else {
			return blockState.isOf(Blocks.GRAVEL) && isPosAboveAirOrFluid(chunk, blockPos) ? Blocks.STONE.getDefaultState() : blockState;
		}
	}

	private static boolean isPosAboveAirOrFluid(Chunk chunk, BlockPos pos) {
		BlockState blockState = chunk.getBlockState(pos.down());
		return blockState.isAir() || !blockState.getFluidState().isEmpty();
	}
}
