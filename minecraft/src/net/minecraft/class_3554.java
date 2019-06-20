package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;

public abstract class class_3554 {
	private final int field_15783;
	private final LongLinkedOpenHashSet[] field_15785;
	private final Long2ByteFunction field_15784;
	private int field_15781;
	private volatile boolean field_15782;

	protected class_3554(int i, int j, int k) {
		if (i >= 254) {
			throw new IllegalArgumentException("Level count must be < 254.");
		} else {
			this.field_15783 = i;
			this.field_15785 = new LongLinkedOpenHashSet[i];

			for (int l = 0; l < i; l++) {
				this.field_15785[l] = new LongLinkedOpenHashSet(j, 0.5F) {
					@Override
					protected void rehash(int i) {
						if (i > j) {
							super.rehash(i);
						}
					}
				};
			}

			this.field_15784 = new Long2ByteOpenHashMap(k, 0.5F) {
				@Override
				protected void rehash(int i) {
					if (i > k) {
						super.rehash(i);
					}
				}
			};
			this.field_15784.defaultReturnValue((byte)-1);
			this.field_15781 = i;
		}
	}

	private int method_15490(int i, int j) {
		int k = i;
		if (i > j) {
			k = j;
		}

		if (k > this.field_15783 - 1) {
			k = this.field_15783 - 1;
		}

		return k;
	}

	private void method_15481(int i) {
		int j = this.field_15781;
		this.field_15781 = i;

		for (int k = j + 1; k < i; k++) {
			if (!this.field_15785[k].isEmpty()) {
				this.field_15781 = k;
				break;
			}
		}
	}

	protected void method_15483(long l) {
		int i = this.field_15784.get(l) & 255;
		if (i != 255) {
			int j = this.method_15480(l);
			int k = this.method_15490(j, i);
			this.method_15493(l, k, this.field_15783, true);
			this.field_15782 = this.field_15781 < this.field_15783;
		}
	}

	private void method_15493(long l, int i, int j, boolean bl) {
		if (bl) {
			this.field_15784.remove(l);
		}

		this.field_15785[i].remove(l);
		if (this.field_15785[i].isEmpty() && this.field_15781 == i) {
			this.method_15481(j);
		}
	}

	private void method_15479(long l, int i, int j) {
		this.field_15784.put(l, (byte)i);
		this.field_15785[j].add(l);
		if (this.field_15781 > j) {
			this.field_15781 = j;
		}
	}

	protected void method_15491(long l) {
		this.method_15478(l, l, this.field_15783 - 1, false);
	}

	protected void method_15478(long l, long m, int i, boolean bl) {
		this.method_15482(l, m, i, this.method_15480(m), this.field_15784.get(m) & 255, bl);
		this.field_15782 = this.field_15781 < this.field_15783;
	}

	private void method_15482(long l, long m, int i, int j, int k, boolean bl) {
		if (!this.method_15494(m)) {
			i = class_3532.method_15340(i, 0, this.field_15783 - 1);
			j = class_3532.method_15340(j, 0, this.field_15783 - 1);
			boolean bl2;
			if (k == 255) {
				bl2 = true;
				k = j;
			} else {
				bl2 = false;
			}

			int n;
			if (bl) {
				n = Math.min(k, i);
			} else {
				n = class_3532.method_15340(this.method_15486(m, l, i), 0, this.field_15783 - 1);
			}

			int o = this.method_15490(j, k);
			if (j != n) {
				int p = this.method_15490(j, n);
				if (o != p && !bl2) {
					this.method_15493(m, o, p, false);
				}

				this.method_15479(m, n, p);
			} else if (!bl2) {
				this.method_15493(m, o, this.field_15783, true);
			}
		}
	}

	protected final void method_15484(long l, long m, int i, boolean bl) {
		int j = this.field_15784.get(m) & 255;
		int k = class_3532.method_15340(this.method_15488(l, m, i), 0, this.field_15783 - 1);
		if (bl) {
			this.method_15482(l, m, k, this.method_15480(m), j, true);
		} else {
			int n;
			boolean bl2;
			if (j == 255) {
				bl2 = true;
				n = class_3532.method_15340(this.method_15480(m), 0, this.field_15783 - 1);
			} else {
				n = j;
				bl2 = false;
			}

			if (k == n) {
				this.method_15482(l, m, this.field_15783 - 1, bl2 ? n : this.method_15480(m), j, false);
			}
		}
	}

	protected final boolean method_15489() {
		return this.field_15782;
	}

	protected final int method_15492(int i) {
		if (this.field_15781 >= this.field_15783) {
			return i;
		} else {
			while (this.field_15781 < this.field_15783 && i > 0) {
				i--;
				LongLinkedOpenHashSet longLinkedOpenHashSet = this.field_15785[this.field_15781];
				long l = longLinkedOpenHashSet.removeFirstLong();
				int j = class_3532.method_15340(this.method_15480(l), 0, this.field_15783 - 1);
				if (longLinkedOpenHashSet.isEmpty()) {
					this.method_15481(this.field_15783);
				}

				int k = this.field_15784.remove(l) & 255;
				if (k < j) {
					this.method_15485(l, k);
					this.method_15487(l, k, true);
				} else if (k > j) {
					this.method_15479(l, k, this.method_15490(this.field_15783 - 1, k));
					this.method_15485(l, this.field_15783 - 1);
					this.method_15487(l, j, false);
				}
			}

			this.field_15782 = this.field_15781 < this.field_15783;
			return i;
		}
	}

	protected abstract boolean method_15494(long l);

	protected abstract int method_15486(long l, long m, int i);

	protected abstract void method_15487(long l, int i, boolean bl);

	protected abstract int method_15480(long l);

	protected abstract void method_15485(long l, int i);

	protected abstract int method_15488(long l, long m, int i);
}
