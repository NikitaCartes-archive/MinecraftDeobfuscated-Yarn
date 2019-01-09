package net.minecraft;

import java.util.BitSet;

public final class class_244 extends class_251 {
	private final BitSet field_1359;
	private int field_1358;
	private int field_1357;
	private int field_1356;
	private int field_1355;
	private int field_1354;
	private int field_1360;

	public class_244(int i, int j, int k) {
		this(i, j, k, i, j, k, 0, 0, 0);
	}

	public class_244(int i, int j, int k, int l, int m, int n, int o, int p, int q) {
		super(i, j, k);
		this.field_1359 = new BitSet(i * j * k);
		this.field_1358 = l;
		this.field_1357 = m;
		this.field_1356 = n;
		this.field_1355 = o;
		this.field_1354 = p;
		this.field_1360 = q;
	}

	public class_244(class_251 arg) {
		super(arg.field_1374, arg.field_1373, arg.field_1372);
		if (arg instanceof class_244) {
			this.field_1359 = (BitSet)((class_244)arg).field_1359.clone();
		} else {
			this.field_1359 = new BitSet(this.field_1374 * this.field_1373 * this.field_1372);

			for (int i = 0; i < this.field_1374; i++) {
				for (int j = 0; j < this.field_1373; j++) {
					for (int k = 0; k < this.field_1372; k++) {
						if (arg.method_1063(i, j, k)) {
							this.field_1359.set(this.method_1039(i, j, k));
						}
					}
				}
			}
		}

		this.field_1358 = arg.method_1055(class_2350.class_2351.field_11048);
		this.field_1357 = arg.method_1055(class_2350.class_2351.field_11052);
		this.field_1356 = arg.method_1055(class_2350.class_2351.field_11051);
		this.field_1355 = arg.method_1045(class_2350.class_2351.field_11048);
		this.field_1354 = arg.method_1045(class_2350.class_2351.field_11052);
		this.field_1360 = arg.method_1045(class_2350.class_2351.field_11051);
	}

	protected int method_1039(int i, int j, int k) {
		return (i * this.field_1373 + j) * this.field_1372 + k;
	}

	@Override
	public boolean method_1063(int i, int j, int k) {
		return this.field_1359.get(this.method_1039(i, j, k));
	}

	@Override
	public void method_1049(int i, int j, int k, boolean bl, boolean bl2) {
		this.field_1359.set(this.method_1039(i, j, k), bl2);
		if (bl && bl2) {
			this.field_1358 = Math.min(this.field_1358, i);
			this.field_1357 = Math.min(this.field_1357, j);
			this.field_1356 = Math.min(this.field_1356, k);
			this.field_1355 = Math.max(this.field_1355, i + 1);
			this.field_1354 = Math.max(this.field_1354, j + 1);
			this.field_1360 = Math.max(this.field_1360, k + 1);
		}
	}

	@Override
	public boolean method_1056() {
		return this.field_1359.isEmpty();
	}

	@Override
	public int method_1055(class_2350.class_2351 arg) {
		return arg.method_10173(this.field_1358, this.field_1357, this.field_1356);
	}

	@Override
	public int method_1045(class_2350.class_2351 arg) {
		return arg.method_10173(this.field_1355, this.field_1354, this.field_1360);
	}

	@Override
	protected boolean method_1059(int i, int j, int k, int l) {
		if (k < 0 || l < 0 || i < 0) {
			return false;
		} else {
			return k < this.field_1374 && l < this.field_1373 && j <= this.field_1372
				? this.field_1359.nextClearBit(this.method_1039(k, l, i)) >= this.method_1039(k, l, j)
				: false;
		}
	}

	@Override
	protected void method_1060(int i, int j, int k, int l, boolean bl) {
		this.field_1359.set(this.method_1039(k, l, i), this.method_1039(k, l, j), bl);
	}

	static class_244 method_1040(class_251 arg, class_251 arg2, class_255 arg3, class_255 arg4, class_255 arg5, class_247 arg6) {
		class_244 lv = new class_244(arg3.method_1066().size() - 1, arg4.method_1066().size() - 1, arg5.method_1066().size() - 1);
		int[] is = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
		arg3.method_1065((i, j, k) -> {
			boolean[] bls = new boolean[]{false};
			boolean bl = arg4.method_1065((l, m, n) -> {
				boolean[] bls2 = new boolean[]{false};
				boolean blx = arg5.method_1065((o, p, q) -> {
					boolean blxx = arg6.apply(arg.method_1044(i, l, o), arg2.method_1044(j, m, p));
					if (blxx) {
						lv.field_1359.set(lv.method_1039(k, n, q));
						is[2] = Math.min(is[2], q);
						is[5] = Math.max(is[5], q);
						bls2[0] = true;
					}

					return true;
				});
				if (bls2[0]) {
					is[1] = Math.min(is[1], n);
					is[4] = Math.max(is[4], n);
					bls[0] = true;
				}

				return blx;
			});
			if (bls[0]) {
				is[0] = Math.min(is[0], k);
				is[3] = Math.max(is[3], k);
			}

			return bl;
		});
		lv.field_1358 = is[0];
		lv.field_1357 = is[1];
		lv.field_1356 = is[2];
		lv.field_1355 = is[3] + 1;
		lv.field_1354 = is[4] + 1;
		lv.field_1360 = is[5] + 1;
		return lv;
	}
}
