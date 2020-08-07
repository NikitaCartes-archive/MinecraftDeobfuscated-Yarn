package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.FindEntityTask;
import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAngryAtTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GoToIfNearbyTask;
import net.minecraft.entity.ai.brain.task.GoToNearbyPositionTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.dynamic.GlobalPos;

public class PiglinBruteBrain {
	protected static Brain<?> create(PiglinBruteEntity piglinBruteEntity, Brain<PiglinBruteEntity> brain) {
		method_30257(piglinBruteEntity, brain);
		method_30260(piglinBruteEntity, brain);
		method_30262(piglinBruteEntity, brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.field_18594));
		brain.setDefaultActivity(Activity.field_18595);
		brain.resetPossibleActivities();
		return brain;
	}

	protected static void method_30250(PiglinBruteEntity piglinBruteEntity) {
		GlobalPos globalPos = GlobalPos.create(piglinBruteEntity.world.getRegistryKey(), piglinBruteEntity.getBlockPos());
		piglinBruteEntity.getBrain().remember(MemoryModuleType.field_18438, globalPos);
	}

	private static void method_30257(PiglinBruteEntity piglinBruteEntity, Brain<PiglinBruteEntity> brain) {
		brain.setTaskList(
			Activity.field_18594, 0, ImmutableList.of(new LookAroundTask(45, 90), new WanderAroundTask(), new OpenDoorsTask(), new ForgetAngryAtTargetTask<>())
		);
	}

	private static void method_30260(PiglinBruteEntity piglinBruteEntity, Brain<PiglinBruteEntity> brain) {
		brain.setTaskList(
			Activity.field_18595,
			10,
			ImmutableList.of(
				new UpdateAttackTargetTask<>(PiglinBruteBrain::method_30247), method_30244(), method_30254(), new FindInteractionTargetTask(EntityType.field_6097, 4)
			)
		);
	}

	private static void method_30262(PiglinBruteEntity piglinBruteEntity, Brain<PiglinBruteEntity> brain) {
		brain.setTaskList(
			Activity.field_22396,
			10,
			ImmutableList.of(
				new ForgetAttackTargetTask<>(livingEntity -> !method_30248(piglinBruteEntity, livingEntity)), new RangedApproachTask(1.0F), new MeleeAttackTask(20)
			),
			MemoryModuleType.field_22355
		);
	}

	private static RandomTask<PiglinBruteEntity> method_30244() {
		return new RandomTask<>(
			ImmutableList.of(
				Pair.of(new FollowMobTask(EntityType.field_6097, 8.0F), 1),
				Pair.of(new FollowMobTask(EntityType.field_22281, 8.0F), 1),
				Pair.of(new FollowMobTask(EntityType.field_25751, 8.0F), 1),
				Pair.of(new FollowMobTask(8.0F), 1),
				Pair.of(new WaitTask(30, 60), 1)
			)
		);
	}

	private static RandomTask<PiglinBruteEntity> method_30254() {
		return new RandomTask<>(
			ImmutableList.of(
				Pair.of(new StrollTask(0.6F), 2),
				Pair.of(FindEntityTask.create(EntityType.field_22281, 8, MemoryModuleType.field_18447, 0.6F, 2), 2),
				Pair.of(FindEntityTask.create(EntityType.field_25751, 8, MemoryModuleType.field_18447, 0.6F, 2), 2),
				Pair.of(new GoToNearbyPositionTask(MemoryModuleType.field_18438, 0.6F, 2, 100), 2),
				Pair.of(new GoToIfNearbyTask(MemoryModuleType.field_18438, 0.6F, 5), 2),
				Pair.of(new WaitTask(30, 60), 1)
			)
		);
	}

	protected static void method_30256(PiglinBruteEntity piglinBruteEntity) {
		Brain<PiglinBruteEntity> brain = piglinBruteEntity.getBrain();
		Activity activity = (Activity)brain.getFirstPossibleNonCoreActivity().orElse(null);
		brain.resetPossibleActivities(ImmutableList.of(Activity.field_22396, Activity.field_18595));
		Activity activity2 = (Activity)brain.getFirstPossibleNonCoreActivity().orElse(null);
		if (activity != activity2) {
			method_30261(piglinBruteEntity);
		}

		piglinBruteEntity.setAttacking(brain.hasMemoryModule(MemoryModuleType.field_22355));
	}

	private static boolean method_30248(AbstractPiglinEntity abstractPiglinEntity, LivingEntity livingEntity) {
		return method_30247(abstractPiglinEntity).filter(livingEntity2 -> livingEntity2 == livingEntity).isPresent();
	}

	private static Optional<? extends LivingEntity> method_30247(AbstractPiglinEntity abstractPiglinEntity) {
		Optional<LivingEntity> optional = LookTargetUtil.getEntity(abstractPiglinEntity, MemoryModuleType.field_22333);
		if (optional.isPresent() && method_30245((LivingEntity)optional.get())) {
			return optional;
		} else {
			Optional<? extends LivingEntity> optional2 = method_30249(abstractPiglinEntity, MemoryModuleType.field_22354);
			return optional2.isPresent() ? optional2 : abstractPiglinEntity.getBrain().getOptionalMemory(MemoryModuleType.field_25360);
		}
	}

	private static boolean method_30245(LivingEntity livingEntity) {
		return EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(livingEntity);
	}

	private static Optional<? extends LivingEntity> method_30249(
		AbstractPiglinEntity abstractPiglinEntity, MemoryModuleType<? extends LivingEntity> memoryModuleType
	) {
		return abstractPiglinEntity.getBrain().getOptionalMemory(memoryModuleType).filter(livingEntity -> livingEntity.isInRange(abstractPiglinEntity, 12.0));
	}

	protected static void method_30251(PiglinBruteEntity piglinBruteEntity, LivingEntity livingEntity) {
		if (!(livingEntity instanceof AbstractPiglinEntity)) {
			PiglinBrain.tryRevenge(piglinBruteEntity, livingEntity);
		}
	}

	protected static void method_30258(PiglinBruteEntity piglinBruteEntity) {
		if ((double)piglinBruteEntity.world.random.nextFloat() < 0.0125) {
			method_30261(piglinBruteEntity);
		}
	}

	private static void method_30261(PiglinBruteEntity piglinBruteEntity) {
		piglinBruteEntity.getBrain().getFirstPossibleNonCoreActivity().ifPresent(activity -> {
			if (activity == Activity.field_22396) {
				piglinBruteEntity.playAngrySound();
			}
		});
	}
}
