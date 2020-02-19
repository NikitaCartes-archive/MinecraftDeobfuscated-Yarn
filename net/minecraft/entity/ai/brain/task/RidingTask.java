/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class RidingTask<E extends LivingEntity, T extends Entity>
extends Task<E> {
    private final int range;
    private final BiPredicate<E, Entity> alternativeRideCondition;

    public RidingTask(int range, BiPredicate<E, Entity> alternativeRideCondition) {
        super(ImmutableMap.of(MemoryModuleType.RIDE_TARGET, MemoryModuleState.REGISTERED));
        this.range = range;
        this.alternativeRideCondition = alternativeRideCondition;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        Entity entity2 = ((Entity)entity).getVehicle();
        Entity entity3 = ((LivingEntity)entity).getBrain().getOptionalMemory(MemoryModuleType.RIDE_TARGET).orElse(null);
        if (entity2 == null && entity3 == null) {
            return false;
        }
        Entity entity4 = entity2 == null ? entity3 : entity2;
        return !this.canRideTarget(entity, entity4) || this.alternativeRideCondition.test(entity, entity4);
    }

    private boolean canRideTarget(E entity, Entity target) {
        return target.isAlive() && target.isInRange((Entity)entity, this.range) && target.world == ((LivingEntity)entity).world;
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        ((LivingEntity)entity).stopRiding();
        ((LivingEntity)entity).getBrain().forget(MemoryModuleType.RIDE_TARGET);
    }
}

