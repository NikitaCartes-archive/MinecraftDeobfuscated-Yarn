/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;

public class WantNewItemTask<E extends PiglinEntity>
extends Task<E> {
    private final int range;

    public WantNewItemTask(int range) {
        super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleState.REGISTERED));
        this.range = range;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E piglinEntity) {
        if (!((LivingEntity)piglinEntity).getOffhandStack().isEmpty()) {
            return false;
        }
        Optional<ItemEntity> optional = ((PiglinEntity)piglinEntity).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
        if (!optional.isPresent()) {
            return true;
        }
        return !optional.get().isInRange((Entity)piglinEntity, this.range);
    }

    @Override
    protected void run(ServerWorld serverWorld, E piglinEntity, long l) {
        ((PiglinEntity)piglinEntity).getBrain().forget(MemoryModuleType.ADMIRING_ITEM);
    }
}

