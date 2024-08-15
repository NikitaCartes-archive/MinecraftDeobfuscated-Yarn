package net.minecraft.world;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.block.WireOrientation;

public class DefaultRedstoneController extends RedstoneController {
	public DefaultRedstoneController(RedstoneWireBlock redstoneWireBlock) {
		super(redstoneWireBlock);
	}

	@Override
	public void update(World world, BlockPos pos, BlockState state, @Nullable WireOrientation orientation) {
		int i = this.calculateTotalPowerAt(world, pos);
		if ((Integer)state.get(RedstoneWireBlock.POWER) != i) {
			if (world.getBlockState(pos) == state) {
				world.setBlockState(pos, state.with(RedstoneWireBlock.POWER, Integer.valueOf(i)), Block.NOTIFY_LISTENERS);
			}

			Set<BlockPos> set = Sets.<BlockPos>newHashSet();
			set.add(pos);

			for (Direction direction : Direction.values()) {
				set.add(pos.offset(direction));
			}

			for (BlockPos blockPos : set) {
				world.updateNeighborsAlways(blockPos, this.wire);
			}
		}
	}

	private int calculateTotalPowerAt(World world, BlockPos pos) {
		int i = this.getStrongPowerAt(world, pos);
		return i == 15 ? i : Math.max(i, this.calculateWirePowerAt(world, pos));
	}
}
