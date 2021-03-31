package net.minecraft;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.util.NoSuchElementException;
import net.minecraft.util.math.MathHelper;

public class class_6136 extends LongLinkedOpenHashSet {
	private final class_6136.class_6137 field_31715;

	public class_6136(int i, float f) {
		super(i, f);
		this.field_31715 = new class_6136.class_6137(i / 64, f);
	}

	@Override
	public boolean add(long l) {
		return this.field_31715.method_35487(l);
	}

	@Override
	public boolean rem(long l) {
		return this.field_31715.method_35488(l);
	}

	@Override
	public long removeFirstLong() {
		return this.field_31715.method_35481();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		return this.field_31715.isEmpty();
	}

	public static class class_6137 extends Long2LongLinkedOpenHashMap {
		private static final int field_31716 = MathHelper.log2(60000000);
		private static final int field_31717 = MathHelper.log2(60000000);
		private static final int field_31718 = 64 - field_31716 - field_31717;
		private static final int field_31719 = 0;
		private static final int field_31720 = field_31718;
		private static final int field_31721 = field_31718 + field_31717;
		private static final long field_31722 = 3L << field_31721 | 3L | 3L << field_31720;
		private int field_31723 = -1;
		private long field_31724;
		private final int field_31725;

		public class_6137(int i, float f) {
			super(i, f);
			this.field_31725 = i;
		}

		static long method_35483(long l) {
			return l & ~field_31722;
		}

		static int method_35486(long l) {
			int i = (int)(l >>> field_31721 & 3L);
			int j = (int)(l >>> 0 & 3L);
			int k = (int)(l >>> field_31720 & 3L);
			return i << 4 | k << 2 | j;
		}

		static long method_35484(long l, int i) {
			l |= (long)(i >>> 4 & 3) << field_31721;
			l |= (long)(i >>> 2 & 3) << field_31720;
			return l | (long)(i >>> 0 & 3) << 0;
		}

		public boolean method_35487(long l) {
			long m = method_35483(l);
			int i = method_35486(l);
			long n = 1L << i;
			int j;
			if (m == 0L) {
				if (this.containsNullKey) {
					return this.method_35482(this.n, n);
				}

				this.containsNullKey = true;
				j = this.n;
			} else {
				if (this.field_31723 != -1 && m == this.field_31724) {
					return this.method_35482(this.field_31723, n);
				}

				long[] ls = this.key;
				j = (int)HashCommon.mix(m) & this.mask;

				for (long o = ls[j]; o != 0L; o = ls[j]) {
					if (o == m) {
						this.field_31723 = j;
						this.field_31724 = m;
						return this.method_35482(j, n);
					}

					j = j + 1 & this.mask;
				}
			}

			this.key[j] = m;
			this.value[j] = n;
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

		private boolean method_35482(int i, long l) {
			boolean bl = (this.value[i] & l) != 0L;
			this.value[i] = this.value[i] | l;
			return bl;
		}

		public boolean method_35488(long l) {
			long m = method_35483(l);
			int i = method_35486(l);
			long n = 1L << i;
			if (m == 0L) {
				return this.containsNullKey ? this.method_35489(n) : false;
			} else if (this.field_31723 != -1 && m == this.field_31724) {
				return this.method_35485(this.field_31723, n);
			} else {
				long[] ls = this.key;
				int j = (int)HashCommon.mix(m) & this.mask;

				for (long o = ls[j]; o != 0L; o = ls[j]) {
					if (m == o) {
						this.field_31723 = j;
						this.field_31724 = m;
						return this.method_35485(j, n);
					}

					j = j + 1 & this.mask;
				}

				return false;
			}
		}

		private boolean method_35489(long l) {
			if ((this.value[this.n] & l) == 0L) {
				return false;
			} else {
				this.value[this.n] = this.value[this.n] & ~l;
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

		private boolean method_35485(int i, long l) {
			if ((this.value[i] & l) == 0L) {
				return false;
			} else {
				this.value[i] = this.value[i] & ~l;
				if (this.value[i] != 0L) {
					return true;
				} else {
					this.field_31723 = -1;
					this.size--;
					this.fixPointers(i);
					this.shiftKeys(i);
					if (this.size < this.maxFill / 4 && this.n > 16) {
						this.rehash(this.n / 2);
					}

					return true;
				}
			}
		}

		public long method_35481() {
			if (this.size == 0) {
				throw new NoSuchElementException();
			} else {
				int i = this.first;
				long l = this.key[i];
				int j = Long.numberOfTrailingZeros(this.value[i]);
				this.value[i] = this.value[i] & ~(1L << j);
				if (this.value[i] == 0L) {
					this.removeFirstLong();
					this.field_31723 = -1;
				}

				return method_35484(l, j);
			}
		}

		@Override
		protected void rehash(int i) {
			if (i > this.field_31725) {
				super.rehash(i);
			}
		}
	}
}
