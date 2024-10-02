package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GoToLookTargetTask;
import net.minecraft.entity.ai.brain.task.LookAtMobWithIntervalTask;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.UpdateLookControlTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class CreakingBrain {
	protected static final ImmutableList<? extends SensorType<? extends Sensor<? super CreakingEntity>>> SENSORS = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS
	);
	protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
		MemoryModuleType.MOBS,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.NEAREST_VISIBLE_PLAYER,
		MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.WALK_TARGET,
		MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
		MemoryModuleType.PATH,
		MemoryModuleType.ATTACK_TARGET,
		MemoryModuleType.ATTACK_COOLING_DOWN
	);

	static void addCoreTasks(Brain<CreakingEntity> brain) {
		brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new StayAboveWaterTask<CreakingEntity>(0.8F) {
			protected boolean shouldRun(ServerWorld serverWorld, CreakingEntity creakingEntity) {
				return creakingEntity.isUnrooted() && super.shouldRun(serverWorld, (LivingEntity)creakingEntity);
			}
		}, new UpdateLookControlTask(45, 90), new MoveToTargetTask()));
	}

	static void addIdleTasks(Brain<CreakingEntity> brain) {
		brain.setTaskList(
			Activity.IDLE,
			10,
			ImmutableList.of(
				UpdateAttackTargetTask.create(
					(world, creaking) -> creaking.isActive() && creaking.isUnrooted(),
					(world, creaking) -> creaking.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER)
				),
				LookAtMobWithIntervalTask.follow(8.0F, UniformIntProvider.create(30, 60)),
				new RandomTask<>(ImmutableList.of(Pair.of(StrollTask.create(0.2F), 2), Pair.of(GoToLookTargetTask.create(0.2F, 3), 2), Pair.of(new WaitTask(30, 60), 1)))
			)
		);
	}

	static void addFightTasks(Brain<CreakingEntity> brain) {
		brain.setTaskList(
			Activity.FIGHT,
			10,
			ImmutableList.of(RangedApproachTask.create(1.0F), MeleeAttackTask.create(CreakingEntity::isUnrooted, 40), ForgetAttackTargetTask.create()),
			MemoryModuleType.ATTACK_TARGET
		);
	}

	public static Brain.Profile<CreakingEntity> createBrainProfile() {
		return Brain.createProfile(MEMORY_MODULES, SENSORS);
	}

	public static Brain<CreakingEntity> create(Brain<CreakingEntity> brain) {
		addCoreTasks(brain);
		addIdleTasks(brain);
		addFightTasks(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	public static void updateActivities(CreakingEntity creaking) {
		if (!creaking.isUnrooted()) {
			creaking.getBrain().resetPossibleActivities();
		} else {
			creaking.getBrain().resetPossibleActivities(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
		}
	}
}
