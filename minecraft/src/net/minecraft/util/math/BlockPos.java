package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents the position of a block in a three-dimensional volume.
 * 
 * <p>The position is integer-valued.
 * 
 * <p>A block position may be mutable; hence, when using block positions
 * obtained from other places as map keys, etc., you should call {@link
 * #toImmutable()} to obtain an immutable block position.
 */
@Immutable
public class BlockPos extends Vec3i {
	public static final Codec<BlockPos> field_25064 = Codec.INT_STREAM
		.<BlockPos>comapFlatMap(
			intStream -> Util.toIntArray(intStream, 3).map(is -> new BlockPos(is[0], is[1], is[2])),
			blockPos -> IntStream.of(new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()})
		)
		.stable();
	private static final Logger LOGGER = LogManager.getLogger();
	/**
	 * The block position which x, y, and z values are all zero.
	 */
	public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
	private static final int SIZE_BITS_X = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
	private static final int SIZE_BITS_Z = SIZE_BITS_X;
	private static final int SIZE_BITS_Y = 64 - SIZE_BITS_X - SIZE_BITS_Z;
	private static final long BITS_X = (1L << SIZE_BITS_X) - 1L;
	private static final long BITS_Y = (1L << SIZE_BITS_Y) - 1L;
	private static final long BITS_Z = (1L << SIZE_BITS_Z) - 1L;
	private static final int BIT_SHIFT_Z = SIZE_BITS_Y;
	private static final int BIT_SHIFT_X = SIZE_BITS_Y + SIZE_BITS_Z;

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

	public long asLong() {
		return asLong(this.getX(), this.getY(), this.getZ());
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
		return this.offset(Direction.UP);
	}

	public BlockPos up(int distance) {
		return this.offset(Direction.UP, distance);
	}

	public BlockPos down() {
		return this.offset(Direction.DOWN);
	}

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

	public BlockPos offset(Direction direction, int i) {
		return i == 0
			? this
			: new BlockPos(this.getX() + direction.getOffsetX() * i, this.getY() + direction.getOffsetY() * i, this.getZ() + direction.getOffsetZ() * i);
	}

	public BlockPos method_30513(Direction.Axis axis, int i) {
		if (i == 0) {
			return this;
		} else {
			int j = axis == Direction.Axis.X ? i : 0;
			int k = axis == Direction.Axis.Y ? i : 0;
			int l = axis == Direction.Axis.Z ? i : 0;
			return new BlockPos(this.getX() + j, this.getY() + k, this.getZ() + l);
		}
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
	public BlockPos.Mutable mutableCopy() {
		return new BlockPos.Mutable(this.getX(), this.getY(), this.getZ());
	}

	public static Iterable<BlockPos> method_27156(Random random, int i, int j, int k, int l, int m, int n, int o) {
		int p = m - j + 1;
		int q = n - k + 1;
		int r = o - l + 1;
		return () -> new AbstractIterator<BlockPos>() {
				final BlockPos.Mutable field_23945 = new BlockPos.Mutable();
				int field_23946 = i;

				protected BlockPos computeNext() {
					if (this.field_23946 <= 0) {
						return this.endOfData();
					} else {
						BlockPos blockPos = this.field_23945.set(j + random.nextInt(p), k + random.nextInt(q), l + random.nextInt(r));
						this.field_23946--;
						return blockPos;
					}
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
	public static Iterable<BlockPos> iterateOutwards(BlockPos center, int xRange, int yRange, int zRange) {
		int i = xRange + yRange + zRange;
		int j = center.getX();
		int k = center.getY();
		int l = center.getZ();
		return () -> new AbstractIterator<BlockPos>() {
				private final BlockPos.Mutable field_23378 = new BlockPos.Mutable();
				private int manhattanDistance;
				private int limitX;
				private int limitY;
				private int dx;
				private int dy;
				private boolean field_23379;

				protected BlockPos computeNext() {
					if (this.field_23379) {
						this.field_23379 = false;
						this.field_23378.setZ(l - (this.field_23378.getZ() - l));
						return this.field_23378;
					} else {
						BlockPos blockPos;
						for (blockPos = null; blockPos == null; this.dy++) {
							if (this.dy > this.limitY) {
								this.dx++;
								if (this.dx > this.limitX) {
									this.manhattanDistance++;
									if (this.manhattanDistance > i) {
										return this.endOfData();
									}

									this.limitX = Math.min(xRange, this.manhattanDistance);
									this.dx = -this.limitX;
								}

								this.limitY = Math.min(yRange, this.manhattanDistance - Math.abs(this.dx));
								this.dy = -this.limitY;
							}

							int i = this.dx;
							int j = this.dy;
							int k = this.manhattanDistance - Math.abs(i) - Math.abs(j);
							if (k <= zRange) {
								this.field_23379 = k != 0;
								blockPos = this.field_23378.set(j + i, k + j, l + k);
							}
						}

						return blockPos;
					}
				}
			};
	}

	public static Optional<BlockPos> findClosest(BlockPos pos, int horizontalRange, int verticalRange, Predicate<BlockPos> condition) {
		return streamOutwards(pos, horizontalRange, verticalRange, horizontalRange).filter(condition).findFirst();
	}

	public static Stream<BlockPos> streamOutwards(BlockPos center, int maxX, int maxY, int maxZ) {
		return StreamSupport.stream(iterateOutwards(center, maxX, maxY, maxZ).spliterator(), false);
	}

	public static Iterable<BlockPos> iterate(BlockPos start, BlockPos end) {
		return iterate(
			Math.min(start.getX(), end.getX()),
			Math.min(start.getY(), end.getY()),
			Math.min(start.getZ(), end.getZ()),
			Math.max(start.getX(), end.getX()),
			Math.max(start.getY(), end.getY()),
			Math.max(start.getZ(), end.getZ())
		);
	}

	public static Stream<BlockPos> stream(BlockPos start, BlockPos end) {
		return StreamSupport.stream(iterate(start, end).spliterator(), false);
	}

	public static Stream<BlockPos> stream(BlockBox box) {
		return stream(
			Math.min(box.minX, box.maxX),
			Math.min(box.minY, box.maxY),
			Math.min(box.minZ, box.maxZ),
			Math.max(box.minX, box.maxX),
			Math.max(box.minY, box.maxY),
			Math.max(box.minZ, box.maxZ)
		);
	}

	public static Stream<BlockPos> method_29715(Box box) {
		return stream(
			MathHelper.floor(box.minX),
			MathHelper.floor(box.minY),
			MathHelper.floor(box.minZ),
			MathHelper.floor(box.maxX),
			MathHelper.floor(box.maxY),
			MathHelper.floor(box.maxZ)
		);
	}

	public static Stream<BlockPos> stream(int startX, int startY, int startZ, int endX, int endY, int endZ) {
		return StreamSupport.stream(iterate(startX, startY, startZ, endX, endY, endZ).spliterator(), false);
	}

	public static Iterable<BlockPos> iterate(int startX, int startY, int startZ, int endX, int endY, int endZ) {
		int i = endX - startX + 1;
		int j = endY - startY + 1;
		int k = endZ - startZ + 1;
		int l = i * j * k;
		return () -> new AbstractIterator<BlockPos>() {
				private final BlockPos.Mutable field_23380 = new BlockPos.Mutable();
				private int index;

				protected BlockPos computeNext() {
					if (this.index == l) {
						return this.endOfData();
					} else {
						int i = this.index % i;
						int j = this.index / i;
						int k = j % j;
						int l = j / j;
						this.index++;
						return this.field_23380.set(startX + i, startY + k, startZ + l);
					}
				}
			};
	}

	public static Iterable<BlockPos.Mutable> method_30512(BlockPos blockPos, int i, Direction direction, Direction direction2) {
		Validate.validState(direction.getAxis() != direction2.getAxis(), "The two directions cannot be on the same axis");
		return () -> new AbstractIterator<BlockPos.Mutable>() {
				private final Direction[] field_25903 = new Direction[]{direction, direction2, direction.getOpposite(), direction2.getOpposite()};
				private final BlockPos.Mutable field_25904 = blockPos.mutableCopy().move(direction2);
				private final int field_25905 = 4 * i;
				private int field_25906 = -1;
				private int field_25907;
				private int field_25908;
				private int field_25909 = this.field_25904.getX();
				private int field_25910 = this.field_25904.getY();
				private int field_25911 = this.field_25904.getZ();

				protected BlockPos.Mutable computeNext() {
					this.field_25904.set(this.field_25909, this.field_25910, this.field_25911).move(this.field_25903[(this.field_25906 + 4) % 4]);
					this.field_25909 = this.field_25904.getX();
					this.field_25910 = this.field_25904.getY();
					this.field_25911 = this.field_25904.getZ();
					if (this.field_25908 >= this.field_25907) {
						if (this.field_25906 >= this.field_25905) {
							return this.endOfData();
						}

						this.field_25906++;
						this.field_25908 = 0;
						this.field_25907 = this.field_25906 / 2 + 1;
					}

					this.field_25908++;
					return this.field_25904;
				}
			};
	}

	public static class Mutable extends BlockPos {
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
		public BlockPos method_30513(Direction.Axis axis, int i) {
			return super.method_30513(axis, i).toImmutable();
		}

		@Override
		public BlockPos rotate(BlockRotation rotation) {
			return super.rotate(rotation).toImmutable();
		}

		/**
		 * Sets the x, y, and z of this mutable block position.
		 */
		public BlockPos.Mutable set(int x, int y, int z) {
			this.setX(x);
			this.setY(y);
			this.setZ(z);
			return this;
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

		/**
		 * Sets this mutable block position to the offset position of the given
		 * pos by the given direction.
		 */
		public BlockPos.Mutable set(Vec3i pos, Direction direction) {
			return this.set(pos.getX() + direction.getOffsetX(), pos.getY() + direction.getOffsetY(), pos.getZ() + direction.getOffsetZ());
		}

		/**
		 * Sets this mutable block position to the sum of the given position and the
		 * given x, y, and z.
		 */
		public BlockPos.Mutable set(Vec3i pos, int x, int y, int z) {
			return this.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
		}

		/**
		 * Moves this mutable block position by 1 block in the given direction.
		 */
		public BlockPos.Mutable move(Direction direction) {
			return this.move(direction, 1);
		}

		/**
		 * Moves this mutable block position by the given distance in the given
		 * direction.
		 */
		public BlockPos.Mutable move(Direction direction, int distance) {
			return this.set(
				this.getX() + direction.getOffsetX() * distance, this.getY() + direction.getOffsetY() * distance, this.getZ() + direction.getOffsetZ() * distance
			);
		}

		/**
		 * Moves the mutable block position by the delta x, y, and z provided.
		 */
		public BlockPos.Mutable move(int dx, int dy, int dz) {
			return this.set(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
		}

		public BlockPos.Mutable method_30927(Vec3i vec3i) {
			return this.set(this.getX() + vec3i.getX(), this.getY() + vec3i.getY(), this.getZ() + vec3i.getZ());
		}

		public BlockPos.Mutable method_27158(Direction.Axis axis, int i, int j) {
			switch (axis) {
				case X:
					return this.set(MathHelper.clamp(this.getX(), i, j), this.getY(), this.getZ());
				case Y:
					return this.set(this.getX(), MathHelper.clamp(this.getY(), i, j), this.getZ());
				case Z:
					return this.set(this.getX(), this.getY(), MathHelper.clamp(this.getZ(), i, j));
				default:
					throw new IllegalStateException("Unable to clamp axis " + axis);
			}
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
	}
}
