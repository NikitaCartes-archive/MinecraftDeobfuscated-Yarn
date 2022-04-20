/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class WalkTowardsLookTargetTask<E extends LivingEntity>
extends Task<E> {
    private final Function<LivingEntity, Optional<LookTarget>> lookTargetFunction;
    private final int range;
    private final int field_38933;
    private final float speed;

    public WalkTowardsLookTargetTask(Function<LivingEntity, Optional<LookTarget>> lookTargetFunction, int range, int i, float f) {
        super(Map.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
        this.lookTargetFunction = lookTargetFunction;
        this.range = range;
        this.field_38933 = i;
        this.speed = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        Optional<LookTarget> optional = this.lookTargetFunction.apply((LivingEntity)entity);
        if (optional.isEmpty()) {
            return false;
        }
        LookTarget lookTarget = optional.get();
        return lookTarget.isSeenBy((LivingEntity)entity) && !((Entity)entity).getPos().isInRange(lookTarget.getPos(), this.field_38933);
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        LookTargetUtil.walkTowards(entity, this.lookTargetFunction.apply((LivingEntity)entity).get(), this.speed, this.range);
    }
}

