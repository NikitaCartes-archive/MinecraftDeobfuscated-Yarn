package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.block.WireOrientation;

public abstract class RedstoneController {
	protected final RedstoneWireBlock wire;

	protected RedstoneController(RedstoneWireBlock wire) {
		this.wire = wire;
	}

	public abstract void update(World world, BlockPos pos, BlockState state, @Nullable WireOrientation orientation);

	protected int getStrongPowerAt(World world, BlockPos pos) {
		return this.wire.getStrongPower(world, pos);
	}

	protected int getWirePowerAt(BlockPos world, BlockState pos) {
		return pos.isOf(this.wire) ? (Integer)pos.get(RedstoneWireBlock.POWER) : 0;
	}

	protected int calculateWirePowerAt(World world, BlockPos pos) {
		int i = 0;

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			BlockState blockState = world.getBlockState(blockPos);
			i = Math.max(i, this.getWirePowerAt(blockPos, blockState));
			BlockPos blockPos2 = pos.up();
			if (blockState.isSolidBlock(world, blockPos) && !world.getBlockState(blockPos2).isSolidBlock(world, blockPos2)) {
				BlockPos blockPos3 = blockPos.up();
				i = Math.max(i, this.getWirePowerAt(blockPos3, world.getBlockState(blockPos3)));
			} else if (!blockState.isSolidBlock(world, blockPos)) {
				BlockPos blockPos3 = blockPos.down();
				i = Math.max(i, this.getWirePowerAt(blockPos3, world.getBlockState(blockPos3)));
			}
		}

		return Math.max(0, i - 1);
	}
}
