/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Streams;
import java.util.Collections;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.Dimension;
import org.jetbrains.annotations.Nullable;

public interface ViewableWorld
extends ExtendedBlockView {
    default public boolean isAir(BlockPos blockPos) {
        return this.getBlockState(blockPos).isAir();
    }

    default public boolean method_8626(BlockPos blockPos) {
        if (blockPos.getY() >= this.getSeaLevel()) {
            return this.isSkyVisible(blockPos);
        }
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), this.getSeaLevel(), blockPos.getZ());
        if (!this.isSkyVisible(blockPos2)) {
            return false;
        }
        blockPos2 = blockPos2.down();
        while (blockPos2.getY() > blockPos.getY()) {
            BlockState blockState = this.getBlockState(blockPos2);
            if (blockState.getLightSubtracted(this, blockPos2) > 0 && !blockState.getMaterial().isLiquid()) {
                return false;
            }
            blockPos2 = blockPos2.down();
        }
        return true;
    }

    public int getLightLevel(BlockPos var1, int var2);

    @Nullable
    public Chunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    @Deprecated
    public boolean isChunkLoaded(int var1, int var2);

    public BlockPos getTopPosition(Heightmap.Type var1, BlockPos var2);

    public int getTop(Heightmap.Type var1, int var2, int var3);

    default public float getBrightness(BlockPos blockPos) {
        return this.getDimension().getLightLevelToBrightness()[this.getLightLevel(blockPos)];
    }

    public int getAmbientDarkness();

    public WorldBorder getWorldBorder();

    public boolean intersectsEntities(@Nullable Entity var1, VoxelShape var2);

    default public int getEmittedStrongRedstonePower(BlockPos blockPos, Direction direction) {
        return this.getBlockState(blockPos).getStrongRedstonePower(this, blockPos, direction);
    }

    public boolean isClient();

    public int getSeaLevel();

    default public Chunk getChunk(BlockPos blockPos) {
        return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    default public Chunk getChunk(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.FULL, true);
    }

    default public Chunk getChunk(int i, int j, ChunkStatus chunkStatus) {
        return this.getChunk(i, j, chunkStatus, true);
    }

    default public ChunkStatus getLeastChunkStatusForCollisionCalculation() {
        return ChunkStatus.EMPTY;
    }

    default public boolean canPlace(BlockState blockState, BlockPos blockPos, EntityContext entityContext) {
        VoxelShape voxelShape = blockState.getCollisionShape(this, blockPos, entityContext);
        return voxelShape.isEmpty() || this.intersectsEntities(null, voxelShape.offset(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
    }

    default public boolean intersectsEntities(Entity entity) {
        return this.intersectsEntities(entity, VoxelShapes.cuboid(entity.getBoundingBox()));
    }

    default public boolean doesNotCollide(BoundingBox boundingBox) {
        return this.doesNotCollide(null, boundingBox, Collections.emptySet());
    }

    default public boolean doesNotCollide(Entity entity) {
        return this.doesNotCollide(entity, entity.getBoundingBox(), Collections.emptySet());
    }

    default public boolean doesNotCollide(Entity entity, BoundingBox boundingBox) {
        return this.doesNotCollide(entity, boundingBox, Collections.emptySet());
    }

    default public boolean doesNotCollide(@Nullable Entity entity, BoundingBox boundingBox, Set<Entity> set) {
        return this.getCollisionShapes(entity, boundingBox, set).allMatch(VoxelShape::isEmpty);
    }

    default public Stream<VoxelShape> getCollisionShapes(@Nullable Entity entity, VoxelShape voxelShape, Set<Entity> set) {
        return Stream.empty();
    }

    default public Stream<VoxelShape> getCollisionShapes(final @Nullable Entity entity, BoundingBox boundingBox, Set<Entity> set) {
        final VoxelShape voxelShape = VoxelShapes.cuboid(boundingBox);
        final int i = MathHelper.floor(voxelShape.getMinimum(Direction.Axis.X) - 1.0E-7) - 1;
        final int j = MathHelper.floor(voxelShape.getMaximum(Direction.Axis.X) + 1.0E-7) + 1;
        final int k = MathHelper.floor(voxelShape.getMinimum(Direction.Axis.Y) - 1.0E-7) - 1;
        final int l = MathHelper.floor(voxelShape.getMaximum(Direction.Axis.Y) + 1.0E-7) + 1;
        final int m = MathHelper.floor(voxelShape.getMinimum(Direction.Axis.Z) - 1.0E-7) - 1;
        final int n = MathHelper.floor(voxelShape.getMaximum(Direction.Axis.Z) + 1.0E-7) + 1;
        final EntityContext entityContext = entity == null ? EntityContext.absent() : EntityContext.of(entity);
        final CuboidBlockIterator cuboidBlockIterator = new CuboidBlockIterator(i, k, m, j, l, n);
        final BlockPos.Mutable mutable = new BlockPos.Mutable();
        return Streams.concat(StreamSupport.stream(new Spliterators.AbstractSpliterator<VoxelShape>(Long.MAX_VALUE, 0){
            boolean field_19296;
            {
                super(l2, i2);
                this.field_19296 = entity == null;
            }

            @Override
            public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
                if (!this.field_19296) {
                    this.field_19296 = true;
                    VoxelShape voxelShape4 = ViewableWorld.this.getWorldBorder().asVoxelShape();
                    boolean bl = VoxelShapes.matchesAnywhere(voxelShape4, VoxelShapes.cuboid(entity.getBoundingBox().contract(1.0E-7)), BooleanBiFunction.AND);
                    boolean bl2 = VoxelShapes.matchesAnywhere(voxelShape4, VoxelShapes.cuboid(entity.getBoundingBox().expand(1.0E-7)), BooleanBiFunction.AND);
                    if (!bl && bl2) {
                        consumer.accept(voxelShape4);
                        return true;
                    }
                }
                while (cuboidBlockIterator.step()) {
                    VoxelShape voxelShape2;
                    VoxelShape voxelShape3;
                    int n2;
                    int m2;
                    Chunk chunk;
                    int i2 = cuboidBlockIterator.getX();
                    int j2 = cuboidBlockIterator.getY();
                    int k2 = cuboidBlockIterator.getZ();
                    int l2 = 0;
                    if (i2 == i || i2 == j) {
                        ++l2;
                    }
                    if (j2 == k || j2 == l) {
                        ++l2;
                    }
                    if (k2 == m || k2 == n) {
                        ++l2;
                    }
                    if (l2 >= 3 || (chunk = ViewableWorld.this.getChunk(m2 = i2 >> 4, n2 = k2 >> 4, ViewableWorld.this.getLeastChunkStatusForCollisionCalculation(), false)) == null) continue;
                    mutable.set(i2, j2, k2);
                    BlockState blockState = chunk.getBlockState(mutable);
                    if (l2 == 1 && !blockState.method_17900() || l2 == 2 && blockState.getBlock() != Blocks.MOVING_PISTON || !VoxelShapes.matchesAnywhere(voxelShape, voxelShape3 = (voxelShape2 = ViewableWorld.this.getBlockState(mutable).getCollisionShape(ViewableWorld.this, mutable, entityContext)).offset(i2, j2, k2), BooleanBiFunction.AND)) continue;
                    consumer.accept(voxelShape3);
                    return true;
                }
                return false;
            }
        }, false), this.getCollisionShapes(entity, voxelShape, set));
    }

    default public boolean isWaterAt(BlockPos blockPos) {
        return this.getFluidState(blockPos).matches(FluidTags.WATER);
    }

    default public boolean intersectsFluid(BoundingBox boundingBox) {
        int i = MathHelper.floor(boundingBox.minX);
        int j = MathHelper.ceil(boundingBox.maxX);
        int k = MathHelper.floor(boundingBox.minY);
        int l = MathHelper.ceil(boundingBox.maxY);
        int m = MathHelper.floor(boundingBox.minZ);
        int n = MathHelper.ceil(boundingBox.maxZ);
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (int o = i; o < j; ++o) {
                for (int p = k; p < l; ++p) {
                    for (int q = m; q < n; ++q) {
                        BlockState blockState = this.getBlockState(pooledMutable.method_10113(o, p, q));
                        if (blockState.getFluidState().isEmpty()) continue;
                        boolean bl = true;
                        return bl;
                    }
                }
            }
        }
        return false;
    }

    default public int getLightLevel(BlockPos blockPos) {
        return this.method_8603(blockPos, this.getAmbientDarkness());
    }

    default public int method_8603(BlockPos blockPos, int i) {
        if (blockPos.getX() < -30000000 || blockPos.getZ() < -30000000 || blockPos.getX() >= 30000000 || blockPos.getZ() >= 30000000) {
            return 15;
        }
        return this.getLightLevel(blockPos, i);
    }

    @Deprecated
    default public boolean isBlockLoaded(BlockPos blockPos) {
        return this.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    @Deprecated
    default public boolean isAreaLoaded(BlockPos blockPos, BlockPos blockPos2) {
        return this.isAreaLoaded(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
    }

    @Deprecated
    default public boolean isAreaLoaded(int i, int j, int k, int l, int m, int n) {
        if (m < 0 || j >= 256) {
            return false;
        }
        k >>= 4;
        l >>= 4;
        n >>= 4;
        for (int o = i >>= 4; o <= l; ++o) {
            for (int p = k; p <= n; ++p) {
                if (this.isChunkLoaded(o, p)) continue;
                return false;
            }
        }
        return true;
    }

    public Dimension getDimension();
}

