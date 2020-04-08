package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class BasaltColumnsFeature extends Feature<BasaltColumnsFeatureConfig> {
	public BasaltColumnsFeature(Function<Dynamic<?>, ? extends BasaltColumnsFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld,
		StructureAccessor structureAccessor,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		BasaltColumnsFeatureConfig basaltColumnsFeatureConfig
	) {
		int i = chunkGenerator.getSeaLevel();
		BlockPos blockPos2 = method_27094(iWorld, i, blockPos.mutableCopy().method_27158(Direction.Axis.Y, 1, iWorld.getHeight() - 1), Integer.MAX_VALUE);
		if (blockPos2 == null) {
			return false;
		} else {
			int j = method_27099(random, basaltColumnsFeatureConfig);
			boolean bl = random.nextFloat() < 0.9F;
			int k = bl ? 5 : 8;
			int l = bl ? 50 : 15;
			boolean bl2 = false;

			for (BlockPos blockPos3 : BlockPos.method_27156(
				random, l, blockPos2.getX() - k, blockPos2.getY(), blockPos2.getZ() - k, blockPos2.getX() + k, blockPos2.getY(), blockPos2.getZ() + k
			)) {
				int m = j - blockPos3.getManhattanDistance(blockPos2);
				if (m >= 0) {
					bl2 |= this.method_27096(iWorld, i, blockPos3, m, method_27100(random, basaltColumnsFeatureConfig));
				}
			}

			return bl2;
		}
	}

	private boolean method_27096(IWorld iWorld, int i, BlockPos blockPos, int j, int k) {
		boolean bl = false;

		for (BlockPos blockPos2 : BlockPos.iterate(
			blockPos.getX() - k, blockPos.getY(), blockPos.getZ() - k, blockPos.getX() + k, blockPos.getY(), blockPos.getZ() + k
		)) {
			int l = blockPos2.getManhattanDistance(blockPos);
			BlockPos blockPos3 = method_27095(iWorld, i, blockPos2)
				? method_27094(iWorld, i, blockPos2.mutableCopy(), l)
				: method_27098(iWorld, blockPos2.mutableCopy(), l);
			if (blockPos3 != null) {
				int m = j - l / 2;

				for (BlockPos.Mutable mutable = blockPos3.mutableCopy(); m >= 0; m--) {
					if (method_27095(iWorld, i, mutable)) {
						this.setBlockState(iWorld, mutable, Blocks.BASALT.getDefaultState());
						mutable.move(Direction.UP);
						bl = true;
					} else {
						if (iWorld.getBlockState(mutable).getBlock() != Blocks.BASALT) {
							break;
						}

						mutable.move(Direction.UP);
					}
				}
			}
		}

		return bl;
	}

	@Nullable
	private static BlockPos method_27094(IWorld iWorld, int i, BlockPos.Mutable mutable, int j) {
		while (mutable.getY() > 1 && j > 0) {
			j--;
			if (method_27095(iWorld, i, mutable)) {
				BlockState blockState = iWorld.getBlockState(mutable.move(Direction.DOWN));
				mutable.move(Direction.UP);
				Block block = blockState.getBlock();
				if (block != Blocks.LAVA && block != Blocks.BEDROCK && block != Blocks.MAGMA_BLOCK && !blockState.isAir()) {
					return mutable;
				}
			}

			mutable.move(Direction.DOWN);
		}

		return null;
	}

	@Nullable
	private static BlockPos method_27098(IWorld iWorld, BlockPos.Mutable mutable, int i) {
		while (mutable.getY() < iWorld.getHeight() && i > 0) {
			i--;
			if (iWorld.getBlockState(mutable).isAir()) {
				return mutable;
			}

			mutable.move(Direction.UP);
		}

		return null;
	}

	private static int method_27099(Random random, BasaltColumnsFeatureConfig basaltColumnsFeatureConfig) {
		return basaltColumnsFeatureConfig.minHeight + random.nextInt(basaltColumnsFeatureConfig.maxHeight - basaltColumnsFeatureConfig.minHeight + 1);
	}

	private static int method_27100(Random random, BasaltColumnsFeatureConfig basaltColumnsFeatureConfig) {
		return basaltColumnsFeatureConfig.minReach + random.nextInt(basaltColumnsFeatureConfig.maxReach - basaltColumnsFeatureConfig.minReach + 1);
	}

	private static boolean method_27095(IWorld iWorld, int i, BlockPos blockPos) {
		BlockState blockState = iWorld.getBlockState(blockPos);
		return blockState.isAir() || blockState.getBlock() == Blocks.LAVA && blockPos.getY() <= i;
	}
}
