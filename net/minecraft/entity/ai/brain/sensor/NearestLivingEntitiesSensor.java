/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

public class NearestLivingEntitiesSensor<T extends LivingEntity>
extends Sensor<T> {
    @Override
    protected void sense(ServerWorld world, T entity) {
        Box box = ((Entity)entity).getBoundingBox().expand(this.method_43081(), this.method_43082(), this.method_43081());
        List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, box, e -> e != entity && e.isAlive());
        list.sort(Comparator.comparingDouble(arg_0 -> entity.squaredDistanceTo(arg_0)));
        Brain<?> brain = ((LivingEntity)entity).getBrain();
        brain.remember(MemoryModuleType.MOBS, list);
        brain.remember(MemoryModuleType.VISIBLE_MOBS, new LivingTargetCache((LivingEntity)entity, list));
    }

    protected int method_43081() {
        return 16;
    }

    protected int method_43082() {
        return 16;
    }

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS);
    }
}

