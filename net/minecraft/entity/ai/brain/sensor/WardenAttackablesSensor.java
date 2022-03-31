/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;

public class WardenAttackablesSensor
extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_ATTACKABLE);
    }

    @Override
    protected void sense(ServerWorld world, LivingEntity entity2) {
        entity2.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).flatMap(memory -> memory.findAny(entity -> WardenEntity.isValidTarget(entity) && entity.getType() == EntityType.PLAYER, entity -> WardenEntity.isValidTarget(entity) && entity.getType() != EntityType.PLAYER)).ifPresentOrElse(entity -> entity2.getBrain().remember(MemoryModuleType.NEAREST_ATTACKABLE, entity), () -> entity2.getBrain().forget(MemoryModuleType.NEAREST_ATTACKABLE));
    }
}

