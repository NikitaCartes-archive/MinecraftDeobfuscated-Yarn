/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPlantPartBlock
extends Block {
    protected final Direction growthDirection;
    protected final boolean tickWater;
    protected final VoxelShape outlineShape;

    protected AbstractPlantPartBlock(AbstractBlock.Settings settings, Direction growthDirection, VoxelShape outlineShape, boolean tickWater) {
        super(settings);
        this.growthDirection = growthDirection;
        this.outlineShape = outlineShape;
        this.tickWater = tickWater;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(this.growthDirection));
        if (blockState.isOf(this.getStem()) || blockState.isOf(this.getPlant())) {
            return this.getPlant().getDefaultState();
        }
        return this.getRandomGrowthState(ctx.getWorld());
    }

    public BlockState getRandomGrowthState(WorldAccess worldAccess) {
        return this.getDefaultState();
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.offset(this.growthDirection.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        if (!this.canAttachTo(blockState)) {
            return false;
        }
        return blockState.isOf(this.getStem()) || blockState.isOf(this.getPlant()) || blockState.isSideSolidFullSquare(world, blockPos, this.growthDirection);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    protected boolean canAttachTo(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.outlineShape;
    }

    protected abstract AbstractPlantStemBlock getStem();

    protected abstract Block getPlant();
}

