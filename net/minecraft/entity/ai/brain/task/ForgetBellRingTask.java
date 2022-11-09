/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.apache.commons.lang3.mutable.MutableInt;

public class ForgetBellRingTask {
    private static final int MIN_HEARD_BELL_TIME = 300;

    public static Task<LivingEntity> create(int maxHiddenSeconds, int distance) {
        int i = maxHiddenSeconds * 20;
        MutableInt mutableInt = new MutableInt(0);
        return TaskTriggerer.task(context -> context.group(context.queryMemoryValue(MemoryModuleType.HIDING_PLACE), context.queryMemoryValue(MemoryModuleType.HEARD_BELL_TIME)).apply(context, (hidingPlace, heardBellTime) -> (world, entity, time) -> {
            boolean bl;
            long l = (Long)context.getValue(heardBellTime);
            boolean bl2 = bl = l + 300L <= time;
            if (mutableInt.getValue() > i || bl) {
                heardBellTime.forget();
                hidingPlace.forget();
                entity.getBrain().refreshActivities(world.getTimeOfDay(), world.getTime());
                mutableInt.setValue(0);
                return true;
            }
            BlockPos blockPos = ((GlobalPos)context.getValue(hidingPlace)).getPos();
            if (blockPos.isWithinDistance(entity.getBlockPos(), (double)distance)) {
                mutableInt.increment();
            }
            return true;
        }));
    }
}

