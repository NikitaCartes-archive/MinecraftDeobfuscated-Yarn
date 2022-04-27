package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.GiveInventoryToLookTargetTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.NoPenaltyStrollTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.TemptationCooldownTask;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WalkToNearestVisibleWantedItemTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsLookTargetTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;

public class AllayBrain {
	private static final float field_38406 = 1.0F;
	private static final float field_38407 = 2.25F;
	private static final float field_38408 = 1.75F;
	private static final int field_38938 = 4;
	private static final int field_38939 = 16;
	private static final int field_38410 = 6;
	private static final int field_38411 = 30;
	private static final int field_38412 = 60;
	private static final int field_38413 = 600;
	private static final int field_38940 = 32;

	protected static Brain<?> create(Brain<AllayEntity> brain) {
		addCoreActivities(brain);
		addIdleActivities(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	private static void addCoreActivities(Brain<AllayEntity> brain) {
		brain.setTaskList(
			Activity.CORE,
			0,
			ImmutableList.of(
				new StayAboveWaterTask(0.8F),
				new LookAroundTask(45, 90),
				new WanderAroundTask(),
				new TemptationCooldownTask(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS),
				new TemptationCooldownTask(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS)
			)
		);
	}

	private static void addIdleActivities(Brain<AllayEntity> brain) {
		brain.setTaskList(
			Activity.IDLE,
			ImmutableList.of(
				Pair.of(0, new WalkToNearestVisibleWantedItemTask<>(allay -> true, 1.75F, true, 32)),
				Pair.of(1, new GiveInventoryToLookTargetTask<>(AllayBrain::getLookTarget, 2.25F)),
				Pair.of(2, new WalkTowardsLookTargetTask<>(AllayBrain::getLookTarget, 4, 16, 2.25F)),
				Pair.of(3, new TimeLimitedTask<>(new FollowMobTask(allay -> true, 6.0F), UniformIntProvider.create(30, 60))),
				Pair.of(
					4,
					new RandomTask<>(
						ImmutableList.of(Pair.of(new NoPenaltyStrollTask(1.0F), 2), Pair.of(new GoTowardsLookTarget(1.0F, 3), 2), Pair.of(new WaitTask(30, 60), 1))
					)
				)
			),
			ImmutableSet.of()
		);
	}

	public static void updateActivities(AllayEntity allay) {
		allay.getBrain().resetPossibleActivities(ImmutableList.of(Activity.IDLE));
	}

	public static void rememberNoteBlock(LivingEntity allay, BlockPos pos) {
		Brain<?> brain = allay.getBrain();
		GlobalPos globalPos = GlobalPos.create(allay.getWorld().getRegistryKey(), pos);
		Optional<GlobalPos> optional = brain.getOptionalMemory(MemoryModuleType.LIKED_NOTEBLOCK);
		if (optional.isEmpty()) {
			brain.remember(MemoryModuleType.LIKED_NOTEBLOCK, globalPos);
			brain.remember(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, 600);
		} else if (((GlobalPos)optional.get()).equals(globalPos)) {
			brain.remember(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, 600);
		}
	}

	private static Optional<LookTarget> getLookTarget(LivingEntity allay) {
		Brain<?> brain = allay.getBrain();
		Optional<GlobalPos> optional = brain.getOptionalMemory(MemoryModuleType.LIKED_NOTEBLOCK);
		if (optional.isPresent()) {
			GlobalPos globalPos = (GlobalPos)optional.get();
			if (shouldGoTowardsNoteBlock(allay, brain, globalPos)) {
				return Optional.of(new BlockPosLookTarget(globalPos.getPos().up()));
			}

			brain.forget(MemoryModuleType.LIKED_NOTEBLOCK);
		}

		return getLikedLookTarget(allay);
	}

	private static boolean shouldGoTowardsNoteBlock(LivingEntity allay, Brain<?> brain, GlobalPos pos) {
		Optional<Integer> optional = brain.getOptionalMemory(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS);
		World world = allay.getWorld();
		return world.getRegistryKey() == pos.getDimension() && world.getBlockState(pos.getPos()).isOf(Blocks.NOTE_BLOCK) && optional.isPresent();
	}

	private static Optional<LookTarget> getLikedLookTarget(LivingEntity allay) {
		return getLikedPlayer(allay).map(player -> new EntityLookTarget(player, true));
	}

	public static Optional<ServerPlayerEntity> getLikedPlayer(LivingEntity allay) {
		World world = allay.getWorld();
		if (!world.isClient() && world instanceof ServerWorld serverWorld) {
			Optional<UUID> optional = allay.getBrain().getOptionalMemory(MemoryModuleType.LIKED_PLAYER);
			if (optional.isPresent()) {
				return serverWorld.getEntity((UUID)optional.get()) instanceof ServerPlayerEntity serverPlayerEntity ? Optional.of(serverPlayerEntity) : Optional.empty();
			}
		}

		return Optional.empty();
	}
}
