/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public interface NeighborUpdater {
    public static final Direction[] UPDATE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH};
    public static final NeighborUpdater NOOP = new NeighborUpdater(){

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

    public void updateNeighbor(BlockPos var1, Block var2, BlockPos var3);

    public void updateNeighbor(BlockState var1, BlockPos var2, Block var3, BlockPos var4, boolean var5);

    default public void updateNeighbors(BlockPos pos, Block sourceBlock, @Nullable Direction except) {
        for (Direction direction : UPDATE_ORDER) {
            if (direction == except) continue;
            this.updateNeighbor(pos.offset(direction), sourceBlock, pos);
        }
    }

    public static void tryNeighborUpdate(ServerWorld world, BlockState state, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        try {
            state.neighborUpdate(world, pos, sourceBlock, sourcePos, notify);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Exception while updating neighbours");
            CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
            crashReportSection.add("Source block type", () -> {
                try {
                    return String.format("ID #%s (%s // %s)", Registry.BLOCK.getId(sourceBlock), sourceBlock.getTranslationKey(), sourceBlock.getClass().getCanonicalName());
                } catch (Throwable throwable) {
                    return "ID #" + Registry.BLOCK.getId(sourceBlock);
                }
            });
            CrashReportSection.addBlockInfo(crashReportSection, world, pos, state);
            throw new CrashException(crashReport);
        }
    }
}

