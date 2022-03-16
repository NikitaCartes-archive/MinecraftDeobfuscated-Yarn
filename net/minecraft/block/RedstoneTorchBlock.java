/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class RedstoneTorchBlock
extends TorchBlock {
    public static final BooleanProperty LIT = Properties.LIT;
    private static final Map<BlockView, List<BurnoutEntry>> BURNOUT_MAP = new WeakHashMap<BlockView, List<BurnoutEntry>>();
    public static final int field_31227 = 60;
    public static final int field_31228 = 8;
    public static final int field_31229 = 160;
    private static final int SCHEDULED_TICK_DELAY = 2;

    protected RedstoneTorchBlock(AbstractBlock.Settings settings) {
        super(settings, DustParticleEffect.DEFAULT);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LIT, true));
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved) {
            return;
        }
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get(LIT).booleanValue() && Direction.UP != direction) {
            return 15;
        }
        return 0;
    }

    protected boolean shouldUnpower(World world, BlockPos pos, BlockState state) {
        return world.isEmittingRedstonePower(pos.down(), Direction.DOWN);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean bl = this.shouldUnpower(world, pos, state);
        List<BurnoutEntry> list = BURNOUT_MAP.get(world);
        while (list != null && !list.isEmpty() && world.getTime() - list.get((int)0).time > 60L) {
            list.remove(0);
        }
        if (state.get(LIT).booleanValue()) {
            if (bl) {
                world.setBlockState(pos, (BlockState)state.with(LIT, false), Block.NOTIFY_ALL);
                if (RedstoneTorchBlock.isBurnedOut(world, pos, true)) {
                    world.syncWorldEvent(WorldEvents.REDSTONE_TORCH_BURNS_OUT, pos, 0);
                    world.createAndScheduleBlockTick(pos, world.getBlockState(pos).getBlock(), 160);
                }
            }
        } else if (!bl && !RedstoneTorchBlock.isBurnedOut(world, pos, false)) {
            world.setBlockState(pos, (BlockState)state.with(LIT, true), Block.NOTIFY_ALL);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (state.get(LIT).booleanValue() == this.shouldUnpower(world, pos, state) && !world.getBlockTickScheduler().isTicking(pos, this)) {
            world.createAndScheduleBlockTick(pos, this, 2);
        }
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (direction == Direction.DOWN) {
            return state.getWeakRedstonePower(world, pos, direction);
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.get(LIT).booleanValue()) {
            return;
        }
        double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        double e = (double)pos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2;
        double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        world.addParticle(this.particle, d, e, f, 0.0, 0.0, 0.0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    private static boolean isBurnedOut(World world2, BlockPos pos, boolean addNew) {
        List list = BURNOUT_MAP.computeIfAbsent(world2, world -> Lists.newArrayList());
        if (addNew) {
            list.add(new BurnoutEntry(pos.toImmutable(), world2.getTime()));
        }
        int i = 0;
        for (int j = 0; j < list.size(); ++j) {
            BurnoutEntry burnoutEntry = (BurnoutEntry)list.get(j);
            if (!burnoutEntry.pos.equals(pos) || ++i < 8) continue;
            return true;
        }
        return false;
    }

    public static class BurnoutEntry {
        final BlockPos pos;
        final long time;

        public BurnoutEntry(BlockPos pos, long time) {
            this.pos = pos;
            this.time = time;
        }
    }
}

