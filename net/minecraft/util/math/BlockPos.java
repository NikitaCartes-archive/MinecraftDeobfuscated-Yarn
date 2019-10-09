/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class BlockPos
extends Vec3i
implements DynamicSerializable {
    private static final Logger LOGGER = LogManager.getLogger();
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

    public BlockPos(Entity entity) {
        this(entity.getX(), entity.getY(), entity.getZ());
    }

    public BlockPos(Vec3d vec3d) {
        this(vec3d.x, vec3d.y, vec3d.z);
    }

    public BlockPos(Position position) {
        this(position.getX(), position.getY(), position.getZ());
    }

    public BlockPos(Vec3i vec3i) {
        this(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    public static <T> BlockPos deserialize(Dynamic<T> dynamic) {
        int[] is;
        Spliterator.OfInt ofInt = dynamic.asIntStream().spliterator();
        if (ofInt.tryAdvance(arg_0 -> BlockPos.method_19441(is = new int[3], arg_0)) && ofInt.tryAdvance(i -> {
            is[1] = i;
        })) {
            ofInt.tryAdvance(i -> {
                is[2] = i;
            });
        }
        return new BlockPos(is[0], is[1], is[2]);
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return dynamicOps.createIntList(IntStream.of(this.getX(), this.getY(), this.getZ()));
    }

    public static long offset(long l, Direction direction) {
        return BlockPos.add(l, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    public static long add(long l, int i, int j, int k) {
        return BlockPos.asLong(BlockPos.unpackLongX(l) + i, BlockPos.unpackLongY(l) + j, BlockPos.unpackLongZ(l) + k);
    }

    public static int unpackLongX(long l) {
        return (int)(l << 64 - BIT_SHIFT_X - SIZE_BITS_X >> 64 - SIZE_BITS_X);
    }

    public static int unpackLongY(long l) {
        return (int)(l << 64 - SIZE_BITS_Y >> 64 - SIZE_BITS_Y);
    }

    public static int unpackLongZ(long l) {
        return (int)(l << 64 - BIT_SHIFT_Z - SIZE_BITS_Z >> 64 - SIZE_BITS_Z);
    }

    public static BlockPos fromLong(long l) {
        return new BlockPos(BlockPos.unpackLongX(l), BlockPos.unpackLongY(l), BlockPos.unpackLongZ(l));
    }

    public static long asLong(int i, int j, int k) {
        long l = 0L;
        l |= ((long)i & BITS_X) << BIT_SHIFT_X;
        l |= ((long)j & BITS_Y) << 0;
        return l |= ((long)k & BITS_Z) << BIT_SHIFT_Z;
    }

    public static long removeChunkSectionLocalY(long l) {
        return l & 0xFFFFFFFFFFFFFFF0L;
    }

    public long asLong() {
        return BlockPos.asLong(this.getX(), this.getY(), this.getZ());
    }

    public BlockPos add(double d, double e, double f) {
        if (d == 0.0 && e == 0.0 && f == 0.0) {
            return this;
        }
        return new BlockPos((double)this.getX() + d, (double)this.getY() + e, (double)this.getZ() + f);
    }

    public BlockPos add(int i, int j, int k) {
        if (i == 0 && j == 0 && k == 0) {
            return this;
        }
        return new BlockPos(this.getX() + i, this.getY() + j, this.getZ() + k);
    }

    public BlockPos add(Vec3i vec3i) {
        return this.add(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    public BlockPos subtract(Vec3i vec3i) {
        return this.add(-vec3i.getX(), -vec3i.getY(), -vec3i.getZ());
    }

    public BlockPos up() {
        return this.up(1);
    }

    public BlockPos up(int i) {
        return this.method_10079(Direction.UP, i);
    }

    public BlockPos method_10074() {
        return this.down(1);
    }

    public BlockPos down(int i) {
        return this.method_10079(Direction.DOWN, i);
    }

    public BlockPos north() {
        return this.north(1);
    }

    public BlockPos north(int i) {
        return this.method_10079(Direction.NORTH, i);
    }

    public BlockPos south() {
        return this.south(1);
    }

    public BlockPos south(int i) {
        return this.method_10079(Direction.SOUTH, i);
    }

    public BlockPos west() {
        return this.west(1);
    }

    public BlockPos west(int i) {
        return this.method_10079(Direction.WEST, i);
    }

    public BlockPos east() {
        return this.east(1);
    }

    public BlockPos east(int i) {
        return this.method_10079(Direction.EAST, i);
    }

    public BlockPos offset(Direction direction) {
        return this.method_10079(direction, 1);
    }

    public BlockPos method_10079(Direction direction, int i) {
        if (i == 0) {
            return this;
        }
        return new BlockPos(this.getX() + direction.getOffsetX() * i, this.getY() + direction.getOffsetY() * i, this.getZ() + direction.getOffsetZ() * i);
    }

    public BlockPos rotate(BlockRotation blockRotation) {
        switch (blockRotation) {
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
    public BlockPos crossProduct(Vec3i vec3i) {
        return new BlockPos(this.getY() * vec3i.getZ() - this.getZ() * vec3i.getY(), this.getZ() * vec3i.getX() - this.getX() * vec3i.getZ(), this.getX() * vec3i.getY() - this.getY() * vec3i.getX());
    }

    public BlockPos toImmutable() {
        return this;
    }

    public static Iterable<BlockPos> iterate(BlockPos blockPos, BlockPos blockPos2) {
        return BlockPos.iterate(Math.min(blockPos.getX(), blockPos2.getX()), Math.min(blockPos.getY(), blockPos2.getY()), Math.min(blockPos.getZ(), blockPos2.getZ()), Math.max(blockPos.getX(), blockPos2.getX()), Math.max(blockPos.getY(), blockPos2.getY()), Math.max(blockPos.getZ(), blockPos2.getZ()));
    }

    public static Stream<BlockPos> stream(BlockPos blockPos, BlockPos blockPos2) {
        return BlockPos.stream(Math.min(blockPos.getX(), blockPos2.getX()), Math.min(blockPos.getY(), blockPos2.getY()), Math.min(blockPos.getZ(), blockPos2.getZ()), Math.max(blockPos.getX(), blockPos2.getX()), Math.max(blockPos.getY(), blockPos2.getY()), Math.max(blockPos.getZ(), blockPos2.getZ()));
    }

    public static Stream<BlockPos> method_23627(BlockBox blockBox) {
        return BlockPos.stream(Math.min(blockBox.minX, blockBox.maxX), Math.min(blockBox.minY, blockBox.maxY), Math.min(blockBox.minZ, blockBox.maxZ), Math.max(blockBox.minX, blockBox.maxX), Math.max(blockBox.minY, blockBox.maxY), Math.max(blockBox.minZ, blockBox.maxZ));
    }

    public static Stream<BlockPos> stream(final int i, final int j, final int k, final int l, final int m, final int n) {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<BlockPos>((long)((l - i + 1) * (m - j + 1) * (n - k + 1)), 64){
            final CuboidBlockIterator connector;
            final Mutable position;
            {
                super(l2, i2);
                this.connector = new CuboidBlockIterator(i, j, k, l, m, n);
                this.position = new Mutable();
            }

            @Override
            public boolean tryAdvance(Consumer<? super BlockPos> consumer) {
                if (this.connector.step()) {
                    consumer.accept(this.position.set(this.connector.getX(), this.connector.getY(), this.connector.getZ()));
                    return true;
                }
                return false;
            }
        }, false);
    }

    public static Iterable<BlockPos> iterate(final int i, final int j, final int k, final int l, final int m, final int n) {
        return () -> new AbstractIterator<BlockPos>(){
            final CuboidBlockIterator iterator;
            final Mutable pos;
            {
                this.iterator = new CuboidBlockIterator(i, j, k, l, m, n);
                this.pos = new Mutable();
            }

            protected BlockPos method_10106() {
                return this.iterator.step() ? this.pos.set(this.iterator.getX(), this.iterator.getY(), this.iterator.getZ()) : (BlockPos)this.endOfData();
            }

            @Override
            protected /* synthetic */ Object computeNext() {
                return this.method_10106();
            }
        };
    }

    @Override
    public /* synthetic */ Vec3i crossProduct(Vec3i vec3i) {
        return this.crossProduct(vec3i);
    }

    @Override
    public /* synthetic */ Vec3i method_23226(Direction direction, int i) {
        return this.method_10079(direction, i);
    }

    @Override
    public /* synthetic */ Vec3i method_23227(int i) {
        return this.down(i);
    }

    @Override
    public /* synthetic */ Vec3i method_23228() {
        return this.method_10074();
    }

    private static /* synthetic */ void method_19441(int[] is, int i) {
        is[0] = i;
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

    public static final class PooledMutable
    extends Mutable
    implements AutoCloseable {
        private boolean free;
        private static final List<PooledMutable> POOL = Lists.newArrayList();

        private PooledMutable(int i, int j, int k) {
            super(i, j, k);
        }

        public static PooledMutable get() {
            return PooledMutable.get(0, 0, 0);
        }

        public static PooledMutable getEntityPos(Entity entity) {
            return PooledMutable.get(entity.getX(), entity.getY(), entity.getZ());
        }

        public static PooledMutable get(double d, double e, double f) {
            return PooledMutable.get(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public static PooledMutable get(int i, int j, int k) {
            List<PooledMutable> list = POOL;
            synchronized (list) {
                PooledMutable pooledMutable;
                if (!POOL.isEmpty() && (pooledMutable = POOL.remove(POOL.size() - 1)) != null && pooledMutable.free) {
                    pooledMutable.free = false;
                    pooledMutable.method_10113(i, j, k);
                    return pooledMutable;
                }
            }
            return new PooledMutable(i, j, k);
        }

        public PooledMutable method_10113(int i, int j, int k) {
            return (PooledMutable)super.set(i, j, k);
        }

        public PooledMutable method_10110(Entity entity) {
            return (PooledMutable)super.set(entity);
        }

        public PooledMutable method_10112(double d, double e, double f) {
            return (PooledMutable)super.set(d, e, f);
        }

        public PooledMutable method_10114(Vec3i vec3i) {
            return (PooledMutable)super.set(vec3i);
        }

        public PooledMutable method_10118(Direction direction) {
            return (PooledMutable)super.setOffset(direction);
        }

        public PooledMutable method_10116(Direction direction, int i) {
            return (PooledMutable)super.setOffset(direction, i);
        }

        public PooledMutable method_10108(int i, int j, int k) {
            return (PooledMutable)super.setOffset(i, j, k);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void close() {
            List<PooledMutable> list = POOL;
            synchronized (list) {
                if (POOL.size() < 100) {
                    POOL.add(this);
                }
                this.free = true;
            }
        }

        @Override
        public /* synthetic */ Mutable setOffset(int i, int j, int k) {
            return this.method_10108(i, j, k);
        }

        @Override
        public /* synthetic */ Mutable setOffset(Direction direction, int i) {
            return this.method_10116(direction, i);
        }

        @Override
        public /* synthetic */ Mutable setOffset(Direction direction) {
            return this.method_10118(direction);
        }

        @Override
        public /* synthetic */ Mutable set(Vec3i vec3i) {
            return this.method_10114(vec3i);
        }

        @Override
        public /* synthetic */ Mutable set(double d, double e, double f) {
            return this.method_10112(d, e, f);
        }

        @Override
        public /* synthetic */ Mutable set(Entity entity) {
            return this.method_10110(entity);
        }

        @Override
        public /* synthetic */ Mutable set(int i, int j, int k) {
            return this.method_10113(i, j, k);
        }
    }

    public static class Mutable
    extends BlockPos {
        protected int x;
        protected int y;
        protected int z;

        public Mutable() {
            this(0, 0, 0);
        }

        public Mutable(BlockPos blockPos) {
            this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }

        public Mutable(int i, int j, int k) {
            super(0, 0, 0);
            this.x = i;
            this.y = j;
            this.z = k;
        }

        public Mutable(double d, double e, double f) {
            this(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
        }

        public Mutable(Entity entity) {
            this(entity.getX(), entity.getY(), entity.getZ());
        }

        @Override
        public BlockPos add(double d, double e, double f) {
            return super.add(d, e, f).toImmutable();
        }

        @Override
        public BlockPos add(int i, int j, int k) {
            return super.add(i, j, k).toImmutable();
        }

        @Override
        public BlockPos method_10079(Direction direction, int i) {
            return super.method_10079(direction, i).toImmutable();
        }

        @Override
        public BlockPos rotate(BlockRotation blockRotation) {
            return super.rotate(blockRotation).toImmutable();
        }

        @Override
        public int getX() {
            return this.x;
        }

        @Override
        public int getY() {
            return this.y;
        }

        @Override
        public int getZ() {
            return this.z;
        }

        public Mutable set(int i, int j, int k) {
            this.x = i;
            this.y = j;
            this.z = k;
            return this;
        }

        public Mutable set(Entity entity) {
            return this.set(entity.getX(), entity.getY(), entity.getZ());
        }

        public Mutable set(double d, double e, double f) {
            return this.set(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
        }

        public Mutable set(Vec3i vec3i) {
            return this.set(vec3i.getX(), vec3i.getY(), vec3i.getZ());
        }

        public Mutable set(long l) {
            return this.set(Mutable.unpackLongX(l), Mutable.unpackLongY(l), Mutable.unpackLongZ(l));
        }

        public Mutable set(AxisCycleDirection axisCycleDirection, int i, int j, int k) {
            return this.set(axisCycleDirection.choose(i, j, k, Direction.Axis.X), axisCycleDirection.choose(i, j, k, Direction.Axis.Y), axisCycleDirection.choose(i, j, k, Direction.Axis.Z));
        }

        public Mutable setOffset(Direction direction) {
            return this.setOffset(direction, 1);
        }

        public Mutable setOffset(Direction direction, int i) {
            return this.set(this.x + direction.getOffsetX() * i, this.y + direction.getOffsetY() * i, this.z + direction.getOffsetZ() * i);
        }

        public Mutable setOffset(int i, int j, int k) {
            return this.set(this.x + i, this.y + j, this.z + k);
        }

        public void setX(int i) {
            this.x = i;
        }

        public void setY(int i) {
            this.y = i;
        }

        public void setZ(int i) {
            this.z = i;
        }

        @Override
        public BlockPos toImmutable() {
            return new BlockPos(this);
        }

        @Override
        public /* synthetic */ Vec3i crossProduct(Vec3i vec3i) {
            return super.crossProduct(vec3i);
        }

        @Override
        public /* synthetic */ Vec3i method_23226(Direction direction, int i) {
            return this.method_10079(direction, i);
        }

        @Override
        public /* synthetic */ Vec3i method_23227(int i) {
            return super.down(i);
        }

        @Override
        public /* synthetic */ Vec3i method_23228() {
            return super.method_10074();
        }
    }
}

