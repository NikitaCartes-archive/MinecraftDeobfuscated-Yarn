package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class DefaultSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	private static final int field_31700 = 50;

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
		long m,
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
			l
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
		int seaLevel
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
		if (i == 0) {
			boolean bl = false;

			for (int j = height; j >= 50; j--) {
				mutable.set(x, j, z);
				BlockState blockState = chunk.getBlockState(mutable);
				if (blockState.isAir()) {
					bl = false;
				} else if (blockState.isOf(defaultBlock.getBlock())) {
					if (!bl) {
						BlockState blockState2;
						if (j >= seaLevel) {
							blockState2 = Blocks.AIR.getDefaultState();
						} else if (j == seaLevel - 1) {
							blockState2 = biome.getTemperature(mutable) < 0.15F ? Blocks.ICE.getDefaultState() : fluidBlock;
						} else if (j >= seaLevel - (7 + i)) {
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
			int jx = -1;

			for (int k = height; k >= 50; k--) {
				mutable.set(x, k, z);
				BlockState blockState2 = chunk.getBlockState(mutable);
				if (blockState2.isAir()) {
					jx = -1;
				} else if (blockState2.isOf(defaultBlock.getBlock())) {
					if (jx == -1) {
						jx = i;
						BlockState blockState4;
						if (k >= seaLevel + 2) {
							blockState4 = topBlock;
						} else if (k >= seaLevel - 1) {
							blockState3 = underBlock;
							blockState4 = topBlock;
						} else if (k >= seaLevel - 4) {
							blockState3 = underBlock;
							blockState4 = underBlock;
						} else if (k >= seaLevel - (7 + i)) {
							blockState4 = blockState3;
						} else {
							blockState3 = defaultBlock;
							blockState4 = underwaterBlock;
						}

						chunk.setBlockState(mutable, blockState4, false);
					} else if (jx > 0) {
						jx--;
						chunk.setBlockState(mutable, blockState3, false);
						if (jx == 0 && blockState3.isOf(Blocks.SAND) && i > 1) {
							jx = random.nextInt(4) + Math.max(0, k - seaLevel);
							blockState3 = blockState3.isOf(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
						}
					}
				}
			}
		}
	}
}
