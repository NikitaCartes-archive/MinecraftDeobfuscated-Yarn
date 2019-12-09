/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class ForgetBellRingTask
extends Task<LivingEntity> {
    private final int field_19154;
    private final int field_19000;
    private int field_19001;

    public ForgetBellRingTask(int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.HIDING_PLACE, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleState.VALUE_PRESENT));
        this.field_19000 = i * 20;
        this.field_19001 = 0;
        this.field_19154 = j;
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        boolean bl;
        Brain<?> brain = entity.getBrain();
        Optional<Long> optional = brain.getOptionalMemory(MemoryModuleType.HEARD_BELL_TIME);
        boolean bl2 = bl = optional.get() + 300L <= time;
        if (this.field_19001 > this.field_19000 || bl) {
            brain.forget(MemoryModuleType.HEARD_BELL_TIME);
            brain.forget(MemoryModuleType.HIDING_PLACE);
            brain.refreshActivities(world.getTimeOfDay(), world.getTime());
            this.field_19001 = 0;
            return;
        }
        BlockPos blockPos = brain.getOptionalMemory(MemoryModuleType.HIDING_PLACE).get().getPos();
        if (blockPos.isWithinDistance(new BlockPos(entity), (double)(this.field_19154 + 1))) {
            ++this.field_19001;
        }
    }
}

