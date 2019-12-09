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

    public BlockPos(int x, int y, int i) {
        super(x, y, i);
    }

    public BlockPos(double x, double d, double e) {
        super(x, d, e);
    }

    public BlockPos(Entity entity) {
        this(entity.getX(), entity.getY(), entity.getZ());
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
    public <T> T serialize(DynamicOps<T> ops) {
        return ops.createIntList(IntStream.of(this.getX(), this.getY(), this.getZ()));
    }

    public static long offset(long value, Direction direction) {
        return BlockPos.add(value, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    public static long add(long value, int x, int y, int z) {
        return BlockPos.asLong(BlockPos.unpackLongX(value) + x, BlockPos.unpackLongY(value) + y, BlockPos.unpackLongZ(value) + z);
    }

    public static int unpackLongX(long x) {
        return (int)(x << 64 - BIT_SHIFT_X - SIZE_BITS_X >> 64 - SIZE_BITS_X);
    }

    public static int unpackLongY(long y) {
        return (int)(y << 64 - SIZE_BITS_Y >> 64 - SIZE_BITS_Y);
    }

    public static int unpackLongZ(long z) {
        return (int)(z << 64 - BIT_SHIFT_Z - SIZE_BITS_Z >> 64 - SIZE_BITS_Z);
    }

    public static BlockPos fromLong(long value) {
        return new BlockPos(BlockPos.unpackLongX(value), BlockPos.unpackLongY(value), BlockPos.unpackLongZ(value));
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

    public long asLong() {
        return BlockPos.asLong(this.getX(), this.getY(), this.getZ());
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

    public BlockPos up() {
        return this.offset(Direction.UP);
    }

    public BlockPos up(int distance) {
        return this.offset(Direction.UP, distance);
    }

    @Override
    public BlockPos down() {
        return this.offset(Direction.DOWN);
    }

    @Override
    public BlockPos down(int distance) {
        return this.offset(Direction.DOWN, distance);
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

    public BlockPos toImmutable() {
        return this;
    }

    public static Iterable<BlockPos> iterate(BlockPos pos1, BlockPos pos2) {
        return BlockPos.iterate(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()), Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
    }

    public static Stream<BlockPos> stream(BlockPos pos1, BlockPos pos2) {
        return BlockPos.stream(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()), Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
    }

    public static Stream<BlockPos> method_23627(BlockBox blockBox) {
        return BlockPos.stream(Math.min(blockBox.minX, blockBox.maxX), Math.min(blockBox.minY, blockBox.maxY), Math.min(blockBox.minZ, blockBox.maxZ), Math.max(blockBox.minX, blockBox.maxX), Math.max(blockBox.minY, blockBox.maxY), Math.max(blockBox.minZ, blockBox.maxZ));
    }

    public static Stream<BlockPos> stream(final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<BlockPos>((long)((maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1)), 64){
            final CuboidBlockIterator connector;
            final Mutable position;
            {
                super(l, i);
                this.connector = new CuboidBlockIterator(minX, minY, minZ, maxX, maxY, maxZ);
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

    public static Iterable<BlockPos> iterate(final int minX, final int maxX, final int minY, final int maxY, final int minZ, final int maxZ) {
        return () -> new AbstractIterator<BlockPos>(){
            final CuboidBlockIterator iterator;
            final Mutable pos;
            {
                this.iterator = new CuboidBlockIterator(minX, maxX, minY, maxY, minZ, maxZ);
                this.pos = new Mutable();
            }

            @Override
            protected BlockPos computeNext() {
                return this.iterator.step() ? this.pos.set(this.iterator.getX(), this.iterator.getY(), this.iterator.getZ()) : (BlockPos)this.endOfData();
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
    public /* synthetic */ Vec3i offset(Direction direction, int i) {
        return this.offset(direction, i);
    }

    @Override
    public /* synthetic */ Vec3i down(int i) {
        return this.down(i);
    }

    @Override
    public /* synthetic */ Vec3i down() {
        return this.down();
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

        private PooledMutable(int x, int y, int z) {
            super(x, y, z);
        }

        public static PooledMutable get() {
            return PooledMutable.get(0, 0, 0);
        }

        public static PooledMutable getEntityPos(Entity entity) {
            return PooledMutable.get(entity.getX(), entity.getY(), entity.getZ());
        }

        public static PooledMutable get(double x, double y, double z) {
            return PooledMutable.get(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public static PooledMutable get(int x, int y, int z) {
            List<PooledMutable> list = POOL;
            synchronized (list) {
                PooledMutable pooledMutable;
                if (!POOL.isEmpty() && (pooledMutable = POOL.remove(POOL.size() - 1)) != null && pooledMutable.free) {
                    pooledMutable.free = false;
                    pooledMutable.set(x, y, z);
                    return pooledMutable;
                }
            }
            return new PooledMutable(x, y, z);
        }

        @Override
        public PooledMutable set(int i, int j, int k) {
            return (PooledMutable)super.set(i, j, k);
        }

        @Override
        public PooledMutable set(Entity entity) {
            return (PooledMutable)super.set(entity);
        }

        @Override
        public PooledMutable set(double d, double e, double f) {
            return (PooledMutable)super.set(d, e, f);
        }

        @Override
        public PooledMutable set(Vec3i vec3i) {
            return (PooledMutable)super.set(vec3i);
        }

        @Override
        public PooledMutable setOffset(Direction direction) {
            return (PooledMutable)super.setOffset(direction);
        }

        @Override
        public PooledMutable setOffset(Direction direction, int i) {
            return (PooledMutable)super.setOffset(direction, i);
        }

        @Override
        public PooledMutable setOffset(int i, int j, int k) {
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
        public /* synthetic */ Mutable setOffset(int x, int y, int z) {
            return this.setOffset(x, y, z);
        }

        @Override
        public /* synthetic */ Mutable setOffset(Direction direction, int distance) {
            return this.setOffset(direction, distance);
        }

        @Override
        public /* synthetic */ Mutable setOffset(Direction direction) {
            return this.setOffset(direction);
        }

        @Override
        public /* synthetic */ Mutable set(Vec3i pos) {
            return this.set(pos);
        }

        @Override
        public /* synthetic */ Mutable set(double x, double y, double z) {
            return this.set(x, y, z);
        }

        @Override
        public /* synthetic */ Mutable set(Entity entity) {
            return this.set(entity);
        }

        @Override
        public /* synthetic */ Mutable set(int x, int y, int z) {
            return this.set(x, y, z);
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

        public Mutable(BlockPos pos) {
            this(pos.getX(), pos.getY(), pos.getZ());
        }

        public Mutable(int y, int z, int i) {
            super(0, 0, 0);
            this.x = y;
            this.y = z;
            this.z = i;
        }

        public Mutable(double d, double e, double f) {
            this(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
        }

        public Mutable(Entity entity) {
            this(entity.getX(), entity.getY(), entity.getZ());
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
        public BlockPos rotate(BlockRotation rotation) {
            return super.rotate(rotation).toImmutable();
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

        public Mutable set(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public Mutable set(Entity entity) {
            return this.set(entity.getX(), entity.getY(), entity.getZ());
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

        public Mutable setOffset(Direction direction) {
            return this.setOffset(direction, 1);
        }

        public Mutable setOffset(Direction direction, int distance) {
            return this.set(this.x + direction.getOffsetX() * distance, this.y + direction.getOffsetY() * distance, this.z + direction.getOffsetZ() * distance);
        }

        public Mutable setOffset(int x, int y, int z) {
            return this.set(this.x + x, this.y + y, this.z + z);
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public void setZ(int z) {
            this.z = z;
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
        public /* synthetic */ Vec3i offset(Direction direction, int i) {
            return this.offset(direction, i);
        }

        @Override
        public /* synthetic */ Vec3i down(int i) {
            return super.down(i);
        }

        @Override
        public /* synthetic */ Vec3i down() {
            return super.down();
        }
    }
}

