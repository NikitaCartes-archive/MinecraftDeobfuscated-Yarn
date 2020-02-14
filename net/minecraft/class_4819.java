/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.class_4836;
import net.minecraft.class_4838;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class class_4819<E extends class_4836>
extends Task<E> {
    public class_4819() {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.HUNTED_RECENTLY, MemoryModuleState.REGISTERED));
    }

    @Override
    protected void run(ServerWorld serverWorld, E arg, long l) {
        if (this.method_24595(arg)) {
            class_4838.method_24762(arg);
        }
    }

    private boolean method_24595(E arg) {
        LivingEntity livingEntity = ((class_4836)arg).getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
        return livingEntity.getType() == EntityType.HOGLIN && livingEntity.getHealth() <= 0.0f;
    }
}

