/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class LeavesBlock
extends Block {
    public static final IntProperty DISTANCE = Properties.DISTANCE_1_7;
    public static final BooleanProperty PERSISTENT = Properties.PERSISTENT;

    public LeavesBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(DISTANCE, 7)).with(PERSISTENT, false));
    }

    @Override
    public boolean hasRandomTicks(BlockState blockState) {
        return blockState.get(DISTANCE) == 7 && blockState.get(PERSISTENT) == false;
    }

    @Override
    public void randomTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if (!blockState.get(PERSISTENT).booleanValue() && blockState.get(DISTANCE) == 7) {
            LeavesBlock.dropStacks(blockState, serverWorld, blockPos);
            serverWorld.removeBlock(blockPos, false);
        }
    }

    @Override
    public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        serverWorld.setBlockState(blockPos, LeavesBlock.updateDistanceFromLogs(blockState, serverWorld, blockPos), 3);
    }

    @Override
    public int getOpacity(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return 1;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        int i = LeavesBlock.getDistanceFromLog(blockState2) + 1;
        if (i != 1 || blockState.get(DISTANCE) != i) {
            iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
        }
        return blockState;
    }

    private static BlockState updateDistanceFromLogs(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
        int i = 7;
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (Direction direction : Direction.values()) {
                pooledMutable.method_10114(blockPos).method_10118(direction);
                i = Math.min(i, LeavesBlock.getDistanceFromLog(iWorld.getBlockState(pooledMutable)) + 1);
                if (i != 1) continue;
                break;
            }
        }
        return (BlockState)blockState.with(DISTANCE, i);
    }

    private static int getDistanceFromLog(BlockState blockState) {
        if (BlockTags.LOGS.contains(blockState.getBlock())) {
            return 0;
        }
        if (blockState.getBlock() instanceof LeavesBlock) {
            return blockState.get(DISTANCE);
        }
        return 7;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (!world.hasRain(blockPos.up())) {
            return;
        }
        if (random.nextInt(15) != 1) {
            return;
        }
        BlockPos blockPos2 = blockPos.method_10074();
        BlockState blockState2 = world.getBlockState(blockPos2);
        if (blockState2.isOpaque() && blockState2.isSideSolidFullSquare(world, blockPos2, Direction.UP)) {
            return;
        }
        double d = (float)blockPos.getX() + random.nextFloat();
        double e = (double)blockPos.getY() - 0.05;
        double f = (float)blockPos.getZ() + random.nextFloat();
        world.addParticle(ParticleTypes.DRIPPING_WATER, d, e, f, 0.0, 0.0, 0.0);
    }

    @Override
    public boolean canSuffocate(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return false;
    }

    @Override
    public boolean allowsSpawning(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
        return entityType == EntityType.OCELOT || entityType == EntityType.PARROT;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, PERSISTENT);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return LeavesBlock.updateDistanceFromLogs((BlockState)this.getDefaultState().with(PERSISTENT, true), itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos());
    }
}

