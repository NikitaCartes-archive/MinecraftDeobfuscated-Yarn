package net.minecraft.world;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.AbstractVoxelShapeContainer;
import net.minecraft.util.shape.BitSetVoxelShapeContainer;
import net.minecraft.util.shape.OffsetVoxelShape;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;

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

	boolean isChunkLoaded(int i, int j);

	BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos);

	int getTop(Heightmap.Type type, int i, int j);

	default float method_8610(BlockPos blockPos) {
		return this.getDimension().method_12456()[this.method_8602(blockPos)];
	}

	@Nullable
	default PlayerEntity getClosestPlayer(Entity entity, double d) {
		return this.getClosestPlayer(entity.x, entity.y, entity.z, d, false);
	}

	@Nullable
	default PlayerEntity getClosestSurvivalPlayer(Entity entity, double d) {
		return this.getClosestPlayer(entity.x, entity.y, entity.z, d, true);
	}

	@Nullable
	default PlayerEntity getClosestPlayer(double d, double e, double f, double g, boolean bl) {
		Predicate<Entity> predicate = bl ? EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR : EntityPredicates.EXCEPT_SPECTATOR;
		return this.getClosestPlayer(d, e, f, g, predicate);
	}

	@Nullable
	PlayerEntity getClosestPlayer(double d, double e, double f, double g, Predicate<Entity> predicate);

	int getAmbientDarkness();

	WorldBorder getWorldBorder();

	boolean method_8611(@Nullable Entity entity, VoxelShape voxelShape);

	int getEmittedStrongRedstonePower(BlockPos blockPos, Direction direction);

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

	default boolean method_8628(BlockState blockState, BlockPos blockPos) {
		VoxelShape voxelShape = blockState.getCollisionShape(this, blockPos);
		return voxelShape.isEmpty() || this.method_8611(null, voxelShape.offset((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
	}

	default boolean method_8606(@Nullable Entity entity, BoundingBox boundingBox) {
		return this.method_8611(entity, VoxelShapes.cube(boundingBox));
	}

	default Stream<VoxelShape> method_8601(@Nullable Entity entity, VoxelShape voxelShape, VoxelShape voxelShape2, boolean bl) {
		int i = MathHelper.floor(voxelShape.getMinimum(Direction.Axis.X)) - 1;
		int j = MathHelper.ceil(voxelShape.getMaximum(Direction.Axis.X)) + 1;
		int k = MathHelper.floor(voxelShape.getMinimum(Direction.Axis.Y)) - 1;
		int l = MathHelper.ceil(voxelShape.getMaximum(Direction.Axis.Y)) + 1;
		int m = MathHelper.floor(voxelShape.getMinimum(Direction.Axis.Z)) - 1;
		int n = MathHelper.ceil(voxelShape.getMaximum(Direction.Axis.Z)) + 1;
		WorldBorder worldBorder = this.getWorldBorder();
		boolean bl2 = worldBorder.getBoundWest() < (double)i
			&& (double)j < worldBorder.getBoundEast()
			&& worldBorder.getBoundNorth() < (double)m
			&& (double)n < worldBorder.getBoundSouth();
		AbstractVoxelShapeContainer abstractVoxelShapeContainer = new BitSetVoxelShapeContainer(j - i, l - k, n - m);
		Predicate<VoxelShape> predicate = voxelShape2x -> !voxelShape2x.isEmpty() && VoxelShapes.compareShapes(voxelShape, voxelShape2x, BooleanBiFunction.AND);
		VerticalEntityPosition verticalEntityPosition = entity == null ? VerticalEntityPosition.minValue() : VerticalEntityPosition.fromEntity(entity);
		AtomicReference<ChunkPos> atomicReference = new AtomicReference(new ChunkPos(i >> 4, m >> 4));
		AtomicReference<Chunk> atomicReference2 = new AtomicReference(this.getChunk(i >> 4, m >> 4, ChunkStatus.EMPTY, false));
		Stream<VoxelShape> stream = StreamSupport.stream(BlockPos.iterateBoxPositions(i, k, m, j - 1, l - 1, n - 1).spliterator(), false).map(blockPos -> {
			int o = blockPos.getX();
			int p = blockPos.getY();
			int q = blockPos.getZ();
			if (World.isHeightInvalid(p)) {
				return VoxelShapes.empty();
			} else {
				boolean bl3 = o == i || o == j - 1;
				boolean bl4 = p == k || p == l - 1;
				boolean bl5 = q == m || q == n - 1;
				ChunkPos chunkPos = (ChunkPos)atomicReference.get();
				int r = o >> 4;
				int s = q >> 4;
				Chunk chunk;
				if (chunkPos.x == r && chunkPos.z == s) {
					chunk = (Chunk)atomicReference2.get();
				} else {
					chunk = this.getChunk(r, s, ChunkStatus.EMPTY, false);
					atomicReference.set(new ChunkPos(r, s));
					atomicReference2.set(chunk);
				}

				if ((!bl3 || !bl4) && (!bl4 || !bl5) && (!bl5 || !bl3) && chunk != null) {
					VoxelShape voxelShape2x;
					if (bl && !bl2 && !worldBorder.contains(blockPos)) {
						voxelShape2x = VoxelShapes.fullCube();
					} else {
						voxelShape2x = chunk.getBlockState(blockPos).getCollisionShape(this, blockPos, verticalEntityPosition);
					}

					VoxelShape voxelShape3 = voxelShape2.offset((double)(-o), (double)(-p), (double)(-q));
					if (VoxelShapes.compareShapes(voxelShape3, voxelShape2x, BooleanBiFunction.AND)) {
						return VoxelShapes.empty();
					} else if (voxelShape2x == VoxelShapes.fullCube()) {
						abstractVoxelShapeContainer.modify(o - i, p - k, q - m, true, true);
						return VoxelShapes.empty();
					} else {
						return voxelShape2x.offset((double)o, (double)p, (double)q);
					}
				} else {
					return VoxelShapes.empty();
				}
			}
		}).filter(predicate);
		return Stream.concat(stream, Stream.generate(() -> new OffsetVoxelShape(abstractVoxelShapeContainer, i, k, m)).limit(1L).filter(predicate));
	}

	default Stream<VoxelShape> getCollisionVoxelShapes(@Nullable Entity entity, BoundingBox boundingBox, double d, double e, double f) {
		return this.getCollisionVoxelShapes(entity, boundingBox, Collections.emptySet(), d, e, f);
	}

	default Stream<VoxelShape> getCollisionVoxelShapes(@Nullable Entity entity, BoundingBox boundingBox, Set<Entity> set, double d, double e, double f) {
		double g = 1.0E-7;
		VoxelShape voxelShape = VoxelShapes.cube(boundingBox);
		VoxelShape voxelShape2 = VoxelShapes.cube(boundingBox.offset(d > 0.0 ? -1.0E-7 : 1.0E-7, e > 0.0 ? -1.0E-7 : 1.0E-7, f > 0.0 ? -1.0E-7 : 1.0E-7));
		VoxelShape voxelShape3 = VoxelShapes.combine(VoxelShapes.cube(boundingBox.stretch(d, e, f).expand(1.0E-7)), voxelShape2, BooleanBiFunction.ONLY_FIRST);
		return this.method_8600(entity, voxelShape3, voxelShape, set);
	}

	default Stream<VoxelShape> method_8607(@Nullable Entity entity, BoundingBox boundingBox) {
		return this.method_8600(entity, VoxelShapes.cube(boundingBox), VoxelShapes.empty(), Collections.emptySet());
	}

	default Stream<VoxelShape> method_8600(@Nullable Entity entity, VoxelShape voxelShape, VoxelShape voxelShape2, Set<Entity> set) {
		boolean bl = entity != null && entity.method_5686();
		boolean bl2 = entity != null && this.method_8625(entity);
		if (entity != null && bl == bl2) {
			entity.method_5789(!bl2);
		}

		return this.method_8601(entity, voxelShape, voxelShape2, bl2);
	}

	default boolean method_8625(Entity entity) {
		WorldBorder worldBorder = this.getWorldBorder();
		double d = worldBorder.getBoundWest();
		double e = worldBorder.getBoundNorth();
		double f = worldBorder.getBoundEast();
		double g = worldBorder.getBoundSouth();
		if (entity.method_5686()) {
			d++;
			e++;
			f--;
			g--;
		} else {
			d--;
			e--;
			f++;
			g++;
		}

		return entity.x > d && entity.x < f && entity.z > e && entity.z < g;
	}

	default boolean method_8590(@Nullable Entity entity, BoundingBox boundingBox, Set<Entity> set) {
		return this.method_8600(entity, VoxelShapes.cube(boundingBox), VoxelShapes.empty(), set).allMatch(VoxelShape::isEmpty);
	}

	default boolean method_8587(@Nullable Entity entity, BoundingBox boundingBox) {
		return this.method_8590(entity, boundingBox, Collections.emptySet());
	}

	default boolean isWaterAt(BlockPos blockPos) {
		return this.getFluidState(blockPos).matches(FluidTags.field_15517);
	}

	default boolean method_8599(BoundingBox boundingBox) {
		int i = MathHelper.floor(boundingBox.minX);
		int j = MathHelper.ceil(boundingBox.maxX);
		int k = MathHelper.floor(boundingBox.minY);
		int l = MathHelper.ceil(boundingBox.maxY);
		int m = MathHelper.floor(boundingBox.minZ);
		int n = MathHelper.ceil(boundingBox.maxZ);

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

	default int method_8602(BlockPos blockPos) {
		return this.method_8603(blockPos, this.getAmbientDarkness());
	}

	default int method_8603(BlockPos blockPos, int i) {
		if (blockPos.getX() < -30000000 || blockPos.getZ() < -30000000 || blockPos.getX() >= 30000000 || blockPos.getZ() >= 30000000) {
			return 15;
		} else if (this.getBlockState(blockPos).usesNeighborLightValues(this, blockPos)) {
			int j = this.getLightLevel(blockPos.up(), i);
			int k = this.getLightLevel(blockPos.east(), i);
			int l = this.getLightLevel(blockPos.west(), i);
			int m = this.getLightLevel(blockPos.south(), i);
			int n = this.getLightLevel(blockPos.north(), i);
			if (k > j) {
				j = k;
			}

			if (l > j) {
				j = l;
			}

			if (m > j) {
				j = m;
			}

			if (n > j) {
				j = n;
			}

			return j;
		} else {
			return this.getLightLevel(blockPos, i);
		}
	}

	default boolean isBlockLoaded(BlockPos blockPos) {
		return this.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	default boolean isAreaLoaded(BlockPos blockPos, BlockPos blockPos2) {
		return this.isAreaLoaded(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
	}

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
