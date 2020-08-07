package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WalkTowardClosestAdultTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;

public class HoglinBrain {
	private static final IntRange AVOID_MEMORY_DURATION = Durations.betweenSeconds(5, 20);
	private static final IntRange WALK_TOWARD_CLOSEST_ADULT_RANGE = IntRange.between(5, 16);

	protected static Brain<?> create(Brain<HoglinEntity> brain) {
		addCoreTasks(brain);
		addIdleTasks(brain);
		addFightTasks(brain);
		addAvoidTasks(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.field_18594));
		brain.setDefaultActivity(Activity.field_18595);
		brain.resetPossibleActivities();
		return brain;
	}

	private static void addCoreTasks(Brain<HoglinEntity> brain) {
		brain.setTaskList(Activity.field_18594, 0, ImmutableList.of(new LookAroundTask(45, 90), new WanderAroundTask()));
	}

	private static void addIdleTasks(Brain<HoglinEntity> brain) {
		brain.setTaskList(
			Activity.field_18595,
			10,
			ImmutableList.of(
				new PacifyTask(MemoryModuleType.field_22474, 200),
				new BreedTask(EntityType.field_21973, 0.6F),
				GoToRememberedPositionTask.toBlock(MemoryModuleType.field_22474, 1.0F, 8, true),
				new UpdateAttackTargetTask(HoglinBrain::getNearestVisibleTargetablePlayer),
				new ConditionalTask<PathAwareEntity>(
					HoglinEntity::isAdult, (Task<? super PathAwareEntity>)GoToRememberedPositionTask.toEntity(MemoryModuleType.field_22345, 0.4F, 8, false)
				),
				new TimeLimitedTask<LivingEntity>(new FollowMobTask(8.0F), IntRange.between(30, 60)),
				new WalkTowardClosestAdultTask(WALK_TOWARD_CLOSEST_ADULT_RANGE, 0.6F),
				makeRandomWalkTask()
			)
		);
	}

	private static void addFightTasks(Brain<HoglinEntity> brain) {
		brain.setTaskList(
			Activity.field_22396,
			10,
			ImmutableList.of(
				new PacifyTask(MemoryModuleType.field_22474, 200),
				new BreedTask(EntityType.field_21973, 0.6F),
				new RangedApproachTask(1.0F),
				new ConditionalTask<>(HoglinEntity::isAdult, new MeleeAttackTask(40)),
				new ConditionalTask<>(PassiveEntity::isBaby, new MeleeAttackTask(15)),
				new ForgetAttackTargetTask(),
				new ForgetTask(HoglinBrain::hasBreedTarget, MemoryModuleType.field_22355)
			),
			MemoryModuleType.field_22355
		);
	}

	private static void addAvoidTasks(Brain<HoglinEntity> brain) {
		brain.setTaskList(
			Activity.field_22399,
			10,
			ImmutableList.of(
				GoToRememberedPositionTask.toEntity(MemoryModuleType.field_22357, 1.3F, 15, false),
				makeRandomWalkTask(),
				new TimeLimitedTask<LivingEntity>(new FollowMobTask(8.0F), IntRange.between(30, 60)),
				new ForgetTask(HoglinBrain::method_25947, MemoryModuleType.field_22357)
			),
			MemoryModuleType.field_22357
		);
	}

	private static RandomTask<HoglinEntity> makeRandomWalkTask() {
		return new RandomTask<>(ImmutableList.of(Pair.of(new StrollTask(0.4F), 2), Pair.of(new GoTowardsLookTarget(0.4F, 3), 2), Pair.of(new WaitTask(30, 60), 1)));
	}

	protected static void refreshActivities(HoglinEntity hoglin) {
		Brain<HoglinEntity> brain = hoglin.getBrain();
		Activity activity = (Activity)brain.getFirstPossibleNonCoreActivity().orElse(null);
		brain.resetPossibleActivities(ImmutableList.of(Activity.field_22396, Activity.field_22399, Activity.field_18595));
		Activity activity2 = (Activity)brain.getFirstPossibleNonCoreActivity().orElse(null);
		if (activity != activity2) {
			method_30083(hoglin).ifPresent(hoglin::method_30081);
		}

		hoglin.setAttacking(brain.hasMemoryModule(MemoryModuleType.field_22355));
	}

	protected static void onAttacking(HoglinEntity hoglin, LivingEntity target) {
		if (!hoglin.isBaby()) {
			if (target.getType() == EntityType.field_22281 && hasMoreHoglinsAround(hoglin)) {
				avoid(hoglin, target);
				askAdultsToAvoid(hoglin, target);
			} else {
				askAdultsForHelp(hoglin, target);
			}
		}
	}

	private static void askAdultsToAvoid(HoglinEntity hoglin, LivingEntity target) {
		getAdultHoglinsAround(hoglin).forEach(hoglinx -> avoidEnemy(hoglinx, target));
	}

	private static void avoidEnemy(HoglinEntity hoglin, LivingEntity target) {
		Brain<HoglinEntity> brain = hoglin.getBrain();
		LivingEntity livingEntity = LookTargetUtil.getCloserEntity(hoglin, brain.getOptionalMemory(MemoryModuleType.field_22357), target);
		livingEntity = LookTargetUtil.getCloserEntity(hoglin, brain.getOptionalMemory(MemoryModuleType.field_22355), livingEntity);
		avoid(hoglin, livingEntity);
	}

	private static void avoid(HoglinEntity hoglin, LivingEntity target) {
		hoglin.getBrain().forget(MemoryModuleType.field_22355);
		hoglin.getBrain().forget(MemoryModuleType.field_18445);
		hoglin.getBrain().remember(MemoryModuleType.field_22357, target, (long)AVOID_MEMORY_DURATION.choose(hoglin.world.random));
	}

	private static Optional<? extends LivingEntity> getNearestVisibleTargetablePlayer(HoglinEntity hoglin) {
		return !isNearPlayer(hoglin) && !hasBreedTarget(hoglin) ? hoglin.getBrain().getOptionalMemory(MemoryModuleType.field_22354) : Optional.empty();
	}

	static boolean isWarpedFungusAround(HoglinEntity hoglin, BlockPos pos) {
		Optional<BlockPos> optional = hoglin.getBrain().getOptionalMemory(MemoryModuleType.field_22474);
		return optional.isPresent() && ((BlockPos)optional.get()).isWithinDistance(pos, 8.0);
	}

	private static boolean method_25947(HoglinEntity hoglinEntity) {
		return hoglinEntity.isAdult() && !hasMoreHoglinsAround(hoglinEntity);
	}

	private static boolean hasMoreHoglinsAround(HoglinEntity hoglin) {
		if (hoglin.isBaby()) {
			return false;
		} else {
			int i = (Integer)hoglin.getBrain().getOptionalMemory(MemoryModuleType.field_22347).orElse(0);
			int j = (Integer)hoglin.getBrain().getOptionalMemory(MemoryModuleType.field_22348).orElse(0) + 1;
			return i > j;
		}
	}

	protected static void onAttacked(HoglinEntity hoglin, LivingEntity attacker) {
		Brain<HoglinEntity> brain = hoglin.getBrain();
		brain.forget(MemoryModuleType.field_22353);
		brain.forget(MemoryModuleType.field_18448);
		if (hoglin.isBaby()) {
			avoidEnemy(hoglin, attacker);
		} else {
			targetEnemy(hoglin, attacker);
		}
	}

	private static void targetEnemy(HoglinEntity hoglin, LivingEntity target) {
		if (!hoglin.getBrain().hasActivity(Activity.field_22399) || target.getType() != EntityType.field_22281) {
			if (EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(target)) {
				if (target.getType() != EntityType.field_21973) {
					if (!LookTargetUtil.isNewTargetTooFar(hoglin, target, 4.0)) {
						setAttackTarget(hoglin, target);
						askAdultsForHelp(hoglin, target);
					}
				}
			}
		}
	}

	private static void setAttackTarget(HoglinEntity hoglin, LivingEntity target) {
		Brain<HoglinEntity> brain = hoglin.getBrain();
		brain.forget(MemoryModuleType.field_19293);
		brain.forget(MemoryModuleType.field_18448);
		brain.remember(MemoryModuleType.field_22355, target, 200L);
	}

	private static void askAdultsForHelp(HoglinEntity hoglin, LivingEntity target) {
		getAdultHoglinsAround(hoglin).forEach(hoglinx -> setAttackTargetIfCloser(hoglinx, target));
	}

	private static void setAttackTargetIfCloser(HoglinEntity hoglin, LivingEntity targetCandidate) {
		if (!isNearPlayer(hoglin)) {
			Optional<LivingEntity> optional = hoglin.getBrain().getOptionalMemory(MemoryModuleType.field_22355);
			LivingEntity livingEntity = LookTargetUtil.getCloserEntity(hoglin, optional, targetCandidate);
			setAttackTarget(hoglin, livingEntity);
		}
	}

	public static Optional<SoundEvent> method_30083(HoglinEntity hoglinEntity) {
		return hoglinEntity.getBrain().getFirstPossibleNonCoreActivity().map(activity -> method_30082(hoglinEntity, activity));
	}

	private static SoundEvent method_30082(HoglinEntity hoglinEntity, Activity activity) {
		if (activity == Activity.field_22399 || hoglinEntity.canConvert()) {
			return SoundEvents.field_22261;
		} else if (activity == Activity.field_22396) {
			return SoundEvents.field_22257;
		} else {
			return method_30085(hoglinEntity) ? SoundEvents.field_22261 : SoundEvents.field_22256;
		}
	}

	private static List<HoglinEntity> getAdultHoglinsAround(HoglinEntity hoglin) {
		return (List<HoglinEntity>)hoglin.getBrain().getOptionalMemory(MemoryModuleType.field_22344).orElse(ImmutableList.of());
	}

	private static boolean method_30085(HoglinEntity hoglinEntity) {
		return hoglinEntity.getBrain().hasMemoryModule(MemoryModuleType.field_22474);
	}

	private static boolean hasBreedTarget(HoglinEntity hoglin) {
		return hoglin.getBrain().hasMemoryModule(MemoryModuleType.field_18448);
	}

	protected static boolean isNearPlayer(HoglinEntity hoglin) {
		return hoglin.getBrain().hasMemoryModule(MemoryModuleType.field_22353);
	}
}
