/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestVisibleLivingEntitySensor;

public class VillagerHostilesSensor
extends NearestVisibleLivingEntitySensor {
    private static final ImmutableMap<EntityType<?>, Float> SQUARED_DISTANCES_FOR_DANGER = ImmutableMap.builder().put(EntityType.DROWNED, Float.valueOf(8.0f)).put(EntityType.EVOKER, Float.valueOf(12.0f)).put(EntityType.HUSK, Float.valueOf(8.0f)).put(EntityType.ILLUSIONER, Float.valueOf(12.0f)).put(EntityType.PILLAGER, Float.valueOf(15.0f)).put(EntityType.RAVAGER, Float.valueOf(12.0f)).put(EntityType.VEX, Float.valueOf(8.0f)).put(EntityType.VINDICATOR, Float.valueOf(10.0f)).put(EntityType.ZOGLIN, Float.valueOf(10.0f)).put(EntityType.ZOMBIE, Float.valueOf(8.0f)).put(EntityType.ZOMBIE_VILLAGER, Float.valueOf(8.0f)).build();

    @Override
    protected boolean matches(LivingEntity entity, LivingEntity target) {
        return this.isHostile(target) && this.isCloseEnoughForDanger(entity, target);
    }

    private boolean isCloseEnoughForDanger(LivingEntity villager, LivingEntity target) {
        float f = SQUARED_DISTANCES_FOR_DANGER.get(target.getType()).floatValue();
        return target.squaredDistanceTo(villager) <= (double)(f * f);
    }

    @Override
    protected MemoryModuleType<LivingEntity> getOutputMemoryModule() {
        return MemoryModuleType.NEAREST_HOSTILE;
    }

    private boolean isHostile(LivingEntity entity) {
        return SQUARED_DISTANCES_FOR_DANGER.containsKey(entity.getType());
    }
}

