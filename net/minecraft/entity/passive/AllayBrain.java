/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.FollowMobWithIntervalTask;
import net.minecraft.entity.ai.brain.task.GiveInventoryToLookTargetTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTargetTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.TemptationCooldownTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WalkTask;
import net.minecraft.entity.ai.brain.task.WalkToNearestVisibleWantedItemTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsLookTargetTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;

public class AllayBrain {
    private static final float field_38406 = 1.0f;
    private static final float field_38407 = 2.25f;
    private static final float field_38408 = 1.75f;
    private static final float field_39113 = 2.5f;
    private static final int field_38938 = 4;
    private static final int field_38939 = 16;
    private static final int field_38410 = 6;
    private static final int field_38411 = 30;
    private static final int field_38412 = 60;
    private static final int field_38413 = 600;
    private static final int field_38940 = 32;
    private static final int field_40130 = 20;

    protected static Brain<?> create(Brain<AllayEntity> brain) {
        AllayBrain.addCoreActivities(brain);
        AllayBrain.addIdleActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<AllayEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new StayAboveWaterTask(0.8f), new WalkTask(2.5f), new LookAroundTask(45, 90), new WanderAroundTask(), new TemptationCooldownTask(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS), new TemptationCooldownTask(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS)));
    }

    private static void addIdleActivities(Brain<AllayEntity> brain) {
        brain.setTaskList(Activity.IDLE, ImmutableList.of(Pair.of(0, WalkToNearestVisibleWantedItemTask.create(allay -> true, 1.75f, true, 32)), Pair.of(1, new GiveInventoryToLookTargetTask(AllayBrain::getLookTarget, 2.25f, 20)), Pair.of(2, WalkTowardsLookTargetTask.create(AllayBrain::getLookTarget, 4, 16, 2.25f)), Pair.of(3, FollowMobWithIntervalTask.follow(6.0f, UniformIntProvider.create(30, 60))), Pair.of(4, new RandomTask(ImmutableList.of(Pair.of(StrollTask.createSolidTargeting(1.0f), 2), Pair.of(GoTowardsLookTargetTask.create(1.0f, 3), 2), Pair.of(new WaitTask(30, 60), 1))))), ImmutableSet.of());
    }

    public static void updateActivities(AllayEntity allay) {
        allay.getBrain().resetPossibleActivities(ImmutableList.of(Activity.IDLE));
    }

    public static void rememberNoteBlock(LivingEntity allay, BlockPos pos) {
        Brain<?> brain = allay.getBrain();
        GlobalPos globalPos = GlobalPos.create(allay.getWorld().getRegistryKey(), pos);
        Optional<GlobalPos> optional = brain.getOptionalRegisteredMemory(MemoryModuleType.LIKED_NOTEBLOCK);
        if (optional.isEmpty()) {
            brain.remember(MemoryModuleType.LIKED_NOTEBLOCK, globalPos);
            brain.remember(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, 600);
        } else if (optional.get().equals(globalPos)) {
            brain.remember(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, 600);
        }
    }

    private static Optional<LookTarget> getLookTarget(LivingEntity allay) {
        Brain<?> brain = allay.getBrain();
        Optional<GlobalPos> optional = brain.getOptionalRegisteredMemory(MemoryModuleType.LIKED_NOTEBLOCK);
        if (optional.isPresent()) {
            GlobalPos globalPos = optional.get();
            if (AllayBrain.shouldGoTowardsNoteBlock(allay, brain, globalPos)) {
                return Optional.of(new BlockPosLookTarget(globalPos.getPos().up()));
            }
            brain.forget(MemoryModuleType.LIKED_NOTEBLOCK);
        }
        return AllayBrain.getLikedLookTarget(allay);
    }

    private static boolean shouldGoTowardsNoteBlock(LivingEntity allay, Brain<?> brain, GlobalPos pos) {
        Optional<Integer> optional = brain.getOptionalRegisteredMemory(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS);
        World world = allay.getWorld();
        return world.getRegistryKey() == pos.getDimension() && world.getBlockState(pos.getPos()).isOf(Blocks.NOTE_BLOCK) && optional.isPresent();
    }

    private static Optional<LookTarget> getLikedLookTarget(LivingEntity allay) {
        return AllayBrain.getLikedPlayer(allay).map(player -> new EntityLookTarget((Entity)player, true));
    }

    public static Optional<ServerPlayerEntity> getLikedPlayer(LivingEntity allay) {
        World world = allay.getWorld();
        if (!world.isClient() && world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            Optional<UUID> optional = allay.getBrain().getOptionalRegisteredMemory(MemoryModuleType.LIKED_PLAYER);
            if (optional.isPresent()) {
                Entity entity = serverWorld.getEntity(optional.get());
                if (entity instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                    if ((serverPlayerEntity.interactionManager.isSurvivalLike() || serverPlayerEntity.interactionManager.isCreative()) && serverPlayerEntity.isInRange(allay, 64.0)) {
                        return Optional.of(serverPlayerEntity);
                    }
                }
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}

