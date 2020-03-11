/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractPlantPartBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AbstractPlantStemBlock
extends AbstractPlantPartBlock
implements Fertilizable {
    public static final IntProperty AGE = Properties.AGE_25;
    private final double growthChance;

    protected AbstractPlantStemBlock(Block.Settings settings, Direction growthDirection, VoxelShape outlineShape, boolean tickWater, double growthChance) {
        super(settings, growthDirection, outlineShape, tickWater);
        this.growthChance = growthChance;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    public BlockState getRandomGrowthState(IWorld world) {
        return (BlockState)this.getDefaultState().with(AGE, world.getRandom().nextInt(25));
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos blockPos;
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
            return;
        }
        if (state.get(AGE) < 25 && random.nextDouble() < this.growthChance && this.chooseStemState(world.getBlockState(blockPos = pos.offset(this.growthDirection)))) {
            world.setBlockState(blockPos, (BlockState)state.cycle(AGE));
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (facing == this.growthDirection.getOpposite() && !state.canPlaceAt(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        if (facing == this.growthDirection && neighborState.getBlock() == this) {
            return this.getPlant().getDefaultState();
        }
        if (this.tickWater) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return this.chooseStemState(world.getBlockState(pos.offset(this.growthDirection)));
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockPos blockPos = pos.offset(this.growthDirection);
        int i = Math.min(state.get(AGE) + 1, 25);
        for (double d = 1.0; this.chooseStemState(world.getBlockState(blockPos)) && random.nextDouble() < d; d *= 0.94) {
            world.setBlockState(blockPos, (BlockState)state.with(AGE, i));
            blockPos = blockPos.offset(this.growthDirection);
            i = Math.min(i + 1, 25);
        }
    }

    protected abstract boolean chooseStemState(BlockState var1);

    @Override
    protected AbstractPlantStemBlock getStem() {
        return this;
    }
}

