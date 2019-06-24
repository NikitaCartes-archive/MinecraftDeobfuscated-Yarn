package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class HugeBrownMushroomFeature extends Feature<PlantedFeatureConfig> {
	public HugeBrownMushroomFeature(Function<Dynamic<?>, ? extends PlantedFeatureConfig> function) {
		super(function);
	}

	public boolean method_13362(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, PlantedFeatureConfig plantedFeatureConfig
	) {
		int i = random.nextInt(3) + 4;
		if (random.nextInt(12) == 0) {
			i *= 2;
		}

		int j = blockPos.getY();
		if (j >= 1 && j + i + 1 < 256) {
			Block block = iWorld.getBlockState(blockPos.down()).getBlock();
			if (!Block.isNaturalDirt(block) && block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM) {
				return false;
			} else {
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int k = 0; k <= 1 + i; k++) {
					int l = k <= 3 ? 0 : 3;

					for (int m = -l; m <= l; m++) {
						for (int n = -l; n <= l; n++) {
							BlockState blockState = iWorld.getBlockState(mutable.set(blockPos).setOffset(m, k, n));
							if (!blockState.isAir() && !blockState.matches(BlockTags.LEAVES)) {
								return false;
							}
						}
					}
				}

				BlockState blockState2 = Blocks.BROWN_MUSHROOM_BLOCK
					.getDefaultState()
					.with(MushroomBlock.UP, Boolean.valueOf(true))
					.with(MushroomBlock.DOWN, Boolean.valueOf(false));
				int l = 3;

				for (int m = -3; m <= 3; m++) {
					for (int nx = -3; nx <= 3; nx++) {
						boolean bl = m == -3;
						boolean bl2 = m == 3;
						boolean bl3 = nx == -3;
						boolean bl4 = nx == 3;
						boolean bl5 = bl || bl2;
						boolean bl6 = bl3 || bl4;
						if (!bl5 || !bl6) {
							mutable.set(blockPos).setOffset(m, i, nx);
							if (!iWorld.getBlockState(mutable).isFullOpaque(iWorld, mutable)) {
								boolean bl7 = bl || bl6 && m == -2;
								boolean bl8 = bl2 || bl6 && m == 2;
								boolean bl9 = bl3 || bl5 && nx == -2;
								boolean bl10 = bl4 || bl5 && nx == 2;
								this.setBlockState(
									iWorld,
									mutable,
									blockState2.with(MushroomBlock.WEST, Boolean.valueOf(bl7))
										.with(MushroomBlock.EAST, Boolean.valueOf(bl8))
										.with(MushroomBlock.NORTH, Boolean.valueOf(bl9))
										.with(MushroomBlock.SOUTH, Boolean.valueOf(bl10))
								);
							}
						}
					}
				}

				BlockState blockState3 = Blocks.MUSHROOM_STEM
					.getDefaultState()
					.with(MushroomBlock.UP, Boolean.valueOf(false))
					.with(MushroomBlock.DOWN, Boolean.valueOf(false));

				for (int nxx = 0; nxx < i; nxx++) {
					mutable.set(blockPos).setOffset(Direction.UP, nxx);
					if (!iWorld.getBlockState(mutable).isFullOpaque(iWorld, mutable)) {
						if (plantedFeatureConfig.planted) {
							iWorld.setBlockState(mutable, blockState3, 3);
						} else {
							this.setBlockState(iWorld, mutable, blockState3);
						}
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}
}
