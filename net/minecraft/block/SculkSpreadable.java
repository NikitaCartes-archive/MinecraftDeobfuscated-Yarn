/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Collection;
import java.util.Random;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SculkVeinBlock;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public interface SculkSpreadable {
    public static final SculkSpreadable VEIN_ONLY_SPREADER = new SculkSpreadable(){

        @Override
        public boolean spread(WorldAccess world, BlockPos pos, BlockState state, @Nullable Collection<Direction> directions, boolean markForPostProcessing) {
            if (directions == null) {
                return ((SculkVeinBlock)Blocks.SCULK_VEIN).getSamePositionOnlyGrower().grow(world.getBlockState(pos), world, pos, markForPostProcessing) > 0L;
            }
            if (!directions.isEmpty()) {
                if (state.isAir() || state.getFluidState().isOf(Fluids.WATER)) {
                    return SculkVeinBlock.place(world, pos, state, directions);
                }
                return false;
            }
            return SculkSpreadable.super.spread(world, pos, state, directions, markForPostProcessing);
        }

        @Override
        public int spread(SculkSpreadManager.Cursor cursor, WorldAccess world, BlockPos catalystPos, Random random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock) {
            return cursor.getDecay() > 0 ? cursor.getCharge() : 0;
        }

        @Override
        public int getDecay(int oldDecay) {
            return Math.max(oldDecay - 1, 0);
        }
    };

    default public byte getUpdate() {
        return 1;
    }

    default public void spreadAtSamePosition(WorldAccess world, BlockState state, BlockPos pos, Random random) {
    }

    default public boolean method_41470(WorldAccess worldAccess, BlockPos blockPos, Random random) {
        return false;
    }

    default public boolean spread(WorldAccess world, BlockPos pos, BlockState state, @Nullable Collection<Direction> directions, boolean markForPostProcessing) {
        return ((AbstractLichenBlock)Blocks.SCULK_VEIN).getGrower().grow(state, world, pos, markForPostProcessing) > 0L;
    }

    default public boolean shouldConvertToSpreadable() {
        return true;
    }

    default public int getDecay(int oldDecay) {
        return 1;
    }

    public int spread(SculkSpreadManager.Cursor var1, WorldAccess var2, BlockPos var3, Random var4, SculkSpreadManager var5, boolean var6);
}

