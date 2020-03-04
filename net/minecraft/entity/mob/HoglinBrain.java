/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.BreedTask;
import net.minecraft.entity.ai.brain.task.ConditionalTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetTask;
import net.minecraft.entity.ai.brain.task.GoToRememberedPositionTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.PacifyTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;

public class HoglinBrain {
    private static final IntRange AVOID_MEMORY_DURATION = Durations.betweenSeconds(5, 20);

    protected static Brain<?> create(HoglinEntity hoglin, Dynamic<?> data) {
        Brain<HoglinEntity> brain = new Brain<HoglinEntity>(HoglinEntity.MEMORY_MODULE_TYPES, HoglinEntity.SENSOR_TYPES, data);
        HoglinBrain.addCoreTasks(hoglin, brain);
        HoglinBrain.addIdleTasks(hoglin, brain);
        HoglinBrain.addFightTasks(hoglin, brain);
        HoglinBrain.addAvoidTasks(hoglin, brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreTasks(HoglinEntity hoglin, Brain<HoglinEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new LookAroundTask(45, 90), new WanderAroundTask(200)));
    }

    private static void addIdleTasks(HoglinEntity hoglin, Brain<HoglinEntity> brain) {
        float f = hoglin.method_24915();
        brain.setTaskList(Activity.IDLE, 10, ImmutableList.of(new PacifyTask(MemoryModuleType.NEAREST_REPELLENT, 200), new BreedTask(EntityType.HOGLIN), GoToRememberedPositionTask.toBlock(MemoryModuleType.NEAREST_REPELLENT, f * 1.8f, 8, true), new UpdateAttackTargetTask<HoglinEntity>(HoglinBrain::getNearestVisibleTargetablePlayer), new ConditionalTask<MobEntityWithAi>(HoglinEntity::isAdult, GoToRememberedPositionTask.toEntity(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, f, 8, false)), new TimeLimitedTask<LivingEntity>(new FollowMobTask(8.0f), IntRange.between(30, 60)), HoglinBrain.makeRandomWalkTask(f)));
    }

    private static void addFightTasks(HoglinEntity hoglin, Brain<HoglinEntity> brain) {
        float f = hoglin.method_24915();
        brain.setTaskList(Activity.FIGHT, 10, ImmutableList.of(new PacifyTask(MemoryModuleType.NEAREST_REPELLENT, 200), new BreedTask(EntityType.HOGLIN), new RangedApproachTask(f * 1.8f), new ConditionalTask<MobEntity>(HoglinEntity::isAdult, new MeleeAttackTask(1.5, 40)), new ConditionalTask<MobEntity>(PassiveEntity::isBaby, new MeleeAttackTask(1.5, 15)), new ForgetAttackTargetTask()), MemoryModuleType.ATTACK_TARGET);
    }

