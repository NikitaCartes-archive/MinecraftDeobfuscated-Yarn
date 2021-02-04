package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public abstract class HugeMushroomFeature extends Feature<HugeMushroomFeatureConfig> {
	public HugeMushroomFeature(Codec<HugeMushroomFeatureConfig> codec) {
		super(codec);
	}

	protected void generateStem(WorldAccess world, Random random, BlockPos pos, HugeMushroomFeatureConfig config, int height, BlockPos.Mutable mutable) {
		for (int i = 0; i < height; i++) {
			mutable.set(pos).move(Direction.UP, i);
			if (!world.getBlockState(mutable).isOpaqueFullCube(world, mutable)) {
				this.setBlockState(world, mutable, config.stemProvider.getBlockState(random, pos));
			}
		}
	}

	protected int getHeight(Random random) {
		int i = random.nextInt(3) + 4;
		if (random.nextInt(12) == 0) {
			i *= 2;
		}

		return i;
	}

	protected boolean canGenerate(WorldAccess world, BlockPos pos, int height, BlockPos.Mutable mutable, HugeMushroomFeatureConfig config) {
		int i = pos.getY();
		if (i >= world.getBottomY() + 1 && i + height + 1 < world.getTopY()) {
			BlockState blockState = world.getBlockState(pos.down());
			if (!isSoil(blockState) && !blockState.isIn(BlockTags.MUSHROOM_GROW_BLOCK)) {
				return false;
			} else {
				for (int j = 0; j <= height; j++) {
					int k = this.getCapSize(-1, -1, config.foliageRadius, j);

					for (int l = -k; l <= k; l++) {
						for (int m = -k; m <= k; m++) {
							BlockState blockState2 = world.getBlockState(mutable.set(pos, l, j, m));
							if (!blockState2.isAir() && !blockState2.isIn(BlockTags.LEAVES)) {
								return false;
							}
						}
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean generate(FeatureContext<HugeMushroomFeatureConfig> featureContext) {
		StructureWorldAccess structureWorldAccess = featureContext.getWorld();
		BlockPos blockPos = featureContext.getPos();
		Random random = featureContext.getRandom();
		HugeMushroomFeatureConfig hugeMushroomFeatureConfig = featureContext.getConfig();
		int i = this.getHeight(random);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		if (!this.canGenerate(structureWorldAccess, blockPos, i, mutable, hugeMushroomFeatureConfig)) {
			return false;
		} else {
			this.generateCap(structureWorldAccess, random, blockPos, i, mutable, hugeMushroomFeatureConfig);
			this.generateStem(structureWorldAccess, random, blockPos, hugeMushroomFeatureConfig, i, mutable);
			return true;
		}
	}

	protected abstract int getCapSize(int i, int j, int capSize, int y);

	protected abstract void generateCap(WorldAccess world, Random random, BlockPos start, int y, BlockPos.Mutable mutable, HugeMushroomFeatureConfig config);
}
