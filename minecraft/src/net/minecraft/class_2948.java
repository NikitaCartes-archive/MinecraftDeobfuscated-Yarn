package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class class_2948 extends class_2944<class_3111> {
	private static final class_2680 field_13342 = class_2246.field_10431.method_9564();
	private static final class_2680 field_13343 = class_2246.field_10503.method_9564();

	public class_2948(Function<Dynamic<?>, ? extends class_3111> function, boolean bl) {
		super(function, bl);
	}

	private void method_12811(class_3747 arg, class_2338 arg2, float f) {
		int i = (int)((double)f + 0.618);

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k <= i; k++) {
				if (Math.pow((double)Math.abs(j) + 0.5, 2.0) + Math.pow((double)Math.abs(k) + 0.5, 2.0) <= (double)(f * f)) {
					class_2338 lv = arg2.method_10069(j, 0, k);
					if (method_16420(arg, lv)) {
						this.method_13153(arg, lv, field_13343);
					}
				}
			}
		}
	}

	private float method_12807(int i, int j) {
		if ((float)j < (float)i * 0.3F) {
			return -1.0F;
		} else {
			float f = (float)i / 2.0F;
			float g = f - (float)j;
			float h = class_3532.method_15355(f * f - g * g);
			if (g == 0.0F) {
				h = f;
			} else if (Math.abs(g) >= f) {
				return 0.0F;
			}

			return h * 0.5F;
		}
	}

	private float method_12804(int i) {
		if (i < 0 || i >= 5) {
			return -1.0F;
		} else {
			return i != 0 && i != 4 ? 3.0F : 2.0F;
		}
	}

	private void method_12810(class_3747 arg, class_2338 arg2) {
		for (int i = 0; i < 5; i++) {
			this.method_12811(arg, arg2.method_10086(i), this.method_12804(i));
		}
	}

	private int method_12808(Set<class_2338> set, class_3747 arg, class_2338 arg2, class_2338 arg3, boolean bl) {
		if (!bl && Objects.equals(arg2, arg3)) {
			return -1;
		} else {
			class_2338 lv = arg3.method_10069(-arg2.method_10263(), -arg2.method_10264(), -arg2.method_10260());
			int i = this.method_12805(lv);
			float f = (float)lv.method_10263() / (float)i;
			float g = (float)lv.method_10264() / (float)i;
			float h = (float)lv.method_10260() / (float)i;

			for (int j = 0; j <= i; j++) {
				class_2338 lv2 = arg2.method_10080((double)(0.5F + (float)j * f), (double)(0.5F + (float)j * g), (double)(0.5F + (float)j * h));
				if (bl) {
					this.method_12773(set, arg, lv2, field_13342.method_11657(class_2410.field_11459, this.method_12809(arg2, lv2)));
				} else if (!method_16432(arg, lv2)) {
					return j;
				}
			}

			return -1;
		}
	}

	private int method_12805(class_2338 arg) {
		int i = class_3532.method_15382(arg.method_10263());
		int j = class_3532.method_15382(arg.method_10264());
		int k = class_3532.method_15382(arg.method_10260());
		if (k > i && k > j) {
			return k;
		} else {
			return j > i ? j : i;
		}
	}

	private class_2350.class_2351 method_12809(class_2338 arg, class_2338 arg2) {
		class_2350.class_2351 lv = class_2350.class_2351.field_11052;
		int i = Math.abs(arg2.method_10263() - arg.method_10263());
		int j = Math.abs(arg2.method_10260() - arg.method_10260());
		int k = Math.max(i, j);
		if (k > 0) {
			if (i == k) {
				lv = class_2350.class_2351.field_11048;
			} else if (j == k) {
				lv = class_2350.class_2351.field_11051;
			}
		}

		return lv;
	}

	private void method_12802(class_3747 arg, int i, class_2338 arg2, List<class_2948.class_2949> list) {
		for (class_2948.class_2949 lv : list) {
			if (this.method_12801(i, lv.method_12812() - arg2.method_10264())) {
				this.method_12810(arg, lv);
			}
		}
	}

	private boolean method_12801(int i, int j) {
		return (double)j >= (double)i * 0.2;
	}

	private void method_12806(Set<class_2338> set, class_3747 arg, class_2338 arg2, int i) {
		this.method_12808(set, arg, arg2, arg2.method_10086(i), true);
	}

	private void method_12800(Set<class_2338> set, class_3747 arg, int i, class_2338 arg2, List<class_2948.class_2949> list) {
		for (class_2948.class_2949 lv : list) {
			int j = lv.method_12812();
			class_2338 lv2 = new class_2338(arg2.method_10263(), j, arg2.method_10260());
			if (!lv2.equals(lv) && this.method_12801(i, j - arg2.method_10264())) {
				this.method_12808(set, arg, lv2, lv, true);
			}
		}
	}

	@Override
	public boolean method_12775(Set<class_2338> set, class_3747 arg, Random random, class_2338 arg2) {
		Random random2 = new Random(random.nextLong());
		int i = this.method_12803(set, arg, arg2, 5 + random2.nextInt(12));
		if (i == -1) {
			return false;
		} else {
			this.method_16427(arg, arg2.method_10074());
			int j = (int)((double)i * 0.618);
			if (j >= i) {
				j = i - 1;
			}

			double d = 1.0;
			int k = (int)(1.382 + Math.pow(1.0 * (double)i / 13.0, 2.0));
			if (k < 1) {
				k = 1;
			}

			int l = arg2.method_10264() + j;
			int m = i - 5;
			List<class_2948.class_2949> list = Lists.<class_2948.class_2949>newArrayList();
			list.add(new class_2948.class_2949(arg2.method_10086(m), l));

			for (; m >= 0; m--) {
				float f = this.method_12807(i, m);
				if (!(f < 0.0F)) {
					for (int n = 0; n < k; n++) {
						double e = 1.0;
						double g = 1.0 * (double)f * ((double)random2.nextFloat() + 0.328);
						double h = (double)(random2.nextFloat() * 2.0F) * Math.PI;
						double o = g * Math.sin(h) + 0.5;
						double p = g * Math.cos(h) + 0.5;
						class_2338 lv = arg2.method_10080(o, (double)(m - 1), p);
						class_2338 lv2 = lv.method_10086(5);
						if (this.method_12808(set, arg, lv, lv2, false) == -1) {
							int q = arg2.method_10263() - lv.method_10263();
							int r = arg2.method_10260() - lv.method_10260();
							double s = (double)lv.method_10264() - Math.sqrt((double)(q * q + r * r)) * 0.381;
							int t = s > (double)l ? l : (int)s;
							class_2338 lv3 = new class_2338(arg2.method_10263(), t, arg2.method_10260());
							if (this.method_12808(set, arg, lv3, lv, false) == -1) {
								list.add(new class_2948.class_2949(lv, lv3.method_10264()));
							}
						}
					}
				}
			}

			this.method_12802(arg, i, arg2, list);
			this.method_12806(set, arg, arg2, j);
			this.method_12800(set, arg, i, arg2, list);
			return true;
		}
	}

	private int method_12803(Set<class_2338> set, class_3747 arg, class_2338 arg2, int i) {
		if (!method_16433(arg, arg2.method_10074())) {
			return -1;
		} else {
			int j = this.method_12808(set, arg, arg2, arg2.method_10086(i - 1), false);
			if (j == -1) {
				return i;
			} else {
				return j < 6 ? -1 : j;
			}
		}
	}

	static class class_2949 extends class_2338 {
		private final int field_13344;

		public class_2949(class_2338 arg, int i) {
			super(arg.method_10263(), arg.method_10264(), arg.method_10260());
			this.field_13344 = i;
		}

		public int method_12812() {
			return this.field_13344;
		}
	}
}
