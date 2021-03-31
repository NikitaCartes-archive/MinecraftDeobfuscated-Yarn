/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class WalkTowardClosestAdultTask<E extends PassiveEntity>
extends Task<E> {
    private final UniformIntProvider executionRange;
    private final Function<LivingEntity, Float> speed;

    public WalkTowardClosestAdultTask(UniformIntProvider executionRange, float speed) {
        this(executionRange, entity -> Float.valueOf(speed));
    }

    public WalkTowardClosestAdultTask(UniformIntProvider executionRange, Function<LivingEntity, Float> speed) {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
        this.executionRange = executionRange;
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E passiveEntity) {
        if (!((PassiveEntity)passiveEntity).isBaby()) {
            return false;
        }
        PassiveEntity passiveEntity2 = this.getNearestVisibleAdult(passiveEntity);
        return ((Entity)passiveEntity).isInRange(passiveEntity2, this.executionRange.getMax() + 1) && !((Entity)passiveEntity).isInRange(passiveEntity2, this.executionRange.getMin());
    }

    @Override
    protected void run(ServerWorld serverWorld, E passiveEntity, long l) {
        LookTargetUtil.walkTowards(passiveEntity, this.getNearestVisibleAdult(passiveEntity), this.speed.apply((LivingEntity)passiveEntity).floatValue(), this.executionRange.getMin() - 1);
    }

    private PassiveEntity getNearestVisibleAdult(E entity) {
        return ((LivingEntity)entity).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT).get();
    }
}

