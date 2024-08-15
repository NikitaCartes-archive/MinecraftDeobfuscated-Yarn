package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

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
	public static final Codec<BlockPos> CODEC = Codec.INT_STREAM
		.<BlockPos>comapFlatMap(
			stream -> Util.decodeFixedLengthArray(stream, 3).map(values -> new BlockPos(values[0], values[1], values[2])),
			pos -> IntStream.of(new int[]{pos.getX(), pos.getY(), pos.getZ()})
		)
		.stable();
	public static final PacketCodec<ByteBuf, BlockPos> PACKET_CODEC = new PacketCodec<ByteBuf, BlockPos>() {
		public BlockPos decode(ByteBuf byteBuf) {
			return PacketByteBuf.readBlockPos(byteBuf);
		}

		public void encode(ByteBuf byteBuf, BlockPos blockPos) {
			PacketByteBuf.writeBlockPos(byteBuf, blockPos);
		}
	};
	private static final Logger LOGGER = LogUtils.getLogger();
	/**
	 * The block position which x, y, and z values are all zero.
	 */
	public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
	private static final int SIZE_BITS_X = 1 + MathHelper.floorLog2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
	private static final int SIZE_BITS_Z = SIZE_BITS_X;
	public static final int SIZE_BITS_Y = 64 - SIZE_BITS_X - SIZE_BITS_Z;
	private static final long BITS_X = (1L << SIZE_BITS_X) - 1L;
	private static final long BITS_Y = (1L << SIZE_BITS_Y) - 1L;
	private static final long BITS_Z = (1L << SIZE_BITS_Z) - 1L;
	private static final int field_33083 = 0;
	private static final int BIT_SHIFT_Z = SIZE_BITS_Y;
	private static final int BIT_SHIFT_X = SIZE_BITS_Y + SIZE_BITS_Z;

	public BlockPos(int i, int j, int k) {
		super(i, j, k);
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
		return new BlockPos(unpackLongX(packedPos), unpackLongY(packedPos), unpackLongZ(packedPos));
	}

	public static BlockPos ofFloored(double x, double y, double z) {
		return new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
	}

	public static BlockPos ofFloored(Position pos) {
		return ofFloored(pos.getX(), pos.getY(), pos.getZ());
	}

	public static BlockPos min(BlockPos a, BlockPos b) {
		return new BlockPos(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()));
	}

	public static BlockPos max(BlockPos a, BlockPos b) {
		return new BlockPos(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()));
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

	public BlockPos add(int i, int j, int k) {
		return i == 0 && j == 0 && k == 0 ? this : new BlockPos(this.getX() + i, this.getY() + j, this.getZ() + k);
	}

	/**
	 * {@return the center of this block position}
	 * 
	 * @see Vec3d#ofCenter
	 */
	public Vec3d toCenterPos() {
		return Vec3d.ofCenter(this);
	}

	public Vec3d toBottomCenterPos() {
		return Vec3d.ofBottomCenter(this);
	}

	public BlockPos add(Vec3i vec3i) {
		return this.add(vec3i.getX(), vec3i.getY(), vec3i.getZ());
	}

	public BlockPos subtract(Vec3i vec3i) {
		return this.add(-vec3i.getX(), -vec3i.getY(), -vec3i.getZ());
	}

	public BlockPos multiply(int i) {
		if (i == 1) {
			return this;
		} else {
			return i == 0 ? ORIGIN : new BlockPos(this.getX() * i, this.getY() * i, this.getZ() * i);
		}
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

	public BlockPos offset(Direction.Axis axis, int i) {
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

	public BlockPos withY(int y) {
		return new BlockPos(this.getX(), y, this.getZ());
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

	public Vec3d clampToWithin(Vec3d pos) {
		return new Vec3d(
			MathHelper.clamp(pos.x, (double)((float)this.getX() + 1.0E-5F), (double)this.getX() + 1.0 - 1.0E-5F),
			MathHelper.clamp(pos.y, (double)((float)this.getY() + 1.0E-5F), (double)this.getY() + 1.0 - 1.0E-5F),
			MathHelper.clamp(pos.z, (double)((float)this.getZ() + 1.0E-5F), (double)this.getZ() + 1.0 - 1.0E-5F)
		);
	}

	/**
	 * Iterates through {@code count} random block positions in a given range around the given position.
	 * 
	 * <p>The iterator yields positions in no specific order. The same position
	 * may be returned multiple times by the iterator.
	 * 
	 * @param range the maximum distance from the given pos in any axis
	 * @param around the {@link BlockPos} to iterate around
	 * @param count the number of positions to iterate
	 */
	public static Iterable<BlockPos> iterateRandomly(Random random, int count, BlockPos around, int range) {
		return iterateRandomly(
			random, count, around.getX() - range, around.getY() - range, around.getZ() - range, around.getX() + range, around.getY() + range, around.getZ() + range
		);
	}

	@Deprecated
	public static Stream<BlockPos> streamSouthEastSquare(BlockPos pos) {
		return Stream.of(pos, pos.south(), pos.east(), pos.south().east());
	}

	/**
	 * Iterates through {@code count} random block positions in the given area.
	 * 
	 * <p>The iterator yields positions in no specific order. The same position
	 * may be returned multiple times by the iterator.
	 * 
	 * @param count the number of positions to iterate
	 * @param minX the minimum x value for returned positions
	 * @param minY the minimum y value for returned positions
	 * @param minZ the minimum z value for returned positions
	 * @param maxX the maximum x value for returned positions
	 * @param maxY the maximum y value for returned positions
	 * @param maxZ the maximum z value for returned positions
	 */
	public static Iterable<BlockPos> iterateRandomly(Random random, int count, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		int i = maxX - minX + 1;
		int j = maxY - minY + 1;
		int k = maxZ - minZ + 1;
		return () -> new AbstractIterator<BlockPos>() {
				final BlockPos.Mutable pos = new BlockPos.Mutable();
				int remaining = count;

				protected BlockPos computeNext() {
					if (this.remaining <= 0) {
						return this.endOfData();
					} else {
						BlockPos blockPos = this.pos.set(minX + random.nextInt(i), minY + random.nextInt(j), minZ + random.nextInt(k));
						this.remaining--;
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
	 * @param rangeY the maximum y difference from the center
	 * @param rangeZ the maximum z difference from the center
	 * @param center the center of iteration
	 * @param rangeX the maximum x difference from the center
	 */
	public static Iterable<BlockPos> iterateOutwards(BlockPos center, int rangeX, int rangeY, int rangeZ) {
		int i = rangeX + rangeY + rangeZ;
		int j = center.getX();
		int k = center.getY();
		int l = center.getZ();
		return () -> new AbstractIterator<BlockPos>() {
				private final BlockPos.Mutable pos = new BlockPos.Mutable();
				private int manhattanDistance;
				private int limitX;
				private int limitY;
				private int dx;
				private int dy;
				private boolean swapZ;

				protected BlockPos computeNext() {
					if (this.swapZ) {
						this.swapZ = false;
						this.pos.setZ(l - (this.pos.getZ() - l));
						return this.pos;
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

									this.limitX = Math.min(rangeX, this.manhattanDistance);
									this.dx = -this.limitX;
								}

								this.limitY = Math.min(rangeY, this.manhattanDistance - Math.abs(this.dx));
								this.dy = -this.limitY;
							}

							int i = this.dx;
							int j = this.dy;
							int k = this.manhattanDistance - Math.abs(i) - Math.abs(j);
							if (k <= rangeZ) {
								this.swapZ = k != 0;
								blockPos = this.pos.set(j + i, k + j, l + k);
							}
						}

						return blockPos;
					}
				}
			};
	}

	public static Optional<BlockPos> findClosest(BlockPos pos, int horizontalRange, int verticalRange, Predicate<BlockPos> condition) {
		for (BlockPos blockPos : iterateOutwards(pos, horizontalRange, verticalRange, horizontalRange)) {
			if (condition.test(blockPos)) {
				return Optional.of(blockPos);
			}
		}

		return Optional.empty();
	}

	public static Stream<BlockPos> streamOutwards(BlockPos center, int maxX, int maxY, int maxZ) {
		return StreamSupport.stream(iterateOutwards(center, maxX, maxY, maxZ).spliterator(), false);
	}

	public static Iterable<BlockPos> iterate(Box box) {
		BlockPos blockPos = ofFloored(box.minX, box.minY, box.minZ);
		BlockPos blockPos2 = ofFloored(box.maxX, box.maxY, box.maxZ);
		return iterate(blockPos, blockPos2);
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
			Math.min(box.getMinX(), box.getMaxX()),
			Math.min(box.getMinY(), box.getMaxY()),
			Math.min(box.getMinZ(), box.getMaxZ()),
			Math.max(box.getMinX(), box.getMaxX()),
			Math.max(box.getMinY(), box.getMaxY()),
			Math.max(box.getMinZ(), box.getMaxZ())
		);
	}

	public static Stream<BlockPos> stream(Box box) {
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
				private final BlockPos.Mutable pos = new BlockPos.Mutable();
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
						return this.pos.set(startX + i, startY + k, startZ + l);
					}
				}
			};
	}

	/**
	 * Iterates block positions around the {@code center} in a square of
	 * ({@code 2 * radius + 1}) by ({@code 2 * radius + 1}). The blocks
	 * are iterated in a (square) spiral around the center.
	 * 
	 * <p>The first block returned is the center, then the iterator moves
	 * a block towards the first direction, followed by moving along
	 * the second direction.
	 * 
	 * @throws IllegalStateException when the 2 directions lie on the same axis
	 * 
	 * @param firstDirection the direction the iterator moves first
	 * @param secondDirection the direction the iterator moves after the first
	 * @param center the center of iteration
	 * @param radius the maximum chebychev distance
	 */
	public static Iterable<BlockPos.Mutable> iterateInSquare(BlockPos center, int radius, Direction firstDirection, Direction secondDirection) {
		Validate.validState(firstDirection.getAxis() != secondDirection.getAxis(), "The two directions cannot be on the same axis");
		return () -> new AbstractIterator<BlockPos.Mutable>() {
				private final Direction[] directions = new Direction[]{firstDirection, secondDirection, firstDirection.getOpposite(), secondDirection.getOpposite()};
				private final BlockPos.Mutable pos = center.mutableCopy().move(secondDirection);
				private final int maxDirectionChanges = 4 * radius;
				private int directionChangeCount = -1;
				private int maxSteps;
				private int steps;
				private int currentX = this.pos.getX();
				private int currentY = this.pos.getY();
				private int currentZ = this.pos.getZ();

				protected BlockPos.Mutable computeNext() {
					this.pos.set(this.currentX, this.currentY, this.currentZ).move(this.directions[(this.directionChangeCount + 4) % 4]);
					this.currentX = this.pos.getX();
					this.currentY = this.pos.getY();
					this.currentZ = this.pos.getZ();
					if (this.steps >= this.maxSteps) {
						if (this.directionChangeCount >= this.maxDirectionChanges) {
							return this.endOfData();
						}

						this.directionChangeCount++;
						this.steps = 0;
						this.maxSteps = this.directionChangeCount / 2 + 1;
					}

					this.steps++;
					return this.pos;
				}
			};
	}

	/**
	 * Iterates from {@code pos} recursively, like in a fill tool in a raster image editor.
	 * {@code callback} is called once (and only once) for each position it finds. When this
	 * returns {@code true} and the depth/iteration limit is not reached yet, {@code nextQueuer}
	 * queues the next (usually neighboring) positions to iterate, with the depth incremented by one.
	 * 
	 * @return the total number of iterations
	 * 
	 * @param nextQueuer a function that enqueues the next positions
	 * @param pos the starting position
	 * @param maxDepth the maximum depth of iteration
	 * @param maxIterations the maximum number of total iterations
	 */
	public static int iterateRecursively(
		BlockPos pos, int maxDepth, int maxIterations, BiConsumer<BlockPos, Consumer<BlockPos>> nextQueuer, Predicate<BlockPos> callback
	) {
		Queue<Pair<BlockPos, Integer>> queue = new ArrayDeque();
		LongSet longSet = new LongOpenHashSet();
		queue.add(Pair.of(pos, 0));
		int i = 0;

		while (!queue.isEmpty()) {
			Pair<BlockPos, Integer> pair = (Pair<BlockPos, Integer>)queue.poll();
			BlockPos blockPos = pair.getLeft();
			int j = pair.getRight();
			long l = blockPos.asLong();
			if (longSet.add(l) && callback.test(blockPos)) {
				if (++i >= maxIterations) {
					return i;
				}

				if (j < maxDepth) {
					nextQueuer.accept(blockPos, (Consumer)queuedPos -> queue.add(Pair.of(queuedPos, j + 1)));
				}
			}
		}

		return i;
	}

	public static class Mutable extends BlockPos {
		public Mutable() {
			this(0, 0, 0);
		}

		public Mutable(int i, int j, int k) {
			super(i, j, k);
		}

		public Mutable(double x, double y, double z) {
			this(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
		}

		@Override
		public BlockPos add(int i, int j, int k) {
			return super.add(i, j, k).toImmutable();
		}

		@Override
		public BlockPos multiply(int i) {
			return super.multiply(i).toImmutable();
		}

		@Override
		public BlockPos offset(Direction direction, int i) {
			return super.offset(direction, i).toImmutable();
		}

		@Override
		public BlockPos offset(Direction.Axis axis, int i) {
			return super.offset(axis, i).toImmutable();
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
		 * Sets this mutable block position to the sum of the given vectors.
		 */
		public BlockPos.Mutable set(Vec3i vec1, Vec3i vec2) {
			return this.set(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY(), vec1.getZ() + vec2.getZ());
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

		public BlockPos.Mutable move(Vec3i vec) {
			return this.set(this.getX() + vec.getX(), this.getY() + vec.getY(), this.getZ() + vec.getZ());
		}

		/**
		 * Clamps the component corresponding to the given {@code axis} between {@code min} and {@code max}.
		 */
		public BlockPos.Mutable clamp(Direction.Axis axis, int min, int max) {
			switch (axis) {
				case X:
					return this.set(MathHelper.clamp(this.getX(), min, max), this.getY(), this.getZ());
				case Y:
					return this.set(this.getX(), MathHelper.clamp(this.getY(), min, max), this.getZ());
				case Z:
					return this.set(this.getX(), this.getY(), MathHelper.clamp(this.getZ(), min, max));
				default:
					throw new IllegalStateException("Unable to clamp axis " + axis);
			}
		}

		public BlockPos.Mutable setX(int i) {
			super.setX(i);
			return this;
		}

		public BlockPos.Mutable setY(int i) {
			super.setY(i);
			return this;
		}

		public BlockPos.Mutable setZ(int i) {
			super.setZ(i);
			return this;
		}

		@Override
		public BlockPos toImmutable() {
			return new BlockPos(this);
		}
	}
}
