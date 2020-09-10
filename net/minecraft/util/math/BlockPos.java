/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Unmodifiable;

/**
 * Represents the position of a block in a three-dimensional volume.
 * 
 * <p>The position is integer-valued.
 * 
 * <p>A block position may be mutable; hence, when using block positions
 * obtained from other places as map keys, etc., you should call {@link
 * #toImmutable()} to obtain an immutable block position.
 */
@Unmodifiable
public class BlockPos
extends Vec3i {
    public static final Codec<BlockPos> CODEC = Codec.INT_STREAM.comapFlatMap(stream -> Util.toIntArray(stream, 3).map(values -> new BlockPos(values[0], values[1], values[2])), pos -> IntStream.of(pos.getX(), pos.getY(), pos.getZ())).stable();
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * The block position which x, y, and z values are all zero.
     */
    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
    private static final int SIZE_BITS_X;
    private static final int SIZE_BITS_Z;
    private static final int SIZE_BITS_Y;
    private static final long BITS_X;
    private static final long BITS_Y;
    private static final long BITS_Z;
    private static final int BIT_SHIFT_Z;
    private static final int BIT_SHIFT_X;

    public BlockPos(int i, int j, int k) {
        super(i, j, k);
    }

    public BlockPos(double d, double e, double f) {
        super(d, e, f);
    }

    public BlockPos(Vec3d pos) {
        this(pos.x, pos.y, pos.z);
    }

    public BlockPos(Position pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos(Vec3i pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public static long offset(long value, Direction direction) {
        return BlockPos.add(value, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    public static long add(long value, int x, int y, int z) {
        return BlockPos.asLong(BlockPos.unpackLongX(value) + x, BlockPos.unpackLongY(value) + y, BlockPos.unpackLongZ(value) + z);
    }

    public static int unpackLongX(long packedPos) {
        return (int)(packedPos << 64 - BIT_SHIFT_X - SIZE_BITS_X >> 64 - SIZE_BITS_X);
    }

    public static int unpackLongY(long packedPos) {
        return (int)(packedPos << 64 - SIZE_BITS_Y >> 64 - SIZE_BITS_Y);
    }

    public static int unpackLongZ(long packedPos) {
        return (int)(packedPos << 64 - BIT_SHIFT_Z - SIZE_BITS_Z >> 64 - SIZE_BITS_Z);
    }

    public static BlockPos fromLong(long packedPos) {
        return new BlockPos(BlockPos.unpackLongX(packedPos), BlockPos.unpackLongY(packedPos), BlockPos.unpackLongZ(packedPos));
    }

    public long asLong() {
        return BlockPos.asLong(this.getX(), this.getY(), this.getZ());
    }

    public static long asLong(int x, int y, int z) {
        long l = 0L;
        l |= ((long)x & BITS_X) << BIT_SHIFT_X;
        l |= ((long)y & BITS_Y) << 0;
        return l |= ((long)z & BITS_Z) << BIT_SHIFT_Z;
    }

    public static long removeChunkSectionLocalY(long y) {
        return y & 0xFFFFFFFFFFFFFFF0L;
    }

    public BlockPos add(double x, double y, double z) {
        if (x == 0.0 && y == 0.0 && z == 0.0) {
            return this;
        }
        return new BlockPos((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z);
    }

    public BlockPos add(int x, int y, int z) {
        if (x == 0 && y == 0 && z == 0) {
            return this;
        }
        return new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public BlockPos add(Vec3i pos) {
        return this.add(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos subtract(Vec3i pos) {
        return this.add(-pos.getX(), -pos.getY(), -pos.getZ());
    }

    @Override
    public BlockPos up() {
        return this.offset(Direction.UP);
    }

    @Override
    public BlockPos up(int distance) {
        return this.offset(Direction.UP, distance);
    }

    @Override
    public BlockPos down() {
        return this.offset(Direction.DOWN);
    }

    @Override
    public BlockPos down(int i) {
        return this.offset(Direction.DOWN, i);
    }

    public BlockPos north() {
        return this.offset(Direction.NORTH);
    }

    public BlockPos north(int distance) {
        return this.offset(Direction.NORTH, distance);
    }

    public BlockPos south() {
        return this.offset(Direction.SOUTH);
    }

    public BlockPos south(int distance) {
        return this.offset(Direction.SOUTH, distance);
    }

    public BlockPos west() {
        return this.offset(Direction.WEST);
    }

    public BlockPos west(int distance) {
        return this.offset(Direction.WEST, distance);
    }

    public BlockPos east() {
        return this.offset(Direction.EAST);
    }

    public BlockPos east(int distance) {
        return this.offset(Direction.EAST, distance);
    }

    public BlockPos offset(Direction direction) {
        return new BlockPos(this.getX() + direction.getOffsetX(), this.getY() + direction.getOffsetY(), this.getZ() + direction.getOffsetZ());
    }

    @Override
    public BlockPos offset(Direction direction, int i) {
        if (i == 0) {
            return this;
        }
        return new BlockPos(this.getX() + direction.getOffsetX() * i, this.getY() + direction.getOffsetY() * i, this.getZ() + direction.getOffsetZ() * i);
    }

    public BlockPos offset(Direction.Axis axis, int distance) {
        if (distance == 0) {
            return this;
        }
        int i = axis == Direction.Axis.X ? distance : 0;
        int j = axis == Direction.Axis.Y ? distance : 0;
        int k = axis == Direction.Axis.Z ? distance : 0;
        return new BlockPos(this.getX() + i, this.getY() + j, this.getZ() + k);
    }

    public BlockPos rotate(BlockRotation rotation) {
        switch (rotation) {
            default: {
                return this;
            }
            case CLOCKWISE_90: {
                return new BlockPos(-this.getZ(), this.getY(), this.getX());
            }
            case CLOCKWISE_180: {
                return new BlockPos(-this.getX(), this.getY(), -this.getZ());
            }
            case COUNTERCLOCKWISE_90: 
        }
        return new BlockPos(this.getZ(), this.getY(), -this.getX());
    }

    @Override
    public BlockPos crossProduct(Vec3i pos) {
        return new BlockPos(this.getY() * pos.getZ() - this.getZ() * pos.getY(), this.getZ() * pos.getX() - this.getX() * pos.getZ(), this.getX() * pos.getY() - this.getY() * pos.getX());
    }

    /**
     * Returns an immutable block position with the same x, y, and z as this
     * position.
     * 
     * <p>This method should be called when a block position is used as map
     * keys as to prevent side effects of mutations of mutable block positions.
     */
    public BlockPos toImmutable() {
        return this;
    }

    /**
     * Returns a mutable copy of this block position.
     * 
     * <p>If this block position is a mutable one, mutation to this block
     * position won't affect the returned position.
     */
    public Mutable mutableCopy() {
        return new Mutable(this.getX(), this.getY(), this.getZ());
    }

    /**
     * Iterates through {@code count} random block positions in the given area.
     * 
     * <p>The iterator yields positions in no specific order. The same position
     * may be returned multiple times by the iterator.
     * 
     * @param random the {@link Random} object used to compute new positions
     * @param count the number of positions to iterate
     * @param minX the minimum x value for returned positions
     * @param minY the minimum y value for returned positions
     * @param minZ the minimum z value for returned positions
     * @param maxX the maximum x value for returned positions
     * @param maxY the maximum y value for returned positions
     * @param maxZ the maximum z value for returned positions
     */
    public static Iterable<BlockPos> iterateRandomly(final Random random, final int count, final int minX, final int minY, final int minZ, int maxX, int maxY, int maxZ) {
        final int i = maxX - minX + 1;
        final int j = maxY - minY + 1;
        final int k = maxZ - minZ + 1;
        return () -> new AbstractIterator<BlockPos>(){
            final Mutable pos = new Mutable();
            int remaining = count;

            @Override
            protected BlockPos computeNext() {
                if (this.remaining <= 0) {
                    return (BlockPos)this.endOfData();
                }
                Mutable blockPos = this.pos.set(minX + random.nextInt(i), minY + random.nextInt(j), minZ + random.nextInt(k));
                --this.remaining;
                return blockPos;
            }

            @Override
            protected /* synthetic */ Object computeNext() {
                return this.computeNext();
            }
        };
    }

    /**
     * Iterates block positions around the {@code center}. The iteration order
     * is mainly based on the manhattan distance of the position from the
     * center.
     * 
     * <p>For the same manhattan distance, the positions are iterated by y
     * offset, from negative to positive. For the same y offset, the positions
     * are iterated by x offset, from negative to positive. For the two
     * positions with the same x and y offsets and the same manhattan distance,
     * the one with a positive z offset is visited first before the one with a
     * negative z offset.
     * 
     * @param center the center of iteration
     * @param xRange the maximum x difference from the center
     * @param yRange the maximum y difference from the center
     * @param zRange the maximum z difference from the center
     */
    public static Iterable<BlockPos> iterateOutwards(BlockPos center, final int xRange, final int yRange, final int zRange) {
        final int i = xRange + yRange + zRange;
        final int j = center.getX();
        final int k = center.getY();
        final int l = center.getZ();
        return () -> new AbstractIterator<BlockPos>(){
            private final Mutable pos = new Mutable();
            private int manhattanDistance;
            private int limitX;
            private int limitY;
            private int dx;
            private int dy;
            private boolean field_23379;

            @Override
            protected BlockPos computeNext() {
                if (this.field_23379) {
                    this.field_23379 = false;
                    this.pos.setZ(l - (this.pos.getZ() - l));
                    return this.pos;
                }
                Mutable blockPos = null;
                while (blockPos == null) {
                    if (this.dy > this.limitY) {
                        ++this.dx;
                        if (this.dx > this.limitX) {
                            ++this.manhattanDistance;
                            if (this.manhattanDistance > i) {
                                return (BlockPos)this.endOfData();
                            }
                            this.limitX = Math.min(xRange, this.manhattanDistance);
                            this.dx = -this.limitX;
                        }
                        this.limitY = Math.min(yRange, this.manhattanDistance - Math.abs(this.dx));
                        this.dy = -this.limitY;
                    }
                    int i2 = this.dx;
                    int j2 = this.dy;
                    int k2 = this.manhattanDistance - Math.abs(i2) - Math.abs(j2);
                    if (k2 <= zRange) {
                        this.field_23379 = k2 != 0;
                        blockPos = this.pos.set(j + i2, k + j2, l + k2);
                    }
                    ++this.dy;
                }
                return blockPos;
            }

            @Override
            protected /* synthetic */ Object computeNext() {
                return this.computeNext();
            }
        };
    }

    public static Optional<BlockPos> findClosest(BlockPos pos, int horizontalRange, int verticalRange, Predicate<BlockPos> condition) {
        return BlockPos.streamOutwards(pos, horizontalRange, verticalRange, horizontalRange).filter(condition).findFirst();
    }

    public static Stream<BlockPos> streamOutwards(BlockPos center, int maxX, int maxY, int maxZ) {
        return StreamSupport.stream(BlockPos.iterateOutwards(center, maxX, maxY, maxZ).spliterator(), false);
    }

    public static Iterable<BlockPos> iterate(BlockPos start, BlockPos end) {
        return BlockPos.iterate(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()), Math.min(start.getZ(), end.getZ()), Math.max(start.getX(), end.getX()), Math.max(start.getY(), end.getY()), Math.max(start.getZ(), end.getZ()));
    }

    public static Stream<BlockPos> stream(BlockPos start, BlockPos end) {
        return StreamSupport.stream(BlockPos.iterate(start, end).spliterator(), false);
    }

    public static Stream<BlockPos> stream(BlockBox box) {
        return BlockPos.stream(Math.min(box.minX, box.maxX), Math.min(box.minY, box.maxY), Math.min(box.minZ, box.maxZ), Math.max(box.minX, box.maxX), Math.max(box.minY, box.maxY), Math.max(box.minZ, box.maxZ));
    }

    public static Stream<BlockPos> stream(Box box) {
        return BlockPos.stream(MathHelper.floor(box.minX), MathHelper.floor(box.minY), MathHelper.floor(box.minZ), MathHelper.floor(box.maxX), MathHelper.floor(box.maxY), MathHelper.floor(box.maxZ));
    }

    public static Stream<BlockPos> stream(int startX, int startY, int startZ, int endX, int endY, int endZ) {
        return StreamSupport.stream(BlockPos.iterate(startX, startY, startZ, endX, endY, endZ).spliterator(), false);
    }

    public static Iterable<BlockPos> iterate(final int startX, final int startY, final int startZ, int endX, int endY, int endZ) {
        final int i = endX - startX + 1;
        final int j = endY - startY + 1;
        int k = endZ - startZ + 1;
        final int l = i * j * k;
        return () -> new AbstractIterator<BlockPos>(){
            private final Mutable pos = new Mutable();
            private int index;

            @Override
            protected BlockPos computeNext() {
                if (this.index == l) {
                    return (BlockPos)this.endOfData();
                }
                int i2 = this.index % i;
                int j2 = this.index / i;
                int k = j2 % j;
                int l2 = j2 / j;
                ++this.index;
                return this.pos.set(startX + i2, startY + k, startZ + l2);
            }

            @Override
            protected /* synthetic */ Object computeNext() {
                return this.computeNext();
            }
        };
    }

    public static Iterable<Mutable> method_30512(final BlockPos blockPos, final int i, final Direction direction, final Direction direction2) {
        Validate.validState(direction.getAxis() != direction2.getAxis(), "The two directions cannot be on the same axis", new Object[0]);
        return () -> new AbstractIterator<Mutable>(){
            private final Direction[] directions;
            private final Mutable pos;
            private final int field_25905;
            private int field_25906;
            private int field_25907;
            private int field_25908;
            private int field_25909;
            private int field_25910;
            private int field_25911;
            {
                this.directions = new Direction[]{direction, direction2, direction.getOpposite(), direction2.getOpposite()};
                this.pos = blockPos.mutableCopy().move(direction2);
                this.field_25905 = 4 * i;
                this.field_25906 = -1;
                this.field_25909 = this.pos.getX();
                this.field_25910 = this.pos.getY();
                this.field_25911 = this.pos.getZ();
            }

            @Override
            protected Mutable computeNext() {
                this.pos.set(this.field_25909, this.field_25910, this.field_25911).move(this.directions[(this.field_25906 + 4) % 4]);
                this.field_25909 = this.pos.getX();
                this.field_25910 = this.pos.getY();
                this.field_25911 = this.pos.getZ();
                if (this.field_25908 >= this.field_25907) {
                    if (this.field_25906 >= this.field_25905) {
                        return (Mutable)this.endOfData();
                    }
                    ++this.field_25906;
                    this.field_25908 = 0;
                    this.field_25907 = this.field_25906 / 2 + 1;
                }
                ++this.field_25908;
                return this.pos;
            }

            @Override
            protected /* synthetic */ Object computeNext() {
                return this.computeNext();
            }
        };
    }

    @Override
    public /* synthetic */ Vec3i crossProduct(Vec3i vec) {
        return this.crossProduct(vec);
    }

    @Override
    public /* synthetic */ Vec3i offset(Direction direction, int distance) {
        return this.offset(direction, distance);
    }

    @Override
    public /* synthetic */ Vec3i down(int distance) {
        return this.down(distance);
    }

    @Override
    public /* synthetic */ Vec3i down() {
        return this.down();
    }

    @Override
    public /* synthetic */ Vec3i up(int distance) {
        return this.up(distance);
    }

    @Override
    public /* synthetic */ Vec3i up() {
        return this.up();
    }

    static {
        SIZE_BITS_Z = SIZE_BITS_X = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
        SIZE_BITS_Y = 64 - SIZE_BITS_X - SIZE_BITS_Z;
        BITS_X = (1L << SIZE_BITS_X) - 1L;
        BITS_Y = (1L << SIZE_BITS_Y) - 1L;
        BITS_Z = (1L << SIZE_BITS_Z) - 1L;
        BIT_SHIFT_Z = SIZE_BITS_Y;
        BIT_SHIFT_X = SIZE_BITS_Y + SIZE_BITS_Z;
    }

    public static class Mutable
    extends BlockPos {
        public Mutable() {
            this(0, 0, 0);
        }

        public Mutable(int i, int j, int k) {
            super(i, j, k);
        }

        public Mutable(double d, double e, double f) {
            this(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
        }

        @Override
        public BlockPos add(double x, double y, double z) {
            return super.add(x, y, z).toImmutable();
        }

        @Override
        public BlockPos add(int x, int y, int z) {
            return super.add(x, y, z).toImmutable();
        }

        @Override
        public BlockPos offset(Direction direction, int i) {
            return super.offset(direction, i).toImmutable();
        }

        @Override
        public BlockPos offset(Direction.Axis axis, int distance) {
            return super.offset(axis, distance).toImmutable();
        }

        @Override
        public BlockPos rotate(BlockRotation rotation) {
            return super.rotate(rotation).toImmutable();
        }

        public Mutable set(int x, int y, int z) {
            this.setX(x);
            this.setY(y);
            this.setZ(z);
            return this;
        }

        public Mutable set(double x, double y, double z) {
            return this.set(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
        }

        public Mutable set(Vec3i pos) {
            return this.set(pos.getX(), pos.getY(), pos.getZ());
        }

        public Mutable set(long pos) {
            return this.set(Mutable.unpackLongX(pos), Mutable.unpackLongY(pos), Mutable.unpackLongZ(pos));
        }

        public Mutable set(AxisCycleDirection axis, int x, int y, int z) {
            return this.set(axis.choose(x, y, z, Direction.Axis.X), axis.choose(x, y, z, Direction.Axis.Y), axis.choose(x, y, z, Direction.Axis.Z));
        }

        public Mutable set(Vec3i pos, Direction direction) {
            return this.set(pos.getX() + direction.getOffsetX(), pos.getY() + direction.getOffsetY(), pos.getZ() + direction.getOffsetZ());
        }

        public Mutable set(Vec3i pos, int x, int y, int z) {
            return this.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
        }

        public Mutable move(Direction direction) {
            return this.move(direction, 1);
        }

        public Mutable move(Direction direction, int distance) {
            return this.set(this.getX() + direction.getOffsetX() * distance, this.getY() + direction.getOffsetY() * distance, this.getZ() + direction.getOffsetZ() * distance);
        }

        public Mutable move(int dx, int dy, int dz) {
            return this.set(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
        }

        public Mutable move(Vec3i vec) {
            return this.set(this.getX() + vec.getX(), this.getY() + vec.getY(), this.getZ() + vec.getZ());
        }

        public Mutable clamp(Direction.Axis axis, int min, int max) {
            switch (axis) {
                case X: {
                    return this.set(MathHelper.clamp(this.getX(), min, max), this.getY(), this.getZ());
                }
                case Y: {
                    return this.set(this.getX(), MathHelper.clamp(this.getY(), min, max), this.getZ());
                }
                case Z: {
                    return this.set(this.getX(), this.getY(), MathHelper.clamp(this.getZ(), min, max));
                }
            }
            throw new IllegalStateException("Unable to clamp axis " + axis);
        }

        @Override
        public void setX(int x) {
            super.setX(x);
        }

        @Override
        public void setY(int y) {
            super.setY(y);
        }

        @Override
        public void setZ(int z) {
            super.setZ(z);
        }

        @Override
        public BlockPos toImmutable() {
            return new BlockPos(this);
        }

        @Override
        public /* synthetic */ Vec3i crossProduct(Vec3i vec) {
            return super.crossProduct(vec);
        }

        @Override
        public /* synthetic */ Vec3i offset(Direction direction, int distance) {
            return this.offset(direction, distance);
        }

        @Override
        public /* synthetic */ Vec3i down(int distance) {
            return super.down(distance);
        }

        @Override
        public /* synthetic */ Vec3i down() {
            return super.down();
        }

        @Override
        public /* synthetic */ Vec3i up(int distance) {
            return super.up(distance);
        }

        @Override
        public /* synthetic */ Vec3i up() {
            return super.up();
        }
    }
}

