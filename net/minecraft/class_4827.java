/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.class_4836;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class class_4827<E extends class_4836>
extends Task<E> {
    private final int field_22328;

    public class_4827(int i) {
        super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleState.REGISTERED));
        this.field_22328 = i;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E arg) {
        if (!((LivingEntity)arg).getOffHandStack().isEmpty()) {
            return false;
        }
        Optional<ItemEntity> optional = ((class_4836)arg).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
        if (!optional.isPresent()) {
            return true;
        }
        return !optional.get().method_24516((Entity)arg, this.field_22328);
    }

    @Override
    protected void run(ServerWorld serverWorld, E arg, long l) {
        ((class_4836)arg).getBrain().forget(MemoryModuleType.ADMIRING_ITEM);
    }
}

