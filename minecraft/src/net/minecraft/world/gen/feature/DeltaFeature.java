package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
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

public class DeltaFeature extends Feature<DeltaFeatureConfig> {
	private static final ImmutableList<Block> field_24133 = ImmutableList.of(
		Blocks.BEDROCK, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER
	);
	private static final Direction[] field_23883 = Direction.values();

	private static int method_27104(Random random, DeltaFeatureConfig deltaFeatureConfig) {
		return deltaFeatureConfig.minRadius + random.nextInt(deltaFeatureConfig.maxRadius - deltaFeatureConfig.minRadius + 1);
	}

	private static int method_27105(Random random, DeltaFeatureConfig deltaFeatureConfig) {
		return random.nextInt(deltaFeatureConfig.maxRim + 1);
	}

	public DeltaFeature(Function<Dynamic<?>, ? extends DeltaFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld,
		StructureAccessor structureAccessor,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		DeltaFeatureConfig deltaFeatureConfig
	) {
		BlockPos blockPos2 = method_27102(iWorld, blockPos.mutableCopy().method_27158(Direction.Axis.Y, 1, iWorld.getHeight() - 1));
		if (blockPos2 == null) {
			return false;
		} else {
			boolean bl = false;
			boolean bl2 = random.nextDouble() < 0.9;
			int i = bl2 ? method_27105(random, deltaFeatureConfig) : 0;
			int j = bl2 ? method_27105(random, deltaFeatureConfig) : 0;
			boolean bl3 = bl2 && i != 0 && j != 0;
			int k = method_27104(random, deltaFeatureConfig);
			int l = method_27104(random, deltaFeatureConfig);
			int m = Math.max(k, l);

			for (BlockPos blockPos3 : BlockPos.iterateOutwards(blockPos2, k, 0, l)) {
				if (blockPos3.getManhattanDistance(blockPos2) > m) {
					break;
				}

				if (method_27103(iWorld, blockPos3, deltaFeatureConfig)) {
					if (bl3) {
						bl = true;
						this.setBlockState(iWorld, blockPos3, deltaFeatureConfig.rim);
					}

					BlockPos blockPos4 = blockPos3.add(i, 0, j);
					if (method_27103(iWorld, blockPos4, deltaFeatureConfig)) {
						bl = true;
						this.setBlockState(iWorld, blockPos4, deltaFeatureConfig.contents);
					}
				}
			}

			return bl;
		}
	}

	private static boolean method_27103(IWorld iWorld, BlockPos blockPos, DeltaFeatureConfig deltaFeatureConfig) {
		BlockState blockState = iWorld.getBlockState(blockPos);
		if (blockState.isOf(deltaFeatureConfig.contents.getBlock())) {
			return false;
		} else if (field_24133.contains(blockState.getBlock())) {
			return false;
		} else {
			for (Direction direction : field_23883) {
				boolean bl = iWorld.getBlockState(blockPos.offset(direction)).isAir();
				if (bl && direction != Direction.UP || !bl && direction == Direction.UP) {
					return false;
				}
			}

			return true;
		}
	}

	@Nullable
	private static BlockPos method_27102(IWorld iWorld, BlockPos.Mutable mutable) {
		while (mutable.getY() > 1) {
			if (iWorld.getBlockState(mutable).isAir()) {
				BlockState blockState = iWorld.getBlockState(mutable.move(Direction.DOWN));
				mutable.move(Direction.UP);
				if (!blockState.isOf(Blocks.LAVA) && !blockState.isOf(Blocks.BEDROCK) && !blockState.isAir()) {
					return mutable;
				}
			}

			mutable.move(Direction.DOWN);
		}

		return null;
	}
}
