package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.concurrent.Immutable;
import net.minecraft.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPos extends Vec3i {
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
	private static final long BIT_MASK_CHUNK_SECTION = 15L << BIT_SHIFT_X | 15L | 15L << BIT_SHIFT_Z;

	public BlockPos(int i, int j, int k) {
		super(i, j, k);
	}

	public BlockPos(double d, double e, double f) {
		super(d, e, f);
	}

	public BlockPos(Entity entity) {
		this(entity.x, entity.y, entity.z);
	}

	public BlockPos(Vec3d vec3d) {
		this(vec3d.x, vec3d.y, vec3d.z);
	}

	public BlockPos(Vec3i vec3i) {
		this(vec3i.getX(), vec3i.getY(), vec3i.getZ());
	}

	public static long toChunkSectionOrigin(long l) {
		return l & ~BIT_MASK_CHUNK_SECTION;
	}

	public static long offset(long l, Direction direction) {
		return add(l, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
	}

	public static long add(long l, int i, int j, int k) {
		return asLong(unpackLongX(l) + i, unpackLongY(l) + j, unpackLongZ(l) + k);
	}

	public static boolean isHeightInvalid(long l) {
		int i = unpackLongY(l);
		return i < 0 || i >= 256;
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
		return new BlockPos(unpackLongX(l), unpackLongY(l), unpackLongZ(l));
	}

	public static long asLong(int i, int j, int k) {
		long l = 0L;
		l |= ((long)i & BITS_X) << BIT_SHIFT_X;
		l |= ((long)j & BITS_Y) << 0;
		return l | ((long)k & BITS_Z) << BIT_SHIFT_Z;
	}

	public static long removeChunkSectionLocalY(long l) {
		return l & -16L;
	}

	public static long removeY(long l) {
		return l & ~(BITS_Y << 0);
	}

	public long asLong() {
		return asLong(this.getX(), this.getY(), this.getZ());
	}

	public BlockPos add(double d, double e, double f) {
		return d == 0.0 && e == 0.0 && f == 0.0 ? this : new BlockPos((double)this.getX() + d, (double)this.getY() + e, (double)this.getZ() + f);
	}

	public BlockPos add(int i, int j, int k) {
		return i == 0 && j == 0 && k == 0 ? this : new BlockPos(this.getX() + i, this.getY() + j, this.getZ() + k);
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
		return this.offset(Direction.UP, i);
	}

	public BlockPos down() {
		return this.down(1);
	}

	public BlockPos down(int i) {
		return this.offset(Direction.DOWN, i);
	}

	public BlockPos north() {
		return this.north(1);
	}

	public BlockPos north(int i) {
		return this.offset(Direction.NORTH, i);
	}

	public BlockPos south() {
		return this.south(1);
	}

	public BlockPos south(int i) {
		return this.offset(Direction.SOUTH, i);
	}

	public BlockPos west() {
		return this.west(1);
	}

	public BlockPos west(int i) {
		return this.offset(Direction.WEST, i);
	}

	public BlockPos east() {
		return this.east(1);
	}

	public BlockPos east(int i) {
		return this.offset(Direction.EAST, i);
	}

	public BlockPos offset(Direction direction) {
		return this.offset(direction, 1);
	}

	public BlockPos offset(Direction direction, int i) {
		return i == 0
			? this
			: new BlockPos(this.getX() + direction.getOffsetX() * i, this.getY() + direction.getOffsetY() * i, this.getZ() + direction.getOffsetZ() * i);
	}

	public BlockPos method_10070(net.minecraft.util.Rotation rotation) {
		switch (rotation) {
			case ROT_0:
			default:
				return this;
			case ROT_90:
				return new BlockPos(-this.getZ(), this.getY(), this.getX());
			case ROT_180:
				return new BlockPos(-this.getX(), this.getY(), -this.getZ());
			case ROT_270:
				return new BlockPos(this.getZ(), this.getY(), -this.getX());
		}
	}

	public BlockPos crossProduct(Vec3i vec3i) {
		return new BlockPos(
			this.getY() * vec3i.getZ() - this.getZ() * vec3i.getY(),
			this.getZ() * vec3i.getX() - this.getX() * vec3i.getZ(),
			this.getX() * vec3i.getY() - this.getY() * vec3i.getX()
		);
	}

	public BlockPos toImmutable() {
		return this;
	}

	public static Iterable<BlockPos> iterateBoxPositions(BlockPos blockPos, BlockPos blockPos2) {
		return iterateBoxPositions(
			Math.min(blockPos.getX(), blockPos2.getX()),
			Math.min(blockPos.getY(), blockPos2.getY()),
			Math.min(blockPos.getZ(), blockPos2.getZ()),
			Math.max(blockPos.getX(), blockPos2.getX()),
			Math.max(blockPos.getY(), blockPos2.getY()),
			Math.max(blockPos.getZ(), blockPos2.getZ())
		);
	}

	public static Iterable<BlockPos> iterateBoxPositions(int i, int j, int k, int l, int m, int n) {
		return () -> new AbstractIterator<BlockPos>() {
				private BlockPos.Mutable field_17596;

				protected BlockPos method_10106() {
					if (this.field_17596 == null) {
						this.field_17596 = new BlockPos.Mutable(i, j, k);
						return this.field_17596;
					} else if (this.field_17596.xMut == l && this.field_17596.yMut == m && this.field_17596.zMut == n) {
						return this.endOfData();
					} else {
						if (this.field_17596.xMut < l) {
							this.field_17596.xMut++;
						} else if (this.field_17596.yMut < m) {
							this.field_17596.xMut = i;
							this.field_17596.yMut++;
						} else if (this.field_17596.zMut < n) {
							this.field_17596.xMut = i;
							this.field_17596.yMut = j;
							this.field_17596.zMut++;
						}

						return this.field_17596;
					}
				}
			};
	}

	public static class Mutable extends BlockPos {
		protected int xMut;
		protected int yMut;
		protected int zMut;

		public Mutable() {
			this(0, 0, 0);
		}

		public Mutable(BlockPos blockPos) {
			this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		}

		public Mutable(int i, int j, int k) {
			super(0, 0, 0);
			this.xMut = i;
			this.yMut = j;
			this.zMut = k;
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
		public BlockPos offset(Direction direction, int i) {
			return super.offset(direction, i).toImmutable();
		}

		@Override
		public BlockPos method_10070(net.minecraft.util.Rotation rotation) {
			return super.method_10070(rotation).toImmutable();
		}

		@Override
		public int getX() {
			return this.xMut;
		}

		@Override
		public int getY() {
			return this.yMut;
		}

		@Override
		public int getZ() {
			return this.zMut;
		}

		public BlockPos.Mutable set(int i, int j, int k) {
			this.xMut = i;
			this.yMut = j;
			this.zMut = k;
			return this;
		}

		public BlockPos.Mutable set(Entity entity) {
			return this.set(entity.x, entity.y, entity.z);
		}

		public BlockPos.Mutable set(double d, double e, double f) {
			return this.set(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
		}

		public BlockPos.Mutable set(Vec3i vec3i) {
			return this.set(vec3i.getX(), vec3i.getY(), vec3i.getZ());
		}

		public BlockPos.Mutable setFromLong(long l) {
			return this.set(unpackLongX(l), unpackLongY(l), unpackLongZ(l));
		}

		public BlockPos.Mutable setOffset(Direction direction) {
			return this.setOffset(direction, 1);
		}

		public BlockPos.Mutable setOffset(Direction direction, int i) {
			return this.set(this.xMut + direction.getOffsetX() * i, this.yMut + direction.getOffsetY() * i, this.zMut + direction.getOffsetZ() * i);
		}

		public BlockPos.Mutable method_10100(int i, int j, int k) {
			return this.set(this.xMut + i, this.yMut + j, this.zMut + k);
		}

		public void setY(int i) {
			this.yMut = i;
		}

		@Override
		public BlockPos toImmutable() {
			return new BlockPos(this);
		}
	}

	public static final class PooledMutable extends BlockPos.Mutable implements AutoCloseable {
		private boolean field_11004;
		private static final List<BlockPos.PooledMutable> POOL = Lists.<BlockPos.PooledMutable>newArrayList();

		private PooledMutable(int i, int j, int k) {
			super(i, j, k);
		}

		public static BlockPos.PooledMutable get() {
			return get(0, 0, 0);
		}

		public static BlockPos.PooledMutable getEntityPos(Entity entity) {
			return get(entity.x, entity.y, entity.z);
		}

		public static BlockPos.PooledMutable get(double d, double e, double f) {
			return get(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
		}

		public static BlockPos.PooledMutable get(int i, int j, int k) {
			synchronized (POOL) {
				if (!POOL.isEmpty()) {
					BlockPos.PooledMutable pooledMutable = (BlockPos.PooledMutable)POOL.remove(POOL.size() - 1);
					if (pooledMutable != null && pooledMutable.field_11004) {
						pooledMutable.field_11004 = false;
						pooledMutable.method_10113(i, j, k);
						return pooledMutable;
					}
				}
			}

			return new BlockPos.PooledMutable(i, j, k);
		}

		public BlockPos.PooledMutable method_10113(int i, int j, int k) {
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

		public BlockPos.PooledMutable method_10108(int i, int j, int k) {
			return (BlockPos.PooledMutable)super.method_10100(i, j, k);
		}

		public void close() {
			synchronized (POOL) {
				if (POOL.size() < 100) {
					POOL.add(this);
				}

				this.field_11004 = true;
			}
		}
	}
}
