package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.GoToNearbyPositionTask;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.village.VillagerProfession;

public class VillagerTaskListProvider {
	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCoreTasks(VillagerProfession villagerProfession, float f) {
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
			Pair.of(10, new FindPointOfInterestTask(villagerProfession.getWorkStation(), MemoryModuleType.field_18439, true)),
			Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.field_18517, MemoryModuleType.field_18438, false)),
			Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.field_18518, MemoryModuleType.field_18440, true)),
			Pair.of(10, new GoToWorkTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getWorkTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			method_20242(),
			Pair.of(
				5,
				new RandomTask<>(
					ImmutableList.of(
						Pair.of(new VillagerWorkTask(), 7),
						Pair.of(new GoToIfNearbyTask(MemoryModuleType.field_18439, 4), 2),
						Pair.of(new GoToNearbyPositionTask(MemoryModuleType.field_18439, 1, 10), 5),
						Pair.of(new GoToSecondaryPositionTask(MemoryModuleType.field_18873, 0.4F, 1, 6, MemoryModuleType.field_18439), 5),
						Pair.of(new FarmerVillagerTask(), 5)
					)
				)
			),
			Pair.of(10, new HoldTradeOffersTask(400, 1600)),
			Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
			Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.field_18439, f, 9, 100)),
			Pair.of(3, new GiveGiftsToHeroTask(100)),
			Pair.of(3, new ForgetCompletedPointOfInterestTask(villagerProfession.getWorkStation(), MemoryModuleType.field_18439)),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getPlayTasks(float f) {
		return ImmutableList.of(
			Pair.of(0, new WanderAroundTask(100)),
			method_20241(),
			Pair.of(5, new PlayWithVillagerBabiesTask()),
			Pair.of(
				5,
				new RandomTask<>(
					ImmutableSet.of(Pair.of(MemoryModuleType.field_19006, MemoryModuleState.field_18457)),
					ImmutableList.of(
						Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.field_18447, f, 2), 2),
						Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.field_18447, f, 2), 1),
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

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getRestTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.field_18438, f, 1, 150)),
			Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.field_18517, MemoryModuleType.field_18438)),
			Pair.of(3, new SleepTask()),
			method_20242(),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getMeetTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(2, new RandomTask<>(ImmutableList.of(Pair.of(new GoToIfNearbyTask(MemoryModuleType.field_18440, 40), 2), Pair.of(new MeetVillagerTask(), 2)))),
			Pair.of(10, new HoldTradeOffersTask(400, 1600)),
			Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
			Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.field_18440, f, 6, 100)),
			Pair.of(3, new GiveGiftsToHeroTask(100)),
			Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.field_18518, MemoryModuleType.field_18440)),
			Pair.of(
				3,
				new CompositeTask<>(
					ImmutableSet.of(),
					ImmutableSet.of(MemoryModuleType.field_18447),
					CompositeTask.Order.field_18348,
					CompositeTask.RunMode.field_18855,
					ImmutableList.of(Pair.of(new GatherItemsVillagerTask(), 1))
				)
			),
			method_20241(),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getIdleTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(
				2,
				new RandomTask<>(
					ImmutableList.of(
						Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.field_18447, f, 2), 2),
						Pair.of(
							new FindEntityTask<>(EntityType.VILLAGER, 8, VillagerEntity::isReadyToBreed, VillagerEntity::isReadyToBreed, MemoryModuleType.field_18448, f, 2), 1
						),
						Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.field_18447, f, 2), 1),
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
					ImmutableSet.of(),
					ImmutableSet.of(MemoryModuleType.field_18447),
					CompositeTask.Order.field_18348,
					CompositeTask.RunMode.field_18855,
					ImmutableList.of(Pair.of(new GatherItemsVillagerTask(), 1))
				)
			),
			Pair.of(
				3,
				new CompositeTask<>(
					ImmutableSet.of(),
					ImmutableSet.of(MemoryModuleType.field_18448),
					CompositeTask.Order.field_18348,
					CompositeTask.RunMode.field_18855,
					ImmutableList.of(Pair.of(new VillagerBreedTask(), 1))
				)
			),
			method_20241(),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getPanicTasks(VillagerProfession villagerProfession, float f) {
		float g = f * 1.5F;
		return ImmutableList.of(
			Pair.of(0, new StopPanicingTask()),
			Pair.of(1, new GoToNearbyEntityTask(MemoryModuleType.field_18453, g)),
			Pair.of(1, new GoToNearbyEntityTask(MemoryModuleType.field_18452, g)),
			Pair.of(3, new FindWalkTargetTask(g)),
			method_20242()
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getPreRaidTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(0, new RingBellTask()),
			Pair.of(
				0,
				new RandomTask<>(
					ImmutableList.of(Pair.of(new VillagerWalkTowardsTask(MemoryModuleType.field_18440, f * 1.5F, 3, 150), 6), Pair.of(new FindWalkTargetTask(f * 1.5F), 2))
				)
			),
			method_20242(),
			Pair.of(99, new EndRaidTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getRaidTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(0, new RandomTask<>(ImmutableList.of(Pair.of(new SeekSkyAfterRaidWinTask(f), 5), Pair.of(new RunAroundAfterRaidTask(f * 1.1F), 2)))),
			Pair.of(0, new CelebrateRaidWinTask(600, 600)),
			Pair.of(2, new HideInHomeDuringRaidTask(24, f * 1.4F)),
			method_20242(),
			Pair.of(99, new EndRaidTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getHideTasks(VillagerProfession villagerProfession, float f) {
		int i = 2;
		return ImmutableList.of(Pair.of(0, new ForgetBellRingTask(15, 2)), Pair.of(1, new HideInHomeTask(32, f * 1.25F, 2)), method_20242());
	}

	private static Pair<Integer, Task<LivingEntity>> method_20241() {
		return Pair.of(
			5,
			new RandomTask<>(
				ImmutableList.of(
					Pair.of(new FollowMobTask(EntityType.CAT, 8.0F), 8),
					Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 2),
					Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2),
					Pair.of(new FollowMobTask(EntityCategory.field_6294, 8.0F), 1),
					Pair.of(new FollowMobTask(EntityCategory.field_6300, 8.0F), 1),
					Pair.of(new FollowMobTask(EntityCategory.field_6302, 8.0F), 1),
					Pair.of(new WaitTask(30, 60), 2)
				)
			)
		);
	}

	private static Pair<Integer, Task<LivingEntity>> method_20242() {
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
