/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.Timestamp;
import net.minecraft.util.math.BlockPos;

public class SleepTask
extends Task<LivingEntity> {
    private long startTime;

    public SleepTask() {
        super(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.LAST_WOKEN, MemoryModuleState.REGISTERED));
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        if (entity.hasVehicle()) {
            return false;
        }
        Brain<?> brain = entity.getBrain();
        GlobalPos globalPos = brain.getOptionalMemory(MemoryModuleType.HOME).get();
        if (!Objects.equals(world.getDimension().getType(), globalPos.getDimension())) {
            return false;
        }
        Optional<Timestamp> optional = brain.getOptionalMemory(MemoryModuleType.LAST_WOKEN);
        if (optional.isPresent() && world.getTime() - optional.get().getTime() < 100L) {
            return false;
        }
        BlockState blockState = world.getBlockState(globalPos.getPos());
        return globalPos.getPos().isWithinDistance(entity.getPos(), 2.0) && blockState.getBlock().isIn(BlockTags.BEDS) && blockState.get(BedBlock.OCCUPIED) == false;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        Optional<GlobalPos> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.HOME);
        if (!optional.isPresent()) {
            return false;
        }
        BlockPos blockPos = optional.get().getPos();
        return entity.getBrain().hasActivity(Activity.REST) && entity.getY() > (double)blockPos.getY() + 0.4 && blockPos.isWithinDistance(entity.getPos(), 1.14);
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        if (time > this.startTime) {
            entity.getBrain().getOptionalMemory(MemoryModuleType.OPENED_DOORS).ifPresent(set -> OpenDoorsTask.closeOpenedDoors(world, ImmutableList.of(), 0, entity, entity.getBrain()));
            entity.sleep(entity.getBrain().getOptionalMemory(MemoryModuleType.HOME).get().getPos());
        }
    }

    @Override
    protected boolean isTimeLimitExceeded(long time) {
        return false;
    }

    @Override
    protected void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        if (entity.isSleeping()) {
            entity.wakeUp();
            this.startTime = time + 40L;
        }
    }
}

