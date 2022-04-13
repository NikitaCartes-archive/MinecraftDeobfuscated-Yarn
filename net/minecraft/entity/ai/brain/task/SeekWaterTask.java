/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

public class SeekWaterTask
extends Task<PathAwareEntity> {
    private final int range;
    private final float speed;
    private long seekWaterTime;

    public SeekWaterTask(int range, float speed) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED));
        this.range = range;
        this.speed = speed;
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        this.seekWaterTime = l + 20L + 2L;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
        return !pathAwareEntity.world.getFluidState(pathAwareEntity.getBlockPos()).isIn(FluidTags.WATER);
    }

    @Override
    protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        if (l < this.seekWaterTime) {
            return;
        }
        BlockPos blockPos = null;
        BlockPos blockPos2 = null;
        BlockPos blockPos3 = pathAwareEntity.getBlockPos();
        Iterable<BlockPos> iterable = BlockPos.iterateOutwards(blockPos3, this.range, this.range, this.range);
        for (BlockPos blockPos4 : iterable) {
            if (blockPos4.getX() == blockPos3.getX() && blockPos4.getZ() == blockPos3.getZ()) continue;
            BlockState blockState = pathAwareEntity.world.getBlockState(blockPos4.up());
            BlockState blockState2 = pathAwareEntity.world.getBlockState(blockPos4);
            if (!blockState2.isOf(Blocks.WATER)) continue;
            if (blockState.isAir()) {
                blockPos = blockPos4.toImmutable();
                break;
            }
            if (blockPos2 != null || blockPos4.isWithinDistance(pathAwareEntity.getPos(), 1.5)) continue;
            blockPos2 = blockPos4.toImmutable();
        }
        if (blockPos == null) {
            blockPos = blockPos2;
        }
        if (blockPos != null) {
            this.seekWaterTime = l + 40L;
            LookTargetUtil.walkTowards((LivingEntity)pathAwareEntity, blockPos, this.speed, 0);
        }
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (PathAwareEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (PathAwareEntity)entity, time);
    }
}

