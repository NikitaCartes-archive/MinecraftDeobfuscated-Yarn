package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.BiasedLongJumpTask;
import net.minecraft.entity.ai.brain.task.BreedTask;
import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.task.CroakTask;
import net.minecraft.entity.ai.brain.task.FleeTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.FrogEatEntityTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTargetTask;
import net.minecraft.entity.ai.brain.task.LayFrogSpawnTask;
import net.minecraft.entity.ai.brain.task.LeapingChargeTask;
import net.minecraft.entity.ai.brain.task.LongJumpTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookAtMobWithIntervalTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.ai.brain.task.TemptTask;
import net.minecraft.entity.ai.brain.task.TemptationCooldownTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsLandTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsWaterTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class FrogBrain {
	private static final float FLEE_SPEED = 2.0F;
	private static final float field_37470 = 1.0F;
	private static final float field_37471 = 1.0F;
	private static final float field_37472 = 1.0F;
	private static final float field_37473 = 0.75F;
	private static final UniformIntProvider longJumpCooldownRange = UniformIntProvider.create(100, 140);
	private static final int field_37475 = 2;
	private static final int field_37476 = 4;
	private static final float field_37477 = 1.5F;
	private static final float TEMPT_SPEED = 1.25F;

	protected static void coolDownLongJump(FrogEntity frog, Random random) {
		frog.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, longJumpCooldownRange.get(random));
	}

	protected static Brain<?> create(Brain<FrogEntity> brain) {
		addCoreActivities(brain);
		addIdleActivities(brain);
		addSwimActivities(brain);
		addLaySpawnActivities(brain);
		addTongueActivities(brain);
		addLongJumpActivities(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	private static void addCoreActivities(Brain<FrogEntity> brain) {
		brain.setTaskList(
			Activity.CORE,
			0,
			ImmutableList.of(
				new FleeTask(2.0F),
				new LookAroundTask(45, 90),
				new WanderAroundTask(),
				new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
				new TemptationCooldownTask(MemoryModuleType.LONG_JUMP_COOLING_DOWN)
			)
		);
	}

	private static void addIdleActivities(Brain<FrogEntity> brain) {
		brain.setTaskList(
			Activity.IDLE,
			ImmutableList.of(
				Pair.of(0, LookAtMobWithIntervalTask.follow(EntityType.PLAYER, 6.0F, UniformIntProvider.create(30, 60))),
				Pair.of(0, new BreedTask(EntityType.FROG, 1.0F)),
				Pair.of(1, new TemptTask(frog -> 1.25F)),
				Pair.of(
					2, UpdateAttackTargetTask.create(FrogBrain::isNotBreeding, frog -> frog.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_ATTACKABLE))
				),
				Pair.of(3, WalkTowardsLandTask.create(6, 1.0F)),
				Pair.of(
					4,
					new RandomTask<>(
						ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT),
						ImmutableList.of(
							Pair.of(StrollTask.create(1.0F), 1),
							Pair.of(GoTowardsLookTargetTask.create(1.0F, 3), 1),
							Pair.of(new CroakTask(), 3),
							Pair.of(TaskTriggerer.predicate(Entity::isOnGround), 2)
						)
					)
				)
			),
			ImmutableSet.of(
				Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.IS_IN_WATER, MemoryModuleState.VALUE_ABSENT)
			)
		);
	}

	private static void addSwimActivities(Brain<FrogEntity> brain) {
		brain.setTaskList(
			Activity.SWIM,
			ImmutableList.of(
				Pair.of(0, LookAtMobWithIntervalTask.follow(EntityType.PLAYER, 6.0F, UniformIntProvider.create(30, 60))),
				Pair.of(1, new TemptTask(frog -> 1.25F)),
				Pair.of(
					2, UpdateAttackTargetTask.create(FrogBrain::isNotBreeding, frog -> frog.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_ATTACKABLE))
				),
				Pair.of(3, WalkTowardsLandTask.create(8, 1.5F)),
				Pair.of(
					5,
					new CompositeTask<>(
						ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT),
						ImmutableSet.of(),
						CompositeTask.Order.ORDERED,
						CompositeTask.RunMode.TRY_ALL,
						ImmutableList.of(
							Pair.of(StrollTask.createDynamicRadius(0.75F), 1),
							Pair.of(StrollTask.create(1.0F, true), 1),
							Pair.of(GoTowardsLookTargetTask.create(1.0F, 3), 1),
							Pair.of(TaskTriggerer.predicate(Entity::isInsideWaterOrBubbleColumn), 5)
						)
					)
				)
			),
			ImmutableSet.of(
				Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.IS_IN_WATER, MemoryModuleState.VALUE_PRESENT)
			)
		);
	}

	private static void addLaySpawnActivities(Brain<FrogEntity> brain) {
		brain.setTaskList(
			Activity.LAY_SPAWN,
			ImmutableList.of(
				Pair.of(0, LookAtMobWithIntervalTask.follow(EntityType.PLAYER, 6.0F, UniformIntProvider.create(30, 60))),
				Pair.of(
					1, UpdateAttackTargetTask.create(FrogBrain::isNotBreeding, frog -> frog.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_ATTACKABLE))
				),
				Pair.of(2, WalkTowardsWaterTask.create(8, 1.0F)),
				Pair.of(3, LayFrogSpawnTask.create(Blocks.FROGSPAWN)),
				Pair.of(
					4,
					new RandomTask<>(
						ImmutableList.of(
							Pair.of(StrollTask.create(1.0F), 2),
							Pair.of(GoTowardsLookTargetTask.create(1.0F, 3), 1),
							Pair.of(new CroakTask(), 2),
							Pair.of(TaskTriggerer.predicate(Entity::isOnGround), 1)
						)
					)
				)
			),
			ImmutableSet.of(
				Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.IS_PREGNANT, MemoryModuleState.VALUE_PRESENT)
			)
		);
	}

	private static void addLongJumpActivities(Brain<FrogEntity> brain) {
		brain.setTaskList(
			Activity.LONG_JUMP,
			ImmutableList.of(
				Pair.of(0, new LeapingChargeTask(longJumpCooldownRange, SoundEvents.ENTITY_FROG_STEP)),
				Pair.of(
					1,
					new BiasedLongJumpTask<>(
						longJumpCooldownRange, 2, 4, 1.5F, frog -> SoundEvents.ENTITY_FROG_LONG_JUMP, BlockTags.FROG_PREFER_JUMP_TO, 0.5F, FrogBrain::shouldJumpTo
					)
				)
			),
			ImmutableSet.of(
				Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryModuleState.VALUE_ABSENT),
				Pair.of(MemoryModuleType.BREED_TARGET, MemoryModuleState.VALUE_ABSENT),
				Pair.of(MemoryModuleType.LONG_JUMP_COOLING_DOWN, MemoryModuleState.VALUE_ABSENT),
				Pair.of(MemoryModuleType.IS_IN_WATER, MemoryModuleState.VALUE_ABSENT)
			)
		);
	}

	private static void addTongueActivities(Brain<FrogEntity> brain) {
		brain.setTaskList(
			Activity.TONGUE,
			0,
			ImmutableList.of(ForgetAttackTargetTask.create(), new FrogEatEntityTask(SoundEvents.ENTITY_FROG_TONGUE, SoundEvents.ENTITY_FROG_EAT)),
			MemoryModuleType.ATTACK_TARGET
		);
	}

	private static <E extends MobEntity> boolean shouldJumpTo(E frog, BlockPos pos) {
		World world = frog.getWorld();
		BlockPos blockPos = pos.down();
		if (world.getFluidState(pos).isEmpty() && world.getFluidState(blockPos).isEmpty() && world.getFluidState(pos.up()).isEmpty()) {
			BlockState blockState = world.getBlockState(pos);
			BlockState blockState2 = world.getBlockState(blockPos);
			if (!blockState.isIn(BlockTags.FROG_PREFER_JUMP_TO) && !blockState2.isIn(BlockTags.FROG_PREFER_JUMP_TO)) {
				PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(world, pos.mutableCopy());
				PathNodeType pathNodeType2 = LandPathNodeMaker.getLandNodeType(world, blockPos.mutableCopy());
				return pathNodeType != PathNodeType.TRAPDOOR && (!blockState.isAir() || pathNodeType2 != PathNodeType.TRAPDOOR)
					? LongJumpTask.shouldJumpTo(frog, pos)
					: true;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	private static boolean isNotBreeding(FrogEntity frog) {
		return !LookTargetUtil.hasBreedTarget(frog);
	}

	public static void updateActivities(FrogEntity frog) {
		frog.getBrain().resetPossibleActivities(ImmutableList.of(Activity.TONGUE, Activity.LAY_SPAWN, Activity.LONG_JUMP, Activity.SWIM, Activity.IDLE));
	}

	public static Ingredient getTemptItems() {
		return FrogEntity.SLIME_BALL;
	}
}
