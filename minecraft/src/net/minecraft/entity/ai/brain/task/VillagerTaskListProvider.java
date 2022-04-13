package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class VillagerTaskListProvider {
	private static final float JOB_WALKING_SPEED = 0.4F;

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createCoreTasks(VillagerProfession profession, float speed) {
		return ImmutableList.of(
			Pair.of(0, new StayAboveWaterTask(0.8F)),
			Pair.of(0, new OpenDoorsTask()),
			Pair.of(0, new LookAroundTask(45, 90)),
			Pair.of(0, new PanicTask()),
			Pair.of(0, new WakeUpTask()),
			Pair.of(0, new HideWhenBellRingsTask()),
			Pair.of(0, new StartRaidTask()),
			Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.JOB_SITE)),
			Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.POTENTIAL_JOB_SITE)),
			Pair.of(1, new WanderAroundTask()),
			Pair.of(2, new WorkStationCompetitionTask(profession)),
			Pair.of(3, new FollowCustomerTask(speed)),
			Pair.of(5, new WalkToNearestVisibleWantedItemTask(speed, false, 4)),
			Pair.of(6, new FindPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())),
			Pair.of(7, new WalkTowardJobSiteTask(speed)),
			Pair.of(8, new TakeJobSiteTask(speed)),
			Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME, false, Optional.of((byte)14))),
			Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT, true, Optional.of((byte)14))),
			Pair.of(10, new GoToWorkTask()),
			Pair.of(10, new LoseJobOnSiteLossTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createWorkTasks(VillagerProfession profession, float speed) {
		VillagerWorkTask villagerWorkTask;
		if (profession == VillagerProfession.FARMER) {
			villagerWorkTask = new FarmerWorkTask();
		} else {
			villagerWorkTask = new VillagerWorkTask();
		}

		return ImmutableList.of(
			createBusyFollowTask(),
			Pair.of(
				5,
				new RandomTask<>(
					ImmutableList.of(
						Pair.of(villagerWorkTask, 7),
						Pair.of(new GoToIfNearbyTask(MemoryModuleType.JOB_SITE, 0.4F, 4), 2),
						Pair.of(new GoToNearbyPositionTask(MemoryModuleType.JOB_SITE, 0.4F, 1, 10), 5),
						Pair.of(new GoToSecondaryPositionTask(MemoryModuleType.SECONDARY_JOB_SITE, speed, 1, 6, MemoryModuleType.JOB_SITE), 5),
						Pair.of(new FarmerVillagerTask(), profession == VillagerProfession.FARMER ? 2 : 5),
						Pair.of(new BoneMealTask(), profession == VillagerProfession.FARMER ? 4 : 7)
					)
				)
			),
			Pair.of(10, new HoldTradeOffersTask(400, 1600)),
			Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
			Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.JOB_SITE, speed, 9, 100, 1200)),
			Pair.of(3, new GiveGiftsToHeroTask(100)),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPlayTasks(float speed) {
		return ImmutableList.of(
			Pair.of(0, new WanderAroundTask(80, 120)),
			createFreeFollowTask(),
			Pair.of(5, new PlayWithVillagerBabiesTask()),
			Pair.of(
				5,
				new RandomTask<>(
					ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleState.VALUE_ABSENT),
					ImmutableList.of(
						Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 2),
						Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 1),
						Pair.of(new FindWalkTargetTask(speed), 1),
						Pair.of(new GoTowardsLookTarget(speed, 2), 1),
						Pair.of(new JumpInBedTask(speed), 2),
						Pair.of(new WaitTask(20, 40), 2)
					)
				)
			),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createRestTasks(VillagerProfession profession, float speed) {
		return ImmutableList.of(
			Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.HOME, speed, 1, 150, 1200)),
			Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME)),
			Pair.of(3, new SleepTask()),
			Pair.of(
				5,
				new RandomTask<>(
					ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_ABSENT),
					ImmutableList.of(
						Pair.of(new WalkHomeTask(speed), 1),
						Pair.of(new WanderIndoorsTask(speed), 4),
						Pair.of(new GoToPointOfInterestTask(speed, 4), 2),
						Pair.of(new WaitTask(20, 40), 2)
					)
				)
			),
			createBusyFollowTask(),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createMeetTasks(VillagerProfession profession, float speed) {
		return ImmutableList.of(
			Pair.of(
				2, new RandomTask<>(ImmutableList.of(Pair.of(new GoToIfNearbyTask(MemoryModuleType.MEETING_POINT, 0.4F, 40), 2), Pair.of(new MeetVillagerTask(), 2)))
			),
			Pair.of(10, new HoldTradeOffersTask(400, 1600)),
			Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
			Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.MEETING_POINT, speed, 6, 100, 200)),
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

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createIdleTasks(VillagerProfession profession, float speed) {
		return ImmutableList.of(
			Pair.of(
				2,
				new RandomTask<>(
					ImmutableList.of(
						Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 2),
						Pair.of(
							new FindEntityTask<>(EntityType.VILLAGER, 8, PassiveEntity::isReadyToBreed, PassiveEntity::isReadyToBreed, MemoryModuleType.BREED_TARGET, speed, 2), 1
						),
						Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 1),
						Pair.of(new FindWalkTargetTask(speed), 1),
						Pair.of(new GoTowardsLookTarget(speed, 2), 1),
						Pair.of(new JumpInBedTask(speed), 1),
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

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPanicTasks(VillagerProfession profession, float speed) {
		float f = speed * 1.5F;
		return ImmutableList.of(
			Pair.of(0, new StopPanickingTask()),
			Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.NEAREST_HOSTILE, f, 6, false)),
			Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.HURT_BY_ENTITY, f, 6, false)),
			Pair.of(3, new FindWalkTargetTask(f, 2, 2)),
			createBusyFollowTask()
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPreRaidTasks(VillagerProfession profession, float speed) {
		return ImmutableList.of(
			Pair.of(0, new RingBellTask()),
			Pair.of(
				0,
				new RandomTask<>(
					ImmutableList.of(
						Pair.of(new VillagerWalkTowardsTask(MemoryModuleType.MEETING_POINT, speed * 1.5F, 2, 150, 200), 6), Pair.of(new FindWalkTargetTask(speed * 1.5F), 2)
					)
				)
			),
			createBusyFollowTask(),
			Pair.of(99, new EndRaidTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createRaidTasks(VillagerProfession profession, float speed) {
		return ImmutableList.of(
			Pair.of(0, new RandomTask<>(ImmutableList.of(Pair.of(new SeekSkyAfterRaidWinTask(speed), 5), Pair.of(new RunAroundAfterRaidTask(speed * 1.1F), 2)))),
			Pair.of(0, new CelebrateRaidWinTask(600, 600)),
			Pair.of(2, new HideInHomeDuringRaidTask(24, speed * 1.4F)),
			createBusyFollowTask(),
			Pair.of(99, new EndRaidTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createHideTasks(VillagerProfession profession, float speed) {
		int i = 2;
		return ImmutableList.of(Pair.of(0, new ForgetBellRingTask(15, 3)), Pair.of(1, new HideInHomeTask(32, speed * 1.25F, 2)), createBusyFollowTask());
	}

	private static Pair<Integer, Task<LivingEntity>> createFreeFollowTask() {
		return Pair.of(
			5,
			new RandomTask<>(
				ImmutableList.of(
					Pair.of(new FollowMobTask(EntityType.CAT, 8.0F), 8),
					Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 2),
					Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2),
					Pair.of(new FollowMobTask(SpawnGroup.CREATURE, 8.0F), 1),
					Pair.of(new FollowMobTask(SpawnGroup.WATER_CREATURE, 8.0F), 1),
					Pair.of(new FollowMobTask(SpawnGroup.AXOLOTLS, 8.0F), 1),
					Pair.of(new FollowMobTask(SpawnGroup.UNDERGROUND_WATER_CREATURE, 8.0F), 1),
					Pair.of(new FollowMobTask(SpawnGroup.WATER_AMBIENT, 8.0F), 1),
					Pair.of(new FollowMobTask(SpawnGroup.MONSTER, 8.0F), 1),
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
