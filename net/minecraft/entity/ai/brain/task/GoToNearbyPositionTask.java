/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;

public class GoToNearbyPositionTask
extends Task<PathAwareEntity> {
    private final MemoryModuleType<GlobalPos> memoryModuleType;
    private final int completionRange;
    private final int maxDistance;
    private final float field_25753;
    private long nextRunTime;

    public GoToNearbyPositionTask(MemoryModuleType<GlobalPos> memoryModuleType, float f, int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED, memoryModuleType, MemoryModuleState.VALUE_PRESENT));
        this.memoryModuleType = memoryModuleType;
        this.field_25753 = f;
        this.completionRange = i;
        this.maxDistance = j;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
        Optional<GlobalPos> optional = pathAwareEntity.getBrain().getOptionalMemory(this.memoryModuleType);
        return optional.isPresent() && serverWorld.getRegistryKey() == optional.get().getDimension() && optional.get().getPos().isWithinDistance(pathAwareEntity.getPos(), (double)this.maxDistance);
    }

    @Override
    protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        if (l > this.nextRunTime) {
            Brain<?> brain = pathAwareEntity.getBrain();
            Optional<GlobalPos> optional = brain.getOptionalMemory(this.memoryModuleType);
            optional.ifPresent(globalPos -> brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(globalPos.getPos(), this.field_25753, this.completionRange)));
            this.nextRunTime = l + 80L;
        }
    }
}

