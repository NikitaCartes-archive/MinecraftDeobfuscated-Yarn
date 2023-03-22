package net.minecraft.world;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface RedstoneView extends BlockView {
	Direction[] DIRECTIONS = Direction.values();

	default int getStrongRedstonePower(BlockPos pos, Direction direction) {
		return this.getBlockState(pos).getStrongRedstonePower(this, pos, direction);
	}

	default int getReceivedStrongRedstonePower(BlockPos pos) {
		int i = 0;
		i = Math.max(i, this.getStrongRedstonePower(pos.down(), Direction.DOWN));
		if (i >= 15) {
			return i;
		} else {
			i = Math.max(i, this.getStrongRedstonePower(pos.up(), Direction.UP));
			if (i >= 15) {
				return i;
			} else {
				i = Math.max(i, this.getStrongRedstonePower(pos.north(), Direction.NORTH));
				if (i >= 15) {
					return i;
				} else {
					i = Math.max(i, this.getStrongRedstonePower(pos.south(), Direction.SOUTH));
					if (i >= 15) {
						return i;
					} else {
						i = Math.max(i, this.getStrongRedstonePower(pos.west(), Direction.WEST));
						if (i >= 15) {
							return i;
						} else {
							i = Math.max(i, this.getStrongRedstonePower(pos.east(), Direction.EAST));
							return i >= 15 ? i : i;
						}
					}
				}
			}
		}
	}

	default int getEmittedRedstonePower(BlockPos pos, Direction direction, boolean onlyFromGate) {
		BlockState blockState = this.getBlockState(pos);
		if (onlyFromGate) {
			return AbstractRedstoneGateBlock.isRedstoneGate(blockState) ? this.getStrongRedstonePower(pos, direction) : 0;
		} else if (blockState.isOf(Blocks.REDSTONE_BLOCK)) {
			return 15;
		} else if (blockState.isOf(Blocks.REDSTONE_WIRE)) {
			return (Integer)blockState.get(RedstoneWireBlock.POWER);
		} else {
			return blockState.emitsRedstonePower() ? this.getStrongRedstonePower(pos, direction) : 0;
		}
	}

	default boolean isEmittingRedstonePower(BlockPos pos, Direction direction) {
		return this.getEmittedRedstonePower(pos, direction) > 0;
	}

	default int getEmittedRedstonePower(BlockPos pos, Direction direction) {
		BlockState blockState = this.getBlockState(pos);
		int i = blockState.getWeakRedstonePower(this, pos, direction);
		return blockState.isSolidBlock(this, pos) ? Math.max(i, this.getReceivedStrongRedstonePower(pos)) : i;
	}

	default boolean isReceivingRedstonePower(BlockPos pos) {
		if (this.getEmittedRedstonePower(pos.down(), Direction.DOWN) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(pos.up(), Direction.UP) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(pos.north(), Direction.NORTH) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(pos.south(), Direction.SOUTH) > 0) {
			return true;
		} else {
			return this.getEmittedRedstonePower(pos.west(), Direction.WEST) > 0 ? true : this.getEmittedRedstonePower(pos.east(), Direction.EAST) > 0;
		}
	}

	default int getReceivedRedstonePower(BlockPos pos) {
		int i = 0;

		for (Direction direction : DIRECTIONS) {
			int j = this.getEmittedRedstonePower(pos.offset(direction), direction);
			if (j >= 15) {
				return 15;
			}

			if (j > i) {
				i = j;
			}
		}

		return i;
	}
}
