package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;

public abstract class class_3560<M extends class_3556<M>> extends class_4079 {
	protected static final class_2804 field_15801 = new class_2804();
	private static final class_2350[] field_15799 = class_2350.values();
	private final class_1944 field_15805;
	private final class_2823 field_15803;
	protected final LongSet field_15808 = new LongOpenHashSet();
	protected final LongSet field_15797 = new LongOpenHashSet();
	protected final LongSet field_15804 = new LongOpenHashSet();
	protected volatile M field_15806;
	protected final M field_15796;
	protected final LongSet field_15802 = new LongOpenHashSet();
	protected final LongSet field_16448 = new LongOpenHashSet();
	protected final Long2ObjectMap<class_2804> field_15807 = new Long2ObjectOpenHashMap<>();
	private final LongSet field_15798 = new LongOpenHashSet();
	protected volatile boolean field_15800;

	protected class_3560(class_1944 arg, class_2823 arg2, M arg3) {
		super(3, 16, 256);
		this.field_15805 = arg;
		this.field_15803 = arg2;
		this.field_15796 = arg3;
		this.field_15806 = arg3.method_15504();
		this.field_15806.method_16188();
	}

	protected boolean method_15524(long l) {
		return this.method_15522(l, true) != null;
	}

	@Nullable
	protected class_2804 method_15522(long l, boolean bl) {
		return this.method_15533(bl ? this.field_15796 : this.field_15806, l);
	}

	@Nullable
	protected class_2804 method_15533(M arg, long l) {
		return arg.method_15501(l);
	}

	protected abstract int method_15538(long l);

	protected int method_15537(long l) {
		long m = class_4076.method_18691(l);
		class_2804 lv = this.method_15522(m, true);
		return lv.method_12139(
			class_4076.method_18684(class_2338.method_10061(l)),
			class_4076.method_18684(class_2338.method_10071(l)),
			class_4076.method_18684(class_2338.method_10083(l))
		);
	}

