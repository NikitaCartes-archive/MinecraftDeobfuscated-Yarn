package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class BasaltColumnsFeature extends Feature<BasaltColumnsFeatureConfig> {
	private static final ImmutableList<Block> BLOCKS = ImmutableList.of(
		Blocks.LAVA,
		Blocks.BEDROCK,
		Blocks.MAGMA_BLOCK,
		Blocks.SOUL_SAND,
		Blocks.NETHER_BRICKS,
		Blocks.NETHER_BRICK_FENCE,
		Blocks.NETHER_BRICK_STAIRS,
		Blocks.NETHER_WART,
		Blocks.CHEST,
		Blocks.SPAWNER
	);
	private static final int field_31495 = 5;
	private static final int field_31496 = 50;
	private static final int field_31497 = 8;
	private static final int field_31498 = 15;

	public BasaltColumnsFeature(Codec<BasaltColumnsFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<BasaltColumnsFeatureConfig> context) {
		int i = context.getGenerator().getSeaLevel();
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		Random random = context.getRandom();
		BasaltColumnsFeatureConfig basaltColumnsFeatureConfig = context.getConfig();
		if (!canPlaceAt(structureWorldAccess, i, blockPos.mutableCopy())) {
			return false;
		} else {
			int j = basaltColumnsFeatureConfig.getHeight().get(random);
			boolean bl = random.nextFloat() < 0.9F;
			int k = Math.min(j, bl ? 5 : 8);
			int l = bl ? 50 : 15;
			boolean bl2 = false;

			for (BlockPos blockPos2 : BlockPos.iterateRandomly(
				random, l, blockPos.getX() - k, blockPos.getY(), blockPos.getZ() - k, blockPos.getX() + k, blockPos.getY(), blockPos.getZ() + k
			)) {
				int m = j - blockPos2.getManhattanDistance(blockPos);
				if (m >= 0) {
					bl2 |= this.placeBasaltColumn(structureWorldAccess, i, blockPos2, m, basaltColumnsFeatureConfig.getReach().get(random));
				}
			}

			return bl2;
		}
	}

	private boolean placeBasaltColumn(WorldAccess world, int seaLevel, BlockPos pos, int height, int reach) {
		boolean bl = false;

		for (BlockPos blockPos : BlockPos.iterate(pos.getX() - reach, pos.getY(), pos.getZ() - reach, pos.getX() + reach, pos.getY(), pos.getZ() + reach)) {
			int i = blockPos.getManhattanDistance(pos);
			BlockPos blockPos2 = isAirOrLavaOcean(world, seaLevel, blockPos)
				? moveDownToGround(world, seaLevel, blockPos.mutableCopy(), i)
				: moveUpToAir(world, blockPos.mutableCopy(), i);
			if (blockPos2 != null) {
				int j = height - i / 2;

				for (BlockPos.Mutable mutable = blockPos2.mutableCopy(); j >= 0; j--) {
					if (isAirOrLavaOcean(world, seaLevel, mutable)) {
						this.setBlockState(world, mutable, Blocks.BASALT.getDefaultState());
						mutable.move(Direction.UP);
						bl = true;
					} else {
						if (!world.getBlockState(mutable).isOf(Blocks.BASALT)) {
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
	private static BlockPos moveDownToGround(WorldAccess world, int seaLevel, BlockPos.Mutable mutablePos, int distance) {
		while (mutablePos.getY() > world.getBottomY() + 1 && distance > 0) {
			distance--;
			if (canPlaceAt(world, seaLevel, mutablePos)) {
				return mutablePos;
			}

			mutablePos.move(Direction.DOWN);
		}

		return null;
	}

	private static boolean canPlaceAt(WorldAccess world, int seaLevel, BlockPos.Mutable mutablePos) {
		if (!isAirOrLavaOcean(world, seaLevel, mutablePos)) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(mutablePos.move(Direction.DOWN));
			mutablePos.move(Direction.UP);
			return !blockState.isAir() && !BLOCKS.contains(blockState.getBlock());
		}
	}

	@Nullable
	private static BlockPos moveUpToAir(WorldAccess world, BlockPos.Mutable mutablePos, int distance) {
		while (mutablePos.getY() < world.getTopY() && distance > 0) {
			distance--;
			BlockState blockState = world.getBlockState(mutablePos);
			if (BLOCKS.contains(blockState.getBlock())) {
				return null;
			}

			if (blockState.isAir()) {
				return mutablePos;
			}

			mutablePos.move(Direction.UP);
		}

		return null;
	}

	private static boolean isAirOrLavaOcean(WorldAccess world, int seaLevel, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		return blockState.isAir() || blockState.isOf(Blocks.LAVA) && pos.getY() <= seaLevel;
	}
}
