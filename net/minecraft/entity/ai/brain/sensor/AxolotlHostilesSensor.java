/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.sensor;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestHostileSensor;
import net.minecraft.tag.EntityTypeTags;

public class AxolotlHostilesSensor
extends NearestHostileSensor {
    @Override
    protected Optional<LivingEntity> getNearestHostile(LivingEntity entity) {
        return this.getVisibleMobs(entity).flatMap(list -> list.stream().filter(hostile -> this.shouldTarget(entity, (LivingEntity)hostile)).filter(hostile -> this.isCloseEnoughForDanger(entity, (LivingEntity)hostile)).filter(Entity::isInsideWaterOrBubbleColumn).min(Comparator.comparingDouble(entity::squaredDistanceTo)));
    }

    private boolean shouldTarget(LivingEntity axolotl, LivingEntity hostile) {
        EntityType<?> entityType = hostile.getType();
        if (EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES.contains(entityType)) {
            return true;
        }
        if (EntityTypeTags.AXOLOTL_TEMPTED_HOSTILES.contains(entityType)) {
            Optional<Boolean> optional = axolotl.getBrain().getOptionalMemory(MemoryModuleType.IS_TEMPTED);
            return optional.isPresent() && optional.get() != false;
        }
        return false;
    }

    @Override
    protected boolean isCloseEnoughForDanger(LivingEntity entity, LivingEntity target) {
        return target.squaredDistanceTo(entity) <= 64.0;
    }
}

