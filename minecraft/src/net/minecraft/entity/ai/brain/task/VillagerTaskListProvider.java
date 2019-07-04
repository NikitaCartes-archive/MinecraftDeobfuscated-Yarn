package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.class_4318;
import net.minecraft.class_4458;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.tasks.GoToNearbyPositionTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.village.VillagerProfession;

public class VillagerTaskListProvider {
	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createCoreTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(0, new StayAboveWaterTask(0.4F, 0.8F)),
			Pair.of(0, new OpenDoorsTask()),
			Pair.of(0, new LookAroundTask(45, 90)),
			Pair.of(0, new PanicTask()),
			Pair.of(0, new WakeUpTask()),
			Pair.of(0, new HideWhenBellRingsTask()),
			Pair.of(0, new StartRaidTask()),
			Pair.of(1, new WanderAroundTask(200)),
			Pair.of(2, new FollowCustomerTask(f)),
			Pair.of(5, new class_4318()),
			Pair.of(10, new FindPointOfInterestTask(villagerProfession.getWorkStation(), MemoryModuleType.JOB_SITE, true)),
			Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME, false)),
			Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT, true)),
			Pair.of(10, new GoToWorkTask()),
			Pair.of(10, new LoseJobOnSiteLossTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createWorkTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			createBusyFollowTask(),
			Pair.of(
				5,
				new RandomTask<>(
					ImmutableList.of(
						Pair.of(new VillagerWorkTask(), 7),
						Pair.of(new GoToIfNearbyTask(MemoryModuleType.JOB_SITE, 4), 2),
						Pair.of(new GoToNearbyPositionTask(MemoryModuleType.JOB_SITE, 1, 10), 5),
						Pair.of(new GoToSecondaryPositionTask(MemoryModuleType.SECONDARY_JOB_SITE, 0.4F, 1, 6, MemoryModuleType.JOB_SITE), 5),
						Pair.of(new FarmerVillagerTask(), villagerProfession == VillagerProfession.FARMER ? 2 : 5)
					)
				)
			),
			Pair.of(10, new HoldTradeOffersTask(400, 1600)),
			Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
			Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.JOB_SITE, f, 9, 100, 1200)),
			Pair.of(3, new GiveGiftsToHeroTask(100)),
			Pair.of(3, new ForgetCompletedPointOfInterestTask(villagerProfession.getWorkStation(), MemoryModuleType.JOB_SITE)),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPlayTasks(float f) {
		return ImmutableList.of(
			Pair.of(0, new WanderAroundTask(100)),
			createFreeFollowTask(),
			Pair.of(5, new PlayWithVillagerBabiesTask()),
			Pair.of(
				5,
				new RandomTask<>(
					ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleState.VALUE_ABSENT),
					ImmutableList.of(
						Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 2),
						Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 1),
						Pair.of(new FindWalkTargetTask(f), 1),
						Pair.of(new GoTowardsLookTarget(f, 2), 1),
						Pair.of(new JumpInBedTask(f), 2),
						Pair.of(new WaitTask(20, 40), 2)
					)
				)
			),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createRestTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.HOME, f, 1, 150, 1200)),
			Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME)),
			Pair.of(3, new SleepTask()),
			Pair.of(
				5,
				new RandomTask<>(
					ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_ABSENT),
					ImmutableList.of(Pair.of(new WalkHomeTask(f), 1), Pair.of(new WanderIndoorsTask(f), 4), Pair.of(new class_4458(f, 4), 2), Pair.of(new WaitTask(20, 40), 2))
				)
			),
			createBusyFollowTask(),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createMeetTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(2, new RandomTask<>(ImmutableList.of(Pair.of(new GoToIfNearbyTask(MemoryModuleType.MEETING_POINT, 40), 2), Pair.of(new MeetVillagerTask(), 2)))),
			Pair.of(10, new HoldTradeOffersTask(400, 1600)),
			Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
			Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.MEETING_POINT, f, 6, 100, 200)),
			Pair.of(3, new GiveGiftsToHeroTask(100)),
			Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT)),
			Pair.of(
				3,
				new CompositeTask<>(
					ImmutableMap.of(),
					ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
					CompositeTask.Order.ORDERED,
					CompositeTask.RunMode.RUN_ONE,
					ImmutableList.of(Pair.of(new GatherItemsVillagerTask(), 1))
				)
			),
			createFreeFollowTask(),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createIdleTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(
				2,
				new RandomTask<>(
					ImmutableList.of(
						Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 2),
						Pair.of(
							new FindEntityTask<>(EntityType.VILLAGER, 8, VillagerEntity::isReadyToBreed, VillagerEntity::isReadyToBreed, MemoryModuleType.BREED_TARGET, f, 2), 1
						),
						Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 1),
						Pair.of(new FindWalkTargetTask(f), 1),
						Pair.of(new GoTowardsLookTarget(f, 2), 1),
						Pair.of(new JumpInBedTask(f), 1),
						Pair.of(new WaitTask(30, 60), 1)
					)
				)
			),
			Pair.of(3, new GiveGiftsToHeroTask(100)),
			Pair.of(3, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
			Pair.of(3, new HoldTradeOffersTask(400, 1600)),
			Pair.of(
				3,
				new CompositeTask<>(
					ImmutableMap.of(),
					ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
					CompositeTask.Order.ORDERED,
					CompositeTask.RunMode.RUN_ONE,
					ImmutableList.of(Pair.of(new GatherItemsVillagerTask(), 1))
				)
			),
			Pair.of(
				3,
				new CompositeTask<>(
					ImmutableMap.of(),
					ImmutableSet.of(MemoryModuleType.BREED_TARGET),
					CompositeTask.Order.ORDERED,
					CompositeTask.RunMode.RUN_ONE,
					ImmutableList.of(Pair.of(new VillagerBreedTask(), 1))
				)
			),
			createFreeFollowTask(),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPanicTasks(VillagerProfession villagerProfession, float f) {
		float g = f * 1.5F;
		return ImmutableList.of(
			Pair.of(0, new StopPanicingTask()),
			Pair.of(1, new GoToNearbyEntityTask(MemoryModuleType.NEAREST_HOSTILE, g)),
			Pair.of(1, new GoToNearbyEntityTask(MemoryModuleType.HURT_BY_ENTITY, g)),
			Pair.of(3, new FindWalkTargetTask(g, 2, 2)),
			createBusyFollowTask()
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPreRaidTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(0, new RingBellTask()),
			Pair.of(
				0,
				new RandomTask<>(
					ImmutableList.of(
						Pair.of(new VillagerWalkTowardsTask(MemoryModuleType.MEETING_POINT, f * 1.5F, 2, 150, 200), 6), Pair.of(new FindWalkTargetTask(f * 1.5F), 2)
					)
				)
			),
			createBusyFollowTask(),
			Pair.of(99, new EndRaidTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createRaidTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(0, new RandomTask<>(ImmutableList.of(Pair.of(new SeekSkyAfterRaidWinTask(f), 5), Pair.of(new RunAroundAfterRaidTask(f * 1.1F), 2)))),
			Pair.of(0, new CelebrateRaidWinTask(600, 600)),
			Pair.of(2, new HideInHomeDuringRaidTask(24, f * 1.4F)),
			createBusyFollowTask(),
			Pair.of(99, new EndRaidTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createHideTasks(VillagerProfession villagerProfession, float f) {
		int i = 2;
		return ImmutableList.of(Pair.of(0, new ForgetBellRingTask(15, 2)), Pair.of(1, new HideInHomeTask(32, f * 1.25F, 2)), createBusyFollowTask());
	}

	private static Pair<Integer, Task<LivingEntity>> createFreeFollowTask() {
		return Pair.of(
			5,
			new RandomTask<>(
				ImmutableList.of(
					Pair.of(new FollowMobTask(EntityType.CAT, 8.0F), 8),
					Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 2),
					Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2),
					Pair.of(new FollowMobTask(EntityCategory.CREATURE, 8.0F), 1),
					Pair.of(new FollowMobTask(EntityCategory.WATER_CREATURE, 8.0F), 1),
					Pair.of(new FollowMobTask(EntityCategory.MONSTER, 8.0F), 1),
					Pair.of(new WaitTask(30, 60), 2)
				)
			)
		);
	}

	private static Pair<Integer, Task<LivingEntity>> createBusyFollowTask() {
		return Pair.of(
			5,
			new RandomTask<>(
				ImmutableList.of(
					Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 2), Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2), Pair.of(new WaitTask(30, 60), 8)
				)
			)
		);
	}
}
