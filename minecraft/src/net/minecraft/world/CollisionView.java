package net.minecraft.world;

import com.google.common.collect.Streams;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.border.WorldBorder;

public interface CollisionView extends BlockView {
	WorldBorder getWorldBorder();

	@Nullable
	BlockView getExistingChunk(int chunkX, int chunkZ);

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

	default boolean doesNotCollide(Box box) {
		return this.doesNotCollide(null, box, entity -> true);
	}

	default boolean doesNotCollide(Entity entity) {
		return this.doesNotCollide(entity, entity.getBoundingBox(), entityx -> true);
	}

	default boolean doesNotCollide(Entity entity, Box box) {
		return this.doesNotCollide(entity, box, entityx -> true);
	}

	default boolean doesNotCollide(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
		return this.getCollisions(entity, box, predicate).allMatch(VoxelShape::isEmpty);
	}

	default Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
		return Stream.empty();
	}

	default Stream<VoxelShape> getCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
		return Streams.concat(this.getBlockCollisions(entity, box), this.getEntityCollisions(entity, box, predicate));
	}

	default Stream<VoxelShape> getBlockCollisions(@Nullable Entity entity, Box box) {
		int i = MathHelper.floor(box.x1 - 1.0E-7) - 1;
		int j = MathHelper.floor(box.x2 + 1.0E-7) + 1;
		int k = MathHelper.floor(box.y1 - 1.0E-7) - 1;
		int l = MathHelper.floor(box.y2 + 1.0E-7) + 1;
		int m = MathHelper.floor(box.z1 - 1.0E-7) - 1;
		int n = MathHelper.floor(box.z2 + 1.0E-7) + 1;
		final ShapeContext shapeContext = entity == null ? ShapeContext.absent() : ShapeContext.of(entity);
		final CuboidBlockIterator cuboidBlockIterator = new CuboidBlockIterator(i, k, m, j, l, n);
		final BlockPos.Mutable mutable = new BlockPos.Mutable();
		final VoxelShape voxelShape = VoxelShapes.cuboid(box);
		return StreamSupport.stream(new AbstractSpliterator<VoxelShape>(Long.MAX_VALUE, 1280) {
			boolean field_19296 = entity == null;

			public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
				if (!this.field_19296) {
					this.field_19296 = true;
					VoxelShape voxelShape = CollisionView.this.getWorldBorder().asVoxelShape();
					boolean bl = VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(entity.getBoundingBox().contract(1.0E-7)), BooleanBiFunction.AND);
					boolean bl2 = VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(entity.getBoundingBox().expand(1.0E-7)), BooleanBiFunction.AND);
					if (!bl && bl2) {
						consumer.accept(voxelShape);
						return true;
					}
				}

				while (cuboidBlockIterator.step()) {
					int i = cuboidBlockIterator.getX();
					int j = cuboidBlockIterator.getY();
					int k = cuboidBlockIterator.getZ();
					int l = cuboidBlockIterator.getEdgeCoordinatesCount();
					if (l != 3) {
						int m = i >> 4;
						int n = k >> 4;
						BlockView blockView = CollisionView.this.getExistingChunk(m, n);
						if (blockView != null) {
							mutable.set(i, j, k);
							BlockState blockState = blockView.getBlockState(mutable);
							if ((l != 1 || blockState.exceedsCube()) && (l != 2 || blockState.getBlock() == Blocks.MOVING_PISTON)) {
								VoxelShape voxelShape2 = blockState.getCollisionShape(CollisionView.this, mutable, shapeContext);
								VoxelShape voxelShape3 = voxelShape2.offset((double)i, (double)j, (double)k);
								if (VoxelShapes.matchesAnywhere(voxelShape, voxelShape3, BooleanBiFunction.AND)) {
									consumer.accept(voxelShape3);
									return true;
								}
							}
						}
					}
				}

				return false;
			}
		}, false);
	}
}
