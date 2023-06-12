package net.minecraft.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public interface EntityView {
	/**
	 * Computes a list of entities within some box, excluding the given entity, that satisfy the given predicate.
	 * 
	 * @return a list of entities within a box, excluding the given entity, all satisfying the given predicate
	 * 
	 * @param box the box in which to search for entities
	 * @param predicate a predicate which entities must satisfy in order to be included in the returned list
	 * @param except the entity the box logically surrounds; this entity is ignored if it is inside the box
	 */
	List<Entity> getOtherEntities(@Nullable Entity except, Box box, Predicate<? super Entity> predicate);

	<T extends Entity> List<T> getEntitiesByType(TypeFilter<Entity, T> filter, Box box, Predicate<? super T> predicate);

	/**
	 * Computes a list of entities within some box whose runtime Java class is the same as or is
	 * a subclass of the given class.
	 * 
	 * @return a list of entities within the box whose runtime class is a subclass of the given class
	 * 
	 * @param entityClass the class the list of entities must extend
	 * @param box the box in which to search for entities
	 * @param predicate a predicate which entities must satisfy in order to be included in the returned list
	 */
	default <T extends Entity> List<T> getEntitiesByClass(Class<T> entityClass, Box box, Predicate<? super T> predicate) {
		return this.getEntitiesByType(TypeFilter.instanceOf(entityClass), box, predicate);
	}

	List<? extends PlayerEntity> getPlayers();

	/**
	 * Computes a list of entities within some box, excluding the given entity, that are not spectators.
	 * 
	 * @return a list of entities within a box, excluding the given entity
	 * @see #getOtherEntities(Entity, Box, Predicate)
	 * @see Entity#isSpectator()
	 * 
	 * @param except the entity the box logically surrounds; this entity is ignored if it is inside the box
	 * @param box the box in which to search for entities
	 */
	default List<Entity> getOtherEntities(@Nullable Entity except, Box box) {
		return this.getOtherEntities(except, box, EntityPredicates.EXCEPT_SPECTATOR);
	}

	/**
	 * {@return {@code true} if {@code shape} does not intersect
	 * with non-spectator entities except {@code except}}
	 * 
	 * @implNote This always returns {@code true} if {@code shape} is {@linkplain VoxelShape#isEmpty empty}.
	 */
	default boolean doesNotIntersectEntities(@Nullable Entity except, VoxelShape shape) {
		if (shape.isEmpty()) {
			return true;
		} else {
			for (Entity entity : this.getOtherEntities(except, shape.getBoundingBox())) {
				if (!entity.isRemoved()
					&& entity.intersectionChecked
					&& (except == null || !entity.isConnectedThroughVehicle(except))
					&& VoxelShapes.matchesAnywhere(shape, VoxelShapes.cuboid(entity.getBoundingBox()), BooleanBiFunction.AND)) {
					return false;
				}
			}

			return true;
		}
	}

	default <T extends Entity> List<T> getNonSpectatingEntities(Class<T> entityClass, Box box) {
		return this.getEntitiesByClass(entityClass, box, EntityPredicates.EXCEPT_SPECTATOR);
	}

	default List<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box) {
		if (box.getAverageSideLength() < 1.0E-7) {
			return List.of();
		} else {
			Predicate<Entity> predicate = entity == null ? EntityPredicates.CAN_COLLIDE : EntityPredicates.EXCEPT_SPECTATOR.and(entity::collidesWith);
			List<Entity> list = this.getOtherEntities(entity, box.expand(1.0E-7), predicate);
			if (list.isEmpty()) {
				return List.of();
			} else {
				Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(list.size());

				for (Entity entity2 : list) {
					builder.add(VoxelShapes.cuboid(entity2.getBoundingBox()));
				}

				return builder.build();
			}
		}
	}

	@Nullable
	default PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, @Nullable Predicate<Entity> targetPredicate) {
		double d = -1.0;
		PlayerEntity playerEntity = null;

		for (PlayerEntity playerEntity2 : this.getPlayers()) {
			if (targetPredicate == null || targetPredicate.test(playerEntity2)) {
				double e = playerEntity2.squaredDistanceTo(x, y, z);
				if ((maxDistance < 0.0 || e < maxDistance * maxDistance) && (d == -1.0 || e < d)) {
					d = e;
					playerEntity = playerEntity2;
				}
			}
		}

		return playerEntity;
	}

	@Nullable
	default PlayerEntity getClosestPlayer(Entity entity, double maxDistance) {
		return this.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), maxDistance, false);
	}

	@Nullable
	default PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, boolean ignoreCreative) {
		Predicate<Entity> predicate = ignoreCreative ? EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR : EntityPredicates.EXCEPT_SPECTATOR;
		return this.getClosestPlayer(x, y, z, maxDistance, predicate);
	}

	default boolean isPlayerInRange(double x, double y, double z, double range) {
		for (PlayerEntity playerEntity : this.getPlayers()) {
			if (EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity) && EntityPredicates.VALID_LIVING_ENTITY.test(playerEntity)) {
				double d = playerEntity.squaredDistanceTo(x, y, z);
				if (range < 0.0 || d < range * range) {
					return true;
				}
			}
		}

		return false;
	}

	@Nullable
	default PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity entity) {
		return this.getClosestEntity(this.getPlayers(), targetPredicate, entity, entity.getX(), entity.getY(), entity.getZ());
	}

	@Nullable
	default PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity entity, double x, double y, double z) {
		return this.getClosestEntity(this.getPlayers(), targetPredicate, entity, x, y, z);
	}

	@Nullable
	default PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, double x, double y, double z) {
		return this.getClosestEntity(this.getPlayers(), targetPredicate, null, x, y, z);
	}

	@Nullable
	default <T extends LivingEntity> T getClosestEntity(
		Class<? extends T> entityClass, TargetPredicate targetPredicate, @Nullable LivingEntity entity, double x, double y, double z, Box box
	) {
		return this.getClosestEntity(this.getEntitiesByClass(entityClass, box, entityOfClass -> true), targetPredicate, entity, x, y, z);
	}

	@Nullable
	default <T extends LivingEntity> T getClosestEntity(
		List<? extends T> entityList, TargetPredicate targetPredicate, @Nullable LivingEntity entity, double x, double y, double z
	) {
		double d = -1.0;
		T livingEntity = null;

		for (T livingEntity2 : entityList) {
			if (targetPredicate.test(entity, livingEntity2)) {
				double e = livingEntity2.squaredDistanceTo(x, y, z);
				if (d == -1.0 || e < d) {
					d = e;
					livingEntity = livingEntity2;
				}
			}
		}

		return livingEntity;
	}

	default List<PlayerEntity> getPlayers(TargetPredicate targetPredicate, LivingEntity entity, Box box) {
		List<PlayerEntity> list = Lists.<PlayerEntity>newArrayList();

		for (PlayerEntity playerEntity : this.getPlayers()) {
			if (box.contains(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ()) && targetPredicate.test(entity, playerEntity)) {
				list.add(playerEntity);
			}
		}

		return list;
	}

	default <T extends LivingEntity> List<T> getTargets(Class<T> entityClass, TargetPredicate targetPredicate, LivingEntity targetingEntity, Box box) {
		List<T> list = this.getEntitiesByClass(entityClass, box, livingEntityx -> true);
		List<T> list2 = Lists.<T>newArrayList();

		for (T livingEntity : list) {
			if (targetPredicate.test(targetingEntity, livingEntity)) {
				list2.add(livingEntity);
			}
		}

		return list2;
	}

	@Nullable
	default PlayerEntity getPlayerByUuid(UUID uuid) {
		for (int i = 0; i < this.getPlayers().size(); i++) {
			PlayerEntity playerEntity = (PlayerEntity)this.getPlayers().get(i);
			if (uuid.equals(playerEntity.getUuid())) {
				return playerEntity;
			}
		}

		return null;
	}
}
