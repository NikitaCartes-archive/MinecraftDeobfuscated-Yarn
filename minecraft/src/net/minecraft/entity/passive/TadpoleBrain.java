package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.task.FleeTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTargetTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookAtMobWithIntervalTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.ai.brain.task.TemptTask;
import net.minecraft.entity.ai.brain.task.TemptationCooldownTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class TadpoleBrain {
	private static final float FLEE_SPEED = 2.0F;
	private static final float field_37502 = 0.5F;
	private static final float TEMPT_SPEED = 1.25F;

	protected static Brain<?> create(Brain<TadpoleEntity> brain) {
		addCoreActivities(brain);
		addIdleActivities(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	private static void addCoreActivities(Brain<TadpoleEntity> brain) {
		brain.setTaskList(
			Activity.CORE,
			0,
			ImmutableList.of(
				new FleeTask(2.0F), new LookAroundTask(45, 90), new WanderAroundTask(), new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
			)
		);
	}

	private static void addIdleActivities(Brain<TadpoleEntity> brain) {
		brain.setTaskList(
			Activity.IDLE,
			ImmutableList.of(
				Pair.of(0, LookAtMobWithIntervalTask.follow(EntityType.PLAYER, 6.0F, UniformIntProvider.create(30, 60))),
				Pair.of(1, new TemptTask(livingEntity -> 1.25F)),
				Pair.of(
					2,
					new CompositeTask<>(
						ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT),
						ImmutableSet.of(),
						CompositeTask.Order.ORDERED,
						CompositeTask.RunMode.TRY_ALL,
						ImmutableList.of(
							Pair.of(StrollTask.createDynamicRadius(0.5F), 2),
							Pair.of(GoTowardsLookTargetTask.create(0.5F, 3), 3),
							Pair.of(TaskTriggerer.predicate(Entity::isInsideWaterOrBubbleColumn), 5)
						)
					)
				)
			)
		);
	}

	public static void updateActivities(TadpoleEntity tadpole) {
		tadpole.getBrain().resetPossibleActivities(ImmutableList.of(Activity.IDLE));
	}
}
