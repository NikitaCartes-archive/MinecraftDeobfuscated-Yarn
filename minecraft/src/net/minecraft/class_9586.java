package net.minecraft;

import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.block.enums.Thickness;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;

public class class_9586 {
	protected static double method_59224(double d, double e, double f, double g) {
		if (d < g) {
			d = g;
		}

		double h = 0.384;
		double i = d / e * 0.384;
		double j = 0.75 * Math.pow(i, 1.3333333333333333);
		double k = Math.pow(i, 0.6666666666666666);
		double l = 0.3333333333333333 * Math.log(i);
		double m = f * (j - k - l);
		m = Math.max(m, 0.0);
		return m / 0.384 * e;
	}

	protected static boolean method_59228(StructureWorldAccess structureWorldAccess, BlockPos blockPos, int i) {
		if (method_59234(structureWorldAccess, blockPos)) {
			return false;
		} else {
			float f = 6.0F;
			float g = 6.0F / (float)i;

			for (float h = 0.0F; h < (float) (Math.PI * 2); h += g) {
				int j = (int)(MathHelper.cos(h) * (float)i);
				int k = (int)(MathHelper.sin(h) * (float)i);
				if (method_59234(structureWorldAccess, blockPos.add(j, 0, k))) {
					return false;
				}
			}

			return true;
		}
	}

	protected static boolean method_59226(WorldAccess worldAccess, BlockPos blockPos) {
		return worldAccess.testBlockState(blockPos, class_9586::method_59233);
	}

	protected static boolean method_59234(WorldAccess worldAccess, BlockPos blockPos) {
		return worldAccess.testBlockState(blockPos, class_9586::method_59237);
	}

	protected static void method_59231(PointedDripstoneBlock pointedDripstoneBlock, Direction direction, int i, boolean bl, Consumer<BlockState> consumer) {
		if (i >= 3) {
			consumer.accept(method_59232(pointedDripstoneBlock, direction, Thickness.BASE));

			for (int j = 0; j < i - 3; j++) {
				consumer.accept(method_59232(pointedDripstoneBlock, direction, Thickness.MIDDLE));
			}
		}

		if (i >= 2) {
			consumer.accept(method_59232(pointedDripstoneBlock, direction, Thickness.FRUSTUM));
		}

		if (i >= 1) {
			consumer.accept(method_59232(pointedDripstoneBlock, direction, bl ? Thickness.TIP_MERGE : Thickness.TIP));
		}
	}

	public static void method_59229(Block block, WorldAccess worldAccess, BlockPos blockPos, Direction direction, int i, boolean bl) {
		if (block instanceof PointedDripstoneBlock pointedDripstoneBlock) {
			if (method_59235(pointedDripstoneBlock, worldAccess.getBlockState(blockPos.offset(direction.getOpposite())))) {
				BlockPos.Mutable mutable = blockPos.mutableCopy();
				method_59231(pointedDripstoneBlock, direction, i, bl, blockState -> {
					if (blockState.getBlock() instanceof PointedDripstoneBlock) {
						blockState = blockState.with(PointedDripstoneBlock.WATERLOGGED, Boolean.valueOf(worldAccess.isWater(mutable)));
					}

					worldAccess.setBlockState(mutable, blockState, Block.NOTIFY_LISTENERS);
					mutable.move(direction);
				});
			}
		}
	}

	protected static boolean method_59227(WorldAccess worldAccess, BlockPos blockPos, Block block) {
		BlockState blockState = worldAccess.getBlockState(blockPos);
		if (blockState.isIn(BlockTags.DRIPSTONE_REPLACEABLE_BLOCKS)) {
			worldAccess.setBlockState(blockPos, block.getDefaultState(), Block.NOTIFY_LISTENERS);
			return true;
		} else {
			return false;
		}
	}

	private static BlockState method_59232(PointedDripstoneBlock pointedDripstoneBlock, Direction direction, Thickness thickness) {
		return pointedDripstoneBlock.getDefaultState().with(PointedDripstoneBlock.VERTICAL_DIRECTION, direction).with(PointedDripstoneBlock.THICKNESS, thickness);
	}

	public static boolean method_59230(PointedDripstoneBlock pointedDripstoneBlock, BlockState blockState) {
		return method_59235(pointedDripstoneBlock, blockState) || blockState.isOf(Blocks.LAVA);
	}

	public static boolean method_59235(PointedDripstoneBlock pointedDripstoneBlock, BlockState blockState) {
		return pointedDripstoneBlock.method_59134(blockState) || blockState.isIn(BlockTags.DRIPSTONE_REPLACEABLE_BLOCKS);
	}

	public static boolean method_59233(BlockState blockState) {
		return blockState.isAir() || blockState.isOf(Blocks.WATER);
	}

	public static boolean method_59236(BlockState blockState) {
		return !blockState.isAir() && !blockState.isOf(Blocks.WATER);
	}

	public static boolean method_59237(BlockState blockState) {
		return blockState.isAir() || blockState.isOf(Blocks.WATER) || blockState.isOf(Blocks.LAVA);
	}
}
