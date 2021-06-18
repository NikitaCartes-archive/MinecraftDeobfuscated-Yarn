/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.BreedTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.LeapingChargeTask;
import net.minecraft.entity.ai.brain.task.LongJumpTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.PrepareRamTask;
import net.minecraft.entity.ai.brain.task.RamImpactTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.TemptTask;
import net.minecraft.entity.ai.brain.task.TemptationCooldownTask;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WalkTask;
import net.minecraft.entity.ai.brain.task.WalkTowardClosestAdultTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class GoatBrain {
    public static final int field_33490 = 20;
    public static final int field_33491 = 7;
    private static final UniformIntProvider WALKING_SPEED = UniformIntProvider.create(5, 16);
    private static final float BREEDING_WALK_SPEED = 1.0f;
    private static final float FOLLOWING_TARGET_WALK_SPEED = 1.0f;
    private static final float TEMPTED_WALK_SPEED = 1.25f;
    private static final float FOLLOW_ADULT_WALK_SPEED = 1.25f;
    private static final float NORMAL_WALK_SPEED = 2.0f;
    private static final float field_33498 = 1.25f;
    private static final UniformIntProvider LONG_JUMP_COOLDOWN_RANGE = UniformIntProvider.create(600, 1200);
    public static final int field_33492 = 5;
    public static final int field_33493 = 5;
    public static final float field_33494 = 1.5f;
    private static final UniformIntProvider RAM_COOLDOWN_RANGE = UniformIntProvider.create(600, 6000);
    private static final UniformIntProvider SCREAMING_RAM_COOLDOWN_RANGE = UniformIntProvider.create(100, 300);
    private static final TargetPredicate RAM_TARGET_PREDICATE = TargetPredicate.createAttackable().setPredicate(livingEntity -> !livingEntity.getType().equals(EntityType.GOAT) && livingEntity.world.getWorldBorder().contains(livingEntity.getBoundingBox()));
    private static final float field_33501 = 3.0f;
    public static final int field_33495 = 4;
    public static final float field_33496 = 2.5f;
    public static final float field_33497 = 1.0f;

    protected static void resetLongJumpCooldown(GoatEntity goat) {
        goat.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, LONG_JUMP_COOLDOWN_RANGE.get(goat.world.random));
        goat.getBrain().remember(MemoryModuleType.RAM_COOLDOWN_TICKS, RAM_COOLDOWN_RANGE.get(goat.world.random));
    }

    protected static Brain<?> create(Brain<GoatEntity> brain) {
        GoatBrain.addCoreActivities(brain);
        GoatBrain.addIdleActivities(brain);
        GoatBrain.addLongJumpActivities(brain);
        GoatBrain.addRamActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<GoatEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new StayAboveWaterTask(0.8f), new WalkTask(2.0f), new LookAroundTask(45, 90), new WanderAroundTask(), new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), new TemptationCooldownTask(MemoryModuleType.LONG_JUMP_COOLING_DOWN), new TemptationCooldownTask(MemoryModuleType.RAM_COOLDOWN_TICKS)));
    }

    private static void addIdleActivities(Brain<GoatEntity> brain) {
        brain.setTaskList(Activity.IDLE, ImmutableList.of(Pair.of(0, new TimeLimitedTask<LivingEntity>(new FollowMobTask(EntityType.PLAYER, 6.0f), UniformIntProvider.create(30, 60))), Pair.of(0, new BreedTask(EntityType.GOAT, 1.0f)), Pair.of(1, new TemptTask(livingEntity -> Float.valueOf(1.25f))), Pair.of(2, new WalkTowardClosestAdultTask(WALKING_SPEED, 1.25f)), Pair.of(3, new RandomTask(ImmutableList.of(Pair.of(new StrollTask(1.0f), 2), Pair.of(new GoTowardsLookTarget(1.0f, 3), 2), Pair.of(new WaitTask(30, 60), 1))))), ImmutableSet.of(Pair.of(MemoryModuleType.RAM_TARGET, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_ABSENT)));
    }

    private static void addLongJumpActivities(Brain<GoatEntity> brain) {
        brain.setTaskList(Activity.LONG_JUMP, ImmutableList.of(Pair.of(0, new LeapingChargeTask(LONG_JUMP_COOLDOWN_RANGE, SoundEvents.ENTITY_GOAT_STEP)), Pair.of(1, new LongJumpTask<GoatEntity>(LONG_JUMP_COOLDOWN_RANGE, 5, 5, 1.5f, goatEntity -> goatEntity.isScreaming() ? SoundEvents.ENTITY_GOAT_SCREAMING_LONG_JUMP : SoundEvents.ENTITY_GOAT_LONG_JUMP))), ImmutableSet.of(Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.BREED_TARGET, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.LONG_JUMP_COOLING_DOWN, MemoryModuleState.VALUE_ABSENT)));
    }

    private static void addRamActivities(Brain<GoatEntity> brain) {
        brain.setTaskList(Activity.RAM, ImmutableList.of(Pair.of(0, new RamImpactTask<GoatEntity>(goat -> goat.isScreaming() ? SCREAMING_RAM_COOLDOWN_RANGE : RAM_COOLDOWN_RANGE, RAM_TARGET_PREDICATE, 3.0f, goat -> goat.isBaby() ? 1.0 : 2.5, goatEntity -> goatEntity.isScreaming() ? SoundEvents.ENTITY_GOAT_SCREAMING_RAM_IMPACT : SoundEvents.ENTITY_GOAT_RAM_IMPACT)), Pair.of(1, new PrepareRamTask<GoatEntity>(goatEntity -> goatEntity.isScreaming() ? SCREAMING_RAM_COOLDOWN_RANGE.getMin() : RAM_COOLDOWN_RANGE.getMin(), 4, 7, 1.25f, RAM_TARGET_PREDICATE, 20, goatEntity -> goatEntity.isScreaming() ? SoundEvents.ENTITY_GOAT_SCREAMING_PREPARE_RAM : SoundEvents.ENTITY_GOAT_PREPARE_RAM))), ImmutableSet.of(Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.BREED_TARGET, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleState.VALUE_ABSENT)));
    }

    public static void updateActivities(GoatEntity goat) {
        goat.getBrain().resetPossibleActivities(ImmutableList.of(Activity.RAM, Activity.LONG_JUMP, Activity.IDLE));
    }

    public static Ingredient getTemptItems() {
        return Ingredient.ofItems(Items.WHEAT);
    }
}

