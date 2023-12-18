package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.BreedTask;
import net.minecraft.entity.ai.brain.task.FleeTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTargetTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookAtMobWithIntervalTask;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.RandomLookAroundTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.SingleTickTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.ai.brain.task.TemptTask;
import net.minecraft.entity.ai.brain.task.TemptationCooldownTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WalkTowardClosestAdultTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class ArmadilloBrain {
	public static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.SPIDER_EYE);
	private static final float field_47797 = 2.0F;
	private static final float field_47798 = 1.0F;
	private static final float field_47799 = 1.25F;
	private static final float field_47800 = 1.1F;
	private static final float field_47801 = 1.0F;
	private static final UniformIntProvider field_47802 = UniformIntProvider.create(5, 16);
	private static final ImmutableList<SensorType<? extends Sensor<? super ArmadilloEntity>>> SENSOR_TYPES = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.ARMADILLO_TEMPTATIONS, SensorType.NEAREST_ADULT, SensorType.ARMADILLO_SCARE_DETECTED
	);
	private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULE_TYPES = ImmutableList.of(
		MemoryModuleType.IS_PANICKING,
		MemoryModuleType.HURT_BY,
		MemoryModuleType.HURT_BY_ENTITY,
		MemoryModuleType.WALK_TARGET,
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
		MemoryModuleType.PATH,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.TEMPTING_PLAYER,
		MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
		MemoryModuleType.GAZE_COOLDOWN_TICKS,
		MemoryModuleType.IS_TEMPTED,
		MemoryModuleType.BREED_TARGET,
		MemoryModuleType.NEAREST_VISIBLE_ADULT,
		MemoryModuleType.DANGER_DETECTED_RECENTLY
	);
	private static final SingleTickTask<ArmadilloEntity> field_47805 = TaskTriggerer.task(
		taskContext -> taskContext.group(taskContext.queryMemoryAbsent(MemoryModuleType.DANGER_DETECTED_RECENTLY))
				.apply(taskContext, memoryQueryResult -> (serverWorld, armadillo, l) -> {
						if (armadillo.isNotIdle()) {
							armadillo.unroll(armadillo.canRollUp());
							return true;
						} else {
							return false;
						}
					})
	);

	public static Brain.Profile<ArmadilloEntity> createBrainProfile() {
		return Brain.createProfile(MEMORY_MODULE_TYPES, SENSOR_TYPES);
	}

	protected static Brain<?> create(Brain<ArmadilloEntity> brain) {
		addCoreActivities(brain);
		addIdleActivities(brain);
		addPanicActivities(brain);
		brain.setCoreActivities(Set.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	private static void addCoreActivities(Brain<ArmadilloEntity> brain) {
		brain.setTaskList(
			Activity.CORE, 0, ImmutableList.of(new StayAboveWaterTask(0.8F), new ArmadilloBrain.class_9073(2.0F), new LookAroundTask(45, 90), new WanderAroundTask() {
				@Override
				protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
					if (mobEntity instanceof ArmadilloEntity armadilloEntity && armadilloEntity.isNotIdle()) {
						return false;
					}

					return super.shouldRun(serverWorld, mobEntity);
				}
			}, new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), new TemptationCooldownTask(MemoryModuleType.GAZE_COOLDOWN_TICKS), field_47805)
		);
	}

	private static void addIdleActivities(Brain<ArmadilloEntity> brain) {
		brain.setTaskList(
			Activity.IDLE,
			ImmutableList.of(
				Pair.of(0, LookAtMobWithIntervalTask.follow(EntityType.PLAYER, 6.0F, UniformIntProvider.create(30, 60))),
				Pair.of(1, new BreedTask(EntityType.ARMADILLO, 1.0F)),
				Pair.of(
					2,
					new RandomTask<>(
						ImmutableList.of(
							Pair.of(new TemptTask(livingEntity -> 1.25F, livingEntity -> livingEntity.isBaby() ? 2.5 : 3.5), 1),
							Pair.of(WalkTowardClosestAdultTask.create(field_47802, 1.1F), 1)
						)
					)
				),
				Pair.of(3, new RandomLookAroundTask(UniformIntProvider.create(150, 250), 30.0F, 0.0F, 0.0F)),
				Pair.of(
					4,
					new RandomTask<>(
						ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT),
						ImmutableList.of(Pair.of(StrollTask.create(1.0F), 1), Pair.of(GoTowardsLookTargetTask.create(1.0F, 3), 1), Pair.of(new WaitTask(30, 60), 1))
					)
				)
			)
		);
	}

	private static void addPanicActivities(Brain<ArmadilloEntity> brain) {
		brain.setTaskList(
			Activity.PANIC,
			ImmutableList.of(Pair.of(0, new ArmadilloBrain.class_9072())),
			Set.of(Pair.of(MemoryModuleType.DANGER_DETECTED_RECENTLY, MemoryModuleState.VALUE_PRESENT))
		);
	}

	public static void updateActivities(ArmadilloEntity armadillo) {
		armadillo.getBrain().resetPossibleActivities(ImmutableList.of(Activity.PANIC, Activity.IDLE));
	}

	public static Ingredient getBreedingIngredient() {
		return BREEDING_INGREDIENT;
	}

	public static class class_9072 extends MultiTickTask<ArmadilloEntity> {
		public class_9072() {
			super(Map.of());
		}

		protected void keepRunning(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
			super.keepRunning(serverWorld, armadilloEntity, l);
			if (armadilloEntity.shouldSwitchToScaredState()) {
				armadilloEntity.setState(ArmadilloEntity.State.SCARED);
				if (armadilloEntity.isOnGround()) {
					armadilloEntity.getWorld()
						.playSound(null, armadilloEntity.getBlockPos(), SoundEvents.ENTITY_ARMADILLO_LAND, armadilloEntity.getSoundCategory(), 1.0F, 1.0F);
				}
			}
		}

		protected boolean shouldRun(ServerWorld serverWorld, ArmadilloEntity armadilloEntity) {
			return armadilloEntity.isOnGround();
		}

		protected boolean shouldKeepRunning(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
			return true;
		}

		protected void run(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
			armadilloEntity.startRolling();
		}

		protected void finishRunning(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
			if (!armadilloEntity.canRollUp()) {
				armadilloEntity.unroll(false);
			}
		}
	}

	public static class class_9073 extends FleeTask {
		public class_9073(float f) {
			super(f);
		}

		@Override
		protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
			if (pathAwareEntity instanceof ArmadilloEntity armadilloEntity) {
				armadilloEntity.unroll(true);
			}

			super.run(serverWorld, pathAwareEntity, l);
		}
	}
}
