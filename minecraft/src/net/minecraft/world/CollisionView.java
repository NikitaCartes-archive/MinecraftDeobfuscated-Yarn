package net.minecraft.world;

import com.google.common.collect.Streams;
import java.util.Collections;
import java.util.Set;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.Dimension;

public interface CollisionView extends BlockRenderView {
	default boolean isAir(BlockPos pos) {
		return this.getBlockState(pos).isAir();
	}

	default boolean method_8626(BlockPos blockPos) {
		if (blockPos.getY() >= this.getSeaLevel()) {
			return this.isSkyVisible(blockPos);
		} else {
			BlockPos blockPos2 = new BlockPos(blockPos.getX(), this.getSeaLevel(), blockPos.getZ());
			if (!this.isSkyVisible(blockPos2)) {
				return false;
			} else {
				for (BlockPos var4 = blockPos2.down(); var4.getY() > blockPos.getY(); var4 = var4.down()) {
					BlockState blockState = this.getBlockState(var4);
					if (blockState.getOpacity(this, var4) > 0 && !blockState.getMaterial().isLiquid()) {
						return false;
					}
				}

				return true;
			}
		}
	}

	int getLightLevel(BlockPos blockPos, int i);

	@Nullable
	Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);

	@Deprecated
	boolean isChunkLoaded(int chunkX, int chunkZ);

	BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos);

	int getTop(Heightmap.Type type, int x, int z);

	default float getBrightness(BlockPos blockPos) {
		return this.getDimension().getLightLevelToBrightness()[this.getLightLevel(blockPos)];
	}

	int getAmbientDarkness();

	WorldBorder getWorldBorder();

	boolean intersectsEntities(@Nullable Entity except, VoxelShape shape);

	default int getEmittedStrongRedstonePower(BlockPos pos, Direction direction) {
		return this.getBlockState(pos).getStrongRedstonePower(this, pos, direction);
	}

	boolean isClient();

	int getSeaLevel();

	default Chunk getChunk(BlockPos pos) {
		return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}

	default Chunk getChunk(int chunkX, int chunkZ) {
		return this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
	}

	default Chunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredState) {
		return this.getChunk(chunkX, chunkZ, requiredState, true);
	}

	default ChunkStatus getLeastChunkStatusForCollisionCalculation() {
		return ChunkStatus.EMPTY;
	}

	default boolean canPlace(BlockState state, BlockPos pos, EntityContext context) {
		VoxelShape voxelShape = state.getCollisionShape(this, pos, context);
		return voxelShape.isEmpty() || this.intersectsEntities(null, voxelShape.offset((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()));
	}

	default boolean intersectsEntities(Entity entity) {
		return this.intersectsEntities(entity, VoxelShapes.cuboid(entity.getBoundingBox()));
	}

	default boolean doesNotCollide(Box box) {
		return this.doesNotCollide(null, box, Collections.emptySet());
	}

	default boolean doesNotCollide(Entity entity) {
		return this.doesNotCollide(entity, entity.getBoundingBox(), Collections.emptySet());
	}

	default boolean doesNotCollide(Entity entity, Box box) {
		return this.doesNotCollide(entity, box, Collections.emptySet());
	}

	default boolean doesNotCollide(@Nullable Entity entity, Box entityBoundingBox, Set<Entity> otherEntities) {
		return this.getCollisions(entity, entityBoundingBox, otherEntities).allMatch(VoxelShape::isEmpty);
	}

	default Stream<VoxelShape> method_20743(@Nullable Entity entity, Box box, Set<Entity> set) {
		return Stream.empty();
	}

	default Stream<VoxelShape> getCollisions(@Nullable Entity entity, Box box, Set<Entity> excluded) {
		return Streams.concat(this.method_20812(entity, box), this.method_20743(entity, box, excluded));
	}

	default Stream<VoxelShape> method_20812(@Nullable Entity entity, Box box) {
		int i = MathHelper.floor(box.x1 - 1.0E-7) - 1;
		int j = MathHelper.floor(box.x2 + 1.0E-7) + 1;
		int k = MathHelper.floor(box.y1 - 1.0E-7) - 1;
		int l = MathHelper.floor(box.y2 + 1.0E-7) + 1;
		int m = MathHelper.floor(box.z1 - 1.0E-7) - 1;
		int n = MathHelper.floor(box.z2 + 1.0E-7) + 1;
		final EntityContext entityContext = entity == null ? EntityContext.absent() : EntityContext.of(entity);
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
					int l = cuboidBlockIterator.method_20789();
					if (l != 3) {
						int m = i >> 4;
						int n = k >> 4;
						Chunk chunk = CollisionView.this.getChunk(m, n, CollisionView.this.getLeastChunkStatusForCollisionCalculation(), false);
						if (chunk != null) {
							mutable.set(i, j, k);
							BlockState blockState = chunk.getBlockState(mutable);
							if ((l != 1 || blockState.method_17900()) && (l != 2 || blockState.getBlock() == Blocks.MOVING_PISTON)) {
								VoxelShape voxelShape2 = blockState.getCollisionShape(CollisionView.this, mutable, entityContext);
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

	default boolean isWaterAt(BlockPos pos) {
		return this.getFluidState(pos).matches(FluidTags.WATER);
	}

	default boolean intersectsFluid(Box box) {
		int i = MathHelper.floor(box.x1);
		int j = MathHelper.ceil(box.x2);
		int k = MathHelper.floor(box.y1);
		int l = MathHelper.ceil(box.y2);
		int m = MathHelper.floor(box.z1);
		int n = MathHelper.ceil(box.z2);

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						BlockState blockState = this.getBlockState(pooledMutable.set(o, p, q));
						if (!blockState.getFluidState().isEmpty()) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	default int getLightLevel(BlockPos blockPos) {
		return this.method_8603(blockPos, this.getAmbientDarkness());
	}

	default int method_8603(BlockPos blockPos, int darkness) {
		return blockPos.getX() >= -30000000 && blockPos.getZ() >= -30000000 && blockPos.getX() < 30000000 && blockPos.getZ() < 30000000
			? this.getLightLevel(blockPos, darkness)
			: 15;
	}

	@Deprecated
	default boolean isBlockLoaded(BlockPos blockPos) {
		return this.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	@Deprecated
	default boolean isAreaLoaded(BlockPos min, BlockPos max) {
		return this.isAreaLoaded(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
	}

	@Deprecated
	default boolean isAreaLoaded(int minX, int minY, int minZ, int maxX, int i, int j) {
		if (i >= 0 && minY < 256) {
			minX >>= 4;
			minZ >>= 4;
			maxX >>= 4;
			j >>= 4;

			for (int k = minX; k <= maxX; k++) {
				for (int l = minZ; l <= j; l++) {
					if (!this.isChunkLoaded(k, l)) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	Dimension getDimension();
}
