package net.minecraft.block.piston;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PistonHandler {
	private final World world;
	private final BlockPos posFrom;
	private final boolean field_12247;
	private final BlockPos posTo;
	private final Direction direction;
	private final List<BlockPos> movedBlocks = Lists.<BlockPos>newArrayList();
	private final List<BlockPos> brokenBlocks = Lists.<BlockPos>newArrayList();
	private final Direction field_12248;

	public PistonHandler(World world, BlockPos pos, Direction dir, boolean bl) {
		this.world = world;
		this.posFrom = pos;
		this.field_12248 = dir;
		this.field_12247 = bl;
		if (bl) {
			this.direction = dir;
			this.posTo = pos.offset(dir);
		} else {
			this.direction = dir.getOpposite();
			this.posTo = pos.method_10079(dir, 2);
		}
	}

	public boolean calculatePush() {
		this.movedBlocks.clear();
		this.brokenBlocks.clear();
		BlockState blockState = this.world.getBlockState(this.posTo);
		if (!PistonBlock.isMovable(blockState, this.world, this.posTo, this.direction, false, this.field_12248)) {
			if (this.field_12247 && blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
				this.brokenBlocks.add(this.posTo);
				return true;
			} else {
				return false;
			}
		} else if (!this.tryMove(this.posTo, this.direction)) {
			return false;
		} else {
			for (int i = 0; i < this.movedBlocks.size(); i++) {
				BlockPos blockPos = (BlockPos)this.movedBlocks.get(i);
				if (method_23367(this.world.getBlockState(blockPos).getBlock()) && !this.method_11538(blockPos)) {
					return false;
				}
			}

			return true;
		}
	}

	private static boolean method_23367(Block block) {
		return block == Blocks.SLIME_BLOCK || block == Blocks.HONEY_BLOCK;
	}

	private static boolean method_23675(Block block, Block block2) {
		if (block == Blocks.HONEY_BLOCK && block2 == Blocks.SLIME_BLOCK) {
			return false;
		} else {
			return block == Blocks.SLIME_BLOCK && block2 == Blocks.HONEY_BLOCK ? false : method_23367(block) || method_23367(block2);
		}
	}

	private boolean tryMove(BlockPos blockPos, Direction direction) {
		BlockState blockState = this.world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (blockState.isAir()) {
			return true;
		} else if (!PistonBlock.isMovable(blockState, this.world, blockPos, this.direction, false, direction)) {
			return true;
		} else if (blockPos.equals(this.posFrom)) {
			return true;
		} else if (this.movedBlocks.contains(blockPos)) {
			return true;
		} else {
			int i = 1;
			if (i + this.movedBlocks.size() > 12) {
				return false;
			} else {
				while (method_23367(block)) {
					BlockPos blockPos2 = blockPos.method_10079(this.direction.getOpposite(), i);
					Block block2 = block;
					blockState = this.world.getBlockState(blockPos2);
					block = blockState.getBlock();
					if (blockState.isAir()
						|| !method_23675(block2, block)
						|| !PistonBlock.isMovable(blockState, this.world, blockPos2, this.direction, false, this.direction.getOpposite())
						|| blockPos2.equals(this.posFrom)) {
						break;
					}

					if (++i + this.movedBlocks.size() > 12) {
						return false;
					}
				}

				int j = 0;

				for (int k = i - 1; k >= 0; k--) {
					this.movedBlocks.add(blockPos.method_10079(this.direction.getOpposite(), k));
					j++;
				}

				int k = 1;

				while (true) {
					BlockPos blockPos3 = blockPos.method_10079(this.direction, k);
					int l = this.movedBlocks.indexOf(blockPos3);
					if (l > -1) {
						this.method_11539(j, l);

						for (int m = 0; m <= l + j; m++) {
							BlockPos blockPos4 = (BlockPos)this.movedBlocks.get(m);
							if (method_23367(this.world.getBlockState(blockPos4).getBlock()) && !this.method_11538(blockPos4)) {
								return false;
							}
						}

						return true;
					}

					blockState = this.world.getBlockState(blockPos3);
					if (blockState.isAir()) {
						return true;
					}

					if (!PistonBlock.isMovable(blockState, this.world, blockPos3, this.direction, true, this.direction) || blockPos3.equals(this.posFrom)) {
						return false;
					}

					if (blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
						this.brokenBlocks.add(blockPos3);
						return true;
					}

					if (this.movedBlocks.size() >= 12) {
						return false;
					}

					this.movedBlocks.add(blockPos3);
					j++;
					k++;
				}
			}
		}
	}

	private void method_11539(int i, int j) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();
		List<BlockPos> list2 = Lists.<BlockPos>newArrayList();
		List<BlockPos> list3 = Lists.<BlockPos>newArrayList();
		list.addAll(this.movedBlocks.subList(0, j));
		list2.addAll(this.movedBlocks.subList(this.movedBlocks.size() - i, this.movedBlocks.size()));
		list3.addAll(this.movedBlocks.subList(j, this.movedBlocks.size() - i));
		this.movedBlocks.clear();
		this.movedBlocks.addAll(list);
		this.movedBlocks.addAll(list2);
		this.movedBlocks.addAll(list3);
	}

	private boolean method_11538(BlockPos blockPos) {
		BlockState blockState = this.world.getBlockState(blockPos);

		for (Direction direction : Direction.values()) {
			if (direction.getAxis() != this.direction.getAxis()) {
				BlockPos blockPos2 = blockPos.offset(direction);
				BlockState blockState2 = this.world.getBlockState(blockPos2);
				if (method_23675(blockState2.getBlock(), blockState.getBlock()) && !this.tryMove(blockPos2, direction)) {
					return false;
				}
			}
		}

		return true;
	}

	public List<BlockPos> getMovedBlocks() {
		return this.movedBlocks;
	}

	public List<BlockPos> getBrokenBlocks() {
		return this.brokenBlocks;
	}
}