    private static void addAvoidTasks(HoglinEntity hoglin, Brain<HoglinEntity> brain) {
        float f = hoglin.method_24915() * 2.0f;
        brain.setTaskList(Activity.AVOID, 10, ImmutableList.of(GoToRememberedPositionTask.toEntity(MemoryModuleType.AVOID_TARGET, f, 15, false), HoglinBrain.makeRandomWalkTask(hoglin.method_24915()), new TimeLimitedTask<LivingEntity>(new FollowMobTask(8.0f), IntRange.between(30, 60)), new ForgetTask<HoglinEntity>(HoglinBrain::hasMoreHoglinsAround, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
    }

    private static RandomTask<HoglinEntity> makeRandomWalkTask(float speed) {
        return new RandomTask(ImmutableList.of(Pair.of(new StrollTask(speed), 2), Pair.of(new GoTowardsLookTarget(speed, 3), 2), Pair.of(new WaitTask(30, 60), 1)));
    }

    protected static void refreshActivities(HoglinEntity hoglin) {
        Brain<HoglinEntity> brain = hoglin.getBrain();
        Activity activity = brain.getFirstPossibleNonCoreActivity().orElse(null);
        brain.resetPossibleActivities(ImmutableList.of(Activity.FIGHT, Activity.AVOID, Activity.IDLE));
        Activity activity2 = brain.getFirstPossibleNonCoreActivity().orElse(null);
        if (activity != activity2) {
            HoglinBrain.playSound(hoglin);
        }
        hoglin.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
    }

    protected static void onAttacking(HoglinEntity hoglin, LivingEntity target) {
        if (hoglin.isBaby()) {
            return;
        }
        if (target.getType() == EntityType.PIGLIN && !HoglinBrain.hasMoreHoglinsAround(hoglin)) {
            HoglinBrain.avoid(hoglin, target);
            HoglinBrain.askAdultsToAvoid(hoglin, target);
            return;
        }
        HoglinBrain.askAdultsForHelp(hoglin, target);
    }

    private static void askAdultsToAvoid(HoglinEntity hoglin2, LivingEntity target) {
        HoglinBrain.getAdultHoglinsAround(hoglin2).forEach(hoglin -> HoglinBrain.avoidEnemy(hoglin, target));
    }

    private static void avoidEnemy(HoglinEntity hoglin, LivingEntity target) {
        LivingEntity livingEntity = target;
        Brain<HoglinEntity> brain = hoglin.getBrain();
        livingEntity = LookTargetUtil.getCloserEntity((LivingEntity)hoglin, brain.getOptionalMemory(MemoryModuleType.AVOID_TARGET), livingEntity);
        livingEntity = LookTargetUtil.getCloserEntity((LivingEntity)hoglin, brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET), livingEntity);
        HoglinBrain.avoid(hoglin, livingEntity);
    }

    private static void avoid(HoglinEntity hoglin, LivingEntity target) {
        hoglin.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        hoglin.getBrain().remember(MemoryModuleType.AVOID_TARGET, target, AVOID_MEMORY_DURATION.choose(hoglin.world.random));
    }

    private static Optional<? extends LivingEntity> getNearestVisibleTargetablePlayer(HoglinEntity hoglin) {
        if (HoglinBrain.isNearPlayer(hoglin) || HoglinBrain.hasBreedTarget(hoglin)) {
            return Optional.empty();
        }
        return hoglin.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
    }

    static boolean isWarpedFungusAround(HoglinEntity hoglin, BlockPos pos) {
        Optional<BlockPos> optional = hoglin.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_REPELLENT);
        return optional.isPresent() && optional.get().isWithinDistance(pos, 8.0);
    }

    private static boolean hasMoreHoglinsAround(HoglinEntity hoglin) {
        if (hoglin.isBaby()) {
            return false;
        }
        int i = hoglin.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0);
        int j = hoglin.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0) + 1;
        return j > i;
    }

    protected static void onAttacked(HoglinEntity hoglin, LivingEntity attacker) {
        Brain<HoglinEntity> brain = hoglin.getBrain();
        brain.forget(MemoryModuleType.PACIFIED);
        brain.forget(MemoryModuleType.BREED_TARGET);
        if (hoglin.isBaby()) {
            HoglinBrain.avoidEnemy(hoglin, attacker);
            return;
        }
        HoglinBrain.targetEnemy(hoglin, attacker);
    }

    private static void targetEnemy(HoglinEntity hoglin, LivingEntity target) {
        if (hoglin.getBrain().hasActivity(Activity.AVOID) && target.getType() == EntityType.PIGLIN) {
            return;
        }
        if (!EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(target)) {
            return;
        }
        if (target.getType() == EntityType.HOGLIN) {
            return;
        }
        if (LookTargetUtil.isNewTargetTooFar(hoglin, target, 4.0)) {
            return;
        }
        HoglinBrain.setAttackTarget(hoglin, target);
        HoglinBrain.askAdultsForHelp(hoglin, target);
    }

    private static void setAttackTarget(HoglinEntity hoglin, LivingEntity target) {
        hoglin.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        hoglin.getBrain().remember(MemoryModuleType.ATTACK_TARGET, target, 200L);
    }

    private static void askAdultsForHelp(HoglinEntity hoglin2, LivingEntity target) {
        HoglinBrain.getAdultHoglinsAround(hoglin2).forEach(hoglin -> HoglinBrain.setAttackTargetIfCloser(hoglin, target));
    }

    private static void setAttackTargetIfCloser(HoglinEntity hoglin, LivingEntity targetCandidate) {
        Optional<LivingEntity> optional = hoglin.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
        LivingEntity livingEntity = LookTargetUtil.getCloserEntity((LivingEntity)hoglin, optional, targetCandidate);
        HoglinBrain.setAttackTarget(hoglin, livingEntity);
    }

    private static void playSound(HoglinEntity hoglin) {
        hoglin.getBrain().getFirstPossibleNonCoreActivity().ifPresent(activity -> {
            if (activity == Activity.AVOID) {
                hoglin.playRetreatSound();
            } else if (activity == Activity.FIGHT) {
                hoglin.playFightSound();
            }
        });
    }

    protected static void playSoundAtChance(HoglinEntity hoglin) {
        if ((double)hoglin.world.random.nextFloat() < 0.0125) {
            HoglinBrain.playSound(hoglin);
        }
    }

    private static List<HoglinEntity> getAdultHoglinsAround(HoglinEntity hoglin) {
        return hoglin.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS).orElse(ImmutableList.of());
    }

    private static boolean hasBreedTarget(HoglinEntity hoglin) {
        return hoglin.getBrain().hasMemoryModule(MemoryModuleType.BREED_TARGET);
    }

    protected static boolean isNearPlayer(HoglinEntity hoglin) {
        return hoglin.getBrain().hasMemoryModule(MemoryModuleType.PACIFIED);
    }

    protected static boolean hasIdleActivity(HoglinEntity hoglin) {
        return hoglin.getBrain().hasActivity(Activity.IDLE);
    }
}

