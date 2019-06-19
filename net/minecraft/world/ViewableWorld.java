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
import net.minecraft.util.math.Box;
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

    default public boolean doesNotCollide(Box box) {
        return this.doesNotCollide(null, box, Collections.emptySet());
    }

    default public boolean doesNotCollide(Entity entity) {
        return this.doesNotCollide(entity, entity.getBoundingBox(), Collections.emptySet());
    }

    default public boolean doesNotCollide(Entity entity, Box box) {
        return this.doesNotCollide(entity, box, Collections.emptySet());
    }

    default public boolean doesNotCollide(@Nullable Entity entity, Box box, Set<Entity> set) {
        return this.getCollisionShapes(entity, box, set).allMatch(VoxelShape::isEmpty);
    }

    default public Stream<VoxelShape> method_20743(@Nullable Entity entity, Box box, Set<Entity> set) {
        return Stream.empty();
    }

    default public Stream<VoxelShape> getCollisionShapes(@Nullable Entity entity, Box box, Set<Entity> set) {
        return Streams.concat(this.method_20812(entity, box), this.method_20743(entity, box, set));
    }

    default public Stream<VoxelShape> method_20812(final @Nullable Entity entity, Box box) {
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
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<VoxelShape>(Long.MAX_VALUE, 1280){
            boolean field_19296;
            {
                super(l, i);
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
                    int n;
                    int m;
                    Chunk chunk;
                    int i = cuboidBlockIterator.getX();
                    int j = cuboidBlockIterator.getY();
                    int k = cuboidBlockIterator.getZ();
                    int l = cuboidBlockIterator.method_20789();
                    if (l == 3 || (chunk = ViewableWorld.this.getChunk(m = i >> 4, n = k >> 4, ViewableWorld.this.getLeastChunkStatusForCollisionCalculation(), false)) == null) continue;
                    mutable.set(i, j, k);
                    BlockState blockState = chunk.getBlockState(mutable);
                    if (l == 1 && !blockState.method_17900() || l == 2 && blockState.getBlock() != Blocks.MOVING_PISTON || !VoxelShapes.matchesAnywhere(voxelShape, voxelShape3 = (voxelShape2 = blockState.getCollisionShape(ViewableWorld.this, mutable, entityContext)).offset(i, j, k), BooleanBiFunction.AND)) continue;
                    consumer.accept(voxelShape3);
                    return true;
                }
                return false;
            }
        }, false);
    }

    default public boolean isWaterAt(BlockPos blockPos) {
        return this.getFluidState(blockPos).matches(FluidTags.WATER);
    }

    default public boolean intersectsFluid(Box box) {
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minY);
        int l = MathHelper.ceil(box.maxY);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
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

