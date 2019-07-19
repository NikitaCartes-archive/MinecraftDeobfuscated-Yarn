package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Spliterator.OfInt;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.DynamicSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPos extends Vec3i implements DynamicSerializable {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
	private static final int SIZE_BITS_X = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
	private static final int SIZE_BITS_Z = SIZE_BITS_X;
	private static final int SIZE_BITS_Y = 64 - SIZE_BITS_X - SIZE_BITS_Z;
	private static final long BITS_X = (1L << SIZE_BITS_X) - 1L;
	private static final long BITS_Y = (1L << SIZE_BITS_Y) - 1L;
	private static final long BITS_Z = (1L << SIZE_BITS_Z) - 1L;
	private static final int BIT_SHIFT_Z = SIZE_BITS_Y;
	private static final int BIT_SHIFT_X = SIZE_BITS_Y + SIZE_BITS_Z;

	public BlockPos(int x, int y, int i) {
		super(x, y, i);
	}

	public BlockPos(double x, double d, double e) {
		super(x, d, e);
	}

	public BlockPos(Entity entity) {
		this(entity.x, entity.y, entity.z);
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
		OfInt ofInt = dynamic.asIntStream().spliterator();
		int[] is = new int[3];
		if (ofInt.tryAdvance(i -> is[0] = i) && ofInt.tryAdvance(i -> is[1] = i)) {
			ofInt.tryAdvance(i -> is[2] = i);
		}

		return new BlockPos(is[0], is[1], is[2]);
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return ops.createIntList(IntStream.of(new int[]{this.getX(), this.getY(), this.getZ()}));
	}

	public static long offset(long value, Direction direction) {
		return add(value, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
	}

	public static long add(long value, int x, int y, int z) {
		return asLong(unpackLongX(value) + x, unpackLongY(value) + y, unpackLongZ(value) + z);
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
		return new BlockPos(unpackLongX(value), unpackLongY(value), unpackLongZ(value));
	}

	public static long asLong(int x, int y, int z) {
		long l = 0L;
		l |= ((long)x & BITS_X) << BIT_SHIFT_X;
		l |= ((long)y & BITS_Y) << 0;
		return l | ((long)z & BITS_Z) << BIT_SHIFT_Z;
	}

	public static long removeChunkSectionLocalY(long y) {
		return y & -16L;
	}

	public long asLong() {
		return asLong(this.getX(), this.getY(), this.getZ());
	}

	public BlockPos add(double x, double y, double z) {
		return x == 0.0 && y == 0.0 && z == 0.0 ? this : new BlockPos((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z);
	}

	public BlockPos add(int x, int y, int z) {
		return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
	}

	public BlockPos add(Vec3i pos) {
		return this.add(pos.getX(), pos.getY(), pos.getZ());
	}

	public BlockPos subtract(Vec3i pos) {
		return this.add(-pos.getX(), -pos.getY(), -pos.getZ());
	}

	public BlockPos up() {
		return this.up(1);
	}

	public BlockPos up(int distance) {
		return this.offset(Direction.UP, distance);
	}

	public BlockPos down() {
		return this.down(1);
	}

	public BlockPos down(int distance) {
		return this.offset(Direction.DOWN, distance);
	}

	public BlockPos north() {
		return this.north(1);
	}

	public BlockPos north(int distance) {
		return this.offset(Direction.NORTH, distance);
	}

	public BlockPos south() {
		return this.south(1);
	}

	public BlockPos south(int distance) {
		return this.offset(Direction.SOUTH, distance);
	}

	public BlockPos west() {
		return this.west(1);
	}

	public BlockPos west(int distance) {
		return this.offset(Direction.WEST, distance);
	}

	public BlockPos east() {
		return this.east(1);
	}

	public BlockPos east(int distance) {
		return this.offset(Direction.EAST, distance);
	}

	public BlockPos offset(Direction direction) {
		return this.offset(direction, 1);
	}

	public BlockPos offset(Direction direction, int distance) {
		return distance == 0
			? this
			: new BlockPos(
				this.getX() + direction.getOffsetX() * distance, this.getY() + direction.getOffsetY() * distance, this.getZ() + direction.getOffsetZ() * distance
			);
	}

	public BlockPos rotate(BlockRotation rotation) {
		switch (rotation) {
			case NONE:
			default:
				return this;
			case CLOCKWISE_90:
				return new BlockPos(-this.getZ(), this.getY(), this.getX());
			case CLOCKWISE_180:
				return new BlockPos(-this.getX(), this.getY(), -this.getZ());
			case COUNTERCLOCKWISE_90:
				return new BlockPos(this.getZ(), this.getY(), -this.getX());
		}
	}

	public BlockPos crossProduct(Vec3i pos) {
		return new BlockPos(
			this.getY() * pos.getZ() - this.getZ() * pos.getY(),
			this.getZ() * pos.getX() - this.getX() * pos.getZ(),
			this.getX() * pos.getY() - this.getY() * pos.getX()
		);
	}

	public BlockPos toImmutable() {
		return this;
	}

	public static Iterable<BlockPos> iterate(BlockPos pos1, BlockPos pos2) {
		return iterate(
			Math.min(pos1.getX(), pos2.getX()),
			Math.min(pos1.getY(), pos2.getY()),
			Math.min(pos1.getZ(), pos2.getZ()),
			Math.max(pos1.getX(), pos2.getX()),
			Math.max(pos1.getY(), pos2.getY()),
			Math.max(pos1.getZ(), pos2.getZ())
		);
	}

	public static Stream<BlockPos> stream(BlockPos pos1, BlockPos pos2) {
		return stream(
			Math.min(pos1.getX(), pos2.getX()),
			Math.min(pos1.getY(), pos2.getY()),
			Math.min(pos1.getZ(), pos2.getZ()),
			Math.max(pos1.getX(), pos2.getX()),
			Math.max(pos1.getY(), pos2.getY()),
			Math.max(pos1.getZ(), pos2.getZ())
		);
	}

	public static Stream<BlockPos> stream(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		return StreamSupport.stream(new AbstractSpliterator<BlockPos>((long)((maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1)), 64) {
			final CuboidBlockIterator connector = new CuboidBlockIterator(minX, minY, minZ, maxX, maxY, maxZ);
			final BlockPos.Mutable position = new BlockPos.Mutable();

			public boolean tryAdvance(Consumer<? super BlockPos> consumer) {
				if (this.connector.step()) {
					consumer.accept(this.position.set(this.connector.getX(), this.connector.getY(), this.connector.getZ()));
					return true;
				} else {
					return false;
				}
			}
		}, false);
	}

	public static Iterable<BlockPos> iterate(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return () -> new AbstractIterator<BlockPos>() {
				final CuboidBlockIterator iterator = new CuboidBlockIterator(minX, maxX, minY, maxY, minZ, maxZ);
				final BlockPos.Mutable pos = new BlockPos.Mutable();

				protected BlockPos computeNext() {
					return (BlockPos)(this.iterator.step() ? this.pos.set(this.iterator.getX(), this.iterator.getY(), this.iterator.getZ()) : this.endOfData());
				}
			};
	}

	public static class Mutable extends BlockPos {
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

		@Override
		public BlockPos add(double x, double y, double z) {
			return super.add(x, y, z).toImmutable();
		}

		@Override
		public BlockPos add(int x, int y, int z) {
			return super.add(x, y, z).toImmutable();
		}

		@Override
		public BlockPos offset(Direction direction, int distance) {
			return super.offset(direction, distance).toImmutable();
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

		public BlockPos.Mutable set(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}

		public BlockPos.Mutable set(Entity entity) {
			return this.set(entity.x, entity.y, entity.z);
		}

		public BlockPos.Mutable set(double x, double y, double z) {
			return this.set(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
		}

		public BlockPos.Mutable set(Vec3i pos) {
			return this.set(pos.getX(), pos.getY(), pos.getZ());
		}

		public BlockPos.Mutable set(long pos) {
			return this.set(unpackLongX(pos), unpackLongY(pos), unpackLongZ(pos));
		}

		public BlockPos.Mutable set(AxisCycleDirection axis, int x, int y, int z) {
			return this.set(axis.choose(x, y, z, Direction.Axis.X), axis.choose(x, y, z, Direction.Axis.Y), axis.choose(x, y, z, Direction.Axis.Z));
		}

		public BlockPos.Mutable setOffset(Direction direction) {
			return this.setOffset(direction, 1);
		}

		public BlockPos.Mutable setOffset(Direction direction, int distance) {
			return this.set(this.x + direction.getOffsetX() * distance, this.y + direction.getOffsetY() * distance, this.z + direction.getOffsetZ() * distance);
		}

		public BlockPos.Mutable setOffset(int x, int y, int z) {
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
	}

	public static final class PooledMutable extends BlockPos.Mutable implements AutoCloseable {
		private boolean free;
		private static final List<BlockPos.PooledMutable> POOL = Lists.<BlockPos.PooledMutable>newArrayList();

		private PooledMutable(int x, int y, int z) {
			super(x, y, z);
		}

		public static BlockPos.PooledMutable get() {
			return get(0, 0, 0);
		}

		public static BlockPos.PooledMutable getEntityPos(Entity entity) {
			return get(entity.x, entity.y, entity.z);
		}

		public static BlockPos.PooledMutable get(double x, double y, double z) {
			return get(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
		}

		public static BlockPos.PooledMutable get(int x, int y, int z) {
			synchronized (POOL) {
				if (!POOL.isEmpty()) {
					BlockPos.PooledMutable pooledMutable = (BlockPos.PooledMutable)POOL.remove(POOL.size() - 1);
					if (pooledMutable != null && pooledMutable.free) {
						pooledMutable.free = false;
						pooledMutable.set(x, y, z);
						return pooledMutable;
					}
				}
			}

			return new BlockPos.PooledMutable(x, y, z);
		}

		public BlockPos.PooledMutable set(int i, int j, int k) {
			return (BlockPos.PooledMutable)super.set(i, j, k);
		}

		public BlockPos.PooledMutable set(Entity entity) {
			return (BlockPos.PooledMutable)super.set(entity);
		}

		public BlockPos.PooledMutable set(double d, double e, double f) {
			return (BlockPos.PooledMutable)super.set(d, e, f);
		}

		public BlockPos.PooledMutable set(Vec3i vec3i) {
			return (BlockPos.PooledMutable)super.set(vec3i);
		}

		public BlockPos.PooledMutable setOffset(Direction direction) {
			return (BlockPos.PooledMutable)super.setOffset(direction);
		}

		public BlockPos.PooledMutable setOffset(Direction direction, int i) {
			return (BlockPos.PooledMutable)super.setOffset(direction, i);
		}

		public BlockPos.PooledMutable setOffset(int i, int j, int k) {
			return (BlockPos.PooledMutable)super.setOffset(i, j, k);
		}

		public void close() {
			synchronized (POOL) {
				if (POOL.size() < 100) {
					POOL.add(this);
				}

				this.free = true;
			}
		}
	}
}
