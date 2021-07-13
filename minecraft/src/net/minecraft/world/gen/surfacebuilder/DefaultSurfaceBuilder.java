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
		int i,
		int j,
		int k,
		double d,
		BlockState blockState,
		BlockState blockState2,
		BlockState fluidBlock,
		BlockState topBlock,
		BlockState underBlock,
		int l
	) {
		int m = Integer.MIN_VALUE;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int n = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		BlockState blockState3 = topBlock;
		int o = -1;

		for (int p = k; p >= l; p--) {
			mutable.set(i, p, j);
			BlockState blockState4 = chunk.getBlockState(mutable);
			if (blockState4.isAir()) {
				o = -1;
				m = Integer.MIN_VALUE;
			} else if (!blockState4.isOf(blockState.getBlock())) {
				m = Math.max(p, m);
			} else if (o == -1) {
				o = n;
				BlockState blockState5;
				if (p >= m + 2) {
					blockState5 = fluidBlock;
				} else if (p >= m - 1) {
					blockState3 = topBlock;
					blockState5 = fluidBlock;
				} else if (p >= m - 4) {
					blockState3 = topBlock;
					blockState5 = topBlock;
				} else if (p >= m - (7 + n)) {
					blockState5 = blockState3;
				} else {
					blockState3 = blockState;
					blockState5 = underBlock;
				}

				chunk.setBlockState(mutable, blockState5, false);
			} else if (o > 0) {
				o--;
				chunk.setBlockState(mutable, blockState3, false);
				if (o == 0 && blockState3.isOf(Blocks.SAND) && n > 1) {
					o = random.nextInt(4) + Math.max(0, p - m);
					blockState3 = blockState3.isOf(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
				}
			}
		}
	}
}
