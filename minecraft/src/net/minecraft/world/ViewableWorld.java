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

public interface ViewableWorld extends ExtendedBlockView {
	default boolean isAir(BlockPos blockPos) {
		return this.getBlockState(blockPos).isAir();
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
					if (blockState.getLightSubtracted(this, var4) > 0 && !blockState.getMaterial().isLiquid()) {
						return false;
					}
				}

				return true;
			}
		}
	}

	int getLightLevel(BlockPos blockPos, int i);

	@Nullable
	Chunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl);

	@Deprecated
	boolean isChunkLoaded(int i, int j);

	BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos);

	int getTop(Heightmap.Type type, int i, int j);

	default float getBrightness(BlockPos blockPos) {
		return this.getDimension().getLightLevelToBrightness()[this.getLightLevel(blockPos)];
	}

	int getAmbientDarkness();

	WorldBorder getWorldBorder();

	boolean intersectsEntities(@Nullable Entity entity, VoxelShape voxelShape);

	default int getEmittedStrongRedstonePower(BlockPos blockPos, Direction direction) {
		return this.getBlockState(blockPos).getStrongRedstonePower(this, blockPos, direction);
	}

	boolean isClient();

	int getSeaLevel();

	default Chunk getChunk(BlockPos blockPos) {
		return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	default Chunk getChunk(int i, int j) {
		return this.getChunk(i, j, ChunkStatus.FULL, true);
	}

	default Chunk getChunk(int i, int j, ChunkStatus chunkStatus) {
		return this.getChunk(i, j, chunkStatus, true);
	}

	default ChunkStatus getLeastChunkStatusForCollisionCalculation() {
		return ChunkStatus.EMPTY;
	}

	default boolean canPlace(BlockState blockState, BlockPos blockPos, EntityContext entityContext) {
		VoxelShape voxelShape = blockState.getCollisionShape(this, blockPos, entityContext);
		return voxelShape.isEmpty() || this.intersectsEntities(null, voxelShape.offset((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
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

	default boolean doesNotCollide(@Nullable Entity entity, Box box, Set<Entity> set) {
		return this.getCollisionShapes(entity, box, set).allMatch(VoxelShape::isEmpty);
	}

	default Stream<VoxelShape> method_20743(@Nullable Entity entity, Box box, Set<Entity> set) {
		return Stream.empty();
	}

	default Stream<VoxelShape> getCollisionShapes(@Nullable Entity entity, Box box, Set<Entity> set) {
		return Streams.concat(this.method_20812(entity, box), this.method_20743(entity, box, set));
	}

	default Stream<VoxelShape> method_20812(@Nullable Entity entity, Box box) {
		int i = MathHelper.floor(box.minX - 1.0E-7) - 1;
		int j = MathHelper.floor(box.maxX + 1.0E-7) + 1;
		int k = MathHelper.floor(box.minY - 1.0E-7) - 1;
		int l = MathHelper.floor(box.maxY + 1.0E-7) + 1;
		int m = MathHelper.floor(box.minZ - 1.0E-7) - 1;
		int n = MathHelper.floor(box.maxZ + 1.0E-7) + 1;
		final EntityContext entityContext = entity == null ? EntityContext.absent() : EntityContext.of(entity);
		final CuboidBlockIterator cuboidBlockIterator = new CuboidBlockIterator(i, k, m, j, l, n);
		final BlockPos.Mutable mutable = new BlockPos.Mutable();
		final VoxelShape voxelShape = VoxelShapes.cuboid(box);
		return StreamSupport.stream(new AbstractSpliterator<VoxelShape>(Long.MAX_VALUE, 1280) {
			boolean field_19296 = entity == null;

			public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
				if (!this.field_19296) {
					this.field_19296 = true;
					VoxelShape voxelShape = ViewableWorld.this.getWorldBorder().asVoxelShape();
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
						Chunk chunk = ViewableWorld.this.getChunk(m, n, ViewableWorld.this.getLeastChunkStatusForCollisionCalculation(), false);
						if (chunk != null) {
							mutable.set(i, j, k);
							BlockState blockState = chunk.getBlockState(mutable);
							if ((l != 1 || blockState.method_17900()) && (l != 2 || blockState.getBlock() == Blocks.MOVING_PISTON)) {
								VoxelShape voxelShape2 = blockState.getCollisionShape(ViewableWorld.this, mutable, entityContext);
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

	default boolean isWaterAt(BlockPos blockPos) {
		return this.getFluidState(blockPos).matches(FluidTags.WATER);
	}

	default boolean intersectsFluid(Box box) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						BlockState blockState = this.getBlockState(pooledMutable.method_10113(o, p, q));
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

	default int method_8603(BlockPos blockPos, int i) {
		return blockPos.getX() >= -30000000 && blockPos.getZ() >= -30000000 && blockPos.getX() < 30000000 && blockPos.getZ() < 30000000
			? this.getLightLevel(blockPos, i)
			: 15;
	}

	@Deprecated
	default boolean isBlockLoaded(BlockPos blockPos) {
		return this.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	@Deprecated
	default boolean isAreaLoaded(BlockPos blockPos, BlockPos blockPos2) {
		return this.isAreaLoaded(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
	}

	@Deprecated
	default boolean isAreaLoaded(int i, int j, int k, int l, int m, int n) {
		if (m >= 0 && j < 256) {
			i >>= 4;
			k >>= 4;
			l >>= 4;
			n >>= 4;

			for (int o = i; o <= l; o++) {
				for (int p = k; p <= n; p++) {
					if (!this.isChunkLoaded(o, p)) {
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
