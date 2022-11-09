/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.SingleTickTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;

public class HuntHoglinTask {
    public static SingleTickTask<PiglinEntity> create() {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryValue(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN), context.queryMemoryAbsent(MemoryModuleType.ANGRY_AT), context.queryMemoryAbsent(MemoryModuleType.HUNTED_RECENTLY), context.queryMemoryOptional(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS)).apply(context, (nearestVisibleHuntableHoglin, angryAt, huntedRecently, nearestVisibleAdultPiglins) -> (world, entity, time) -> {
            if (entity.isBaby() || context.getOptionalValue(nearestVisibleAdultPiglins).map(piglin -> piglin.stream().anyMatch(HuntHoglinTask::hasHuntedRecently)).isPresent()) {
                return false;
            }
            HoglinEntity hoglinEntity = (HoglinEntity)context.getValue(nearestVisibleHuntableHoglin);
            PiglinBrain.becomeAngryWith(entity, hoglinEntity);
            PiglinBrain.rememberHunting(entity);
            PiglinBrain.angerAtCloserTargets(entity, hoglinEntity);
            context.getOptionalValue(nearestVisibleAdultPiglins).ifPresent(piglin -> piglin.forEach(PiglinBrain::rememberHunting));
            return true;
        }));
    }

    private static boolean hasHuntedRecently(AbstractPiglinEntity piglin) {
        return piglin.getBrain().hasMemoryModule(MemoryModuleType.HUNTED_RECENTLY);
    }
}

