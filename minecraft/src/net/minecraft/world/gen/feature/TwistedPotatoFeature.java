package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PowerfulPotatoBlock;
import net.minecraft.block.StrongRootsBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class TwistedPotatoFeature extends Feature<DefaultFeatureConfig> {
	public TwistedPotatoFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		Random random = context.getRandom();
		if (canReplace(structureWorldAccess, blockPos)) {
			structureWorldAccess.setBlockState(blockPos, getPowerfulPotatoBlockState(), Block.NOTIFY_LISTENERS);
			placeRoots(structureWorldAccess, blockPos.down(), random, 16);
			return true;
		} else {
			return false;
		}
	}

	private static BlockState getPowerfulPotatoBlockState() {
		return Blocks.POWERFUL_POTATO.getDefaultState().with(PowerfulPotatoBlock.AGE, Integer.valueOf(3));
	}

	public static void placeRoots(WorldAccess world, BlockPos pos, Random random, int maxDistance) {
		world.setBlockState(pos, StrongRootsBlock.getPlacementState(world, pos, Blocks.WEAK_ROOTS.getDefaultState()), Block.NOTIFY_LISTENERS);
		placeRoots(world, pos, random, pos, maxDistance, 0);
	}

	public static boolean canReplace(WorldAccess world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.isOf(Blocks.POWERFUL_POTATO)) {
			return false;
		} else {
			return blockState.isIn(BlockTags.FEATURES_CANNOT_REPLACE) ? false : !blockState.isOf(Blocks.WEAK_ROOTS);
		}
	}

	private static void placeRoots(WorldAccess world, BlockPos pos, Random random, BlockPos startPos, int maxDistance, int rootsPlaced) {
		Block block = Blocks.WEAK_ROOTS;
		int i = random.nextInt(4) + 1;
		if (rootsPlaced == 0) {
			i++;
		}

		for (int j = 0; j < i; j++) {
			BlockPos blockPos = pos.down(j + 1);
			world.setBlockState(blockPos, StrongRootsBlock.getPlacementState(world, blockPos, block.getDefaultState()), Block.NOTIFY_LISTENERS);
			world.setBlockState(blockPos.up(), StrongRootsBlock.getPlacementState(world, blockPos.up(), block.getDefaultState()), Block.NOTIFY_LISTENERS);
		}

		if (rootsPlaced < 4) {
			int j = random.nextInt(4);
			if (rootsPlaced == 0) {
				j++;
			}

			for (int k = 0; k < j; k++) {
				Direction direction = Direction.Type.HORIZONTAL.random(random);
				BlockPos blockPos2 = pos.down(i).offset(direction);
				if (Math.abs(blockPos2.getX() - startPos.getX()) < maxDistance
					&& Math.abs(blockPos2.getZ() - startPos.getZ()) < maxDistance
					&& canReplace(world, blockPos2)
					&& canReplace(world, blockPos2.down())) {
					world.setBlockState(blockPos2, StrongRootsBlock.getPlacementState(world, blockPos2, block.getDefaultState()), Block.NOTIFY_LISTENERS);
					world.setBlockState(
						blockPos2.offset(direction.getOpposite()),
						StrongRootsBlock.getPlacementState(world, blockPos2.offset(direction.getOpposite()), block.getDefaultState()),
						Block.NOTIFY_LISTENERS
					);
					placeRoots(world, blockPos2, random, startPos, maxDistance, rootsPlaced + 1);
				}
			}
		}
	}
}
