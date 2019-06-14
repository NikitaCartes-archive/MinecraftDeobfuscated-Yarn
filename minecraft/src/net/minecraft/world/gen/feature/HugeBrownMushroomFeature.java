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
			Block block = iWorld.method_8320(blockPos.down()).getBlock();
			if (!Block.isNaturalDirt(block) && block != Blocks.field_10219 && block != Blocks.field_10402) {
				return false;
			} else {
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int k = 0; k <= 1 + i; k++) {
					int l = k <= 3 ? 0 : 3;

					for (int m = -l; m <= l; m++) {
						for (int n = -l; n <= l; n++) {
							BlockState blockState = iWorld.method_8320(mutable.set(blockPos).setOffset(m, k, n));
							if (!blockState.isAir() && !blockState.matches(BlockTags.field_15503)) {
								return false;
							}
						}
					}
				}

				BlockState blockState2 = Blocks.field_10580
					.method_9564()
					.method_11657(MushroomBlock.field_11166, Boolean.valueOf(true))
					.method_11657(MushroomBlock.field_11169, Boolean.valueOf(false));
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
							if (!iWorld.method_8320(mutable).isFullOpaque(iWorld, mutable)) {
								boolean bl7 = bl || bl6 && m == -2;
								boolean bl8 = bl2 || bl6 && m == 2;
								boolean bl9 = bl3 || bl5 && nx == -2;
								boolean bl10 = bl4 || bl5 && nx == 2;
								this.setBlockState(
									iWorld,
									mutable,
									blockState2.method_11657(MushroomBlock.field_11167, Boolean.valueOf(bl7))
										.method_11657(MushroomBlock.field_11172, Boolean.valueOf(bl8))
										.method_11657(MushroomBlock.field_11171, Boolean.valueOf(bl9))
										.method_11657(MushroomBlock.field_11170, Boolean.valueOf(bl10))
								);
							}
						}
					}
				}

				BlockState blockState3 = Blocks.field_10556
					.method_9564()
					.method_11657(MushroomBlock.field_11166, Boolean.valueOf(false))
					.method_11657(MushroomBlock.field_11169, Boolean.valueOf(false));

				for (int nxx = 0; nxx < i; nxx++) {
					mutable.set(blockPos).setOffset(Direction.field_11036, nxx);
					if (!iWorld.method_8320(mutable).isFullOpaque(iWorld, mutable)) {
						if (plantedFeatureConfig.planted) {
							iWorld.method_8652(mutable, blockState3, 3);
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
