package net.minecraft.world.block;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public interface NeighborUpdater {
	Direction[] UPDATE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH};
	NeighborUpdater NOOP = new NeighborUpdater() {
		@Override
		public void updateNeighbor(BlockPos pos, Block sourceBlock, BlockPos sourcePos) {
		}

		@Override
		public void updateNeighbor(BlockState state, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		}

		@Override
		public void updateNeighbors(BlockPos pos, Block sourceBlock, @Nullable Direction except) {
		}
	};

	void updateNeighbor(BlockPos pos, Block sourceBlock, BlockPos sourcePos);

	void updateNeighbor(BlockState state, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify);

	default void updateNeighbors(BlockPos pos, Block sourceBlock, @Nullable Direction except) {
		for (Direction direction : UPDATE_ORDER) {
			if (direction != except) {
				this.updateNeighbor(pos.offset(direction), sourceBlock, pos);
			}
		}
	}

	static void tryNeighborUpdate(ServerWorld world, BlockState state, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		try {
			state.neighborUpdate(world, pos, sourceBlock, sourcePos, notify);
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Exception while updating neighbours");
			CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
			crashReportSection.add("Source block type", (CrashCallable<String>)(() -> {
				try {
					return String.format("ID #%s (%s // %s)", Registry.BLOCK.getId(sourceBlock), sourceBlock.getTranslationKey(), sourceBlock.getClass().getCanonicalName());
				} catch (Throwable var2) {
					return "ID #" + Registry.BLOCK.getId(sourceBlock);
				}
			}));
			CrashReportSection.addBlockInfo(crashReportSection, world, pos, state);
			throw new CrashException(crashReport);
		}
	}
}
