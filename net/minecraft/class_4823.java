/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.class_4836;
import net.minecraft.class_4838;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class class_4823<E extends class_4836>
extends Task<E> {
    private final int field_22324;

    public class_4823(int i) {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.ADMIRING_ITEM, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WAS_HIT_BY_PLAYER, MemoryModuleState.VALUE_ABSENT));
        this.field_22324 = i;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E arg) {
        ItemEntity itemEntity = ((class_4836)arg).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
        return class_4838.method_24735(itemEntity.getStack().getItem());
    }

    @Override
    protected void run(ServerWorld serverWorld, E arg, long l) {
        ((class_4836)arg).getBrain().method_24525(MemoryModuleType.ADMIRING_ITEM, true, l, this.field_22324);
    }
}

