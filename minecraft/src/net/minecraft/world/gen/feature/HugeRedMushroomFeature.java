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

public class HugeRedMushroomFeature extends Feature<PlantedFeatureConfig> {
	public HugeRedMushroomFeature(Function<Dynamic<?>, ? extends PlantedFeatureConfig> configFactory) {
		super(configFactory);
	}

	public boolean generate(
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

				for (int k = 0; k <= i; k++) {
					int l = 0;
					if (k < i && k >= i - 3) {
						l = 2;
					} else if (k == i) {
						l = 1;
					}

					for (int m = -l; m <= l; m++) {
						for (int n = -l; n <= l; n++) {
							BlockState blockState = iWorld.getBlockState(mutable.set(blockPos).setOffset(m, k, n));
							if (!blockState.isAir() && !blockState.matches(BlockTags.LEAVES)) {
								return false;
							}
						}
					}
				}

				BlockState blockState2 = Blocks.RED_MUSHROOM_BLOCK.getDefaultState().with(MushroomBlock.DOWN, Boolean.valueOf(false));

				for (int l = i - 3; l <= i; l++) {
					int m = l < i ? 2 : 1;
					int nx = 0;

					for (int o = -m; o <= m; o++) {
						for (int p = -m; p <= m; p++) {
							boolean bl = o == -m;
							boolean bl2 = o == m;
							boolean bl3 = p == -m;
							boolean bl4 = p == m;
							boolean bl5 = bl || bl2;
							boolean bl6 = bl3 || bl4;
							if (l >= i || bl5 != bl6) {
								mutable.set(blockPos).setOffset(o, l, p);
								if (!iWorld.getBlockState(mutable).isFullOpaque(iWorld, mutable)) {
									this.setBlockState(
										iWorld,
										mutable,
										blockState2.with(MushroomBlock.UP, Boolean.valueOf(l >= i - 1))
											.with(MushroomBlock.WEST, Boolean.valueOf(o < 0))
											.with(MushroomBlock.EAST, Boolean.valueOf(o > 0))
											.with(MushroomBlock.NORTH, Boolean.valueOf(p < 0))
											.with(MushroomBlock.SOUTH, Boolean.valueOf(p > 0))
									);
								}
							}
						}
					}
				}

				BlockState blockState3 = Blocks.MUSHROOM_STEM
					.getDefaultState()
					.with(MushroomBlock.UP, Boolean.valueOf(false))
					.with(MushroomBlock.DOWN, Boolean.valueOf(false));

				for (int m = 0; m < i; m++) {
					mutable.set(blockPos).setOffset(Direction.UP, m);
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
