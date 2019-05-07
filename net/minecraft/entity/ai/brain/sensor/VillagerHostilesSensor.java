/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

public class VillagerHostilesSensor
extends Sensor<LivingEntity> {
    private static final ImmutableMap<EntityType<?>, Float> SQUARED_DISTANCES_FOR_DANGER = ImmutableMap.builder().put(EntityType.ZOMBIE, Float.valueOf(8.0f)).put(EntityType.EVOKER, Float.valueOf(12.0f)).put(EntityType.VINDICATOR, Float.valueOf(10.0f)).put(EntityType.VEX, Float.valueOf(8.0f)).put(EntityType.PILLAGER, Float.valueOf(15.0f)).put(EntityType.ILLUSIONER, Float.valueOf(12.0f)).put(EntityType.RAVAGER, Float.valueOf(12.0f)).put(EntityType.HUSK, Float.valueOf(8.0f)).build();

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_HOSTILE);
    }

    @Override
    protected void sense(ServerWorld serverWorld, LivingEntity livingEntity) {
        livingEntity.getBrain().setMemory(MemoryModuleType.NEAREST_HOSTILE, this.getNearestHostile(livingEntity));
    }

    private Optional<LivingEntity> getNearestHostile(LivingEntity livingEntity) {
        return this.getVisibleMobs(livingEntity).flatMap(list -> list.stream().filter(this::isHostile).filter(livingEntity2 -> this.isCloseEnoughForDanger(livingEntity, (LivingEntity)livingEntity2)).min((livingEntity2, livingEntity3) -> this.compareDistances(livingEntity, (LivingEntity)livingEntity2, (LivingEntity)livingEntity3)));
    }

    private Optional<List<LivingEntity>> getVisibleMobs(LivingEntity livingEntity) {
        return livingEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
    }

    private int compareDistances(LivingEntity livingEntity, LivingEntity livingEntity2, LivingEntity livingEntity3) {
        return MathHelper.floor(livingEntity2.squaredDistanceTo(livingEntity) - livingEntity3.squaredDistanceTo(livingEntity));
    }

    private boolean isCloseEnoughForDanger(LivingEntity livingEntity, LivingEntity livingEntity2) {
        float f = SQUARED_DISTANCES_FOR_DANGER.get(livingEntity2.getType()).floatValue();
        return livingEntity2.squaredDistanceTo(livingEntity) <= (double)(f * f);
    }

    private boolean isHostile(LivingEntity livingEntity) {
        return SQUARED_DISTANCES_FOR_DANGER.containsKey(livingEntity.getType());
    }
}

