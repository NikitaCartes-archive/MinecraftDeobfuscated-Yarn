package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.GoToNearbyPositionTask;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.village.VillagerProfession;

public class VillagerTaskListProvider {
	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCoreTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(0, new JumpTask(0.4F, 0.8F)),
			Pair.of(0, new OpenDoorsTask()),
			Pair.of(0, new LookAroundTask(45, 90)),
			Pair.of(0, new PanicTask()),
			Pair.of(0, new WakeUpTask()),
			Pair.of(1, new WanderAroundTask(60)),
			Pair.of(2, new FollowCustomerTask(f)),
			Pair.of(
				5,
				new RandomTask<>(
					ImmutableList.of(
						Pair.of(new FollowMobTask(EntityType.CAT, 8.0F), 5),
						Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 1),
						Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 1),
						Pair.of(new WaitTask(30, 60), 1)
					)
				)
			),
			Pair.of(10, new FindPointOfInterestTask(villagerProfession.getWorkStation(), MemoryModuleType.field_18439, true)),
			Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.field_18517, MemoryModuleType.field_18438, false)),
			Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.field_18518, MemoryModuleType.field_18440, true)),
			Pair.of(10, new GoToWorkTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getWorkTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(
				5,
				new RandomTask<>(
					ImmutableList.of(
						Pair.of(new VillagerWorkTask(), 7),
						Pair.of(new GoToIfNearbyTask(MemoryModuleType.field_18439, 4), 2),
						Pair.of(new GoToNearbyPositionTask(MemoryModuleType.field_18439, 1, 10), 5),
						Pair.of(new GoToRandomMemorizedPositionTask(MemoryModuleType.field_18873, 0.4F, 1, 6, MemoryModuleType.field_18439), 5),
						Pair.of(new FarmerVillagerTask(), 5)
					)
				)
			),
			Pair.of(10, new InteractTask(400, 1600)),
			Pair.of(10, new FindInteractTargetTask(EntityType.PLAYER, 4)),
			Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.field_18439, f, 9, 100)),
			Pair.of(3, new ForgetCompletedPointOfInterestTask(villagerProfession.getWorkStation(), MemoryModuleType.field_18439)),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> method_19612() {
		return ImmutableList.of(Pair.of(0, new WanderAroundTask(100)), Pair.of(5, new GoToSleepingChildTask()), Pair.of(99, new ScheduleActivityTask()));
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getRestTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.field_18438, f, 1, 150)),
			Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.field_18517, MemoryModuleType.field_18438)),
			Pair.of(3, new SleepTask()),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getMeetTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(2, new RandomTask<>(ImmutableList.of(Pair.of(new GoToIfNearbyTask(MemoryModuleType.field_18440, 40), 2), Pair.of(new FindVillagerToMeetTask(), 2)))),
			Pair.of(10, new InteractTask(400, 1600)),
			Pair.of(10, new FindInteractTargetTask(EntityType.PLAYER, 4)),
			Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.field_18440, f, 6, 100)),
			Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.field_18518, MemoryModuleType.field_18440)),
			Pair.of(
				3,
				new CompositeTask<>(
					ImmutableSet.of(),
					ImmutableSet.of(MemoryModuleType.field_18447),
					CompositeTask.Order.field_18348,
					CompositeTask.class_4216.field_18855,
					ImmutableList.of(Pair.of(new GatherItemsVillagerTask(), 1))
				)
			),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getIdleTasks(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(
				2,
				new RandomTask<>(
					ImmutableList.of(
						Pair.of(FindEntityTask.method_18941(EntityType.VILLAGER, 8, MemoryModuleType.field_18447, f, 2), 2),
						Pair.of(
							new FindEntityTask<>(EntityType.VILLAGER, 8, VillagerEntity::isReadyToBreed, VillagerEntity::isReadyToBreed, MemoryModuleType.field_18448, f, 2), 1
						),
						Pair.of(FindEntityTask.method_18941(EntityType.CAT, 8, MemoryModuleType.field_18447, f, 2), 1),
						Pair.of(new FindWalkTarget(f), 1),
						Pair.of(new GoTowardsLookTarget(f), 1),
						Pair.of(new WaitTask(30, 60), 1)
					)
				)
			),
			Pair.of(3, new FindInteractTargetTask(EntityType.PLAYER, 4)),
			Pair.of(3, new InteractTask(400, 1600)),
			Pair.of(3, new VillagerBreedTask()),
			Pair.of(
				3,
				new CompositeTask<>(
					ImmutableSet.of(),
					ImmutableSet.of(MemoryModuleType.field_18447),
					CompositeTask.Order.field_18348,
					CompositeTask.class_4216.field_18855,
					ImmutableList.of(Pair.of(new GatherItemsVillagerTask(), 1))
				)
			),
			Pair.of(
				3,
				new CompositeTask<>(
					ImmutableSet.of(),
					ImmutableSet.of(MemoryModuleType.field_18448),
					CompositeTask.Order.field_18348,
					CompositeTask.class_4216.field_18855,
					ImmutableList.of(Pair.of(new VillagerBreedTask(), 1))
				)
			),
			Pair.of(99, new ScheduleActivityTask())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getPanicTasks(VillagerProfession villagerProfession, float f) {
		float g = f * 1.5F;
		return ImmutableList.of(
			Pair.of(0, new HandleAttackTask()),
			Pair.of(1, new SetWalkTargetToNearbyEntityTask(MemoryModuleType.field_18453, g)),
			Pair.of(1, new SetWalkTargetToNearbyEntityTask(MemoryModuleType.field_18452, g)),
			Pair.of(3, new FindWalkTarget(g))
		);
	}
}
