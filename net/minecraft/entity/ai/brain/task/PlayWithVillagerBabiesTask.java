/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class PlayWithVillagerBabiesTask
extends Task<MobEntityWithAi> {
    public PlayWithVillagerBabiesTask() {
        super(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryModuleState.REGISTERED));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
        return serverWorld.getRandom().nextInt(10) == 0 && this.hasVisibleVillagerBabies(mobEntityWithAi);
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
        LivingEntity livingEntity2 = this.findVisibleVillagerBaby(mobEntityWithAi);
        if (livingEntity2 != null) {
            this.method_19585(serverWorld, mobEntityWithAi, livingEntity2);
            return;
        }
        Optional<LivingEntity> optional = this.method_19588(mobEntityWithAi);
        if (optional.isPresent()) {
            PlayWithVillagerBabiesTask.method_19580(mobEntityWithAi, optional.get());
            return;
        }
        this.getVisibleMob(mobEntityWithAi).ifPresent(livingEntity -> PlayWithVillagerBabiesTask.method_19580(mobEntityWithAi, livingEntity));
    }

    private void method_19585(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, LivingEntity livingEntity) {
        for (int i = 0; i < 10; ++i) {
            Vec3d vec3d = TargetFinder.findGroundTarget(mobEntityWithAi, 20, 8);
            if (vec3d == null || !serverWorld.isNearOccupiedPointOfInterest(new BlockPos(vec3d))) continue;
            mobEntityWithAi.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, 0.6f, 0));
            return;
        }
    }

    private static void method_19580(MobEntityWithAi mobEntityWithAi, LivingEntity livingEntity) {
        Brain<?> brain = mobEntityWithAi.getBrain();
        brain.putMemory(MemoryModuleType.INTERACTION_TARGET, livingEntity);
        brain.putMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(livingEntity));
        brain.putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper(livingEntity), 0.6f, 1));
    }

    private Optional<LivingEntity> getVisibleMob(MobEntityWithAi mobEntityWithAi) {
        return this.getVisibleVillagerBabies(mobEntityWithAi).stream().findAny();
    }

    private Optional<LivingEntity> method_19588(MobEntityWithAi mobEntityWithAi) {
        Map<LivingEntity, Integer> map = this.method_19592(mobEntityWithAi);
        return map.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).filter(entry -> (Integer)entry.getValue() > 0 && (Integer)entry.getValue() <= 5).map(Map.Entry::getKey).findFirst();
    }

    private Map<LivingEntity, Integer> method_19592(MobEntityWithAi mobEntityWithAi) {
        HashMap<LivingEntity, Integer> map = Maps.newHashMap();
        this.getVisibleVillagerBabies(mobEntityWithAi).stream().filter(this::hasInteractionTarget).forEach(livingEntity2 -> map.compute(this.getInteractionTarget((LivingEntity)livingEntity2), (livingEntity, integer) -> integer == null ? 1 : integer + 1));
        return map;
    }

    private List<LivingEntity> getVisibleVillagerBabies(MobEntityWithAi mobEntityWithAi) {
        return mobEntityWithAi.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get();
    }

    private LivingEntity getInteractionTarget(LivingEntity livingEntity) {
        return livingEntity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
    }

    @Nullable
    private LivingEntity findVisibleVillagerBaby(LivingEntity livingEntity) {
        return livingEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get().stream().filter(livingEntity2 -> this.isInteractionTargetOf(livingEntity, (LivingEntity)livingEntity2)).findAny().orElse(null);
    }

    private boolean hasInteractionTarget(LivingEntity livingEntity) {
        return livingEntity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    private boolean isInteractionTargetOf(LivingEntity livingEntity, LivingEntity livingEntity22) {
        return livingEntity22.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).filter(livingEntity2 -> livingEntity2 == livingEntity).isPresent();
    }

    private boolean hasVisibleVillagerBabies(MobEntityWithAi mobEntityWithAi) {
        return mobEntityWithAi.getBrain().hasMemoryModule(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
    }
}

