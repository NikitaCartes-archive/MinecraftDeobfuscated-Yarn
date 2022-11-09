/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PlayWithVillagerBabiesTask {
    private static final int HORIZONTAL_RANGE = 20;
    private static final int VERTICAL_RANGE = 8;
    private static final float WALK_SPEED = 0.6f;
    private static final float PLAYING_WALK_SPEED = 0.6f;
    private static final int MAX_BABY_INTERACTION_COUNT = 5;
    private static final int RUN_CHANCE = 10;

    public static Task<PathAwareEntity> create() {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryValue(MemoryModuleType.VISIBLE_VILLAGER_BABIES), context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryOptional(MemoryModuleType.INTERACTION_TARGET)).apply(context, (visibleVillagerBabies, walkTarget, lookTarget, interactionTarget) -> (world, entity, time) -> {
            if (world.getRandom().nextInt(10) != 0) {
                return false;
            }
            List list = (List)context.getValue(visibleVillagerBabies);
            Optional<LivingEntity> optional = list.stream().filter(baby -> PlayWithVillagerBabiesTask.isInteractionTargetOf(entity, baby)).findAny();
            if (optional.isPresent()) {
                for (int i = 0; i < 10; ++i) {
                    Vec3d vec3d = FuzzyTargeting.find(entity, 20, 8);
                    if (vec3d == null || !world.isNearOccupiedPointOfInterest(new BlockPos(vec3d))) continue;
                    walkTarget.remember(new WalkTarget(vec3d, 0.6f, 0));
                    break;
                }
                return true;
            }
            Optional<LivingEntity> optional2 = PlayWithVillagerBabiesTask.getLeastPopularBabyInteractionTarget(list);
            if (optional2.isPresent()) {
                PlayWithVillagerBabiesTask.setPlayTarget(interactionTarget, lookTarget, walkTarget, optional2.get());
                return true;
            }
            list.stream().findAny().ifPresent(baby -> PlayWithVillagerBabiesTask.setPlayTarget(interactionTarget, lookTarget, walkTarget, baby));
            return true;
        }));
    }

    private static void setPlayTarget(MemoryQueryResult<?, LivingEntity> interactionTarget, MemoryQueryResult<?, LookTarget> lookTarget, MemoryQueryResult<?, WalkTarget> walkTarget, LivingEntity baby) {
        interactionTarget.remember(baby);
        lookTarget.remember(new EntityLookTarget(baby, true));
        walkTarget.remember(new WalkTarget(new EntityLookTarget(baby, false), 0.6f, 1));
    }

    private static Optional<LivingEntity> getLeastPopularBabyInteractionTarget(List<LivingEntity> babies) {
        Map<LivingEntity, Integer> map = PlayWithVillagerBabiesTask.getBabyInteractionTargetCounts(babies);
        return map.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).filter(entry -> (Integer)entry.getValue() > 0 && (Integer)entry.getValue() <= 5).map(Map.Entry::getKey).findFirst();
    }

    private static Map<LivingEntity, Integer> getBabyInteractionTargetCounts(List<LivingEntity> babies) {
        HashMap<LivingEntity, Integer> map = Maps.newHashMap();
        babies.stream().filter(PlayWithVillagerBabiesTask::hasInteractionTarget).forEach(baby -> map.compute(PlayWithVillagerBabiesTask.getInteractionTarget(baby), (target, count) -> count == null ? 1 : count + 1));
        return map;
    }

    private static LivingEntity getInteractionTarget(LivingEntity baby) {
        return baby.getBrain().getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).get();
    }

    private static boolean hasInteractionTarget(LivingEntity baby) {
        return baby.getBrain().getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    private static boolean isInteractionTargetOf(LivingEntity entity, LivingEntity baby) {
        return baby.getBrain().getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).filter(target -> target == entity).isPresent();
    }
}

