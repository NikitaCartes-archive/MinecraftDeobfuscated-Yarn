package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public interface EntityView {
	List<Entity> getEntities(@Nullable Entity entity, BoundingBox boundingBox, @Nullable Predicate<? super Entity> predicate);

	<T extends Entity> List<T> getEntities(Class<? extends T> class_, BoundingBox boundingBox, @Nullable Predicate<? super T> predicate);

	List<? extends PlayerEntity> getPlayers();

	default List<Entity> getEntities(@Nullable Entity entity, BoundingBox boundingBox) {
		return this.getEntities(entity, boundingBox, EntityPredicates.EXCEPT_SPECTATOR);
	}

	default boolean intersectsEntities(@Nullable Entity entity, VoxelShape voxelShape) {
		return voxelShape.isEmpty()
			? true
			: this.getEntities(entity, voxelShape.getBoundingBox())
				.stream()
				.filter(entity2 -> !entity2.removed && entity2.field_6033 && (entity == null || !entity2.isConnectedThroughVehicle(entity)))
				.noneMatch(entityx -> VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(entityx.getBoundingBox()), BooleanBiFunction.AND));
	}

	default <T extends Entity> List<T> getEntities(Class<? extends T> class_, BoundingBox boundingBox) {
		return this.getEntities(class_, boundingBox, EntityPredicates.EXCEPT_SPECTATOR);
	}

	default Stream<VoxelShape> getCollisionShapes(@Nullable Entity entity, VoxelShape voxelShape, Set<Entity> set) {
		if (voxelShape.isEmpty()) {
			return Stream.empty();
		} else {
			BoundingBox boundingBox = voxelShape.getBoundingBox();
			return this.getEntities(entity, boundingBox.expand(0.25))
				.stream()
				.filter(entity2 -> !set.contains(entity2) && (entity == null || !entity.isConnectedThroughVehicle(entity2)))
				.flatMap(
					entity2 -> Stream.of(entity2.getCollisionBox(), entity == null ? null : entity.method_5708(entity2))
							.filter(Objects::nonNull)
							.filter(boundingBox2 -> boundingBox2.intersects(boundingBox))
							.map(VoxelShapes::cuboid)
				);
		}
	}

	@Nullable
	default PlayerEntity getClosestPlayer(double d, double e, double f, double g, @Nullable Predicate<Entity> predicate) {
		double h = -1.0;
		PlayerEntity playerEntity = null;

		for (PlayerEntity playerEntity2 : this.getPlayers()) {
			if (predicate == null || predicate.test(playerEntity2)) {
				double i = playerEntity2.squaredDistanceTo(d, e, f);
				if ((g < 0.0 || i < g * g) && (h == -1.0 || i < h)) {
					h = i;
					playerEntity = playerEntity2;
				}
			}
		}

		return playerEntity;
	}

	@Nullable
	default PlayerEntity getClosestPlayer(Entity entity, double d) {
		return this.getClosestPlayer(entity.x, entity.y, entity.z, d, false);
	}

	@Nullable
	default PlayerEntity getClosestPlayer(double d, double e, double f, double g, boolean bl) {
		Predicate<Entity> predicate = bl ? EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR : EntityPredicates.EXCEPT_SPECTATOR;
		return this.getClosestPlayer(d, e, f, g, predicate);
	}

	@Nullable
	default PlayerEntity getClosestPlayer(double d, double e, double f) {
		double g = -1.0;
		PlayerEntity playerEntity = null;

		for (PlayerEntity playerEntity2 : this.getPlayers()) {
			if (EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity2)) {
				double h = playerEntity2.squaredDistanceTo(d, playerEntity2.y, e);
				if ((f < 0.0 || h < f * f) && (g == -1.0 || h < g)) {
					g = h;
					playerEntity = playerEntity2;
				}
			}
		}

		return playerEntity;
	}

	default boolean isPlayerInRange(double d, double e, double f, double g) {
		for (PlayerEntity playerEntity : this.getPlayers()) {
			if (EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity) && EntityPredicates.VALID_ENTITY_LIVING.test(playerEntity)) {
				double h = playerEntity.squaredDistanceTo(d, e, f);
				if (g < 0.0 || h < g * g) {
					return true;
				}
			}
		}

		return false;
	}

	@Nullable
	default PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity livingEntity) {
		return this.getClosestEntity(this.getPlayers(), targetPredicate, livingEntity, livingEntity.x, livingEntity.y, livingEntity.z);
	}

	@Nullable
	default PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity livingEntity, double d, double e, double f) {
		return this.getClosestEntity(this.getPlayers(), targetPredicate, livingEntity, d, e, f);
	}

	@Nullable
	default PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, double d, double e, double f) {
		return this.getClosestEntity(this.getPlayers(), targetPredicate, null, d, e, f);
	}

	@Nullable
	default <T extends LivingEntity> T getClosestEntity(
		Class<? extends T> class_, TargetPredicate targetPredicate, @Nullable LivingEntity livingEntity, double d, double e, double f, BoundingBox boundingBox
	) {
		return this.getClosestEntity(this.getEntities(class_, boundingBox, null), targetPredicate, livingEntity, d, e, f);
	}

	@Nullable
	default <T extends LivingEntity> T getClosestEntity(
		List<? extends T> list, TargetPredicate targetPredicate, @Nullable LivingEntity livingEntity, double d, double e, double f
	) {
		double g = -1.0;
		T livingEntity2 = null;

		for (T livingEntity3 : list) {
			if (targetPredicate.test(livingEntity, livingEntity3)) {
				double h = livingEntity3.squaredDistanceTo(d, e, f);
				if (g == -1.0 || h < g) {
					g = h;
					livingEntity2 = livingEntity3;
				}
			}
		}

		return livingEntity2;
	}

	default List<PlayerEntity> getPlayersInBox(TargetPredicate targetPredicate, LivingEntity livingEntity, BoundingBox boundingBox) {
		List<PlayerEntity> list = Lists.<PlayerEntity>newArrayList();

		for (PlayerEntity playerEntity : this.getPlayers()) {
			if (boundingBox.contains(playerEntity.x, playerEntity.y, playerEntity.z) && targetPredicate.test(livingEntity, playerEntity)) {
				list.add(playerEntity);
			}
		}

		return list;
	}

	default <T extends LivingEntity> List<T> getTargets(
		Class<? extends T> class_, TargetPredicate targetPredicate, LivingEntity livingEntity, BoundingBox boundingBox
	) {
		List<T> list = this.getEntities(class_, boundingBox, null);
		List<T> list2 = Lists.<T>newArrayList();

		for (T livingEntity2 : list) {
			if (targetPredicate.test(livingEntity, livingEntity2)) {
				list2.add(livingEntity2);
			}
		}

		return list2;
	}

	@Nullable
	default PlayerEntity getPlayerByUuid(UUID uUID) {
		for (int i = 0; i < this.getPlayers().size(); i++) {
			PlayerEntity playerEntity = (PlayerEntity)this.getPlayers().get(i);
			if (uUID.equals(playerEntity.getUuid())) {
				return playerEntity;
			}
		}

		return null;
	}
}
