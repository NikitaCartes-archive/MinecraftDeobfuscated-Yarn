/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class JumpInBedTask
extends Task<MobEntity> {
    private final float walkSpeed;
    @Nullable
    private BlockPos bedPos;
    private int ticksOutOfBedUntilStopped;
    private int jumpsRemaining;
    private int ticksToNextJump;

    public JumpInBedTask(float walkSpeed) {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_BED, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
        this.walkSpeed = walkSpeed;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
        return mobEntity.isBaby() && this.shouldStartJumping(serverWorld, mobEntity);
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        super.run(serverWorld, mobEntity, l);
        this.getNearestBed(mobEntity).ifPresent(blockPos -> {
            this.bedPos = blockPos;
            this.ticksOutOfBedUntilStopped = 100;
            this.jumpsRemaining = 3 + serverWorld.random.nextInt(4);
            this.ticksToNextJump = 0;
            this.setWalkTarget(mobEntity, (BlockPos)blockPos);
        });
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        super.finishRunning(serverWorld, mobEntity, l);
        this.bedPos = null;
        this.ticksOutOfBedUntilStopped = 0;
        this.jumpsRemaining = 0;
        this.ticksToNextJump = 0;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        return mobEntity.isBaby() && this.bedPos != null && this.isBedAt(serverWorld, this.bedPos) && !this.isBedGoneTooLong(serverWorld, mobEntity) && !this.isDoneJumping(serverWorld, mobEntity);
    }

    @Override
    protected boolean isTimeLimitExceeded(long time) {
        return false;
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        if (!this.isAboveBed(serverWorld, mobEntity)) {
            --this.ticksOutOfBedUntilStopped;
            return;
        }
        if (this.ticksToNextJump > 0) {
            --this.ticksToNextJump;
            return;
        }
        if (this.isOnBed(serverWorld, mobEntity)) {
            mobEntity.getJumpControl().setActive();
            --this.jumpsRemaining;
            this.ticksToNextJump = 5;
        }
    }

    private void setWalkTarget(MobEntity mob, BlockPos pos) {
        mob.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, this.walkSpeed, 0));
    }

    private boolean shouldStartJumping(ServerWorld world, MobEntity mob) {
        return this.isAboveBed(world, mob) || this.getNearestBed(mob).isPresent();
    }

    private boolean isAboveBed(ServerWorld world, MobEntity mob) {
        BlockPos blockPos = mob.getBlockPos();
        BlockPos blockPos2 = blockPos.down();
        return this.isBedAt(world, blockPos) || this.isBedAt(world, blockPos2);
    }

    private boolean isOnBed(ServerWorld world, MobEntity mob) {
        return this.isBedAt(world, mob.getBlockPos());
    }

    private boolean isBedAt(ServerWorld world, BlockPos pos) {
        return world.getBlockState(pos).isIn(BlockTags.BEDS);
    }

    private Optional<BlockPos> getNearestBed(MobEntity mob) {
        return mob.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_BED);
    }

    private boolean isBedGoneTooLong(ServerWorld world, MobEntity mob) {
        return !this.isAboveBed(world, mob) && this.ticksOutOfBedUntilStopped <= 0;
    }

    private boolean isDoneJumping(ServerWorld world, MobEntity mob) {
        return this.isAboveBed(world, mob) && this.jumpsRemaining <= 0;
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (MobEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (MobEntity)entity, time);
    }
}

