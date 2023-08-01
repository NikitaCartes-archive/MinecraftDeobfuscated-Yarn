package net.minecraft.world;

import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.border.WorldBorder;

public interface CollisionView extends BlockView {
	WorldBorder getWorldBorder();

	@Nullable
	BlockView getChunkAsView(int chunkX, int chunkZ);

	/**
	 * {@return {@code true} if {@code shape} does not intersect
	 * with non-spectator entities except {@code except}}
	 * 
	 * @implNote This always returns {@code true} if {@code shape} is {@linkplain VoxelShape#isEmpty empty}.
	 */
	default boolean doesNotIntersectEntities(@Nullable Entity except, VoxelShape shape) {
		return true;
	}

	default boolean canPlace(BlockState state, BlockPos pos, ShapeContext context) {
		VoxelShape voxelShape = state.getCollisionShape(this, pos, context);
		return voxelShape.isEmpty() || this.doesNotIntersectEntities(null, voxelShape.offset((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()));
	}

	default boolean doesNotIntersectEntities(Entity entity) {
		return this.doesNotIntersectEntities(entity, VoxelShapes.cuboid(entity.getBoundingBox()));
	}

	default boolean isSpaceEmpty(Box box) {
		return this.isSpaceEmpty(null, box);
	}

	default boolean isSpaceEmpty(Entity entity) {
		return this.isSpaceEmpty(entity, entity.getBoundingBox());
	}

	default boolean isSpaceEmpty(@Nullable Entity entity, Box box) {
		for (VoxelShape voxelShape : this.getBlockCollisions(entity, box)) {
			if (!voxelShape.isEmpty()) {
				return false;
			}
		}

		if (!this.getEntityCollisions(entity, box).isEmpty()) {
			return false;
		} else if (entity == null) {
			return true;
		} else {
			VoxelShape voxelShape2 = this.getWorldBorderCollisions(entity, box);
			return voxelShape2 == null || !VoxelShapes.matchesAnywhere(voxelShape2, VoxelShapes.cuboid(box), BooleanBiFunction.AND);
		}
	}

	default boolean isBlockSpaceEmpty(@Nullable Entity entity, Box box) {
		for (VoxelShape voxelShape : this.getBlockCollisions(entity, box)) {
			if (!voxelShape.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	List<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box);

	default Iterable<VoxelShape> getCollisions(@Nullable Entity entity, Box box) {
		List<VoxelShape> list = this.getEntityCollisions(entity, box);
		Iterable<VoxelShape> iterable = this.getBlockCollisions(entity, box);
		return list.isEmpty() ? iterable : Iterables.concat(list, iterable);
	}

	default Iterable<VoxelShape> getBlockCollisions(@Nullable Entity entity, Box box) {
		return () -> new BlockCollisionSpliterator(this, entity, box, false, (pos, voxelShape) -> voxelShape);
	}

	@Nullable
	private VoxelShape getWorldBorderCollisions(Entity entity, Box box) {
		WorldBorder worldBorder = this.getWorldBorder();
		return worldBorder.canCollide(entity, box) ? worldBorder.asVoxelShape() : null;
	}

	default boolean canCollide(@Nullable Entity entity, Box box) {
		BlockCollisionSpliterator<VoxelShape> blockCollisionSpliterator = new BlockCollisionSpliterator<>(this, entity, box, true, (pos, voxelShape) -> voxelShape);

		while (blockCollisionSpliterator.hasNext()) {
			if (!blockCollisionSpliterator.next().isEmpty()) {
				return true;
			}
		}

		return false;
	}

	default Optional<BlockPos> findSupportingBlockPos(Entity entity, Box box) {
		BlockPos blockPos = null;
		double d = Double.MAX_VALUE;
		BlockCollisionSpliterator<BlockPos> blockCollisionSpliterator = new BlockCollisionSpliterator<>(this, entity, box, false, (pos, voxelShape) -> pos);

		while (blockCollisionSpliterator.hasNext()) {
			BlockPos blockPos2 = blockCollisionSpliterator.next();
			double e = blockPos2.getSquaredDistance(entity.getPos());
			if (e < d || e == d && (blockPos == null || blockPos.compareTo(blockPos2) < 0)) {
				blockPos = blockPos2.toImmutable();
				d = e;
			}
		}

		return Optional.ofNullable(blockPos);
	}

	default Optional<Vec3d> findClosestCollision(@Nullable Entity entity, VoxelShape shape, Vec3d target, double x, double y, double z) {
		if (shape.isEmpty()) {
			return Optional.empty();
		} else {
			Box box = shape.getBoundingBox().expand(x, y, z);
			VoxelShape voxelShape = (VoxelShape)StreamSupport.stream(this.getBlockCollisions(entity, box).spliterator(), false)
				.filter(voxelShapex -> this.getWorldBorder() == null || this.getWorldBorder().contains(voxelShapex.getBoundingBox()))
				.flatMap(voxelShapex -> voxelShapex.getBoundingBoxes().stream())
				.map(boxx -> boxx.expand(x / 2.0, y / 2.0, z / 2.0))
				.map(VoxelShapes::cuboid)
				.reduce(VoxelShapes.empty(), VoxelShapes::union);
			VoxelShape voxelShape2 = VoxelShapes.combineAndSimplify(shape, voxelShape, BooleanBiFunction.ONLY_FIRST);
			return voxelShape2.getClosestPointTo(target);
		}
	}
}
