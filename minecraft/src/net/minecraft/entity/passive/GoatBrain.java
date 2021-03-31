package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.class_6028;
import net.minecraft.class_6029;
import net.minecraft.class_6030;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.BreedTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.TemptTask;
import net.minecraft.entity.ai.brain.task.TemptationCooldownTask;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WalkTowardClosestAdultTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class GoatBrain {
	private static final UniformIntProvider field_30407 = UniformIntProvider.create(5, 16);
	public static final float field_30405 = 1.0F;
	public static final float field_30406 = 1.0F;
	private static final float field_30408 = 1.25F;
	private static final float field_30409 = 1.25F;
	private static final float field_30410 = 2.0F;
	private static final UniformIntProvider LONG_JUMP_COOLDOWN_RANGE = UniformIntProvider.create(600, 1200);

	protected static void resetLongJumpCooldown(GoatEntity goat) {
		goat.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, LONG_JUMP_COOLDOWN_RANGE.get(goat.world.random));
	}

	protected static Brain<?> create(Brain<GoatEntity> brain) {
		addCoreActivities(brain);
		addIdleActivities(brain);
		addLongJumpActivities(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	private static void addCoreActivities(Brain<GoatEntity> brain) {
		brain.setTaskList(
			Activity.CORE,
			0,
			ImmutableList.of(
				new class_6028(2.0F),
				new LookAroundTask(45, 90),
				new WanderAroundTask(),
				new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
				new TemptationCooldownTask(MemoryModuleType.LONG_JUMP_COOLING_DOWN)
			)
		);
	}

	private static void addIdleActivities(Brain<GoatEntity> brain) {
		brain.setTaskList(
			Activity.IDLE,
			ImmutableList.of(
				Pair.of(0, new TimeLimitedTask<>(new FollowMobTask(EntityType.PLAYER, 6.0F), UniformIntProvider.create(30, 60))),
				Pair.of(0, new BreedTask(EntityType.GOAT, 1.0F)),
				Pair.of(1, new TemptTask(livingEntity -> 1.25F)),
				Pair.of(2, new WalkTowardClosestAdultTask<>(field_30407, 1.25F)),
				Pair.of(
					3, new RandomTask<>(ImmutableList.of(Pair.of(new StrollTask(1.0F), 2), Pair.of(new GoTowardsLookTarget(1.0F, 3), 2), Pair.of(new WaitTask(30, 60), 1)))
				)
			)
		);
	}

	private static void addLongJumpActivities(Brain<GoatEntity> brain) {
		brain.setTaskList(
			Activity.LONG_JUMP,
			ImmutableList.of(Pair.of(0, new class_6029(LONG_JUMP_COOLDOWN_RANGE)), Pair.of(1, new class_6030(LONG_JUMP_COOLDOWN_RANGE, 5, 5, 1.5F))),
			ImmutableSet.of(
				Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryModuleState.VALUE_ABSENT),
				Pair.of(MemoryModuleType.BREED_TARGET, MemoryModuleState.VALUE_ABSENT),
				Pair.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT),
				Pair.of(MemoryModuleType.LONG_JUMP_COOLING_DOWN, MemoryModuleState.VALUE_ABSENT)
			)
		);
	}

	public static void updateActivities(GoatEntity goat) {
		goat.getBrain().resetPossibleActivities(ImmutableList.of(Activity.LONG_JUMP, Activity.IDLE));
	}

	public static Ingredient getTemptItems() {
		return Ingredient.ofItems(Items.WHEAT);
	}
}
