package net.minecraft.world.block;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public interface NeighborUpdater {
	Direction[] UPDATE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH};

	void replaceWithStateForNeighborUpdate(Direction direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int flags, int maxUpdateDepth);

	void updateNeighbor(BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation);

	void updateNeighbor(BlockState state, BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation, boolean notify);

	default void updateNeighbors(BlockPos pos, Block sourceBlock, @Nullable Direction except, @Nullable WireOrientation orientation) {
		for (Direction direction : UPDATE_ORDER) {
			if (direction != except) {
				this.updateNeighbor(pos.offset(direction), sourceBlock, null);
			}
		}
	}

	static void replaceWithStateForNeighborUpdate(
		WorldAccess world, Direction direction, BlockPos pos, BlockPos neighborPos, BlockState neighborState, int flags, int maxUpdateDepth
	) {
		BlockState blockState = world.getBlockState(pos);
		if ((flags & Block.SKIP_REDSTONE_WIRE_STATE_REPLACEMENT) == 0 || !blockState.isOf(Blocks.REDSTONE_WIRE)) {
			BlockState blockState2 = blockState.getStateForNeighborUpdate(world, world, pos, direction, neighborPos, neighborState, world.getRandom());
			Block.replace(blockState, blockState2, world, pos, flags, maxUpdateDepth);
		}
	}

	static void tryNeighborUpdate(World world, BlockState state, BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation, boolean notify) {
		try {
			state.neighborUpdate(world, pos, sourceBlock, orientation, notify);
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Exception while updating neighbours");
			CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
			crashReportSection.add(
				"Source block type",
				(CrashCallable<String>)(() -> {
					try {
						return String.format(
							Locale.ROOT, "ID #%s (%s // %s)", Registries.BLOCK.getId(sourceBlock), sourceBlock.getTranslationKey(), sourceBlock.getClass().getCanonicalName()
						);
					} catch (Throwable var2) {
						return "ID #" + Registries.BLOCK.getId(sourceBlock);
					}
				})
			);
			CrashReportSection.addBlockInfo(crashReportSection, world, pos, state);
			throw new CrashException(crashReport);
		}
	}
}
