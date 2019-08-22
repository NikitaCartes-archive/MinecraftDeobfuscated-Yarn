/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.entity.Entity;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class ChunkSectionPos
extends Vec3i {
    private ChunkSectionPos(int i, int j, int k) {
        super(i, j, k);
    }

    public static ChunkSectionPos from(int i, int j, int k) {
        return new ChunkSectionPos(i, j, k);
    }

    public static ChunkSectionPos from(BlockPos blockPos) {
        return new ChunkSectionPos(ChunkSectionPos.toChunkCoord(blockPos.getX()), ChunkSectionPos.toChunkCoord(blockPos.getY()), ChunkSectionPos.toChunkCoord(blockPos.getZ()));
    }

    public static ChunkSectionPos from(ChunkPos chunkPos, int i) {
        return new ChunkSectionPos(chunkPos.x, i, chunkPos.z);
    }

    public static ChunkSectionPos from(Entity entity) {
        return new ChunkSectionPos(ChunkSectionPos.toChunkCoord(MathHelper.floor(entity.x)), ChunkSectionPos.toChunkCoord(MathHelper.floor(entity.y)), ChunkSectionPos.toChunkCoord(MathHelper.floor(entity.z)));
    }

    public static ChunkSectionPos from(long l) {
        return new ChunkSectionPos(ChunkSectionPos.unpackLongX(l), ChunkSectionPos.unpackLongY(l), ChunkSectionPos.unpackLongZ(l));
    }

    public static long offsetPacked(long l, Direction direction) {
        return ChunkSectionPos.offsetPacked(l, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    public static long offsetPacked(long l, int i, int j, int k) {
        return ChunkSectionPos.asLong(ChunkSectionPos.unpackLongX(l) + i, ChunkSectionPos.unpackLongY(l) + j, ChunkSectionPos.unpackLongZ(l) + k);
    }

    public static int toChunkCoord(int i) {
        return i >> 4;
    }

    public static int toLocalCoord(int i) {
        return i & 0xF;
    }

    public static short packToShort(BlockPos blockPos) {
        int i = ChunkSectionPos.toLocalCoord(blockPos.getX());
        int j = ChunkSectionPos.toLocalCoord(blockPos.getY());
        int k = ChunkSectionPos.toLocalCoord(blockPos.getZ());
        return (short)(i << 8 | k << 4 | j);
    }

    public static int fromChunkCoord(int i) {
        return i << 4;
    }

    public static int unpackLongX(long l) {
        return (int)(l << 0 >> 42);
    }

    public static int unpackLongY(long l) {
        return (int)(l << 44 >> 44);
    }

    public static int unpackLongZ(long l) {
        return (int)(l << 22 >> 42);
    }

    public int getChunkX() {
        return this.getX();
    }

    public int getChunkY() {
        return this.getY();
    }

    public int getChunkZ() {
        return this.getZ();
    }

    public int getMinX() {
        return this.getChunkX() << 4;
    }

    public int getMinY() {
        return this.getChunkY() << 4;
    }

    public int getMinZ() {
        return this.getChunkZ() << 4;
    }

    public int getMaxX() {
        return (this.getChunkX() << 4) + 15;
    }

    public int getMaxY() {
        return (this.getChunkY() << 4) + 15;
    }

    public int getMaxZ() {
        return (this.getChunkZ() << 4) + 15;
    }

    public static long toChunkLong(long l) {
        return ChunkSectionPos.asLong(ChunkSectionPos.toChunkCoord(BlockPos.unpackLongX(l)), ChunkSectionPos.toChunkCoord(BlockPos.unpackLongY(l)), ChunkSectionPos.toChunkCoord(BlockPos.unpackLongZ(l)));
    }

    public static long toLightStorageIndex(long l) {
        return l & 0xFFFFFFFFFFF00000L;
    }

    public BlockPos getMinPos() {
        return new BlockPos(ChunkSectionPos.fromChunkCoord(this.getChunkX()), ChunkSectionPos.fromChunkCoord(this.getChunkY()), ChunkSectionPos.fromChunkCoord(this.getChunkZ()));
    }

    public BlockPos getCenterPos() {
        int i = 8;
        return this.getMinPos().add(8, 8, 8);
    }

    public ChunkPos toChunkPos() {
        return new ChunkPos(this.getChunkX(), this.getChunkZ());
    }

    public static long asLong(int i, int j, int k) {
        long l = 0L;
        l |= ((long)i & 0x3FFFFFL) << 42;
        l |= ((long)j & 0xFFFFFL) << 0;
        return l |= ((long)k & 0x3FFFFFL) << 20;
    }

    public long asLong() {
        return ChunkSectionPos.asLong(this.getChunkX(), this.getChunkY(), this.getChunkZ());
    }

    public Stream<BlockPos> streamBlocks() {
        return BlockPos.stream(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ());
    }

    public static Stream<ChunkSectionPos> stream(ChunkSectionPos chunkSectionPos, int i) {
        int j = chunkSectionPos.getChunkX();
        int k = chunkSectionPos.getChunkY();
        int l = chunkSectionPos.getChunkZ();
        return ChunkSectionPos.stream(j - i, k - i, l - i, j + i, k + i, l + i);
    }

    public static Stream<ChunkSectionPos> stream(final int i, final int j, final int k, final int l, final int m, final int n) {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<ChunkSectionPos>((long)((l - i + 1) * (m - j + 1) * (n - k + 1)), 64){
            final CuboidBlockIterator iterator;
            {
                super(l2, i2);
                this.iterator = new CuboidBlockIterator(i, j, k, l, m, n);
            }

            @Override
            public boolean tryAdvance(Consumer<? super ChunkSectionPos> consumer) {
                if (this.iterator.step()) {
                    consumer.accept(new ChunkSectionPos(this.iterator.getX(), this.iterator.getY(), this.iterator.getZ()));
                    return true;
                }
                return false;
            }
        }, false);
    }
}