	protected void method_15525(long l, int i) {
		long m = class_4076.method_18691(l);
		if (this.field_15802.add(m)) {
			this.field_15796.method_15502(m);
		}

		class_2804 lv = this.method_15522(m, true);
		lv.method_12145(
			class_4076.method_18684(class_2338.method_10061(l)),
			class_4076.method_18684(class_2338.method_10071(l)),
			class_4076.method_18684(class_2338.method_10083(l)),
			i
		);

		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				for (int n = -1; n <= 1; n++) {
					this.field_16448.add(class_4076.method_18691(class_2338.method_10096(l, k, n, j)));
				}
			}
		}
	}

	@Override
	protected int method_15480(long l) {
		if (l == Long.MAX_VALUE) {
			return 2;
		} else if (this.field_15808.contains(l)) {
			return 0;
		} else {
			return !this.field_15798.contains(l) && this.field_15796.method_15503(l) ? 1 : 2;
		}
	}

	@Override
	protected int method_18749(long l) {
		if (this.field_15797.contains(l)) {
			return 2;
		} else {
			return !this.field_15808.contains(l) && !this.field_15804.contains(l) ? 2 : 0;
		}
	}

	@Override
	protected void method_15485(long l, int i) {
		int j = this.method_15480(l);
		if (j != 0 && i == 0) {
			this.field_15808.add(l);
			this.field_15804.remove(l);
		}

		if (j == 0 && i != 0) {
			this.field_15808.remove(l);
			this.field_15797.remove(l);
		}

		if (j >= 2 && i != 2) {
			if (this.field_15798.contains(l)) {
				this.field_15798.remove(l);
			} else {
				this.field_15796.method_15499(l, this.method_15529(l));
				this.field_15802.add(l);
				this.method_15523(l);

				for (int k = -1; k <= 1; k++) {
					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 1; n++) {
							this.field_16448.add(class_4076.method_18691(class_2338.method_10096(l, m, n, k)));
						}
					}
				}
			}
		}

		if (j != 2 && i >= 2) {
			this.field_15798.add(l);
		}

		this.field_15800 = !this.field_15798.isEmpty();
	}

	private class_2804 method_15529(long l) {
		class_2804 lv = this.field_15807.get(l);
		return lv != null ? lv : new class_2804();
	}

	protected void method_15536(class_3558<?, ?> arg, long l) {
		int i = class_4076.method_18688(class_4076.method_18686(l));
		int j = class_4076.method_18688(class_4076.method_18689(l));
		int k = class_4076.method_18688(class_4076.method_18690(l));

		for (int m = 0; m < 16; m++) {
			for (int n = 0; n < 16; n++) {
				for (int o = 0; o < 16; o++) {
					long p = class_2338.method_10064(i + m, j + n, k + o);
					arg.method_15483(p);
				}
			}
		}
	}

	protected boolean method_15528() {
		return this.field_15800;
	}

	protected void method_15527(class_3558<M, ?> arg, boolean bl, boolean bl2) {
		if (this.method_15528() || !this.field_15807.isEmpty()) {
			LongIterator objectIterator = this.field_15798.iterator();

			while (objectIterator.hasNext()) {
				long l = (Long)objectIterator.next();
				this.field_15807.remove(l);
				this.method_15536(arg, l);
				this.field_15796.method_15500(l);
			}

			this.field_15796.method_15505();
			objectIterator = this.field_15798.iterator();

			while (objectIterator.hasNext()) {
				long l = (Long)objectIterator.next();
				this.method_15534(l);
			}

			this.field_15798.clear();
			this.field_15800 = false;

			for (Entry<class_2804> entry : this.field_15807.long2ObjectEntrySet()) {
				long m = entry.getLongKey();
				if (this.method_15524(m)) {
					class_2804 lv = (class_2804)entry.getValue();
					if (this.field_15796.method_15501(m) != lv) {
						this.method_15536(arg, m);
						this.field_15796.method_15499(m, lv);
						this.field_15802.add(m);
					}
				}
			}

			this.field_15796.method_15505();
			if (!bl2) {
				objectIterator = this.field_15807.keySet().iterator();

				while (objectIterator.hasNext()) {
					long l = (Long)objectIterator.next();
					if (this.method_15524(l)) {
						int i = class_4076.method_18688(class_4076.method_18686(l));
						int j = class_4076.method_18688(class_4076.method_18689(l));
						int k = class_4076.method_18688(class_4076.method_18690(l));

						for (class_2350 lv2 : field_15799) {
							long n = class_4076.method_18679(l, lv2);
							if (!this.field_15807.containsKey(n) && this.method_15524(n)) {
								for (int o = 0; o < 16; o++) {
									for (int p = 0; p < 16; p++) {
										long q;
										long r;
										switch (lv2) {
											case field_11033:
												q = class_2338.method_10064(i + p, j, k + o);
												r = class_2338.method_10064(i + p, j - 1, k + o);
												break;
											case field_11036:
												q = class_2338.method_10064(i + p, j + 16 - 1, k + o);
												r = class_2338.method_10064(i + p, j + 16, k + o);
												break;
											case field_11043:
												q = class_2338.method_10064(i + o, j + p, k);
												r = class_2338.method_10064(i + o, j + p, k - 1);
												break;
											case field_11035:
												q = class_2338.method_10064(i + o, j + p, k + 16 - 1);
												r = class_2338.method_10064(i + o, j + p, k + 16);
												break;
											case field_11039:
												q = class_2338.method_10064(i, j + o, k + p);
												r = class_2338.method_10064(i - 1, j + o, k + p);
												break;
											default:
												q = class_2338.method_10064(i + 16 - 1, j + o, k + p);
												r = class_2338.method_10064(i + 16, j + o, k + p);
										}

										arg.method_15478(q, r, arg.method_15488(q, r, arg.method_15480(q)), false);
										arg.method_15478(r, q, arg.method_15488(r, q, arg.method_15480(r)), false);
									}
								}
							}
						}
					}
				}
			}

			ObjectIterator<Entry<class_2804>> objectIteratorx = this.field_15807.long2ObjectEntrySet().iterator();

			while (objectIteratorx.hasNext()) {
				Entry<class_2804> entryx = (Entry<class_2804>)objectIteratorx.next();
				long m = entryx.getLongKey();
				if (this.method_15524(m)) {
					objectIteratorx.remove();
				}
			}
		}
	}

	protected void method_15523(long l) {
	}

	protected void method_15534(long l) {
	}

	protected void method_15535(long l, boolean bl) {
	}

	protected void method_15532(long l, class_2804 arg) {
		this.field_15807.put(l, arg);
	}

	protected void method_15526(long l, boolean bl) {
		boolean bl2 = this.field_15808.contains(l);
		if (!bl2 && !bl) {
			this.field_15804.add(l);
			this.method_15478(Long.MAX_VALUE, l, 0, true);
		}

		if (bl2 && bl) {
			this.field_15797.add(l);
			this.method_15478(Long.MAX_VALUE, l, 2, false);
		}
	}

	protected void method_15539() {
		if (this.method_15489()) {
			this.method_15492(Integer.MAX_VALUE);
		}
	}

	protected void method_15530() {
		if (!this.field_15802.isEmpty()) {
			M lv = this.field_15796.method_15504();
			lv.method_16188();
			this.field_15806 = lv;
			this.field_15802.clear();
		}

		if (!this.field_16448.isEmpty()) {
			LongIterator longIterator = this.field_16448.iterator();

			while (longIterator.hasNext()) {
				long l = longIterator.nextLong();
				this.field_15803.method_12247(this.field_15805, class_4076.method_18677(l));
			}

			this.field_16448.clear();
		}
	}
}
