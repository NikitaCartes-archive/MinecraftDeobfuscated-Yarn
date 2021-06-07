/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CaveVines;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CaveVinesHeadBlock
extends AbstractPlantStemBlock
implements Fertilizable,
CaveVines {
    private static final float GROW_CHANCE = 0.11f;

    public CaveVinesHeadBlock(AbstractBlock.Settings settings) {
        super(settings, Direction.DOWN, SHAPE, false, 0.1);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0)).with(BERRIES, false));
    }

    @Override
    protected int getGrowthLength(Random random) {
        return 1;
    }

    @Override
    protected boolean chooseStemState(BlockState state) {
        return state.isAir();
    }

    @Override
    protected Block getPlant() {
        return Blocks.CAVE_VINES_PLANT;
    }

    @Override
    protected BlockState copyState(BlockState from, BlockState to) {
        return (BlockState)to.with(BERRIES, from.get(BERRIES));
    }

    @Override
    protected BlockState age(BlockState state, Random random) {
        return (BlockState)super.age(state, random).with(BERRIES, random.nextFloat() < 0.11f);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(Items.GLOW_BERRIES);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return CaveVines.pickBerries(state, world, pos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(BERRIES);
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return state.get(BERRIES) == false;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.setBlockState(pos, (BlockState)state.with(BERRIES, true), Block.NOTIFY_LISTENERS);
    }
}

