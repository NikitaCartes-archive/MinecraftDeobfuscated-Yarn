package net.minecraft.entity.ai.brain.task;

import java.util.Comparator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class LookTargetUtil {
	public static void lookAtAndWalkTowardsEachOther(LivingEntity livingEntity, LivingEntity livingEntity2) {
		lookAtEachOther(livingEntity, livingEntity2);
		walkTowardsEachOther(livingEntity, livingEntity2);
	}

	public static boolean canSee(Brain<?> brain, LivingEntity livingEntity) {
		return brain.getOptionalMemory(MemoryModuleType.field_18442).filter(list -> list.contains(livingEntity)).isPresent();
	}

	public static boolean canSee(Brain<?> brain, MemoryModuleType<? extends LivingEntity> memoryModuleType, EntityType<?> entityType) {
		return brain.getOptionalMemory(memoryModuleType)
			.filter(livingEntity -> livingEntity.getType() == entityType)
			.filter(LivingEntity::isAlive)
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
		int i = 2;
		walkTowards(livingEntity, livingEntity2, 2);
		walkTowards(livingEntity2, livingEntity, 2);
	}

	public static void walkTowards(LivingEntity livingEntity, LivingEntity livingEntity2, int i) {
		float f = (float)livingEntity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
		EntityPosWrapper entityPosWrapper = new EntityPosWrapper(livingEntity2);
		WalkTarget walkTarget = new WalkTarget(entityPosWrapper, f, i);
		livingEntity.getBrain().putMemory(MemoryModuleType.field_18446, entityPosWrapper);
		livingEntity.getBrain().putMemory(MemoryModuleType.field_18445, walkTarget);
	}

	public static void give(LivingEntity livingEntity, ItemStack itemStack, LivingEntity livingEntity2) {
		double d = livingEntity.y - 0.3F + (double)livingEntity.getStandingEyeHeight();
		ItemEntity itemEntity = new ItemEntity(livingEntity.world, livingEntity.x, d, livingEntity.z, itemStack);
		BlockPos blockPos = new BlockPos(livingEntity2);
		BlockPos blockPos2 = new BlockPos(livingEntity);
		float f = 0.3F;
		Vec3d vec3d = new Vec3d(blockPos.subtract(blockPos2));
		vec3d = vec3d.normalize().multiply(0.3F);
		itemEntity.setVelocity(vec3d);
		itemEntity.setToDefaultPickupDelay();
		livingEntity.world.spawnEntity(itemEntity);
	}

	public static ChunkSectionPos getPosClosestToOccupiedPointOfInterest(ServerWorld serverWorld, ChunkSectionPos chunkSectionPos, int i) {
		int j = serverWorld.getOccupiedPointOfInterestDistance(chunkSectionPos);
		return (ChunkSectionPos)ChunkSectionPos.stream(chunkSectionPos, i)
			.filter(chunkSectionPosx -> serverWorld.getOccupiedPointOfInterestDistance(chunkSectionPosx) < j)
			.min(Comparator.comparingInt(serverWorld::getOccupiedPointOfInterestDistance))
			.orElse(chunkSectionPos);
	}
}
