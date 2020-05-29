/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class RepeaterBlock
extends AbstractRedstoneGateBlock {
    public static final BooleanProperty LOCKED = Properties.LOCKED;
    public static final IntProperty DELAY = Properties.DELAY;

    protected RepeaterBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(DELAY, 1)).with(LOCKED, false)).with(POWERED, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.abilities.allowModifyWorld) {
            return ActionResult.PASS;
        }
        world.setBlockState(pos, (BlockState)state.cycle(DELAY), 3);
        return ActionResult.method_29236(world.isClient);
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return state.get(DELAY) * 2;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = super.getPlacementState(ctx);
        return (BlockState)blockState.with(LOCKED, this.isLocked(ctx.getWorld(), ctx.getBlockPos(), blockState));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (!world.isClient() && direction.getAxis() != state.get(FACING).getAxis()) {
            return (BlockState)state.with(LOCKED, this.isLocked(world, pos, state));
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public boolean isLocked(WorldView worldView, BlockPos pos, BlockState state) {
        return this.getMaxInputLevelSides(worldView, pos, state) > 0;
    }

    @Override
    protected boolean isValidInput(BlockState state) {
        return RepeaterBlock.isRedstoneGate(state);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.get(POWERED).booleanValue()) {
            return;
        }
        Direction direction = state.get(FACING);
        double d = (double)((float)pos.getX() + 0.5f) + (double)(random.nextFloat() - 0.5f) * 0.2;
        double e = (double)((float)pos.getY() + 0.4f) + (double)(random.nextFloat() - 0.5f) * 0.2;
        double f = (double)((float)pos.getZ() + 0.5f) + (double)(random.nextFloat() - 0.5f) * 0.2;
        float g = -5.0f;
        if (random.nextBoolean()) {
            g = state.get(DELAY) * 2 - 1;
        }
        double h = (g /= 16.0f) * (float)direction.getOffsetX();
        double i = g * (float)direction.getOffsetZ();
        world.addParticle(DustParticleEffect.RED, d + h, e, f + i, 0.0, 0.0, 0.0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, DELAY, LOCKED, POWERED);
    }
}

