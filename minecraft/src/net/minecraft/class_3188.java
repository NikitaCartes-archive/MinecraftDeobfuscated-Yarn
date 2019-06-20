package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;

public class class_3188 extends class_3195<class_3111> {
	private boolean field_13851;
	private class_1923[] field_13852;
	private final List<class_3449> field_13853 = Lists.<class_3449>newArrayList();
	private long field_13854;

	public class_3188(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	@Override
	public boolean method_14026(class_2794<?> arg, Random random, int i, int j) {
		if (this.field_13854 != arg.method_12101()) {
			this.method_13986();
		}

		if (!this.field_13851) {
			this.method_13985(arg);
			this.field_13851 = true;
		}

		for (class_1923 lv : this.field_13852) {
			if (i == lv.field_9181 && j == lv.field_9180) {
				return true;
			}
		}

		return false;
	}

	private void method_13986() {
		this.field_13851 = false;
		this.field_13852 = null;
		this.field_13853.clear();
	}

	@Override
	public class_3195.class_3774 method_14016() {
		return class_3188.class_3189::new;
	}

	@Override
	public String method_14019() {
		return "Stronghold";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	@Nullable
	@Override
	public class_2338 method_14015(class_1937 arg, class_2794<? extends class_2888> arg2, class_2338 arg3, int i, boolean bl) {
		if (!arg2.method_12098().method_8754(this)) {
			return null;
		} else {
			if (this.field_13854 != arg.method_8412()) {
				this.method_13986();
			}

			if (!this.field_13851) {
				this.method_13985(arg2);
				this.field_13851 = true;
			}

			class_2338 lv = null;
			class_2338.class_2339 lv2 = new class_2338.class_2339();
			double d = Double.MAX_VALUE;

			for (class_1923 lv3 : this.field_13852) {
				lv2.method_10103((lv3.field_9181 << 4) + 8, 32, (lv3.field_9180 << 4) + 8);
				double e = lv2.method_10262(arg3);
				if (lv == null) {
					lv = new class_2338(lv2);
					d = e;
				} else if (e < d) {
					lv = new class_2338(lv2);
					d = e;
				}
			}

			return lv;
		}
	}

	private void method_13985(class_2794<?> arg) {
		this.field_13854 = arg.method_12101();
		List<class_1959> list = Lists.<class_1959>newArrayList();

		for (class_1959 lv : class_2378.field_11153) {
			if (lv != null && arg.method_12097(lv, class_3031.field_13565)) {
				list.add(lv);
			}
		}

		int i = arg.method_12109().method_12563();
		int j = arg.method_12109().method_12561();
		int k = arg.method_12109().method_12565();
		this.field_13852 = new class_1923[j];
		int l = 0;

		for (class_3449 lv2 : this.field_13853) {
			if (l < this.field_13852.length) {
				this.field_13852[l++] = new class_1923(lv2.method_14967(), lv2.method_14966());
			}
		}

		Random random = new Random();
		random.setSeed(arg.method_12101());
		double d = random.nextDouble() * Math.PI * 2.0;
		int m = l;
		if (l < this.field_13852.length) {
			int n = 0;
			int o = 0;

			for (int p = 0; p < this.field_13852.length; p++) {
				double e = (double)(4 * i + i * o * 6) + (random.nextDouble() - 0.5) * (double)i * 2.5;
				int q = (int)Math.round(Math.cos(d) * e);
				int r = (int)Math.round(Math.sin(d) * e);
				class_2338 lv3 = arg.method_12098().method_8762((q << 4) + 8, (r << 4) + 8, 112, list, random);
				if (lv3 != null) {
					q = lv3.method_10263() >> 4;
					r = lv3.method_10260() >> 4;
				}

				if (p >= m) {
					this.field_13852[p] = new class_1923(q, r);
				}

				d += (Math.PI * 2) / (double)k;
				if (++n == k) {
					o++;
					n = 0;
					k += 2 * k / (o + 1);
					k = Math.min(k, this.field_13852.length - p);
					d += random.nextDouble() * Math.PI * 2.0;
				}
			}
		}
	}

	public static class class_3189 extends class_3449 {
		public class_3189(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l) {
			super(arg, i, j, arg2, arg3, k, l);
		}

		@Override
		public void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3) {
			int k = 0;
			long l = arg.method_12101();

			class_3421.class_3434 lv;
			do {
				this.field_15325.clear();
				this.field_15330 = class_3341.method_14665();
				this.field_16715.method_12663(l + (long)(k++), i, j);
				class_3421.method_14855();
				lv = new class_3421.class_3434(this.field_16715, (i << 4) + 2, (j << 4) + 2);
				this.field_15325.add(lv);
				lv.method_14918(lv, this.field_15325, this.field_16715);
				List<class_3443> list = lv.field_15282;

				while (!list.isEmpty()) {
					int m = this.field_16715.nextInt(list.size());
					class_3443 lv2 = (class_3443)list.remove(m);
					lv2.method_14918(lv, this.field_15325, this.field_16715);
				}

				this.method_14969();
				this.method_14978(arg.method_16398(), this.field_16715, 10);
			} while (this.field_15325.isEmpty() || lv.field_15283 == null);

			((class_3188)this.method_16656()).field_13853.add(this);
		}
	}
}
