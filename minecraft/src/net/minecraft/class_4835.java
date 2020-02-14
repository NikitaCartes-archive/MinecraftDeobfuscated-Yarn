package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.GoToNearbyEntityTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;

public class class_4835 {
	private static final class_4801 field_22369 = class_4802.method_24505(5, 20);

	public static Brain<?> method_24668(HoglinEntity hoglinEntity, Dynamic<?> dynamic) {
		Brain<HoglinEntity> brain = new Brain<>(
			(Collection<MemoryModuleType<?>>)HoglinEntity.field_22366, (Collection<SensorType<? extends Sensor<? super HoglinEntity>>>)HoglinEntity.field_22365, dynamic
		);
		method_24666(hoglinEntity, brain);
		method_24673(hoglinEntity, brain);
		method_24676(hoglinEntity, brain);
		method_24679(hoglinEntity, brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.method_24536();
		return brain;
	}

	private static void method_24666(HoglinEntity hoglinEntity, Brain<HoglinEntity> brain) {
		brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new LookAroundTask(45, 90), new WanderAroundTask(200)));
	}

	private static void method_24673(HoglinEntity hoglinEntity, Brain<HoglinEntity> brain) {
		float f = method_24674(hoglinEntity);
		brain.setTaskList(
			Activity.IDLE,
			10,
			ImmutableList.of(
				new class_4808(MemoryModuleType.NEAREST_VISIBLE_WARPED_FUNGI, 200),
				new class_4806(EntityType.HOGLIN),
				GoToNearbyEntityTask.method_24601(MemoryModuleType.NEAREST_VISIBLE_WARPED_FUNGI, f * 1.8F, 8, true),
				new class_4824(class_4835::method_24684),
				new class_4820<MobEntityWithAi>(
					HoglinEntity::method_24658, (Task<? super MobEntityWithAi>)GoToNearbyEntityTask.method_24603(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, f, 8, false)
				),
				new class_4821<LivingEntity>(new FollowMobTask(8.0F), class_4801.method_24502(30, 60)),
				method_24662(f)
			)
		);
	}

	private static void method_24676(HoglinEntity hoglinEntity, Brain<HoglinEntity> brain) {
		float f = method_24674(hoglinEntity);
		brain.method_24527(
			Activity.FIGHT,
			10,
			ImmutableList.of(
				new class_4808(MemoryModuleType.NEAREST_VISIBLE_WARPED_FUNGI, 200),
				new class_4806(EntityType.HOGLIN),
				new class_4822(f * 1.8F),
				new class_4820<>(HoglinEntity::method_24658, new class_4816(1.5, 40)),
				new class_4820<>(PassiveEntity::isBaby, new class_4816(1.5, 15)),
				new class_4828()
			),
			MemoryModuleType.ATTACK_TARGET
		);
	}

	private static void method_24679(HoglinEntity hoglinEntity, Brain<HoglinEntity> brain) {
		float f = method_24674(hoglinEntity) * 2.0F;
		brain.method_24527(
			Activity.AVOID,
			10,
			ImmutableList.of(
				GoToNearbyEntityTask.method_24603(MemoryModuleType.AVOID_TARGET, f, 15, false),
				method_24662(method_24674(hoglinEntity)),
				new class_4821<LivingEntity>(new FollowMobTask(8.0F), class_4801.method_24502(30, 60)),
				new class_4813(class_4835::method_24686, MemoryModuleType.AVOID_TARGET)
			),
			MemoryModuleType.AVOID_TARGET
		);
	}

	private static RandomTask<HoglinEntity> method_24662(float f) {
		return new RandomTask<>(ImmutableList.of(Pair.of(new class_4818(f), 2), Pair.of(new GoTowardsLookTarget(f, 3), 2), Pair.of(new WaitTask(30, 60), 1)));
	}

	public static void method_24664(HoglinEntity hoglinEntity) {
		Brain<HoglinEntity> brain = hoglinEntity.getBrain();
		Activity activity = (Activity)brain.method_24538().orElse(null);
		brain.method_24531(ImmutableList.of(Activity.FIGHT, Activity.AVOID, Activity.IDLE));
		Activity activity2 = (Activity)brain.method_24538().orElse(null);
		if (activity != activity2) {
			method_24688(hoglinEntity);
		}

		hoglinEntity.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
	}

	public static void method_24665(HoglinEntity hoglinEntity, LivingEntity livingEntity) {
		if (!hoglinEntity.isBaby()) {
			if (livingEntity.getType() == EntityType.PIGLIN && !method_24686(hoglinEntity)) {
				method_24681(hoglinEntity, livingEntity);
				method_24675(hoglinEntity, livingEntity);
			} else {
				method_24687(hoglinEntity, livingEntity);
			}
		}
	}

	private static void method_24675(HoglinEntity hoglinEntity, LivingEntity livingEntity) {
		method_24690(hoglinEntity).forEach(hoglinEntityx -> method_24678(hoglinEntityx, livingEntity));
	}

	private static void method_24678(HoglinEntity hoglinEntity, LivingEntity livingEntity) {
		Brain<HoglinEntity> brain = hoglinEntity.getBrain();
		LivingEntity livingEntity2 = LookTargetUtil.method_24562(hoglinEntity, brain.getOptionalMemory(MemoryModuleType.AVOID_TARGET), livingEntity);
		livingEntity2 = LookTargetUtil.method_24562(hoglinEntity, brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET), livingEntity2);
		method_24681(hoglinEntity, livingEntity2);
	}

	private static void method_24681(HoglinEntity hoglinEntity, LivingEntity livingEntity) {
		hoglinEntity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
		hoglinEntity.getBrain()
			.method_24525(MemoryModuleType.AVOID_TARGET, livingEntity, hoglinEntity.world.getTime(), (long)field_22369.method_24503(hoglinEntity.world.random));
	}

	private static Optional<? extends LivingEntity> method_24684(HoglinEntity hoglinEntity) {
		return !method_24680(hoglinEntity) && !method_24691(hoglinEntity)
			? hoglinEntity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER)
			: Optional.empty();
	}

	public static boolean method_24669(HoglinEntity hoglinEntity, BlockPos blockPos) {
		Optional<BlockPos> optional = hoglinEntity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WARPED_FUNGI);
		return optional.isPresent() && ((BlockPos)optional.get()).isWithinDistance(blockPos, 8.0);
	}

	private static boolean method_24686(HoglinEntity hoglinEntity) {
		if (hoglinEntity.isBaby()) {
			return false;
		} else {
			int i = (Integer)hoglinEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0);
			int j = (Integer)hoglinEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0) + 1;
			return j > i;
		}
	}

	public static void method_24672(HoglinEntity hoglinEntity, LivingEntity livingEntity) {
		Brain<HoglinEntity> brain = hoglinEntity.getBrain();
		brain.forget(MemoryModuleType.PACIFIED);
		brain.forget(MemoryModuleType.BREED_TARGET);
		hoglinEntity.method_24660();
		if (hoglinEntity.isBaby()) {
			method_24678(hoglinEntity, livingEntity);
		} else {
			method_24683(hoglinEntity, livingEntity);
		}
	}

	private static void method_24683(HoglinEntity hoglinEntity, LivingEntity livingEntity) {
		if (!hoglinEntity.getBrain().hasActivity(Activity.AVOID) || livingEntity.getType() != EntityType.PIGLIN) {
			if (EntityPredicates.field_22280.test(livingEntity)) {
				if (livingEntity.getType() != EntityType.HOGLIN) {
					if (!LookTargetUtil.method_24558(hoglinEntity, livingEntity, 4.0)) {
						method_24685(hoglinEntity, livingEntity);
						method_24687(hoglinEntity, livingEntity);
					}
				}
			}
		}
	}

	private static void method_24685(HoglinEntity hoglinEntity, LivingEntity livingEntity) {
		hoglinEntity.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
		hoglinEntity.getBrain().method_24525(MemoryModuleType.ATTACK_TARGET, livingEntity, hoglinEntity.world.getTime(), 200L);
	}

	private static void method_24687(HoglinEntity hoglinEntity, LivingEntity livingEntity) {
		method_24690(hoglinEntity).forEach(hoglinEntityx -> method_24689(hoglinEntityx, livingEntity));
	}

	private static void method_24689(HoglinEntity hoglinEntity, LivingEntity livingEntity) {
		Optional<LivingEntity> optional = hoglinEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
		LivingEntity livingEntity2 = LookTargetUtil.method_24562(hoglinEntity, optional, livingEntity);
		method_24685(hoglinEntity, livingEntity2);
	}

	private static void method_24688(HoglinEntity hoglinEntity) {
		hoglinEntity.getBrain().method_24538().ifPresent(activity -> {
			if (activity == Activity.AVOID) {
				hoglinEntity.method_24661();
			} else if (activity == Activity.FIGHT) {
				hoglinEntity.method_24659();
			}
		});
	}

	public static void method_24671(HoglinEntity hoglinEntity) {
		if ((double)hoglinEntity.world.random.nextFloat() < 0.0125) {
			method_24688(hoglinEntity);
		}
	}

	private static List<HoglinEntity> method_24690(HoglinEntity hoglinEntity) {
		return (List<HoglinEntity>)(hoglinEntity.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS)
			? (List)hoglinEntity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS).get()
			: Lists.<HoglinEntity>newArrayList());
	}

	public static float method_24674(HoglinEntity hoglinEntity) {
		return (float)hoglinEntity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
	}

	public static boolean method_24677(HoglinEntity hoglinEntity) {
		return hoglinEntity.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_PLAYERS);
	}

	private static boolean method_24691(HoglinEntity hoglinEntity) {
		return hoglinEntity.getBrain().hasMemoryModule(MemoryModuleType.BREED_TARGET);
	}

	public static boolean method_24680(HoglinEntity hoglinEntity) {
		return hoglinEntity.getBrain().hasMemoryModule(MemoryModuleType.PACIFIED);
	}

	public static boolean method_24682(HoglinEntity hoglinEntity) {
		return hoglinEntity.getBrain().hasActivity(Activity.IDLE);
	}
}
