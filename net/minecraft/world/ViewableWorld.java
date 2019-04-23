/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.OffsetVoxelShape;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
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

    public int getEmittedStrongRedstonePower(BlockPos var1, Direction var2);

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
        return this.getCollisionShapes(null, boundingBox, Collections.emptySet()).allMatch(VoxelShape::isEmpty);
    }

    default public boolean doesNotCollide(Entity entity) {
        return this.getCollisionShapes(entity, entity.getBoundingBox(), Collections.emptySet()).allMatch(VoxelShape::isEmpty);
    }

    default public boolean doesNotCollide(Entity entity, BoundingBox boundingBox) {
        return this.getCollisionShapes(entity, boundingBox, Collections.emptySet()).allMatch(VoxelShape::isEmpty);
    }

    default public boolean doesNotCollide(Entity entity, BoundingBox boundingBox, Set<Entity> set) {
        return this.getCollisionShapes(entity, boundingBox, set).allMatch(VoxelShape::isEmpty);
    }

    default public Stream<VoxelShape> getCollisionShapes(@Nullable Entity entity, VoxelShape voxelShape, Set<Entity> set) {
        return Stream.empty();
    }

    default public Stream<VoxelShape> getCollisionShapes(@Nullable Entity entity, BoundingBox boundingBox, Set<Entity> set) {
        Stream<Object> stream;
        EntityContext entityContext;
        VoxelShape voxelShape = VoxelShapes.cuboid(boundingBox);
        if (entity == null) {
            entityContext = EntityContext.absent();
            stream = Stream.empty();
        } else {
            entityContext = EntityContext.of(entity);
            VoxelShape voxelShape22 = this.getWorldBorder().asVoxelShape();
            boolean bl = VoxelShapes.matchesAnywhere(voxelShape22, VoxelShapes.cuboid(entity.getBoundingBox().contract(1.0E-7)), BooleanBiFunction.AND);
            boolean bl2 = VoxelShapes.matchesAnywhere(voxelShape22, VoxelShapes.cuboid(entity.getBoundingBox().expand(1.0E-7)), BooleanBiFunction.AND);
            stream = !bl && bl2 ? Stream.concat(Stream.of(voxelShape22), this.getCollisionShapes(entity, voxelShape, set)) : this.getCollisionShapes(entity, voxelShape, set);
        }
        int i = MathHelper.floor(voxelShape.getMinimum(Direction.Axis.X)) - 1;
        int j = MathHelper.ceil(voxelShape.getMaximum(Direction.Axis.X)) + 1;
        int k = MathHelper.floor(voxelShape.getMinimum(Direction.Axis.Y)) - 1;
        int l = MathHelper.ceil(voxelShape.getMaximum(Direction.Axis.Y)) + 1;
        int m = MathHelper.floor(voxelShape.getMinimum(Direction.Axis.Z)) - 1;
        int n = MathHelper.ceil(voxelShape.getMaximum(Direction.Axis.Z)) + 1;
        BitSetVoxelSet voxelSet = new BitSetVoxelSet(j - i, l - k, n - m);
        Predicate<VoxelShape> predicate = voxelShape2 -> !voxelShape2.isEmpty() && VoxelShapes.matchesAnywhere(voxelShape, voxelShape2, BooleanBiFunction.AND);
        AtomicReference<ChunkPos> atomicReference = new AtomicReference<ChunkPos>(new ChunkPos(i >> 4, m >> 4));
        AtomicReference<Chunk> atomicReference2 = new AtomicReference<Chunk>(this.getChunk(i >> 4, m >> 4, this.getLeastChunkStatusForCollisionCalculation(), false));
        Stream<VoxelShape> stream2 = BlockPos.stream(i, k, m, j - 1, l - 1, n - 1).map(blockPos -> {
            Chunk chunk;
            int o = blockPos.getX();
            int p = blockPos.getY();
            int q = blockPos.getZ();
            if (World.isHeightInvalid(p)) {
                return VoxelShapes.empty();
            }
            boolean bl = o == i || o == j - 1;
            boolean bl2 = p == k || p == l - 1;
            boolean bl3 = q == m || q == n - 1;
            ChunkPos chunkPos = (ChunkPos)atomicReference.get();
            int r = o >> 4;
            int s = q >> 4;
            if (chunkPos.x != r || chunkPos.z != s) {
                chunk = this.getChunk(r, s, this.getLeastChunkStatusForCollisionCalculation(), false);
                atomicReference.set(new ChunkPos(r, s));
                atomicReference2.set(chunk);
            } else {
                chunk = (Chunk)atomicReference2.get();
            }
            if (bl && bl2 || bl2 && bl3 || bl3 && bl || chunk == null) {
                return VoxelShapes.empty();
            }
            VoxelShape voxelShape = chunk.getBlockState((BlockPos)blockPos).getCollisionShape(this, (BlockPos)blockPos, entityContext);
            VoxelShape voxelShape2 = VoxelShapes.empty().offset(-o, -p, -q);
            if (VoxelShapes.matchesAnywhere(voxelShape2, voxelShape, BooleanBiFunction.AND)) {
                return VoxelShapes.empty();
            }
            if (voxelShape == VoxelShapes.fullCube()) {
                voxelSet.set(o - i, p - k, q - m, true, true);
                return VoxelShapes.empty();
            }
            return voxelShape.offset(o, p, q);
        });
        return Stream.concat(stream, Stream.concat(stream2, Stream.generate(() -> new OffsetVoxelShape(voxelSet, i, k, m)).limit(1L)).filter(predicate));
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

