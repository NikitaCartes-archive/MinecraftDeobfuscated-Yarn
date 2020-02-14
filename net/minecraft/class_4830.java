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
import net.minecraft.server.world.ServerWorld;

public class class_4830<E extends class_4836>
extends Task<E> {
    public class_4830() {
        super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryModuleState.VALUE_ABSENT));
    }

    @Override
    protected void run(ServerWorld serverWorld, E arg, long l) {
        class_4838.method_24741(arg);
    }
}

