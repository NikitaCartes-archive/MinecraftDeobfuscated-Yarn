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
					WorldBorder worldBorder = CollisionView.this.getWorldBorder();
					boolean bl = CollisionView.method_27087(worldBorder, entity.getBoundingBox().contract(1.0E-7));
					boolean bl2 = bl && !CollisionView.method_27087(worldBorder, entity.getBoundingBox().expand(1.0E-7));
					if (bl2) {
						consumer.accept(worldBorder.asVoxelShape());
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
							if ((l != 1 || blockState.exceedsCube()) && (l != 2 || blockState.isOf(Blocks.MOVING_PISTON))) {
								VoxelShape voxelShape = blockState.getCollisionShape(CollisionView.this, mutable, shapeContext);
								if (voxelShape == VoxelShapes.fullCube()) {
									if (box.intersects((double)i, (double)j, (double)k, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)) {
										consumer.accept(voxelShape.offset((double)i, (double)j, (double)k));
										return true;
									}
								} else {
									VoxelShape voxelShape2 = voxelShape.offset((double)i, (double)j, (double)k);
									if (VoxelShapes.matchesAnywhere(voxelShape2, voxelShape, BooleanBiFunction.AND)) {
										consumer.accept(voxelShape2);
										return true;
									}
								}
							}
						}
					}
				}

				return false;
			}
		}, false);
	}

	static boolean method_27087(WorldBorder worldBorder, Box box) {
		double d = (double)MathHelper.floor(worldBorder.getBoundWest());
		double e = (double)MathHelper.floor(worldBorder.getBoundNorth());
		double f = (double)MathHelper.ceil(worldBorder.getBoundEast());
		double g = (double)MathHelper.ceil(worldBorder.getBoundSouth());
		return box.x1 > d && box.x1 < f && box.z1 > e && box.z1 < g && box.x2 > d && box.x2 < f && box.z2 > e && box.z2 < g;
	}
}
