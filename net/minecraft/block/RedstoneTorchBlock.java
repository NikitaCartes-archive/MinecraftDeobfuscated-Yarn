/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;

public class RedstoneTorchBlock
extends TorchBlock {
    public static final BooleanProperty LIT = Properties.LIT;
    private static final Map<BlockView, List<BurnoutEntry>> BURNOUT_MAP = new WeakHashMap<BlockView, List<BurnoutEntry>>();

    protected RedstoneTorchBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LIT, true));
    }

    @Override
    public int getTickRate(CollisionView collisionView) {
        return 2;
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(blockPos.offset(direction), this);
        }
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (bl) {
            return;
        }
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(blockPos.offset(direction), this);
        }
    }

    @Override
    public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
        if (blockState.get(LIT).booleanValue() && Direction.UP != direction) {
            return 15;
        }
        return 0;
    }

    protected boolean shouldUnpower(World world, BlockPos blockPos, BlockState blockState) {
        return world.isEmittingRedstonePower(blockPos.down(), Direction.DOWN);
    }

    @Override
    public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        RedstoneTorchBlock.update(blockState, world, blockPos, random, this.shouldUnpower(world, blockPos, blockState));
    }

    public static void update(BlockState blockState, World world, BlockPos blockPos, Random random, boolean bl) {
        List<BurnoutEntry> list = BURNOUT_MAP.get(world);
        while (list != null && !list.isEmpty() && world.getTime() - list.get(0).time > 60L) {
            list.remove(0);
        }
        if (blockState.get(LIT).booleanValue()) {
            if (bl) {
                world.setBlockState(blockPos, (BlockState)blockState.with(LIT, false), 3);
                if (RedstoneTorchBlock.isBurnedOut(world, blockPos, true)) {
                    world.playLevelEvent(1502, blockPos, 0);
                    world.getBlockTickScheduler().schedule(blockPos, world.getBlockState(blockPos).getBlock(), 160);
                }
            }
        } else if (!bl && !RedstoneTorchBlock.isBurnedOut(world, blockPos, false)) {
            world.setBlockState(blockPos, (BlockState)blockState.with(LIT, true), 3);
        }
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (blockState.get(LIT).booleanValue() == this.shouldUnpower(world, blockPos, blockState) && !world.getBlockTickScheduler().isTicking(blockPos, this)) {
            world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
        }
    }

    @Override
    public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
        if (direction == Direction.DOWN) {
            return blockState.getWeakRedstonePower(blockView, blockPos, direction);
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState blockState) {
        return true;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (!blockState.get(LIT).booleanValue()) {
            return;
        }
        double d = (double)blockPos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        double e = (double)blockPos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2;
        double f = (double)blockPos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        world.addParticle(DustParticleEffect.RED, d, e, f, 0.0, 0.0, 0.0);
    }

    @Override
    public int getLuminance(BlockState blockState) {
        return blockState.get(LIT) != false ? super.getLuminance(blockState) : 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    private static boolean isBurnedOut(World world, BlockPos blockPos, boolean bl) {
        List list = BURNOUT_MAP.computeIfAbsent(world, blockView -> Lists.newArrayList());
        if (bl) {
            list.add(new BurnoutEntry(blockPos.toImmutable(), world.getTime()));
        }
        int i = 0;
        for (int j = 0; j < list.size(); ++j) {
            BurnoutEntry burnoutEntry = (BurnoutEntry)list.get(j);
            if (!burnoutEntry.pos.equals(blockPos) || ++i < 8) continue;
            return true;
        }
        return false;
    }

    public static class BurnoutEntry {
        private final BlockPos pos;
        private final long time;

        public BurnoutEntry(BlockPos blockPos, long l) {
            this.pos = blockPos;
            this.time = l;
        }
    }
}

