package net.minecraft;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Grid;
import net.minecraft.world.World;

public class class_9517 {
	private final LongList field_50528;
	private final BlockPos field_50529;

	private class_9517(LongList longList, BlockPos blockPos) {
		this.field_50528 = longList;
		this.field_50529 = blockPos;
	}

	public static class_9517 method_58997(Grid grid, Direction direction) {
		LongList longList = new LongArrayList();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Direction direction2 = direction.getOpposite();

		for (BlockPos blockPos : method_59000(grid, direction)) {
			mutable.set(blockPos);
			boolean bl = false;
			int i = direction.getAxis().choose(grid.getXSize(), grid.getYSize(), grid.getZSize());

			for (int j = 0; j < i; j++) {
				BlockState blockState = grid.getBlockState(mutable);
				if (method_58999(blockState)) {
					if (!bl) {
						longList.add(mutable.asLong());
					}

					bl = true;
				} else {
					bl = false;
				}

				mutable.move(direction2);
			}
		}

		return new class_9517(longList, new BlockPos(grid.getXSize(), grid.getYSize(), grid.getZSize()));
	}

	private static Iterable<BlockPos> method_59000(Grid grid, Direction direction) {
		BlockPos blockPos = new BlockPos(
			Math.max(direction.getOffsetX(), 0) * (grid.getXSize() - 1),
			Math.max(direction.getOffsetY(), 0) * (grid.getYSize() - 1),
			Math.max(direction.getOffsetZ(), 0) * (grid.getZSize() - 1)
		);
		BlockPos blockPos2 = blockPos.add(
			direction.getAxis() == Direction.Axis.X ? 0 : grid.getXSize() - 1,
			direction.getAxis() == Direction.Axis.Y ? 0 : grid.getYSize() - 1,
			direction.getAxis() == Direction.Axis.Z ? 0 : grid.getZSize() - 1
		);
		return BlockPos.iterate(blockPos, blockPos2);
	}

	public boolean method_58998(World world, BlockPos blockPos) {
		int i = blockPos.getY();
		int j = i + this.field_50529.getY() - 1;
		if (i >= world.getBottomY() && j < world.getTopY()) {
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			LongIterator longIterator = this.field_50528.longIterator();

			while (longIterator.hasNext()) {
				mutable.set(longIterator.nextLong());
				mutable.move(blockPos);
				if (method_58999(world.getBlockState(mutable))) {
					return true;
				}
			}

			return false;
		} else {
			return true;
		}
	}

	private static boolean method_58999(BlockState blockState) {
		return !blockState.isReplaceable();
	}
}
