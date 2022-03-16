/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.sensor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestVisibleLivingEntitySensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.passive.FrogEntity;

public class FrogAttackablesSensor
extends NearestVisibleLivingEntitySensor {
    public static final float RANGE = 10.0f;

    @Override
    protected boolean matches(LivingEntity entity, LivingEntity target) {
        if (!entity.getBrain().hasMemoryModule(MemoryModuleType.HAS_HUNTING_COOLDOWN) && Sensor.testAttackableTargetPredicate(entity, target) && FrogEntity.isValidFrogTarget(target)) {
            return target.isInRange(entity, 10.0);
        }
        return false;
    }

    @Override
    protected MemoryModuleType<LivingEntity> getOutputMemoryModule() {
        return MemoryModuleType.NEAREST_ATTACKABLE;
    }
}

