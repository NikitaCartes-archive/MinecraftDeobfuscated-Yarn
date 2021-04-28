package net.minecraft.block.piston;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PistonHandler {
	public static final int MAX_MOVABLE_BLOCKS = 12;
	private final World world;
	private final BlockPos posFrom;
	private final boolean retracted;
	private final BlockPos posTo;
	private final Direction motionDirection;
	private final List<BlockPos> movedBlocks = Lists.<BlockPos>newArrayList();
	private final List<BlockPos> brokenBlocks = Lists.<BlockPos>newArrayList();
	private final Direction pistonDirection;

	public PistonHandler(World world, BlockPos pos, Direction dir, boolean retracted) {
		this.world = world;
		this.posFrom = pos;
		this.pistonDirection = dir;
		this.retracted = retracted;
		if (retracted) {
			this.motionDirection = dir;
			this.posTo = pos.offset(dir);
		} else {
			this.motionDirection = dir.getOpposite();
			this.posTo = pos.offset(dir, 2);
		}
	}

	public boolean calculatePush() {
		this.movedBlocks.clear();
		this.brokenBlocks.clear();
		BlockState blockState = this.world.getBlockState(this.posTo);
		if (!PistonBlock.isMovable(blockState, this.world, this.posTo, this.motionDirection, false, this.pistonDirection)) {
			if (this.retracted && blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
				this.brokenBlocks.add(this.posTo);
				return true;
			} else {
				return false;
			}
		} else if (!this.tryMove(this.posTo, this.motionDirection)) {
			return false;
		} else {
			for (int i = 0; i < this.movedBlocks.size(); i++) {
				BlockPos blockPos = (BlockPos)this.movedBlocks.get(i);
				if (isBlockSticky(this.world.getBlockState(blockPos)) && !this.tryMoveAdjacentBlock(blockPos)) {
					return false;
				}
			}

			return true;
		}
	}

	private static boolean isBlockSticky(BlockState state) {
		return state.isOf(Blocks.SLIME_BLOCK) || state.isOf(Blocks.HONEY_BLOCK);
	}

	private static boolean isAdjacentBlockStuck(BlockState state, BlockState adjacentState) {
		if (state.isOf(Blocks.HONEY_BLOCK) && adjacentState.isOf(Blocks.SLIME_BLOCK)) {
			return false;
		} else {
			return state.isOf(Blocks.SLIME_BLOCK) && adjacentState.isOf(Blocks.HONEY_BLOCK) ? false : isBlockSticky(state) || isBlockSticky(adjacentState);
		}
	}

	private boolean tryMove(BlockPos pos, Direction dir) {
		BlockState blockState = this.world.getBlockState(pos);
		if (blockState.isAir()) {
			return true;
		} else if (!PistonBlock.isMovable(blockState, this.world, pos, this.motionDirection, false, dir)) {
			return true;
		} else if (pos.equals(this.posFrom)) {
			return true;
		} else if (this.movedBlocks.contains(pos)) {
			return true;
		} else {
			int i = 1;
			if (i + this.movedBlocks.size() > 12) {
				return false;
			} else {
				while (isBlockSticky(blockState)) {
					BlockPos blockPos = pos.offset(this.motionDirection.getOpposite(), i);
					BlockState blockState2 = blockState;
					blockState = this.world.getBlockState(blockPos);
					if (blockState.isAir()
						|| !isAdjacentBlockStuck(blockState2, blockState)
						|| !PistonBlock.isMovable(blockState, this.world, blockPos, this.motionDirection, false, this.motionDirection.getOpposite())
						|| blockPos.equals(this.posFrom)) {
						break;
					}

					if (++i + this.movedBlocks.size() > 12) {
						return false;
					}
				}

				int j = 0;

				for (int k = i - 1; k >= 0; k--) {
					this.movedBlocks.add(pos.offset(this.motionDirection.getOpposite(), k));
					j++;
				}

				int k = 1;

				while (true) {
					BlockPos blockPos2 = pos.offset(this.motionDirection, k);
					int l = this.movedBlocks.indexOf(blockPos2);
					if (l > -1) {
						this.setMovedBlocks(j, l);

						for (int m = 0; m <= l + j; m++) {
							BlockPos blockPos3 = (BlockPos)this.movedBlocks.get(m);
							if (isBlockSticky(this.world.getBlockState(blockPos3)) && !this.tryMoveAdjacentBlock(blockPos3)) {
								return false;
							}
						}

						return true;
					}

					blockState = this.world.getBlockState(blockPos2);
					if (blockState.isAir()) {
						return true;
					}

					if (!PistonBlock.isMovable(blockState, this.world, blockPos2, this.motionDirection, true, this.motionDirection) || blockPos2.equals(this.posFrom)) {
						return false;
					}

					if (blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
						this.brokenBlocks.add(blockPos2);
						return true;
					}

					if (this.movedBlocks.size() >= 12) {
						return false;
					}

					this.movedBlocks.add(blockPos2);
					j++;
					k++;
				}
			}
		}
	}

	private void setMovedBlocks(int from, int to) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();
		List<BlockPos> list2 = Lists.<BlockPos>newArrayList();
		List<BlockPos> list3 = Lists.<BlockPos>newArrayList();
		list.addAll(this.movedBlocks.subList(0, to));
		list2.addAll(this.movedBlocks.subList(this.movedBlocks.size() - from, this.movedBlocks.size()));
		list3.addAll(this.movedBlocks.subList(to, this.movedBlocks.size() - from));
		this.movedBlocks.clear();
		this.movedBlocks.addAll(list);
		this.movedBlocks.addAll(list2);
		this.movedBlocks.addAll(list3);
	}

	private boolean tryMoveAdjacentBlock(BlockPos pos) {
		BlockState blockState = this.world.getBlockState(pos);

		for (Direction direction : Direction.values()) {
			if (direction.getAxis() != this.motionDirection.getAxis()) {
				BlockPos blockPos = pos.offset(direction);
				BlockState blockState2 = this.world.getBlockState(blockPos);
				if (isAdjacentBlockStuck(blockState2, blockState) && !this.tryMove(blockPos, direction)) {
					return false;
				}
			}
		}

		return true;
	}

	public Direction getMotionDirection() {
		return this.motionDirection;
	}

	public List<BlockPos> getMovedBlocks() {
		return this.movedBlocks;
	}

	public List<BlockPos> getBrokenBlocks() {
		return this.brokenBlocks;
	}
}
