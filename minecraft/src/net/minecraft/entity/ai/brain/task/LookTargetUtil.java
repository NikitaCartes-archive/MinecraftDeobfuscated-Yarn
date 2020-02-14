package net.minecraft.entity.ai.brain.task;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.class_4844;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.LookTarget;
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
		return method_24564(brain, memoryModuleType, livingEntity -> livingEntity.getType() == entityType);
	}

	private static boolean method_24564(Brain<?> brain, MemoryModuleType<? extends LivingEntity> memoryModuleType, Predicate<LivingEntity> predicate) {
		return brain.getOptionalMemory(memoryModuleType)
			.filter(predicate)
			.filter(LivingEntity::isAlive)
			.filter(livingEntity -> canSee(brain, livingEntity))
			.isPresent();
	}

	private static void lookAtEachOther(LivingEntity first, LivingEntity second) {
		lookAt(first, second);
		lookAt(second, first);
	}

	public static void lookAt(LivingEntity entity, LivingEntity target) {
		entity.getBrain().putMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(target));
	}

	private static void walkTowardsEachOther(LivingEntity first, LivingEntity second) {
		int i = 2;
		method_24557(first, second, 2);
		method_24557(second, first, 2);
	}

	public static void method_24557(LivingEntity livingEntity, Entity entity, int i) {
		LookTarget lookTarget = new EntityPosWrapper(entity);
		walkTowards(livingEntity, lookTarget, i);
	}

	public static void method_24561(LivingEntity livingEntity, BlockPos blockPos, int i) {
		LookTarget lookTarget = new BlockPosLookTarget(blockPos);
		walkTowards(livingEntity, lookTarget, i);
	}

	private static void walkTowards(LivingEntity entity, LookTarget lookTarget, int completionRange) {
		float f = (float)entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
		WalkTarget walkTarget = new WalkTarget(lookTarget, f, completionRange);
		entity.getBrain().putMemory(MemoryModuleType.LOOK_TARGET, lookTarget);
		entity.getBrain().putMemory(MemoryModuleType.WALK_TARGET, walkTarget);
	}

	public static void give(LivingEntity entity, ItemStack stack, Vec3d vec3d) {
		double d = entity.getEyeY() - 0.3F;
		ItemEntity itemEntity = new ItemEntity(entity.world, entity.getX(), d, entity.getZ(), stack);
		float f = 0.3F;
		Vec3d vec3d2 = vec3d.subtract(entity.getPos());
		vec3d2 = vec3d2.normalize().multiply(0.3F);
		itemEntity.setVelocity(vec3d2);
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

	public static boolean method_24556(LivingEntity livingEntity, double d) {
		Brain<?> brain = livingEntity.getBrain();
		if (!brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET)) {
			return false;
		} else {
			LivingEntity livingEntity2 = (LivingEntity)brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
			return !method_24565(livingEntity, livingEntity2) ? false : livingEntity2.method_24516(livingEntity, d);
		}
	}

	public static boolean method_24558(LivingEntity livingEntity, LivingEntity livingEntity2, double d) {
		Optional<LivingEntity> optional = livingEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
		if (!optional.isPresent()) {
			return false;
		} else {
			double e = livingEntity.squaredDistanceTo(((LivingEntity)optional.get()).getPos());
			double f = livingEntity.squaredDistanceTo(livingEntity2.getPos());
			return f > e + d * d;
		}
	}

	public static boolean method_24565(LivingEntity livingEntity, LivingEntity livingEntity2) {
		Brain<?> brain = livingEntity.getBrain();
		return !brain.hasMemoryModule(MemoryModuleType.VISIBLE_MOBS)
			? false
			: ((List)brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get()).contains(livingEntity2);
	}

	public static LivingEntity method_24562(LivingEntity livingEntity, Optional<LivingEntity> optional, LivingEntity livingEntity2) {
		return !optional.isPresent() ? livingEntity2 : method_24559(livingEntity, (LivingEntity)optional.get(), livingEntity2);
	}

	public static LivingEntity method_24559(LivingEntity livingEntity, LivingEntity livingEntity2, LivingEntity livingEntity3) {
		Vec3d vec3d = livingEntity2.getPos();
		Vec3d vec3d2 = livingEntity3.getPos();
		return livingEntity.squaredDistanceTo(vec3d) < livingEntity.squaredDistanceTo(vec3d2) ? livingEntity2 : livingEntity3;
	}

	public static Optional<LivingEntity> method_24560(LivingEntity livingEntity, MemoryModuleType<class_4844> memoryModuleType) {
		Optional<class_4844> optional = livingEntity.getBrain().getOptionalMemory(memoryModuleType);
		return optional.map(class_4844::method_24814).map(uUID -> (LivingEntity)((ServerWorld)livingEntity.world).getEntity(uUID));
	}
}
