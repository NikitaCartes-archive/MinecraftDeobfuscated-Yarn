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
	public static void lookAtAndWalkTowardsEachOther(LivingEntity first, LivingEntity second) {
		lookAtEachOther(first, second);
		walkTowardsEachOther(first, second);
	}

	public static boolean canSee(Brain<?> brain, LivingEntity target) {
		return brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).filter(list -> list.contains(target)).isPresent();
	}

	public static boolean canSee(Brain<?> brain, MemoryModuleType<? extends LivingEntity> memoryModuleType, EntityType<?> entityType) {
		return brain.getOptionalMemory(memoryModuleType)
			.filter(livingEntity -> livingEntity.getType() == entityType)
			.filter(LivingEntity::isAlive)
			.filter(livingEntity -> canSee(brain, livingEntity))
			.isPresent();
	}

	public static void lookAtEachOther(LivingEntity first, LivingEntity second) {
		lookAt(first, second);
		lookAt(second, first);
	}

	public static void lookAt(LivingEntity entity, LivingEntity target) {
		entity.getBrain().putMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(target));
	}

	public static void walkTowardsEachOther(LivingEntity first, LivingEntity second) {
		int i = 2;
		walkTowards(first, second, 2);
		walkTowards(second, first, 2);
	}

	public static void walkTowards(LivingEntity entity, LivingEntity target, int completionRange) {
		float f = (float)entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
		EntityPosWrapper entityPosWrapper = new EntityPosWrapper(target);
		WalkTarget walkTarget = new WalkTarget(entityPosWrapper, f, completionRange);
		entity.getBrain().putMemory(MemoryModuleType.LOOK_TARGET, entityPosWrapper);
		entity.getBrain().putMemory(MemoryModuleType.WALK_TARGET, walkTarget);
	}

	public static void give(LivingEntity entity, ItemStack stack, LivingEntity target) {
		double d = entity.y - 0.3F + (double)entity.getStandingEyeHeight();
		ItemEntity itemEntity = new ItemEntity(entity.world, entity.x, d, entity.z, stack);
		BlockPos blockPos = new BlockPos(target);
		BlockPos blockPos2 = new BlockPos(entity);
		float f = 0.3F;
		Vec3d vec3d = new Vec3d(blockPos.subtract(blockPos2));
		vec3d = vec3d.normalize().multiply(0.3F);
		itemEntity.setVelocity(vec3d);
		itemEntity.setToDefaultPickupDelay();
		entity.world.spawnEntity(itemEntity);
	}

	public static ChunkSectionPos getPosClosestToOccupiedPointOfInterest(ServerWorld world, ChunkSectionPos center, int radius) {
		int i = world.getOccupiedPointOfInterestDistance(center);
		return (ChunkSectionPos)ChunkSectionPos.stream(center, radius)
			.filter(chunkSectionPos -> world.getOccupiedPointOfInterestDistance(chunkSectionPos) < i)
			.min(Comparator.comparingInt(world::getOccupiedPointOfInterestDistance))
			.orElse(center);
	}
}
