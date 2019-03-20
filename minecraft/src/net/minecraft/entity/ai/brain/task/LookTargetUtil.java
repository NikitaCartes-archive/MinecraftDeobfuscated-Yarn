package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.attribute.EntityAttributes;

public class LookTargetUtil {
	public static void lookAtAndWalkTowardsEachOther(LivingEntity livingEntity, LivingEntity livingEntity2) {
		lookAtEachOther(livingEntity, livingEntity2);
		walkTowardsEachOther(livingEntity, livingEntity2);
	}

	public static boolean canSee(Brain<?> brain, LivingEntity livingEntity) {
		return brain.getMemory(MemoryModuleType.field_18442).filter(list -> list.contains(livingEntity)).isPresent();
	}

	public static boolean canSee(Brain<?> brain, MemoryModuleType<? extends LivingEntity> memoryModuleType, EntityType<?> entityType) {
		return brain.getMemory(memoryModuleType)
			.filter(livingEntity -> livingEntity.getType() == entityType)
			.filter(LivingEntity::isValid)
			.filter(livingEntity -> canSee(brain, livingEntity))
			.isPresent();
	}

	public static void lookAtEachOther(LivingEntity livingEntity, LivingEntity livingEntity2) {
		lookAt(livingEntity, livingEntity2);
		lookAt(livingEntity2, livingEntity);
	}

	public static void lookAt(LivingEntity livingEntity, LivingEntity livingEntity2) {
		livingEntity.getBrain().putMemory(MemoryModuleType.field_18446, new EntityPosWrapper(livingEntity2));
	}

	public static void walkTowardsEachOther(LivingEntity livingEntity, LivingEntity livingEntity2) {
		walkTowards(livingEntity, livingEntity2);
		walkTowards(livingEntity2, livingEntity);
	}

	public static void walkTowards(LivingEntity livingEntity, LivingEntity livingEntity2) {
		float f = (float)livingEntity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
		int i = 2;
		WalkTarget walkTarget = new WalkTarget(new EntityPosWrapper(livingEntity2), f, 2);
		livingEntity.getBrain().putMemory(MemoryModuleType.field_18445, walkTarget);
	}
}
