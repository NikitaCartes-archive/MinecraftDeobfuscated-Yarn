package net.minecraft;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_3558<M extends class_3556<M>, S extends class_3560<M>> extends class_3554 implements class_3562 {
	private static final class_2350[] field_16513 = class_2350.values();
	protected final class_2823 field_15795;
	protected final class_1944 field_15792;
	protected final S field_15793;
	private boolean field_15794;
	private final class_2338.class_2339 field_16514 = new class_2338.class_2339();
	private final class_2338.class_2339 field_16515 = new class_2338.class_2339();
	private final class_2338.class_2339 field_16512 = new class_2338.class_2339();
	private long[] field_17397 = new long[2];
	private class_1922[] field_17398 = new class_1922[2];

	public class_3558(class_2823 arg, class_1944 arg2, S arg3) {
		super(16, 256, 8192);
		this.field_15795 = arg;
		this.field_15792 = arg2;
		this.field_15793 = arg3;
		this.method_17530();
	}

	@Override
	protected void method_15491(long l) {
		this.field_15793.method_15539();
		if (this.field_15793.method_15524(class_4076.method_18691(l))) {
			super.method_15491(l);
		}
	}

	@Nullable
	private class_1922 method_17529(int i, int j) {
		long l = class_1923.method_8331(i, j);

		for (int k = 0; k < 2; k++) {
			if (l == this.field_17397[k]) {
				return this.field_17398[k];
			}
		}

		class_1922 lv = this.field_15795.method_12246(i, j);

		for (int m = 1; m > 0; m--) {
			this.field_17397[m] = this.field_17397[m - 1];
			this.field_17398[m] = this.field_17398[m - 1];
		}

		this.field_17397[0] = l;
		this.field_17398[0] = lv;
		return lv;
	}

	private void method_17530() {
		Arrays.fill(this.field_17397, class_1923.field_17348);
		Arrays.fill(this.field_17398, null);
	}

	protected int method_16340(long l, long m) {
		this.field_15793.method_15539();
		if (!class_2338.method_10085(l) && !class_2338.method_10085(m)) {
			this.field_16514.method_16363(l);
			this.field_16515.method_16363(m);
			int i = class_4076.method_18675(this.field_16514.method_10263());
			int j = class_4076.method_18675(this.field_16514.method_10260());
			int k = class_4076.method_18675(this.field_16515.method_10263());
			int n = class_4076.method_18675(this.field_16515.method_10260());
			class_1922 lv = this.method_17529(k, n);
			if (lv == null) {
				return 16;
			} else {
				class_2680 lv2 = lv.method_8320(this.field_16515);
				class_1922 lv3 = this.field_15795.method_16399();
				int o = lv2.method_11581(lv3, this.field_16515);
				if (!lv2.method_16386() && o >= 15) {
					return 16;
				} else {
					class_1922 lv4;
					if (i == k && j == n) {
						lv4 = lv;
					} else {
						lv4 = this.method_17529(i, j);
					}

					if (lv4 == null) {
						return 16;
					} else {
						int p = Integer.signum(this.field_16515.method_10263() - this.field_16514.method_10263());
						int q = Integer.signum(this.field_16515.method_10264() - this.field_16514.method_10264());
						int r = Integer.signum(this.field_16515.method_10260() - this.field_16514.method_10260());
						class_2350 lv5 = class_2350.method_16365(this.field_16512.method_10103(p, q, r));
						if (lv5 == null) {
							return 16;
						} else {
							class_2680 lv6 = lv4.method_8320(this.field_16514);
							return method_20049(lv3, lv6, this.field_16514, lv2, this.field_16515, lv5, o);
						}
					}
				}
			}
		} else {
			return 0;
		}
	}

	public static int method_20049(class_1922 arg, class_2680 arg2, class_2338 arg3, class_2680 arg4, class_2338 arg5, class_2350 arg6, int i) {
		boolean bl = arg2.method_11619() && arg2.method_16386();
		boolean bl2 = arg4.method_11619() && arg4.method_16386();
		if (!bl && !bl2) {
			return i;
		} else {
			class_265 lv = bl ? arg2.method_11615(arg, arg3) : class_259.method_1073();
			class_265 lv2 = bl2 ? arg4.method_11615(arg, arg5) : class_259.method_1073();
			return class_259.method_1080(lv, lv2, arg6) ? 16 : i;
		}
	}

	@Override
	protected boolean method_15494(long l) {
		return l == Long.MAX_VALUE;
	}

	@Override
	protected int method_15486(long l, long m, int i) {
		return 0;
	}

	@Override
	protected int method_15480(long l) {
		return l == Long.MAX_VALUE ? 0 : 15 - this.field_15793.method_15537(l);
	}

	protected int method_15517(class_2804 arg, long l) {
		return 15
			- arg.method_12139(
				class_4076.method_18684(class_2338.method_10061(l)),
				class_4076.method_18684(class_2338.method_10071(l)),
				class_4076.method_18684(class_2338.method_10083(l))
			);
	}

	@Override
	protected void method_15485(long l, int i) {
		this.field_15793.method_15525(l, Math.min(15, 15 - i));
	}

	@Override
	protected int method_15488(long l, long m, int i) {
		return 0;
	}

	public boolean method_15518() {
		return this.method_15489() || this.field_15793.method_15489() || this.field_15793.method_15528();
	}

	public int method_15516(int i, boolean bl, boolean bl2) {
		if (!this.field_15794) {
			if (this.field_15793.method_15489()) {
				i = this.field_15793.method_15492(i);
				if (i == 0) {
					return i;
				}
			}

			this.field_15793.method_15527(this, bl, bl2);
		}

		this.field_15794 = true;
		if (this.method_15489()) {
			i = this.method_15492(i);
			this.method_17530();
			if (i == 0) {
				return i;
			}
		}

		this.field_15794 = false;
		this.field_15793.method_15530();
		return i;
	}

	protected void method_15515(long l, class_2804 arg) {
		this.field_15793.method_15532(l, arg);
	}

	@Nullable
	@Override
	public class_2804 method_15544(class_4076 arg) {
		return this.field_15793.method_15522(arg.method_18694(), false);
	}

	@Override
	public int method_15543(class_2338 arg) {
		return this.field_15793.method_15538(arg.method_10063());
	}

	@Environment(EnvType.CLIENT)
	public String method_15520(long l) {
		return "" + this.field_15793.method_15480(l);
	}

	public void method_15513(class_2338 arg) {
		long l = arg.method_10063();
		this.method_15491(l);

		for (class_2350 lv : field_16513) {
			this.method_15491(class_2338.method_10060(l, lv));
		}
	}

	public void method_15514(class_2338 arg, int i) {
	}

	@Override
	public void method_15551(class_4076 arg, boolean bl) {
		this.field_15793.method_15526(arg.method_18694(), bl);
	}

	public void method_15512(class_1923 arg, boolean bl) {
		long l = class_4076.method_18693(class_4076.method_18685(arg.field_9181, 0, arg.field_9180));
		this.field_15793.method_15535(l, bl);
	}
}
