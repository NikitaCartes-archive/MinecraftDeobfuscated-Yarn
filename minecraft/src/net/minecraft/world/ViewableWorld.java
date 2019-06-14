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
		return this.method_8320(blockPos).isAir();
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
					BlockState blockState = this.method_8320(var4);
					if (blockState.getLightSubtracted(this, var4) > 0 && !blockState.method_11620().isLiquid()) {
						return false;
					}
				}

				return true;
			}
		}
	}

	int getLightLevel(BlockPos blockPos, int i);

	@Nullable
	Chunk method_8402(int i, int j, ChunkStatus chunkStatus, boolean bl);

	@Deprecated
	boolean isChunkLoaded(int i, int j);

	BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos);

	int getTop(Heightmap.Type type, int i, int j);

	default float getBrightness(BlockPos blockPos) {
		return this.method_8597().getLightLevelToBrightness()[this.getLightLevel(blockPos)];
	}

	int getAmbientDarkness();

	WorldBorder method_8621();

	boolean method_8611(@Nullable Entity entity, VoxelShape voxelShape);

	default int getEmittedStrongRedstonePower(BlockPos blockPos, Direction direction) {
		return this.method_8320(blockPos).getStrongRedstonePower(this, blockPos, direction);
	}

	boolean isClient();

	int getSeaLevel();

	default Chunk method_16955(BlockPos blockPos) {
		return this.method_8392(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	default Chunk method_8392(int i, int j) {
		return this.method_8402(i, j, ChunkStatus.field_12803, true);
	}

	default Chunk method_16956(int i, int j, ChunkStatus chunkStatus) {
		return this.method_8402(i, j, chunkStatus, true);
	}

	default ChunkStatus method_20311() {
		return ChunkStatus.field_12798;
	}

	default boolean method_8628(BlockState blockState, BlockPos blockPos, EntityContext entityContext) {
		VoxelShape voxelShape = blockState.method_16337(this, blockPos, entityContext);
		return voxelShape.isEmpty() || this.method_8611(null, voxelShape.offset((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
	}

	default boolean intersectsEntities(Entity entity) {
		return this.method_8611(entity, VoxelShapes.method_1078(entity.method_5829()));
	}

	default boolean method_18026(Box box) {
		return this.method_8590(null, box, Collections.emptySet());
	}

	default boolean doesNotCollide(Entity entity) {
		return this.method_8590(entity, entity.method_5829(), Collections.emptySet());
	}

	default boolean method_8587(Entity entity, Box box) {
		return this.method_8590(entity, box, Collections.emptySet());
	}

	default boolean method_8590(@Nullable Entity entity, Box box, Set<Entity> set) {
		return this.method_8600(entity, box, set).allMatch(VoxelShape::isEmpty);
	}

	default Stream<VoxelShape> method_20743(@Nullable Entity entity, Box box, Set<Entity> set) {
		return Stream.empty();
	}

	default Stream<VoxelShape> method_8600(@Nullable Entity entity, Box box, Set<Entity> set) {
		int i = MathHelper.floor(box.minX - 1.0E-7) - 1;
		int j = MathHelper.floor(box.maxX + 1.0E-7) + 1;
		int k = MathHelper.floor(box.minY - 1.0E-7) - 1;
		int l = MathHelper.floor(box.maxY + 1.0E-7) + 1;
		int m = MathHelper.floor(box.minZ - 1.0E-7) - 1;
		int n = MathHelper.floor(box.maxZ + 1.0E-7) + 1;
		final EntityContext entityContext = entity == null ? EntityContext.absent() : EntityContext.of(entity);
		final CuboidBlockIterator cuboidBlockIterator = new CuboidBlockIterator(i, k, m, j, l, n);
		final BlockPos.Mutable mutable = new BlockPos.Mutable();
		final VoxelShape voxelShape = VoxelShapes.method_1078(box);
		return Streams.concat(StreamSupport.stream(new AbstractSpliterator<VoxelShape>(Long.MAX_VALUE, 1280) {
			boolean field_19296 = entity == null;

			public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
				if (!this.field_19296) {
					this.field_19296 = true;
					VoxelShape voxelShape = ViewableWorld.this.method_8621().method_17903();
					boolean bl = VoxelShapes.method_1074(voxelShape, VoxelShapes.method_1078(entity.method_5829().contract(1.0E-7)), BooleanBiFunction.AND);
					boolean bl2 = VoxelShapes.method_1074(voxelShape, VoxelShapes.method_1078(entity.method_5829().expand(1.0E-7)), BooleanBiFunction.AND);
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
						Chunk chunk = ViewableWorld.this.method_8402(m, n, ViewableWorld.this.method_20311(), false);
						if (chunk != null) {
							mutable.set(i, j, k);
							BlockState blockState = chunk.method_8320(mutable);
							if ((l != 1 || blockState.method_17900()) && (l != 2 || blockState.getBlock() == Blocks.field_10008)) {
								VoxelShape voxelShape2 = blockState.method_16337(ViewableWorld.this, mutable, entityContext);
								VoxelShape voxelShape3 = voxelShape2.offset((double)i, (double)j, (double)k);
								if (VoxelShapes.method_1074(voxelShape, voxelShape3, BooleanBiFunction.AND)) {
									consumer.accept(voxelShape3);
									return true;
								}
							}
						}
					}
				}

				return false;
			}
		}, false), this.method_20743(entity, box, set));
	}

	default boolean isWaterAt(BlockPos blockPos) {
		return this.method_8316(blockPos).matches(FluidTags.field_15517);
	}

	default boolean method_8599(Box box) {
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
						BlockState blockState = this.method_8320(pooledMutable.method_10113(o, p, q));
						if (!blockState.method_11618().isEmpty()) {
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

	Dimension method_8597();
}
