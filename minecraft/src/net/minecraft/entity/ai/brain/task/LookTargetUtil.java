package net.minecraft.entity.ai.brain.task;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class LookTargetUtil {
	private LookTargetUtil() {
	}

	public static void lookAtAndWalkTowardsEachOther(LivingEntity first, LivingEntity second, float speed, int walkCompletionRange) {
		lookAtEachOther(first, second);
		walkTowardsEachOther(first, second, speed, walkCompletionRange);
	}

	public static boolean canSee(Brain<?> brain, LivingEntity target) {
		Optional<LivingTargetCache> optional = brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS);
		return optional.isPresent() && ((LivingTargetCache)optional.get()).contains(target);
	}

	public static boolean canSee(Brain<?> brain, MemoryModuleType<? extends LivingEntity> memoryModuleType, EntityType<?> entityType) {
		return canSee(brain, memoryModuleType, entity -> entity.getType() == entityType);
	}

	private static boolean canSee(Brain<?> brain, MemoryModuleType<? extends LivingEntity> memoryType, Predicate<LivingEntity> filter) {
		return brain.getOptionalRegisteredMemory(memoryType).filter(filter).filter(LivingEntity::isAlive).filter(target -> canSee(brain, target)).isPresent();
	}

	private static void lookAtEachOther(LivingEntity first, LivingEntity second) {
		lookAt(first, second);
		lookAt(second, first);
	}

	public static void lookAt(LivingEntity entity, LivingEntity target) {
		entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(target, true));
	}

	private static void walkTowardsEachOther(LivingEntity first, LivingEntity second, float speed, int completionRange) {
		walkTowards(first, second, speed, completionRange);
		walkTowards(second, first, speed, completionRange);
	}

	public static void walkTowards(LivingEntity entity, Entity target, float speed, int completionRange) {
		walkTowards(entity, new EntityLookTarget(target, true), speed, completionRange);
	}

	public static void walkTowards(LivingEntity entity, BlockPos target, float speed, int completionRange) {
		walkTowards(entity, new BlockPosLookTarget(target), speed, completionRange);
	}

	public static void walkTowards(LivingEntity entity, LookTarget target, float speed, int completionRange) {
		WalkTarget walkTarget = new WalkTarget(target, speed, completionRange);
		entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, target);
		entity.getBrain().remember(MemoryModuleType.WALK_TARGET, walkTarget);
	}

	public static void give(LivingEntity entity, ItemStack stack, Vec3d targetLocation) {
		Vec3d vec3d = new Vec3d(0.3F, 0.3F, 0.3F);
		give(entity, stack, targetLocation, vec3d, 0.3F);
	}

	public static void give(LivingEntity entity, ItemStack stack, Vec3d targetLocation, Vec3d velocityFactor, float yOffset) {
		double d = entity.getEyeY() - (double)yOffset;
		ItemEntity itemEntity = new ItemEntity(entity.getWorld(), entity.getX(), d, entity.getZ(), stack);
		itemEntity.setThrower(entity);
		Vec3d vec3d = targetLocation.subtract(entity.getPos());
		vec3d = vec3d.normalize().multiply(velocityFactor.x, velocityFactor.y, velocityFactor.z);
		itemEntity.setVelocity(vec3d);
		itemEntity.setToDefaultPickupDelay();
		entity.getWorld().spawnEntity(itemEntity);
	}

	public static ChunkSectionPos getPosClosestToOccupiedPointOfInterest(ServerWorld world, ChunkSectionPos center, int radius) {
		int i = world.getOccupiedPointOfInterestDistance(center);
		return (ChunkSectionPos)ChunkSectionPos.stream(center, radius)
			.filter(sectionPos -> world.getOccupiedPointOfInterestDistance(sectionPos) < i)
			.min(Comparator.comparingInt(world::getOccupiedPointOfInterestDistance))
			.orElse(center);
	}

	public static boolean isTargetWithinAttackRange(MobEntity mob, LivingEntity target, int rangedWeaponReachReduction) {
		if (mob.getMainHandStack().getItem() instanceof RangedWeaponItem rangedWeaponItem && mob.canUseRangedWeapon(rangedWeaponItem)) {
			int i = rangedWeaponItem.getRange() - rangedWeaponReachReduction;
			return mob.isInRange(target, (double)i);
		}

		return mob.isInAttackRange(target);
	}

	/**
	 * Checks if an entity can be a new attack target for the source entity.
	 * 
	 * @param source the source entity
	 * @param target the attack target candidate
	 * @param extraDistance the max distance this new target can be farther compared to the existing target
	 */
	public static boolean isNewTargetTooFar(LivingEntity source, LivingEntity target, double extraDistance) {
		Optional<LivingEntity> optional = source.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET);
		if (optional.isEmpty()) {
			return false;
		} else {
			double d = source.squaredDistanceTo(((LivingEntity)optional.get()).getPos());
			double e = source.squaredDistanceTo(target.getPos());
			return e > d + extraDistance * extraDistance;
		}
	}

	public static boolean isVisibleInMemory(LivingEntity source, LivingEntity target) {
		Brain<?> brain = source.getBrain();
		return !brain.hasMemoryModule(MemoryModuleType.VISIBLE_MOBS)
			? false
			: ((LivingTargetCache)brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).get()).contains(target);
	}

	public static LivingEntity getCloserEntity(LivingEntity source, Optional<LivingEntity> first, LivingEntity second) {
		return first.isEmpty() ? second : getCloserEntity(source, (LivingEntity)first.get(), second);
	}

	public static LivingEntity getCloserEntity(LivingEntity source, LivingEntity first, LivingEntity second) {
		Vec3d vec3d = first.getPos();
		Vec3d vec3d2 = second.getPos();
		return source.squaredDistanceTo(vec3d) < source.squaredDistanceTo(vec3d2) ? first : second;
	}

	public static Optional<LivingEntity> getEntity(LivingEntity entity, MemoryModuleType<UUID> uuidMemoryModule) {
		Optional<UUID> optional = entity.getBrain().getOptionalRegisteredMemory(uuidMemoryModule);
		return optional.map(uuid -> ((ServerWorld)entity.getWorld()).getEntity(uuid))
			.map(target -> target instanceof LivingEntity livingEntity ? livingEntity : null);
	}

	@Nullable
	public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange) {
		Vec3d vec3d = NoPenaltyTargeting.find(entity, horizontalRange, verticalRange);
		int i = 0;

		while (vec3d != null && !entity.getWorld().getBlockState(BlockPos.ofFloored(vec3d)).canPathfindThrough(NavigationType.WATER) && i++ < 10) {
			vec3d = NoPenaltyTargeting.find(entity, horizontalRange, verticalRange);
		}

		return vec3d;
	}

	public static boolean hasBreedTarget(LivingEntity entity) {
		return entity.getBrain().hasMemoryModule(MemoryModuleType.BREED_TARGET);
	}
}
