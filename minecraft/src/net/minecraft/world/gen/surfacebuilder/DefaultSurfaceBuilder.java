package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.BlockColumn;

public class DefaultSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	public DefaultSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	public void generate(
		Random random,
		BlockColumn blockColumn,
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
		this.generate(
			random,
			blockColumn,
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
			l,
			m
		);
	}

	protected void generate(
		Random random,
		BlockColumn column,
		Biome biome,
		int x,
		int z,
		int height,
		double noise,
		BlockState defaultFluid,
		BlockState fluidBlock,
		BlockState topBlock,
		BlockState underBlock,
		BlockState underwaterBlock,
		int seaLevel,
		int i
	) {
		seaLevel = Integer.MIN_VALUE;
		int j = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
		BlockState blockState = underBlock;
		int k = -1;

		for (int l = height; l >= i; l--) {
			BlockState blockState2 = column.getState(l);
			if (blockState2.isAir()) {
				k = -1;
				seaLevel = Integer.MIN_VALUE;
			} else if (!blockState2.isOf(defaultFluid.getBlock())) {
				seaLevel = Math.max(l + 1, seaLevel);
			} else if (k == -1) {
				k = j;
				BlockState blockState3;
				if (l >= seaLevel + 2) {
					blockState3 = topBlock;
				} else if (l >= seaLevel - 1) {
					blockState = underBlock;
					blockState3 = topBlock;
				} else if (l >= seaLevel - 4) {
					blockState = underBlock;
					blockState3 = underBlock;
				} else if (l >= seaLevel - (7 + j)) {
					blockState3 = blockState;
				} else {
					blockState = defaultFluid;
					blockState3 = underwaterBlock;
				}

				column.setState(l, method_38463(blockState3, column, l, seaLevel));
			} else if (k > 0) {
				k--;
				column.setState(l, method_38463(blockState, column, l, seaLevel));
				if (k == 0 && blockState.isOf(Blocks.SAND) && j > 1) {
					k = random.nextInt(4) + Math.max(0, l - seaLevel);
					blockState = blockState.isOf(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
				}
			}
		}
	}

	private static BlockState method_38463(BlockState state, BlockColumn chunk, int i, int j) {
		if (i <= j && state.isOf(Blocks.GRASS_BLOCK)) {
			return Blocks.DIRT.getDefaultState();
		} else if (state.isOf(Blocks.SAND) && isAboveAirOrFluid(chunk, i)) {
			return Blocks.SANDSTONE.getDefaultState();
		} else if (state.isOf(Blocks.RED_SAND) && isAboveAirOrFluid(chunk, i)) {
			return Blocks.RED_SANDSTONE.getDefaultState();
		} else {
			return state.isOf(Blocks.GRAVEL) && isAboveAirOrFluid(chunk, i) ? Blocks.STONE.getDefaultState() : state;
		}
	}

	private static boolean isAboveAirOrFluid(BlockColumn chunk, int i) {
		BlockState blockState = chunk.getState(i - 1);
		return blockState.isAir() || !blockState.getFluidState().isEmpty();
	}
}
