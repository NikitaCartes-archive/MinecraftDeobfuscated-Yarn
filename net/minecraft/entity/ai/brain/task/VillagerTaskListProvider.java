/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.BoneMealTask;
import net.minecraft.entity.ai.brain.task.CelebrateRaidWinTask;
import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.task.EndRaidTask;
import net.minecraft.entity.ai.brain.task.FarmerVillagerTask;
import net.minecraft.entity.ai.brain.task.FarmerWorkTask;
import net.minecraft.entity.ai.brain.task.FindEntityTask;
import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.FindWalkTargetTask;
import net.minecraft.entity.ai.brain.task.FollowCustomerTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetBellRingTask;
import net.minecraft.entity.ai.brain.task.ForgetCompletedPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.GatherItemsVillagerTask;
import net.minecraft.entity.ai.brain.task.GiveGiftsToHeroTask;
import net.minecraft.entity.ai.brain.task.GoToIfNearbyTask;
import net.minecraft.entity.ai.brain.task.GoToNearbyPositionTask;
import net.minecraft.entity.ai.brain.task.GoToPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.GoToRememberedPositionTask;
import net.minecraft.entity.ai.brain.task.GoToSecondaryPositionTask;
import net.minecraft.entity.ai.brain.task.GoToWorkTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.HideInHomeDuringRaidTask;
import net.minecraft.entity.ai.brain.task.HideInHomeTask;
import net.minecraft.entity.ai.brain.task.HideWhenBellRingsTask;
import net.minecraft.entity.ai.brain.task.HoldTradeOffersTask;
import net.minecraft.entity.ai.brain.task.JumpInBedTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LoseJobOnSiteLossTask;
import net.minecraft.entity.ai.brain.task.MeetVillagerTask;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.PanicTask;
import net.minecraft.entity.ai.brain.task.PlayWithVillagerBabiesTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RingBellTask;
import net.minecraft.entity.ai.brain.task.RunAroundAfterRaidTask;
import net.minecraft.entity.ai.brain.task.ScheduleActivityTask;
import net.minecraft.entity.ai.brain.task.SeekSkyAfterRaidWinTask;
import net.minecraft.entity.ai.brain.task.SleepTask;
import net.minecraft.entity.ai.brain.task.StartRaidTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.StopPanickingTask;
import net.minecraft.entity.ai.brain.task.TakeJobSiteTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerBreedTask;
import net.minecraft.entity.ai.brain.task.VillagerWalkTowardsTask;
import net.minecraft.entity.ai.brain.task.VillagerWorkTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WakeUpTask;
import net.minecraft.entity.ai.brain.task.WalkHomeTask;
import net.minecraft.entity.ai.brain.task.WalkToNearestVisibleWantedItemTask;
import net.minecraft.entity.ai.brain.task.WalkTowardJobSiteTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.ai.brain.task.WanderIndoorsTask;
import net.minecraft.entity.ai.brain.task.WorkStationCompetitionTask;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class VillagerTaskListProvider {
    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createCoreTasks(VillagerProfession profession, float f) {
        return ImmutableList.of(Pair.of(0, new StayAboveWaterTask(0.8f)), Pair.of(0, new OpenDoorsTask()), Pair.of(0, new LookAroundTask(45, 90)), Pair.of(0, new PanicTask()), Pair.of(0, new WakeUpTask()), Pair.of(0, new HideWhenBellRingsTask()), Pair.of(0, new StartRaidTask()), Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.JOB_SITE)), Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.POTENTIAL_JOB_SITE)), Pair.of(1, new WanderAroundTask(200)), Pair.of(2, new WorkStationCompetitionTask(profession)), Pair.of(3, new FollowCustomerTask(f)), new Pair[]{Pair.of(5, new WalkToNearestVisibleWantedItemTask(f, false, 4)), Pair.of(6, new FindPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true)), Pair.of(7, new WalkTowardJobSiteTask(f)), Pair.of(8, new TakeJobSiteTask(f)), Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME, false)), Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT, true)), Pair.of(10, new GoToWorkTask()), Pair.of(10, new LoseJobOnSiteLossTask())});
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createWorkTasks(VillagerProfession profession, float f) {
        VillagerWorkTask villagerWorkTask = profession == VillagerProfession.FARMER ? new FarmerWorkTask() : new VillagerWorkTask();
        return ImmutableList.of(VillagerTaskListProvider.createBusyFollowTask(), Pair.of(5, new RandomTask(ImmutableList.of(Pair.of(villagerWorkTask, 7), Pair.of(new GoToIfNearbyTask(MemoryModuleType.JOB_SITE, 4), 2), Pair.of(new GoToNearbyPositionTask(MemoryModuleType.JOB_SITE, 1, 10), 5), Pair.of(new GoToSecondaryPositionTask(MemoryModuleType.SECONDARY_JOB_SITE, f, 1, 6, MemoryModuleType.JOB_SITE), 5), Pair.of(new FarmerVillagerTask(), profession == VillagerProfession.FARMER ? 2 : 5), Pair.of(new BoneMealTask(), profession == VillagerProfession.FARMER ? 4 : 7)))), Pair.of(10, new HoldTradeOffersTask(400, 1600)), Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)), Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.JOB_SITE, f, 9, 100, 1200)), Pair.of(3, new GiveGiftsToHeroTask(100)), Pair.of(99, new ScheduleActivityTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPlayTasks(float f) {
        return ImmutableList.of(Pair.of(0, new WanderAroundTask(100)), VillagerTaskListProvider.createFreeFollowTask(), Pair.of(5, new PlayWithVillagerBabiesTask()), Pair.of(5, new RandomTask(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleState.VALUE_ABSENT), ImmutableList.of(Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 2), Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 1), Pair.of(new FindWalkTargetTask(f), 1), Pair.of(new GoTowardsLookTarget(f, 2), 1), Pair.of(new JumpInBedTask(f), 2), Pair.of(new WaitTask(20, 40), 2)))), Pair.of(99, new ScheduleActivityTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createRestTasks(VillagerProfession profession, float f) {
        return ImmutableList.of(Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.HOME, f, 1, 150, 1200)), Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME)), Pair.of(3, new SleepTask()), Pair.of(5, new RandomTask(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_ABSENT), ImmutableList.of(Pair.of(new WalkHomeTask(f), 1), Pair.of(new WanderIndoorsTask(f), 4), Pair.of(new GoToPointOfInterestTask(f, 4), 2), Pair.of(new WaitTask(20, 40), 2)))), VillagerTaskListProvider.createBusyFollowTask(), Pair.of(99, new ScheduleActivityTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createMeetTasks(VillagerProfession profession, float f) {
        return ImmutableList.of(Pair.of(2, new RandomTask(ImmutableList.of(Pair.of(new GoToIfNearbyTask(MemoryModuleType.MEETING_POINT, 40), 2), Pair.of(new MeetVillagerTask(), 2)))), Pair.of(10, new HoldTradeOffersTask(400, 1600)), Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)), Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.MEETING_POINT, f, 6, 100, 200)), Pair.of(3, new GiveGiftsToHeroTask(100)), Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT)), Pair.of(3, new CompositeTask(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), CompositeTask.Order.ORDERED, CompositeTask.RunMode.RUN_ONE, ImmutableList.of(Pair.of(new GatherItemsVillagerTask(), 1)))), VillagerTaskListProvider.createFreeFollowTask(), Pair.of(99, new ScheduleActivityTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createIdleTasks(VillagerProfession profession, float f) {
        return ImmutableList.of(Pair.of(2, new RandomTask(ImmutableList.of(Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 2), Pair.of(new FindEntityTask<VillagerEntity, PassiveEntity>(EntityType.VILLAGER, 8, PassiveEntity::isReadyToBreed, PassiveEntity::isReadyToBreed, MemoryModuleType.BREED_TARGET, f, 2), 1), Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 1), Pair.of(new FindWalkTargetTask(f), 1), Pair.of(new GoTowardsLookTarget(f, 2), 1), Pair.of(new JumpInBedTask(f), 1), Pair.of(new WaitTask(30, 60), 1)))), Pair.of(3, new GiveGiftsToHeroTask(100)), Pair.of(3, new FindInteractionTargetTask(EntityType.PLAYER, 4)), Pair.of(3, new HoldTradeOffersTask(400, 1600)), Pair.of(3, new CompositeTask(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), CompositeTask.Order.ORDERED, CompositeTask.RunMode.RUN_ONE, ImmutableList.of(Pair.of(new GatherItemsVillagerTask(), 1)))), Pair.of(3, new CompositeTask(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.BREED_TARGET), CompositeTask.Order.ORDERED, CompositeTask.RunMode.RUN_ONE, ImmutableList.of(Pair.of(new VillagerBreedTask(), 1)))), VillagerTaskListProvider.createFreeFollowTask(), Pair.of(99, new ScheduleActivityTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPanicTasks(VillagerProfession profession, float f) {
        float g = f * 1.5f;
        return ImmutableList.of(Pair.of(0, new StopPanickingTask()), Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.NEAREST_HOSTILE, g, 6, false)), Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.HURT_BY_ENTITY, g, 6, false)), Pair.of(3, new FindWalkTargetTask(g, 2, 2)), VillagerTaskListProvider.createBusyFollowTask());
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPreRaidTasks(VillagerProfession villagerProfession, float f) {
        return ImmutableList.of(Pair.of(0, new RingBellTask()), Pair.of(0, new RandomTask(ImmutableList.of(Pair.of(new VillagerWalkTowardsTask(MemoryModuleType.MEETING_POINT, f * 1.5f, 2, 150, 200), 6), Pair.of(new FindWalkTargetTask(f * 1.5f), 2)))), VillagerTaskListProvider.createBusyFollowTask(), Pair.of(99, new EndRaidTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createRaidTasks(VillagerProfession villagerProfession, float f) {
        return ImmutableList.of(Pair.of(0, new RandomTask(ImmutableList.of(Pair.of(new SeekSkyAfterRaidWinTask(f), 5), Pair.of(new RunAroundAfterRaidTask(f * 1.1f), 2)))), Pair.of(0, new CelebrateRaidWinTask(600, 600)), Pair.of(2, new HideInHomeDuringRaidTask(24, f * 1.4f)), VillagerTaskListProvider.createBusyFollowTask(), Pair.of(99, new EndRaidTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createHideTasks(VillagerProfession villagerProfession, float f) {
        int i = 2;
        return ImmutableList.of(Pair.of(0, new ForgetBellRingTask(15, 3)), Pair.of(1, new HideInHomeTask(32, f * 1.25f, 2)), VillagerTaskListProvider.createBusyFollowTask());
    }

    private static Pair<Integer, Task<LivingEntity>> createFreeFollowTask() {
        return Pair.of(5, new RandomTask(ImmutableList.of(Pair.of(new FollowMobTask(EntityType.CAT, 8.0f), 8), Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0f), 2), Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0f), 2), Pair.of(new FollowMobTask(SpawnGroup.CREATURE, 8.0f), 1), Pair.of(new FollowMobTask(SpawnGroup.WATER_CREATURE, 8.0f), 1), Pair.of(new FollowMobTask(SpawnGroup.WATER_AMBIENT, 8.0f), 1), Pair.of(new FollowMobTask(SpawnGroup.MONSTER, 8.0f), 1), Pair.of(new WaitTask(30, 60), 2))));
    }

    private static Pair<Integer, Task<LivingEntity>> createBusyFollowTask() {
        return Pair.of(5, new RandomTask(ImmutableList.of(Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0f), 2), Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0f), 2), Pair.of(new WaitTask(30, 60), 8))));
    }
}

