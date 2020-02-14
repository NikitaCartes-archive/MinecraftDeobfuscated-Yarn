/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class class_4812<E extends LivingEntity, T extends Entity>
extends Task<E> {
    private final int field_22300;
    private final BiPredicate<E, Entity> field_22301;

    public class_4812(int i, BiPredicate<E, Entity> biPredicate) {
        super(ImmutableMap.of(MemoryModuleType.RIDE_TARGET, MemoryModuleState.REGISTERED));
        this.field_22300 = i;
        this.field_22301 = biPredicate;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        Entity entity2 = ((Entity)entity).getVehicle();
        Entity entity3 = ((LivingEntity)entity).getBrain().getOptionalMemory(MemoryModuleType.RIDE_TARGET).orElse(null);
        if (entity2 == null && entity3 == null) {
            return false;
        }
        Entity entity4 = entity2 == null ? entity3 : entity2;
        return !this.method_24575(entity, entity4) || this.field_22301.test(entity, entity4);
    }

    private boolean method_24575(E livingEntity, Entity entity) {
        return entity.isAlive() && entity.method_24516((Entity)livingEntity, this.field_22300) && entity.world == ((LivingEntity)livingEntity).world;
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        ((LivingEntity)entity).stopRiding();
        ((LivingEntity)entity).getBrain().forget(MemoryModuleType.RIDE_TARGET);
    }
}

