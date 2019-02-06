package net.minecraft.world;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public interface EntityView {
	List<Entity> getEntities(@Nullable Entity entity, BoundingBox boundingBox, @Nullable Predicate<? super Entity> predicate);

	default List<Entity> getVisibleEntities(@Nullable Entity entity, BoundingBox boundingBox) {
		return this.getEntities(entity, boundingBox, EntityPredicates.EXCEPT_SPECTATOR);
	}

	default boolean method_8611(@Nullable Entity entity, VoxelShape voxelShape) {
		return voxelShape.isEmpty()
			? true
			: this.getVisibleEntities(entity, voxelShape.getBoundingBox())
				.stream()
				.filter(entity2 -> !entity2.invalid && entity2.field_6033 && (entity == null || !entity2.method_5794(entity)))
				.noneMatch(entityx -> VoxelShapes.compareShapes(voxelShape, VoxelShapes.cube(entityx.getBoundingBox()), BooleanBiFunction.AND));
	}

	default Stream<VoxelShape> getCollidingEntityBoundingBoxesForEntity(@Nullable Entity entity, VoxelShape voxelShape, Set<Entity> set) {
		if (voxelShape.isEmpty()) {
			return Stream.empty();
		} else {
			BoundingBox boundingBox = voxelShape.getBoundingBox();
			return this.getVisibleEntities(entity, boundingBox.expand(0.25))
				.stream()
				.filter(entity2 -> !set.contains(entity2) && (entity == null || !entity.method_5794(entity2)))
				.flatMap(
					entity2 -> Stream.of(entity2.method_5827(), entity == null ? null : entity.method_5708(entity2))
							.filter(Objects::nonNull)
							.filter(boundingBox2 -> boundingBox2.intersects(boundingBox))
							.map(VoxelShapes::cube)
				);
		}
	}
}
