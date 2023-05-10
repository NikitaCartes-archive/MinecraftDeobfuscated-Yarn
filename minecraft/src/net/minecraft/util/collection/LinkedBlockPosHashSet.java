package net.minecraft.util.collection;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.util.NoSuchElementException;
import net.minecraft.util.math.MathHelper;

/**
 * Represents a set of block positions (long representation).
 * <p>
 * Uses a {@link Long2LongLinkedOpenHashMap} as its internal storage medium
 * to facilitate the quick addition and removal of block positions.
 * <p>
 * Positions are index into a 2x cubed area that then stores as a long, a bitset
 * representing which positions within that area are currently set.
 * <p>
 * This has two major advantages:
 * <ol>
 * <li>Positions that are geometrically close together are grouped together in memory. This localizes adjacent reads and writes.</li>
 * <li>A larger number of positions can be comprised together into one long allowing for a smaller memory footprint.</li>
 * </ol>
 * @see net.minecraft.world.chunk.light.LevelPropagator
 */
public class LinkedBlockPosHashSet extends LongLinkedOpenHashSet {
	private final LinkedBlockPosHashSet.Storage buffer;

	public LinkedBlockPosHashSet(int expectedSize, float loadFactor) {
		super(expectedSize, loadFactor);
		this.buffer = new LinkedBlockPosHashSet.Storage(expectedSize / 64, loadFactor);
	}

	/**
	 * Marks a block position as "set".
	 */
	@Override
	public boolean add(long posLong) {
		return this.buffer.add(posLong);
	}

	/**
	 * Marks a block position as "not set". Effectively removing it from this collection.
	 */
	@Override
	public boolean rem(long posLong) {
		return this.buffer.rem(posLong);
	}

