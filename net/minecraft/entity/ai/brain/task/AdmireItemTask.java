/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;

public class AdmireItemTask<E extends PiglinEntity>
extends Task<E> {
    private final int duration;

    public AdmireItemTask(int duration) {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.ADMIRING_ITEM, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.ADMIRING_DISABLED, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryModuleState.VALUE_ABSENT));
        this.duration = duration;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E piglinEntity) {
        ItemEntity itemEntity = ((PiglinEntity)piglinEntity).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
        return PiglinBrain.isGoldenItem(itemEntity.getStack().getItem());
    }

    @Override
    protected void run(ServerWorld serverWorld, E piglinEntity, long l) {
        ((PiglinEntity)piglinEntity).getBrain().remember(MemoryModuleType.ADMIRING_ITEM, true, this.duration);
    }
}

