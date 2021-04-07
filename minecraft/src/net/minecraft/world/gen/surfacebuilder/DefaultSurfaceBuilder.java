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
		this.generate(
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
			l,
			m
		);
	}

	protected void generate(
		Random random,
		Chunk chunk,
		Biome biome,
		int x,
		int z,
		int height,
		double noise,
		BlockState defaultBlock,
		BlockState fluidBlock,
		BlockState topBlock,
		BlockState underBlock,
		BlockState underwaterBlock,
		int seaLevel,
		int i
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int j = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
		if (j == 0) {
			boolean bl = false;

			for (int k = height; k >= i; k--) {
				mutable.set(x, k, z);
				BlockState blockState = chunk.getBlockState(mutable);
				if (blockState.isAir()) {
					bl = false;
				} else if (blockState.isOf(defaultBlock.getBlock())) {
					if (!bl) {
						BlockState blockState2;
						if (k >= seaLevel) {
							blockState2 = Blocks.AIR.getDefaultState();
						} else if (k == seaLevel - 1) {
							blockState2 = biome.getTemperature(mutable) < 0.15F ? Blocks.ICE.getDefaultState() : fluidBlock;
						} else if (k >= seaLevel - (7 + j)) {
							blockState2 = defaultBlock;
						} else {
							blockState2 = underwaterBlock;
						}

						chunk.setBlockState(mutable, blockState2, false);
					}

					bl = true;
				}
			}
		} else {
			BlockState blockState3 = underBlock;
			int kx = -1;

			for (int l = height; l >= i; l--) {
				mutable.set(x, l, z);
				BlockState blockState2 = chunk.getBlockState(mutable);
				if (blockState2.isAir()) {
					kx = -1;
				} else if (blockState2.isOf(defaultBlock.getBlock())) {
					if (kx == -1) {
						kx = j;
						BlockState blockState4;
						if (l >= seaLevel + 2) {
							blockState4 = topBlock;
						} else if (l >= seaLevel - 1) {
							blockState3 = underBlock;
							blockState4 = topBlock;
						} else if (l >= seaLevel - 4) {
							blockState3 = underBlock;
							blockState4 = underBlock;
						} else if (l >= seaLevel - (7 + j)) {
							blockState4 = blockState3;
						} else {
							blockState3 = defaultBlock;
							blockState4 = underwaterBlock;
						}

						chunk.setBlockState(mutable, blockState4, false);
					} else if (kx > 0) {
						kx--;
						chunk.setBlockState(mutable, blockState3, false);
						if (kx == 0 && blockState3.isOf(Blocks.SAND) && j > 1) {
							kx = random.nextInt(4) + Math.max(0, l - seaLevel);
							blockState3 = blockState3.isOf(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
						}
					}
				}
			}
		}
	}
}
