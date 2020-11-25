/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class LeveledCauldronBlock
extends AbstractCauldronBlock {
    public static final IntProperty LEVEL = Properties.LEVEL_3;
    public static final Predicate<Biome.Precipitation> RAIN_PREDICATE = precipitation -> precipitation == Biome.Precipitation.RAIN;
    public static final Predicate<Biome.Precipitation> SNOW_PREDICATE = precipitation -> precipitation == Biome.Precipitation.SNOW;
    private final Predicate<Biome.Precipitation> precipitationPredicate;

    public LeveledCauldronBlock(AbstractBlock.Settings settings, Predicate<Biome.Precipitation> precipitationPredicate, Map<Item, CauldronBehavior> behaviorMap) {
        super(settings, behaviorMap);
        this.precipitationPredicate = precipitationPredicate;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LEVEL, 1));
    }

    public boolean method_32766(BlockState blockState) {
        return blockState.get(LEVEL) == 3;
    }

    @Override
    protected boolean method_32765(Fluid fluid) {
        return fluid == Fluids.WATER && this.precipitationPredicate == RAIN_PREDICATE;
    }

    @Override
    protected double getFluidHeight(BlockState state) {
        return (6.0 + (double)state.get(LEVEL).intValue() * 3.0) / 16.0;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && entity.isOnFire() && this.isEntityTouchingFluid(state, pos, entity)) {
            entity.extinguish();
            LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
        }
    }

    public static void decrementFluidLevel(BlockState state, World world, BlockPos pos) {
        int i = state.get(LEVEL) - 1;
        world.setBlockState(pos, i == 0 ? Blocks.CAULDRON.getDefaultState() : (BlockState)state.with(LEVEL, i));
    }

    @Override
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        if (!CauldronBlock.canFillWithPrecipitation(world) || state.get(LEVEL) == 3 || !this.precipitationPredicate.test(precipitation)) {
            return;
        }
        world.setBlockState(pos, (BlockState)state.cycle(LEVEL));
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(LEVEL);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    protected void method_32764(BlockState blockState, World world, BlockPos blockPos, Fluid fluid) {
        if (this.method_32766(blockState)) {
            return;
        }
        world.setBlockState(blockPos, (BlockState)blockState.with(LEVEL, blockState.get(LEVEL) + 1));
        world.syncWorldEvent(1047, blockPos, 0);
    }
}

