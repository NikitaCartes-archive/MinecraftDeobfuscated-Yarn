package net.minecraft.world;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.OffsetVoxelShape;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
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

	default ChunkStatus getLeastChunkStatusForCollisionCalculation() {
		return ChunkStatus.EMPTY;
	}

	default boolean canPlace(BlockState blockState, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		VoxelShape voxelShape = blockState.getCollisionShape(this, blockPos, verticalEntityPosition);
		return voxelShape.isEmpty() || this.intersectsEntities(null, voxelShape.offset((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
	}

	default boolean intersectsEntities(Entity entity) {
		return this.intersectsEntities(entity, VoxelShapes.cuboid(entity.getBoundingBox()));
	}

	default boolean doesNotCollide(BoundingBox boundingBox) {
		return this.getCollisionShapes(null, boundingBox, Collections.emptySet()).allMatch(VoxelShape::isEmpty);
	}

	default boolean doesNotCollide(Entity entity) {
		return this.getCollisionShapes(entity, entity.getBoundingBox(), Collections.emptySet()).allMatch(VoxelShape::isEmpty);
	}

	default boolean doesNotCollide(Entity entity, BoundingBox boundingBox) {
		return this.getCollisionShapes(entity, boundingBox, Collections.emptySet()).allMatch(VoxelShape::isEmpty);
	}

	default boolean doesNotCollide(Entity entity, BoundingBox boundingBox, Set<Entity> set) {
		return this.getCollisionShapes(entity, boundingBox, set).allMatch(VoxelShape::isEmpty);
	}

	default Stream<VoxelShape> getCollisionShapes(@Nullable Entity entity, VoxelShape voxelShape, Set<Entity> set) {
		return Stream.empty();
	}

	default Stream<VoxelShape> getCollisionShapes(@Nullable Entity entity, BoundingBox boundingBox, Set<Entity> set) {
		VoxelShape voxelShape = VoxelShapes.cuboid(boundingBox);
		Stream<VoxelShape> stream;
		VerticalEntityPosition verticalEntityPosition;
		if (entity == null) {
			verticalEntityPosition = VerticalEntityPosition.minValue();
			stream = Stream.empty();
		} else {
			verticalEntityPosition = VerticalEntityPosition.fromEntity(entity);
			VoxelShape voxelShape2 = this.getWorldBorder().asVoxelShape();
			boolean bl = VoxelShapes.matchesAnywhere(voxelShape2, VoxelShapes.cuboid(entity.getBoundingBox().contract(1.0E-7)), BooleanBiFunction.AND);
			boolean bl2 = VoxelShapes.matchesAnywhere(voxelShape2, VoxelShapes.cuboid(entity.getBoundingBox().expand(1.0E-7)), BooleanBiFunction.AND);
			if (!bl && bl2) {
				stream = Stream.concat(Stream.of(voxelShape2), this.getCollisionShapes(entity, voxelShape, set));
			} else {
				stream = this.getCollisionShapes(entity, voxelShape, set);
			}
		}

		int i = MathHelper.floor(voxelShape.getMinimum(Direction.Axis.X)) - 1;
		int j = MathHelper.ceil(voxelShape.getMaximum(Direction.Axis.X)) + 1;
		int k = MathHelper.floor(voxelShape.getMinimum(Direction.Axis.Y)) - 1;
		int l = MathHelper.ceil(voxelShape.getMaximum(Direction.Axis.Y)) + 1;
		int m = MathHelper.floor(voxelShape.getMinimum(Direction.Axis.Z)) - 1;
		int n = MathHelper.ceil(voxelShape.getMaximum(Direction.Axis.Z)) + 1;
		VoxelSet voxelSet = new BitSetVoxelSet(j - i, l - k, n - m);
		Predicate<VoxelShape> predicate = voxelShape2x -> !voxelShape2x.isEmpty() && VoxelShapes.matchesAnywhere(voxelShape, voxelShape2x, BooleanBiFunction.AND);
		AtomicReference<ChunkPos> atomicReference = new AtomicReference(new ChunkPos(i >> 4, m >> 4));
		AtomicReference<Chunk> atomicReference2 = new AtomicReference(this.getChunk(i >> 4, m >> 4, this.getLeastChunkStatusForCollisionCalculation(), false));
		Stream<VoxelShape> stream2 = BlockPos.stream(i, k, m, j - 1, l - 1, n - 1).map(blockPos -> {
			int o = blockPos.getX();
			int p = blockPos.getY();
			int q = blockPos.getZ();
			if (World.isHeightInvalid(p)) {
				return VoxelShapes.empty();
			} else {
				boolean blx = o == i || o == j - 1;
				boolean bl2x = p == k || p == l - 1;
				boolean bl3 = q == m || q == n - 1;
				ChunkPos chunkPos = (ChunkPos)atomicReference.get();
				int r = o >> 4;
				int s = q >> 4;
				Chunk chunk;
				if (chunkPos.x == r && chunkPos.z == s) {
					chunk = (Chunk)atomicReference2.get();
				} else {
					chunk = this.getChunk(r, s, this.getLeastChunkStatusForCollisionCalculation(), false);
					atomicReference.set(new ChunkPos(r, s));
					atomicReference2.set(chunk);
				}

				if ((!blx || !bl2x) && (!bl2x || !bl3) && (!bl3 || !blx) && chunk != null) {
					VoxelShape voxelShapex = chunk.getBlockState(blockPos).getCollisionShape(this, blockPos, verticalEntityPosition);
					VoxelShape voxelShape2x = VoxelShapes.empty().offset((double)(-o), (double)(-p), (double)(-q));
					if (VoxelShapes.matchesAnywhere(voxelShape2x, voxelShapex, BooleanBiFunction.AND)) {
						return VoxelShapes.empty();
					} else if (voxelShapex == VoxelShapes.fullCube()) {
						voxelSet.set(o - i, p - k, q - m, true, true);
						return VoxelShapes.empty();
					} else {
						return voxelShapex.offset((double)o, (double)p, (double)q);
					}
				} else {
					return VoxelShapes.empty();
				}
			}
		});
		return Stream.concat(stream, Stream.concat(stream2, Stream.generate(() -> new OffsetVoxelShape(voxelSet, i, k, m)).limit(1L)).filter(predicate));
	}

	default boolean isWaterAt(BlockPos blockPos) {
		return this.getFluidState(blockPos).matches(FluidTags.field_15517);
	}

	default boolean intersectsFluid(BoundingBox boundingBox) {
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
