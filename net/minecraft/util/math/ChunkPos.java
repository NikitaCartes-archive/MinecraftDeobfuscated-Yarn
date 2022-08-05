/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.Nullable;

/**
 * An immutable pair of two integers representing the X and Z coordinates of a chunk.
 * 
 * <p>Chunk positions are usually serialized as a {@code long}.
 */
public class ChunkPos {
    private static final int field_36299 = 1056;
    /**
     * A {@code long}-serialized chunk position {@code 1875066, 1875066}. This is a
     * special value used as a marker.
     */
    public static final long MARKER = ChunkPos.toLong(1875066, 1875066);
    /**
     * The origin of the chunk position, {@code 0, 0}.
     */
    public static final ChunkPos ORIGIN = new ChunkPos(0, 0);
    private static final long field_30953 = 32L;
    private static final long field_30954 = 0xFFFFFFFFL;
    private static final int field_30955 = 5;
    public static final int field_38224 = 32;
    private static final int field_30956 = 31;
    public static final int field_38225 = 31;
    public final int x;
    public final int z;
    private static final int field_30957 = 1664525;
    private static final int field_30958 = 1013904223;
    private static final int field_30959 = -559038737;

    public ChunkPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public ChunkPos(BlockPos pos) {
        this.x = ChunkSectionPos.getSectionCoord(pos.getX());
        this.z = ChunkSectionPos.getSectionCoord(pos.getZ());
    }

    public ChunkPos(long pos) {
        this.x = (int)pos;
        this.z = (int)(pos >> 32);
    }

    public static ChunkPos fromRegion(int x, int z) {
        return new ChunkPos(x << 5, z << 5);
    }

    public static ChunkPos fromRegionCenter(int x, int z) {
        return new ChunkPos((x << 5) + 31, (z << 5) + 31);
    }

    /**
     * {@return the chunk position serialized as {@code long}}
     * 
     * @see #toLong(int, int)
     */
    public long toLong() {
        return ChunkPos.toLong(this.x, this.z);
    }

    /**
     * {@return the chunk position serialized as {@code long}}
     * 
     * <p>This returns {@code chunkX | (chunkZ << 32)}.
     * 
     * @see #toLong()
     */
    public static long toLong(int chunkX, int chunkZ) {
        return (long)chunkX & 0xFFFFFFFFL | ((long)chunkZ & 0xFFFFFFFFL) << 32;
    }

    /**
     * {@return the chunk position of the given {@code pos} serialized as {@code long}}
     * 
     * @see #toLong(int, int)
     */
    public static long toLong(BlockPos pos) {
        return ChunkPos.toLong(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
    }

    public static int getPackedX(long pos) {
        return (int)(pos & 0xFFFFFFFFL);
    }

    public static int getPackedZ(long pos) {
        return (int)(pos >>> 32 & 0xFFFFFFFFL);
    }

    public int hashCode() {
        return ChunkPos.hashCode(this.x, this.z);
    }

    public static int hashCode(int x, int z) {
        int i = 1664525 * x + 1013904223;
        int j = 1664525 * (z ^ 0xDEADBEEF) + 1013904223;
        return i ^ j;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ChunkPos) {
            ChunkPos chunkPos = (ChunkPos)o;
            return this.x == chunkPos.x && this.z == chunkPos.z;
        }
        return false;
    }

    public int getCenterX() {
        return this.getOffsetX(8);
    }

    public int getCenterZ() {
        return this.getOffsetZ(8);
    }

    public int getStartX() {
        return ChunkSectionPos.getBlockCoord(this.x);
    }

    public int getStartZ() {
        return ChunkSectionPos.getBlockCoord(this.z);
    }

    public int getEndX() {
        return this.getOffsetX(15);
    }

    public int getEndZ() {
        return this.getOffsetZ(15);
    }

    public int getRegionX() {
        return this.x >> 5;
    }

    public int getRegionZ() {
        return this.z >> 5;
    }

    public int getRegionRelativeX() {
        return this.x & 0x1F;
    }

    public int getRegionRelativeZ() {
        return this.z & 0x1F;
    }

    public BlockPos getBlockPos(int offsetX, int y, int offsetZ) {
        return new BlockPos(this.getOffsetX(offsetX), y, this.getOffsetZ(offsetZ));
    }

    public int getOffsetX(int offset) {
        return ChunkSectionPos.getOffsetPos(this.x, offset);
    }

    public int getOffsetZ(int offset) {
        return ChunkSectionPos.getOffsetPos(this.z, offset);
    }

    public BlockPos getCenterAtY(int y) {
        return new BlockPos(this.getCenterX(), y, this.getCenterZ());
    }

    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    public BlockPos getStartPos() {
        return new BlockPos(this.getStartX(), 0, this.getStartZ());
    }

    public int getChebyshevDistance(ChunkPos pos) {
        return Math.max(Math.abs(this.x - pos.x), Math.abs(this.z - pos.z));
    }

    public static Stream<ChunkPos> stream(ChunkPos center, int radius) {
        return ChunkPos.stream(new ChunkPos(center.x - radius, center.z - radius), new ChunkPos(center.x + radius, center.z + radius));
    }

    public static Stream<ChunkPos> stream(final ChunkPos pos1, final ChunkPos pos2) {
        int i = Math.abs(pos1.x - pos2.x) + 1;
        int j = Math.abs(pos1.z - pos2.z) + 1;
        final int k = pos1.x < pos2.x ? 1 : -1;
        final int l = pos1.z < pos2.z ? 1 : -1;
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<ChunkPos>((long)(i * j), 64){
            @Nullable
            private ChunkPos position;

            @Override
            public boolean tryAdvance(Consumer<? super ChunkPos> consumer) {
                if (this.position == null) {
                    this.position = pos1;
                } else {
                    int i = this.position.x;
                    int j = this.position.z;
                    if (i == pos2.x) {
                        if (j == pos2.z) {
                            return false;
                        }
                        this.position = new ChunkPos(pos1.x, j + l);
                    } else {
                        this.position = new ChunkPos(i + k, j);
                    }
                }
                consumer.accept(this.position);
                return true;
            }
        }, false);
    }
}

