/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;

public class MeetVillagerTask
extends Task<LivingEntity> {
    public MeetVillagerTask() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.MEETING_POINT, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.INTERACTION_TARGET, MemoryModuleState.VALUE_ABSENT));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity2) {
        Brain<?> brain = livingEntity2.getBrain();
        Optional<GlobalPos> optional = brain.getOptionalMemory(MemoryModuleType.MEETING_POINT);
        return serverWorld.getRandom().nextInt(100) == 0 && optional.isPresent() && Objects.equals(serverWorld.getDimension().getType(), optional.get().getDimension()) && optional.get().getPos().isWithinDistance(livingEntity2.getPos(), 4.0) && brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get().stream().anyMatch(livingEntity -> EntityType.VILLAGER.equals(livingEntity.getType()));
    }

    @Override
    protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        Brain<?> brain = livingEntity.getBrain();
        brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent(list -> list.stream().filter(livingEntity -> EntityType.VILLAGER.equals(livingEntity.getType())).filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= 32.0).findFirst().ifPresent(livingEntity -> {
            brain.putMemory(MemoryModuleType.INTERACTION_TARGET, livingEntity);
            brain.putMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper((LivingEntity)livingEntity));
            brain.putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper((LivingEntity)livingEntity), 0.3f, 1));
        }));
    }
}

