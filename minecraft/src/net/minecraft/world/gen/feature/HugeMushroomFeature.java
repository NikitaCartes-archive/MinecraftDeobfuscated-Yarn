package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public abstract class HugeMushroomFeature extends Feature<HugeMushroomFeatureConfig> {
	public HugeMushroomFeature(Function<Dynamic<?>, ? extends HugeMushroomFeatureConfig> function) {
		super(function);
	}

	protected void generateStem(IWorld world, Random random, BlockPos pos, HugeMushroomFeatureConfig config, int height, BlockPos.Mutable mutable) {
		for (int i = 0; i < height; i++) {
			mutable.set(pos).setOffset(Direction.UP, i);
			if (!world.getBlockState(mutable).isFullOpaque(world, mutable)) {
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

	protected boolean canGenerate(IWorld world, BlockPos pos, int height, BlockPos.Mutable mutable, HugeMushroomFeatureConfig config) {
		int i = pos.getY();
		if (i >= 1 && i + height + 1 < 256) {
			Block block = world.getBlockState(pos.down()).getBlock();
			if (!isDirt(block)) {
				return false;
			} else {
				for (int j = 0; j <= height; j++) {
					int k = this.getCapSize(-1, -1, config.capSize, j);

					for (int l = -k; l <= k; l++) {
						for (int m = -k; m <= k; m++) {
							BlockState blockState = world.getBlockState(mutable.setOffset(pos, l, j, m));
							if (!blockState.isAir() && !blockState.matches(BlockTags.LEAVES)) {
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

	public boolean generate(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		HugeMushroomFeatureConfig hugeMushroomFeatureConfig
	) {
		int i = this.getHeight(random);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		if (!this.canGenerate(iWorld, blockPos, i, mutable, hugeMushroomFeatureConfig)) {
			return false;
		} else {
			this.generateCap(iWorld, random, blockPos, i, mutable, hugeMushroomFeatureConfig);
			this.generateStem(iWorld, random, blockPos, hugeMushroomFeatureConfig, i, mutable);
			return true;
		}
	}

	protected abstract int getCapSize(int i, int j, int capSize, int y);

	protected abstract void generateCap(IWorld world, Random random, BlockPos start, int y, BlockPos.Mutable mutable, HugeMushroomFeatureConfig config);
}
