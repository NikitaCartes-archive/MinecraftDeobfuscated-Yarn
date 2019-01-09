package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;

public class class_3569 extends class_3560<class_3569.class_3570> {
	private static final class_2350[] field_15818 = new class_2350[]{
		class_2350.field_11043, class_2350.field_11035, class_2350.field_11039, class_2350.field_11034
	};
	private final LongSet field_15820 = new LongOpenHashSet();
	private final LongSet field_15815 = new LongOpenHashSet();
	private final LongSet field_15816 = new LongOpenHashSet();
	private final LongSet field_15817 = new LongOpenHashSet();
	private volatile boolean field_15819;

	protected class_3569(class_2823 arg) {
		super(class_1944.field_9284, arg, new class_3569.class_3570(new Long2ObjectOpenHashMap<>(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
	}

	@Override
	protected int method_15538(long l) {
		long m = class_2338.method_10090(l);
		int i = class_2338.method_10071(m);
		class_3569.class_3570 lv = this.field_15806;
		int j = lv.field_15821.get(class_2338.method_10065(m));
		if (j != lv.field_15822 && i < j) {
			class_2804 lv2 = this.method_15533(lv, m);
			if (lv2 == null) {
				for (l = class_2338.method_10091(l); lv2 == null; lv2 = this.method_15533(lv, m)) {
					m = class_2338.method_10096(m, 0, 16, 0);
					i += 16;
					if (i >= j) {
						return 15;
					}

					l = class_2338.method_10096(l, 0, 16, 0);
				}
			}

			return lv2.method_12139(class_2338.method_10061(l) & 15, class_2338.method_10071(l) & 15, class_2338.method_10083(l) & 15);
		} else {
			return 15;
		}
	}

	@Override
	protected void method_15523(long l) {
		int i = class_2338.method_10071(l);
		if (this.field_15796.field_15822 > i) {
			this.field_15796.field_15822 = i;
			this.field_15796.field_15821.defaultReturnValue(this.field_15796.field_15822);
		}

		long m = class_2338.method_10065(l);
		int j = this.field_15796.field_15821.get(m);
		if (j < i + 16) {
			this.field_15796.field_15821.put(m, i + 16);
			if (!this.field_15817.contains(m)) {
				this.field_15815.add(l);
				this.field_15816.remove(l);
				if (j > this.field_15796.field_15822) {
					long n = class_2338.method_10064(class_2338.method_10061(l), j - 16, class_2338.method_10083(l));
					this.field_15815.remove(n);
					this.field_15816.add(n);
				}

				this.method_15569();
			}
		}
	}

	private void method_15569() {
		this.field_15819 = !this.field_15815.isEmpty() || !this.field_15816.isEmpty();
	}

	@Override
	protected void method_15534(long l) {
		long m = class_2338.method_10065(l);
		boolean bl = this.field_15817.contains(m);
		if (!bl) {
			this.field_15816.add(l);
			this.field_15815.remove(l);
		}

		int i = class_2338.method_10071(l);
		if (this.field_15796.field_15821.get(m) == i + 16) {
			long n;
			for (n = l; !this.method_15524(n) && this.method_15567(i); n = class_2338.method_10096(n, 0, -16, 0)) {
				i -= 16;
			}

			if (this.method_15524(n)) {
				this.field_15796.field_15821.put(m, i + 16);
				if (!bl) {
					this.field_15815.add(n);
					this.field_15816.remove(n);
				}
			} else {
				this.field_15796.field_15821.remove(m);
			}
		}

		if (!bl) {
			this.method_15569();
		}
	}

	@Override
	protected void method_15535(long l, boolean bl) {
		if (bl && this.field_15817.add(l)) {
			int i = this.field_15796.field_15821.get(l);
			if (i != this.field_15796.field_15822) {
				long m = class_2338.method_10096(l, 0, i - 16, 0);
				this.field_15816.add(m);
				this.field_15815.remove(m);
				this.method_15569();
			}
		} else if (!bl && this.field_15817.remove(l)) {
			int i = this.field_15796.field_15821.get(l);
			if (i != this.field_15796.field_15822) {
				long m = class_2338.method_10096(l, 0, i - 16, 0);
				this.field_15815.add(m);
				this.field_15816.remove(m);
				this.method_15569();
			}
		}
	}

	@Override
	protected boolean method_15528() {
		return super.method_15528() || this.field_15819;
	}

	@Override
	protected void method_15527(class_3558<class_3569.class_3570, ?> arg, boolean bl, boolean bl2) {
		super.method_15527(arg, bl, bl2);
		if (bl) {
			if (!this.field_15815.isEmpty()) {
				LongIterator var4 = this.field_15815.iterator();

				while (var4.hasNext()) {
					long l = (Long)var4.next();
					int i = this.method_15480(l);
					if (i != 2 && !this.field_15816.contains(l) && this.field_15820.add(l)) {
						if (i == 1) {
							this.method_15536(arg, l);
							if (this.field_15802.add(l)) {
								this.field_15796.method_15502(l);
							}

							Arrays.fill(this.method_15522(l, true).method_12137(), (byte)-1);
							int j = class_2338.method_10061(l);
							int k = class_2338.method_10071(l);
							int m = class_2338.method_10083(l);

							for (class_2350 lv : field_15818) {
								long n = class_2338.method_10096(l, lv.method_10148() * 16, lv.method_10164() * 16, lv.method_10165() * 16);
								if ((this.field_15816.contains(n) || !this.field_15820.contains(n) && !this.field_15815.contains(n)) && this.method_15524(n)) {
									for (int o = 0; o < 16; o++) {
										for (int p = 0; p < 16; p++) {
											long q;
											long r;
											switch (lv) {
												case field_11043:
													q = class_2338.method_10064(j + o, k + p, m);
													r = class_2338.method_10064(j + o, k + p, m - 1);
													break;
												case field_11035:
													q = class_2338.method_10064(j + o, k + p, m + 16 - 1);
													r = class_2338.method_10064(j + o, k + p, m + 16);
													break;
												case field_11039:
													q = class_2338.method_10064(j, k + o, m + p);
													r = class_2338.method_10064(j - 1, k + o, m + p);
													break;
												default:
													q = class_2338.method_10064(j + 16 - 1, k + o, m + p);
													r = class_2338.method_10064(j + 16, k + o, m + p);
											}

											arg.method_15478(q, r, arg.method_15488(q, r, 0), true);
										}
									}
								}
							}

							for (int s = 0; s < 16; s++) {
								for (int t = 0; t < 16; t++) {
									long u = class_2338.method_10096(l, s, 0, t);
									long n = class_2338.method_10096(l, s, -1, t);
									arg.method_15478(u, n, arg.method_15488(u, n, 0), true);
								}
							}
						} else {
							for (int j = 0; j < 16; j++) {
								for (int k = 0; k < 16; k++) {
									long v = class_2338.method_10096(l, j, 15, k);
									arg.method_15478(-1L, v, 0, true);
								}
							}
						}
					}
				}
			}

			this.field_15815.clear();
			if (!this.field_15816.isEmpty()) {
				LongIterator var23 = this.field_15816.iterator();

				while (var23.hasNext()) {
					long l = (Long)var23.next();
					if (this.field_15820.remove(l) && this.method_15524(l)) {
						for (int i = 0; i < 16; i++) {
							for (int j = 0; j < 16; j++) {
								long w = class_2338.method_10096(l, i, 15, j);
								arg.method_15478(-1L, w, 15, false);
							}
						}
					}
				}
			}

			this.field_15816.clear();
			this.field_15819 = false;
		}
	}

	protected boolean method_15567(int i) {
		return i >= this.field_15796.field_15822;
	}

	protected boolean method_15565(long l) {
		int i = class_2338.method_10071(l);
		if ((i & 15) != 15) {
			return false;
		} else {
			long m = class_2338.method_10090(l);
			long n = class_2338.method_10065(m);
			if (this.field_15817.contains(n)) {
				return false;
			} else {
				int j = this.field_15796.field_15821.get(n);
				return j == i + 16;
			}
		}
	}

	protected boolean method_15568(long l) {
		long m = class_2338.method_10065(l);
		int i = this.field_15796.field_15821.get(m);
		return i == this.field_15796.field_15822 || class_2338.method_10071(l) >= i;
	}

	protected boolean method_15566(long l) {
		long m = class_2338.method_10065(l);
		return this.field_15817.contains(m);
	}

	public static final class class_3570 extends class_3556<class_3569.class_3570> {
		private int field_15822;
		private final Long2IntOpenHashMap field_15821;

		public class_3570(Long2ObjectOpenHashMap<class_2804> long2ObjectOpenHashMap, Long2IntOpenHashMap long2IntOpenHashMap, int i) {
			super(long2ObjectOpenHashMap);
			this.field_15821 = long2IntOpenHashMap;
			long2IntOpenHashMap.defaultReturnValue(i);
			this.field_15822 = i;
		}

		public class_3569.class_3570 method_15572() {
			return new class_3569.class_3570(this.field_15791.clone(), this.field_15821.clone(), this.field_15822);
		}
	}
}
