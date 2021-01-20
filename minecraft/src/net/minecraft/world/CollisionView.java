package net.minecraft.world;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.border.WorldBorder;

public interface CollisionView extends BlockView {
	WorldBorder getWorldBorder();

	@Nullable
	BlockView getChunkAsView(int chunkX, int chunkZ);

	default boolean intersectsEntities(@Nullable Entity except, VoxelShape shape) {
		return true;
	}

	default boolean canPlace(BlockState state, BlockPos pos, ShapeContext context) {
		VoxelShape voxelShape = state.getCollisionShape(this, pos, context);
		return voxelShape.isEmpty() || this.intersectsEntities(null, voxelShape.offset((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()));
	}

	default boolean intersectsEntities(Entity entity) {
		return this.intersectsEntities(entity, VoxelShapes.cuboid(entity.getBoundingBox()));
	}

	default boolean isSpaceEmpty(Box box) {
		return this.isSpaceEmpty(null, box, entity -> true);
	}

	default boolean isSpaceEmpty(Entity entity) {
		return this.isSpaceEmpty(entity, entity.getBoundingBox(), entityx -> true);
	}

	default boolean isSpaceEmpty(Entity entity, Box box) {
		return this.isSpaceEmpty(entity, box, entityx -> true);
	}

	default boolean isSpaceEmpty(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
		return this.getCollisions(entity, box, predicate).allMatch(VoxelShape::isEmpty);
	}

	Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate);

	default Stream<VoxelShape> getCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
		return Stream.concat(this.getBlockCollisions(entity, box), this.getEntityCollisions(entity, box, predicate));
	}

	default Stream<VoxelShape> getBlockCollisions(@Nullable Entity entity, Box box) {
		return StreamSupport.stream(new BlockCollisionSpliterator(this, entity, box), false);
	}

	default boolean isBlockSpaceEmpty(@Nullable Entity entity, Box box, BiPredicate<BlockState, BlockPos> biPredicate) {
		return this.getBlockCollisions(entity, box, biPredicate).allMatch(VoxelShape::isEmpty);
	}

	default Stream<VoxelShape> getBlockCollisions(@Nullable Entity entity, Box box, BiPredicate<BlockState, BlockPos> biPredicate) {
		return StreamSupport.stream(new BlockCollisionSpliterator(this, entity, box, biPredicate), false);
	}
}
