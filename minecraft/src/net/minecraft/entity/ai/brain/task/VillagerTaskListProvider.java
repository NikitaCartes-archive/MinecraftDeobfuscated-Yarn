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
	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createCoreTasks(VillagerProfession profession, float f) {
		return ImmutableList.of(
			Pair.of(0, new StayAboveWaterTask(0.8F)),
			Pair.of(0, new OpenDoorsTask()),
			Pair.of(0, new LookAroundTask(45, 90)),
			Pair.of(0, new PanicTask()),
			Pair.of(0, new WakeUpTask()),
			Pair.of(0, new HideWhenBellRingsTask()),
			Pair.of(0, new StartRaidTask()),
			Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.field_18439)),
			Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.field_25160)),
			Pair.of(1, new WanderAroundTask()),
			Pair.of(2, new WorkStationCompetitionTask(profession)),
			Pair.of(3, new FollowCustomerTask(f)),
			Pair.of(5, new WalkToNearestVisibleWantedItemTask(f, false, 4)),
			Pair.of(6, new FindPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.field_18439, MemoryModuleType.field_25160, true, Optional.empty())),
			Pair.of(7, new WalkTowardJobSiteTask(f)),
			Pair.of(8, new TakeJobSiteTask(f)),
			Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.field_18517, MemoryModuleType.field_18438, false, Optional.of((byte)14))),
			Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.field_18518, MemoryModuleType.field_18440, true, Optional.of((byte)14))),
			Pair.of(10, new GoToWorkTask()),
			Pair.of(10, new LoseJobOnSiteLossTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createWorkTasks(VillagerProfession profession, float f) {
		VillagerWorkTask villagerWorkTask;
		if (profession == VillagerProfession.field_17056) {
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
						Pair.of(new GoToIfNearbyTask(MemoryModuleType.field_18439, 0.4F, 4), 2),
						Pair.of(new GoToNearbyPositionTask(MemoryModuleType.field_18439, 0.4F, 1, 10), 5),
						Pair.of(new GoToSecondaryPositionTask(MemoryModuleType.field_18873, f, 1, 6, MemoryModuleType.field_18439), 5),
						Pair.of(new FarmerVillagerTask(), profession == VillagerProfession.field_17056 ? 2 : 5),
						Pair.of(new BoneMealTask(), profession == VillagerProfession.field_17056 ? 4 : 7)
					)
				)
			),
			Pair.of(10, new HoldTradeOffersTask(400, 1600)),
			Pair.of(10, new FindInteractionTargetTask(EntityType.field_6097, 4)),
			Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.field_18439, f, 9, 100, 1200)),
			Pair.of(3, new GiveGiftsToHeroTask(100)),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPlayTasks(float f) {
		return ImmutableList.of(
			Pair.of(0, new WanderAroundTask(80, 120)),
			createFreeFollowTask(),
			Pair.of(5, new PlayWithVillagerBabiesTask()),
			Pair.of(
				5,
				new RandomTask<>(
					ImmutableMap.of(MemoryModuleType.field_19006, MemoryModuleState.field_18457),
					ImmutableList.of(
						Pair.of(FindEntityTask.create(EntityType.field_6077, 8, MemoryModuleType.field_18447, f, 2), 2),
						Pair.of(FindEntityTask.create(EntityType.field_16281, 8, MemoryModuleType.field_18447, f, 2), 1),
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

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createRestTasks(VillagerProfession profession, float f) {
		return ImmutableList.of(
			Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.field_18438, f, 1, 150, 1200)),
			Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.field_18517, MemoryModuleType.field_18438)),
			Pair.of(3, new SleepTask()),
			Pair.of(
				5,
				new RandomTask<>(
					ImmutableMap.of(MemoryModuleType.field_18438, MemoryModuleState.field_18457),
					ImmutableList.of(
						Pair.of(new WalkHomeTask(f), 1), Pair.of(new WanderIndoorsTask(f), 4), Pair.of(new GoToPointOfInterestTask(f, 4), 2), Pair.of(new WaitTask(20, 40), 2)
					)
				)
			),
			createBusyFollowTask(),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createMeetTasks(VillagerProfession profession, float f) {
		return ImmutableList.of(
			Pair.of(2, new RandomTask<>(ImmutableList.of(Pair.of(new GoToIfNearbyTask(MemoryModuleType.field_18440, 0.4F, 40), 2), Pair.of(new MeetVillagerTask(), 2)))),
			Pair.of(10, new HoldTradeOffersTask(400, 1600)),
			Pair.of(10, new FindInteractionTargetTask(EntityType.field_6097, 4)),
			Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.field_18440, f, 6, 100, 200)),
			Pair.of(3, new GiveGiftsToHeroTask(100)),
			Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.field_18518, MemoryModuleType.field_18440)),
			Pair.of(
				3,
				new CompositeTask<>(
					ImmutableMap.of(),
					ImmutableSet.of(MemoryModuleType.field_18447),
					CompositeTask.Order.field_18348,
					CompositeTask.RunMode.field_18855,
					ImmutableList.of(Pair.of(new GatherItemsVillagerTask(), 1))
				)
			),
			createFreeFollowTask(),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createIdleTasks(VillagerProfession profession, float f) {
		return ImmutableList.of(
			Pair.of(
				2,
				new RandomTask<>(
					ImmutableList.of(
						Pair.of(FindEntityTask.create(EntityType.field_6077, 8, MemoryModuleType.field_18447, f, 2), 2),
						Pair.of(
							new FindEntityTask<>(EntityType.field_6077, 8, PassiveEntity::isReadyToBreed, PassiveEntity::isReadyToBreed, MemoryModuleType.field_18448, f, 2), 1
						),
						Pair.of(FindEntityTask.create(EntityType.field_16281, 8, MemoryModuleType.field_18447, f, 2), 1),
						Pair.of(new FindWalkTargetTask(f), 1),
						Pair.of(new GoTowardsLookTarget(f, 2), 1),
						Pair.of(new JumpInBedTask(f), 1),
						Pair.of(new WaitTask(30, 60), 1)
					)
				)
			),
			Pair.of(3, new GiveGiftsToHeroTask(100)),
			Pair.of(3, new FindInteractionTargetTask(EntityType.field_6097, 4)),
			Pair.of(3, new HoldTradeOffersTask(400, 1600)),
			Pair.of(
				3,
				new CompositeTask<>(
					ImmutableMap.of(),
					ImmutableSet.of(MemoryModuleType.field_18447),
					CompositeTask.Order.field_18348,
					CompositeTask.RunMode.field_18855,
					ImmutableList.of(Pair.of(new GatherItemsVillagerTask(), 1))
				)
			),
			Pair.of(
				3,
				new CompositeTask<>(
					ImmutableMap.of(),
					ImmutableSet.of(MemoryModuleType.field_18448),
					CompositeTask.Order.field_18348,
					CompositeTask.RunMode.field_18855,
					ImmutableList.of(Pair.of(new VillagerBreedTask(), 1))
				)
			),
			createFreeFollowTask(),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPanicTasks(VillagerProfession profession, float f) {
		float g = f * 1.5F;
		return ImmutableList.of(
			Pair.of(0, new StopPanickingTask()),
			Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.field_18453, g, 6, false)),
			Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.field_18452, g, 6, false)),
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
						Pair.of(new VillagerWalkTowardsTask(MemoryModuleType.field_18440, f * 1.5F, 2, 150, 200), 6), Pair.of(new FindWalkTargetTask(f * 1.5F), 2)
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
		return ImmutableList.of(Pair.of(0, new ForgetBellRingTask(15, 3)), Pair.of(1, new HideInHomeTask(32, f * 1.25F, 2)), createBusyFollowTask());
	}

	private static Pair<Integer, Task<LivingEntity>> createFreeFollowTask() {
		return Pair.of(
			5,
			new RandomTask<>(
				ImmutableList.of(
					Pair.of(new FollowMobTask(EntityType.field_16281, 8.0F), 8),
					Pair.of(new FollowMobTask(EntityType.field_6077, 8.0F), 2),
					Pair.of(new FollowMobTask(EntityType.field_6097, 8.0F), 2),
					Pair.of(new FollowMobTask(SpawnGroup.field_6294, 8.0F), 1),
					Pair.of(new FollowMobTask(SpawnGroup.field_6300, 8.0F), 1),
					Pair.of(new FollowMobTask(SpawnGroup.field_24460, 8.0F), 1),
					Pair.of(new FollowMobTask(SpawnGroup.field_6302, 8.0F), 1),
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
					Pair.of(new FollowMobTask(EntityType.field_6077, 8.0F), 2), Pair.of(new FollowMobTask(EntityType.field_6097, 8.0F), 2), Pair.of(new WaitTask(30, 60), 8)
				)
			)
		);
	}
}
