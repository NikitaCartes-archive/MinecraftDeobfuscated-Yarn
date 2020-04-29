package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public interface EntityView {
	List<Entity> getEntities(@Nullable Entity except, Box box, @Nullable Predicate<? super Entity> predicate);

	<T extends Entity> List<T> getEntities(Class<? extends T> entityClass, Box box, @Nullable Predicate<? super T> predicate);

	default <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> entityClass, Box box, @Nullable Predicate<? super T> predicate) {
		return this.getEntities(entityClass, box, predicate);
	}

	List<? extends PlayerEntity> getPlayers();

	default List<Entity> getEntities(@Nullable Entity except, Box box) {
		return this.getEntities(except, box, EntityPredicates.EXCEPT_SPECTATOR);
	}

	default boolean intersectsEntities(@Nullable Entity except, VoxelShape shape) {
		if (shape.isEmpty()) {
			return true;
		} else {
			for (Entity entity : this.getEntities(except, shape.getBoundingBox())) {
				if (!entity.removed
					&& entity.inanimate
					&& (except == null || !entity.isConnectedThroughVehicle(except))
					&& VoxelShapes.matchesAnywhere(shape, VoxelShapes.cuboid(entity.getBoundingBox()), BooleanBiFunction.AND)) {
					return false;
				}
			}

			return true;
		}
	}

	default <T extends Entity> List<T> getNonSpectatingEntities(Class<? extends T> entityClass, Box box) {
		return this.getEntities(entityClass, box, EntityPredicates.EXCEPT_SPECTATOR);
	}

	default <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> entityClass, Box box) {
		return this.getEntitiesIncludingUngeneratedChunks(entityClass, box, EntityPredicates.EXCEPT_SPECTATOR);
	}

	default Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
		if (box.getAverageSideLength() < 1.0E-7) {
			return Stream.empty();
		} else {
			Box box2 = box.expand(1.0E-7);
			return this.getEntities(entity, box2)
				.stream()
				.filter(predicate)
				.filter(e -> entity == null || !entity.isConnectedThroughVehicle(e))
				.flatMap(e -> Stream.of(e.getCollisionBox(), entity == null ? null : entity.getHardCollisionBox(e)))
				.filter(Objects::nonNull)
				.filter(box2::intersects)
				.map(VoxelShapes::cuboid);
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
			if (EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity) && EntityPredicates.VALID_ENTITY_LIVING.test(playerEntity)) {
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
		return this.getClosestEntity(this.getEntities(entityClass, box, null), targetPredicate, entity, x, y, z);
	}

	@Nullable
	default <T extends LivingEntity> T getClosestEntityIncludingUngeneratedChunks(
		Class<? extends T> entityClass, TargetPredicate targetPredicate, @Nullable LivingEntity entity, double x, double y, double z, Box box
	) {
		return this.getClosestEntity(this.getEntitiesIncludingUngeneratedChunks(entityClass, box, null), targetPredicate, entity, x, y, z);
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

	default <T extends LivingEntity> List<T> getTargets(Class<? extends T> entityClass, TargetPredicate targetPredicate, LivingEntity targettingEntity, Box box) {
		List<T> list = this.getEntities(entityClass, box, null);
		List<T> list2 = Lists.<T>newArrayList();

		for (T livingEntity : list) {
			if (targetPredicate.test(targettingEntity, livingEntity)) {
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
