/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.class_4836;
import net.minecraft.class_4838;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4826<E extends class_4836>
extends Task<E> {
    public class_4826() {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLIN, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.ANGRY_AT, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.HUNTED_RECENTLY, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleState.REGISTERED));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, class_4836 arg) {
        return !arg.isBaby() && !class_4838.method_24754(arg);
    }

    @Override
    protected void run(ServerWorld serverWorld, E arg, long l) {
        HoglinEntity hoglinEntity = ((class_4836)arg).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLIN).get();
        class_4838.method_24750(arg, hoglinEntity);
        class_4838.method_24762(arg);
        class_4838.method_24742(arg, hoglinEntity);
        class_4838.method_24758(arg);
    }
}

