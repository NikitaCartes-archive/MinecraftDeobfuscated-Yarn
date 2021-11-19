/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class WalkTask
extends Task<PathAwareEntity> {
    private static final int MIN_RUN_TIME = 100;
    private static final int MAX_RUN_TIME = 120;
    private static final int HORIZONTAL_RANGE = 5;
    private static final int VERTICAL_RANGE = 4;
    private final float speed;

    public WalkTask(float speed) {
        super(ImmutableMap.of(MemoryModuleType.HURT_BY, MemoryModuleState.VALUE_PRESENT), 100, 120);
        this.speed = speed;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        return true;
    }

    @Override
    protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        pathAwareEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        Vec3d vec3d;
        if (pathAwareEntity.getNavigation().isIdle() && (vec3d = this.findTarget(pathAwareEntity, serverWorld)) != null) {
            pathAwareEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.speed, 0));
        }
    }

    @Nullable
    private Vec3d findTarget(PathAwareEntity entity, ServerWorld world) {
        Optional<Vec3d> optional;
        if (entity.isOnFire() && (optional = this.findClosestWater(world, entity).map(Vec3d::ofBottomCenter)).isPresent()) {
            return optional.get();
        }
        return FuzzyTargeting.find(entity, 5, 4);
    }

    private Optional<BlockPos> findClosestWater(BlockView world, Entity entity) {
        BlockPos blockPos = entity.getBlockPos();
        if (!world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()) {
            return Optional.empty();
        }
        return BlockPos.findClosest(blockPos, 5, 1, pos -> world.getFluidState((BlockPos)pos).isIn(FluidTags.WATER));
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (PathAwareEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (PathAwareEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (PathAwareEntity)entity, time);
    }
}