	/**
	 * Pops first block position off of this set.
	 */
	@Override
	public long removeFirstLong() {
		return this.buffer.removeFirstLong();
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Checks whether there are any block positions that have been "set".
	 * 
	 * @return {@code true} is this collection is empty.
	 */
	@Override
	public boolean isEmpty() {
		return this.buffer.isEmpty();
	}

	/**
	 * Represents a three-dimensional mapping from a block position to a bitset
	 * of values set at that position.
	 */
	protected static class Storage extends Long2LongLinkedOpenHashMap {
		private static final int STARTING_OFFSET = MathHelper.floorLog2(60000000);
		private static final int HORIZONTAL_COLUMN_BIT_SEPARATION = MathHelper.floorLog2(60000000);
		private static final int FIELD_SPACING = 64 - STARTING_OFFSET - HORIZONTAL_COLUMN_BIT_SEPARATION;
		private static final int Y_BIT_OFFSET = 0;
		private static final int X_BIT_OFFSET = FIELD_SPACING;
		private static final int Z_BIT_OFFSET = FIELD_SPACING + HORIZONTAL_COLUMN_BIT_SEPARATION;
		private static final long MAX_POSITION = 3L << Z_BIT_OFFSET | 3L | 3L << X_BIT_OFFSET;
		private int lastWrittenIndex = -1;
		private long lastWrittenKey;
		private final int expectedSize;

		public Storage(int expectedSize, float loadFactor) {
			super(expectedSize, loadFactor);
			this.expectedSize = expectedSize;
		}

		/**
		 * Converts an individual position into a key
		 * representing the 2x cube region containing that position.
		 */
		static long getKey(long posLong) {
			return posLong & ~MAX_POSITION;
		}

		/**
		 * Gets a position's index relative to its containing 2x cube region
		 */
		static int getBlockOffset(long posLong) {
			int i = (int)(posLong >>> Z_BIT_OFFSET & 3L);
			int j = (int)(posLong >>> 0 & 3L);
			int k = (int)(posLong >>> X_BIT_OFFSET & 3L);
			return i << 4 | k << 2 | j;
		}

		static long getBlockPosLong(long key, int valueLength) {
			key |= (long)(valueLength >>> 4 & 3) << Z_BIT_OFFSET;
			key |= (long)(valueLength >>> 2 & 3) << X_BIT_OFFSET;
			return key | (long)(valueLength >>> 0 & 3) << 0;
		}

		/**
		 * Ensures that this collection contains the specified element (optional operation).
		 * 
		 * @see java.util.Collection#add(Object)
		 */
		public boolean add(long posLong) {
			long l = getKey(posLong);
			int i = getBlockOffset(posLong);
			long m = 1L << i;
			int j;
			if (l == 0L) {
				if (this.containsNullKey) {
					return this.setBits(this.n, m);
				}

				this.containsNullKey = true;
				j = this.n;
			} else {
				if (this.lastWrittenIndex != -1 && l == this.lastWrittenKey) {
					return this.setBits(this.lastWrittenIndex, m);
				}

				long[] ls = this.key;
				j = (int)HashCommon.mix(l) & this.mask;

				for (long n = ls[j]; n != 0L; n = ls[j]) {
					if (n == l) {
						this.lastWrittenIndex = j;
						this.lastWrittenKey = l;
						return this.setBits(j, m);
					}

					j = j + 1 & this.mask;
				}
			}

			this.key[j] = l;
			this.value[j] = m;
			if (this.size == 0) {
				this.first = this.last = j;
				this.link[j] = -1L;
			} else {
				this.link[this.last] = this.link[this.last] ^ (this.link[this.last] ^ (long)j & 4294967295L) & 4294967295L;
				this.link[j] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
				this.last = j;
			}

			if (this.size++ >= this.maxFill) {
				this.rehash(HashCommon.arraySize(this.size + 1, this.f));
			}

			return false;
		}

		/**
		 * Sets flags within a specific 2x cubed region represented by {@code index}.
		 * 
		 * @return {@code true} if the map already contained set bits for the indicated mask
		 *            (i.e. an overlap occurred)
		 * 
		 * @param mask mask of bits to set
		 * @param index zero-based index of a 2x cubed area
		 */
		private boolean setBits(int index, long mask) {
			boolean bl = (this.value[index] & mask) != 0L;
			this.value[index] = this.value[index] | mask;
			return bl;
		}

		/**
		 * Removes a block position from this map.
		 */
		public boolean rem(long posLong) {
			long l = getKey(posLong);
			int i = getBlockOffset(posLong);
			long m = 1L << i;
			if (l == 0L) {
				return this.containsNullKey ? this.unsetBits(m) : false;
			} else if (this.lastWrittenIndex != -1 && l == this.lastWrittenKey) {
				return this.unsetBitsAt(this.lastWrittenIndex, m);
			} else {
				long[] ls = this.key;
				int j = (int)HashCommon.mix(l) & this.mask;

				for (long n = ls[j]; n != 0L; n = ls[j]) {
					if (l == n) {
						this.lastWrittenIndex = j;
						this.lastWrittenKey = l;
						return this.unsetBitsAt(j, m);
					}

					j = j + 1 & this.mask;
				}

				return false;
			}
		}

		/**
		 * Unsets flags within the last 2x cubed region contained within this map.
		 * <p>
		 * This is equivalent to the call {@code unsetBitsAt(this.n, mask)}
		 * 
		 * @return {@code true} if the collection was changed as a result of this call
		 */
		private boolean unsetBits(long mask) {
			if ((this.value[this.n] & mask) == 0L) {
				return false;
			} else {
				this.value[this.n] = this.value[this.n] & ~mask;
				if (this.value[this.n] != 0L) {
					return true;
				} else {
					this.containsNullKey = false;
					this.size--;
					this.fixPointers(this.n);
					if (this.size < this.maxFill / 4 && this.n > 16) {
						this.rehash(this.n / 2);
					}

					return true;
				}
			}
		}

		/**
		 * Unsets flags within a specific 2x cubed region contained within this map.
		 * 
		 * @return {@code true} if the collection was changed as a result of this call
		 */
		private boolean unsetBitsAt(int index, long mask) {
			if ((this.value[index] & mask) == 0L) {
				return false;
			} else {
				this.value[index] = this.value[index] & ~mask;
				if (this.value[index] != 0L) {
					return true;
				} else {
					this.lastWrittenIndex = -1;
					this.size--;
					this.fixPointers(index);
					this.shiftKeys(index);
					if (this.size < this.maxFill / 4 && this.n > 16) {
						this.rehash(this.n / 2);
					}

					return true;
				}
			}
		}

		@Override
		public long removeFirstLong() {
			if (this.size == 0) {
				throw new NoSuchElementException();
			} else {
				int i = this.first;
				long l = this.key[i];
				int j = Long.numberOfTrailingZeros(this.value[i]);
				this.value[i] = this.value[i] & ~(1L << j);
				if (this.value[i] == 0L) {
					this.removeFirstLong();
					this.lastWrittenIndex = -1;
				}

				return getBlockPosLong(l, j);
			}
		}

		@Override
		protected void rehash(int newN) {
			if (newN > this.expectedSize) {
				super.rehash(newN);
			}
		}
	}
}
