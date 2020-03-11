package net.minecraft.entity.ai.brain.task;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
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
		entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(target));
	}

	private static void walkTowardsEachOther(LivingEntity first, LivingEntity second) {
		int i = 2;
		walkTowards(first, second, 2);
		walkTowards(second, first, 2);
	}

	public static void walkTowards(LivingEntity entity, Entity target, int completionRange) {
		LookTarget lookTarget = new EntityLookTarget(target);
		walkTowards(entity, lookTarget, completionRange);
	}

	public static void walkTowards(LivingEntity entity, BlockPos target, int completionRange) {
		LookTarget lookTarget = new BlockPosLookTarget(target);
		walkTowards(entity, lookTarget, completionRange);
	}

	private static void walkTowards(LivingEntity entity, LookTarget target, int completionRange) {
		float f = (float)entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
		WalkTarget walkTarget = new WalkTarget(target, f, completionRange);
		entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, target);
		entity.getBrain().remember(MemoryModuleType.WALK_TARGET, walkTarget);
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

	public static boolean method_25940(MobEntity mobEntity, LivingEntity livingEntity, int i) {
		Item item = mobEntity.getMainHandStack().getItem();
		if (item instanceof RangedWeaponItem && mobEntity.method_25938((RangedWeaponItem)item)) {
			int j = ((RangedWeaponItem)item).getRange() - i;
			return mobEntity.isInRange(livingEntity, (double)j);
		} else {
			return method_25941(mobEntity, livingEntity);
		}
	}

	public static boolean method_25941(LivingEntity livingEntity, LivingEntity livingEntity2) {
		double d = livingEntity.squaredDistanceTo(livingEntity2.getX(), livingEntity2.getY(), livingEntity2.getZ());
		double e = (double)(livingEntity.getWidth() * 2.0F * livingEntity.getWidth() * 2.0F + livingEntity2.getWidth());
		return d <= e;
	}

	/**
	 * Checks if an entity can be a new attack target for the source entity.
	 * 
	 * @param source the source entity
	 * @param target the attack target candidate
	 * @param extraDistance the max distance this new target can be farther compared to the existing target
	 */
	public static boolean isNewTargetTooFar(LivingEntity source, LivingEntity target, double extraDistance) {
		Optional<LivingEntity> optional = source.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
		if (!optional.isPresent()) {
			return false;
		} else {
			double d = source.squaredDistanceTo(((LivingEntity)optional.get()).getPos());
			double e = source.squaredDistanceTo(target.getPos());
			return e > d + extraDistance * extraDistance;
		}
	}

	public static boolean isVisibleInMemory(LivingEntity source, LivingEntity target) {
		Brain<?> brain = source.getBrain();
		return !brain.hasMemoryModule(MemoryModuleType.VISIBLE_MOBS) ? false : ((List)brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get()).contains(target);
	}

	public static LivingEntity getCloserEntity(LivingEntity source, Optional<LivingEntity> first, LivingEntity second) {
		return !first.isPresent() ? second : getCloserEntity(source, (LivingEntity)first.get(), second);
	}

	public static LivingEntity getCloserEntity(LivingEntity source, LivingEntity first, LivingEntity second) {
		Vec3d vec3d = first.getPos();
		Vec3d vec3d2 = second.getPos();
		return source.squaredDistanceTo(vec3d) < source.squaredDistanceTo(vec3d2) ? first : second;
	}

	public static Optional<LivingEntity> getEntity(LivingEntity entity, MemoryModuleType<DynamicSerializableUuid> uuidMemoryModule) {
		Optional<DynamicSerializableUuid> optional = entity.getBrain().getOptionalMemory(uuidMemoryModule);
		return optional.map(DynamicSerializableUuid::getUuid).map(uUID -> (LivingEntity)((ServerWorld)entity.world).getEntity(uUID));
	}
}
