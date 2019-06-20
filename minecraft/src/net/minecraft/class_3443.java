package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public abstract class class_3443 {
	protected static final class_2680 field_15314 = class_2246.field_10543.method_9564();
	protected class_3341 field_15315;
	@Nullable
	private class_2350 field_15312;
	private class_2415 field_15310;
	private class_2470 field_15313;
	protected int field_15316;
	private final class_3773 field_16712;
	private static final Set<class_2248> field_15311 = ImmutableSet.<class_2248>builder()
		.add(class_2246.field_10364)
		.add(class_2246.field_10336)
		.add(class_2246.field_10099)
		.add(class_2246.field_10620)
		.add(class_2246.field_10020)
		.add(class_2246.field_10132)
		.add(class_2246.field_10144)
		.add(class_2246.field_10299)
		.add(class_2246.field_10319)
		.add(class_2246.field_9983)
		.add(class_2246.field_10576)
		.build();

	protected class_3443(class_3773 arg, int i) {
		this.field_16712 = arg;
		this.field_15316 = i;
	}

	public class_3443(class_3773 arg, class_2487 arg2) {
		this(arg, arg2.method_10550("GD"));
		if (arg2.method_10545("BB")) {
			this.field_15315 = new class_3341(arg2.method_10561("BB"));
		}

		int i = arg2.method_10550("O");
		this.method_14926(i == -1 ? null : class_2350.method_10139(i));
	}

	public final class_2487 method_14946() {
		class_2487 lv = new class_2487();
		lv.method_10582("id", class_2378.field_16645.method_10221(this.method_16653()).toString());
		lv.method_10566("BB", this.field_15315.method_14658());
		class_2350 lv2 = this.method_14934();
		lv.method_10569("O", lv2 == null ? -1 : lv2.method_10161());
		lv.method_10569("GD", this.field_15316);
		this.method_14943(lv);
		return lv;
	}

	protected abstract void method_14943(class_2487 arg);

	public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
	}

	public abstract boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3);

	public class_3341 method_14935() {
		return this.field_15315;
	}

	public int method_14923() {
		return this.field_15316;
	}

	public boolean method_16654(class_1923 arg, int i) {
		int j = arg.field_9181 << 4;
		int k = arg.field_9180 << 4;
		return this.field_15315.method_14669(j - i, k - i, j + 15 + i, k + 15 + i);
	}

	public static class_3443 method_14932(List<class_3443> list, class_3341 arg) {
		for (class_3443 lv : list) {
			if (lv.method_14935() != null && lv.method_14935().method_14657(arg)) {
				return lv;
			}
		}

		return null;
	}

	protected boolean method_14937(class_1922 arg, class_3341 arg2) {
		int i = Math.max(this.field_15315.field_14381 - 1, arg2.field_14381);
		int j = Math.max(this.field_15315.field_14380 - 1, arg2.field_14380);
		int k = Math.max(this.field_15315.field_14379 - 1, arg2.field_14379);
		int l = Math.min(this.field_15315.field_14378 + 1, arg2.field_14378);
		int m = Math.min(this.field_15315.field_14377 + 1, arg2.field_14377);
		int n = Math.min(this.field_15315.field_14376 + 1, arg2.field_14376);
		class_2338.class_2339 lv = new class_2338.class_2339();

		for (int o = i; o <= l; o++) {
			for (int p = k; p <= n; p++) {
				if (arg.method_8320(lv.method_10103(o, j, p)).method_11620().method_15797()) {
					return true;
				}

				if (arg.method_8320(lv.method_10103(o, m, p)).method_11620().method_15797()) {
					return true;
				}
			}
		}

		for (int o = i; o <= l; o++) {
			for (int p = j; p <= m; p++) {
				if (arg.method_8320(lv.method_10103(o, p, k)).method_11620().method_15797()) {
					return true;
				}

				if (arg.method_8320(lv.method_10103(o, p, n)).method_11620().method_15797()) {
					return true;
				}
			}
		}

		for (int o = k; o <= n; o++) {
			for (int p = j; p <= m; p++) {
				if (arg.method_8320(lv.method_10103(i, p, o)).method_11620().method_15797()) {
					return true;
				}

				if (arg.method_8320(lv.method_10103(l, p, o)).method_11620().method_15797()) {
					return true;
				}
			}
		}

		return false;
	}

	protected int method_14928(int i, int j) {
		class_2350 lv = this.method_14934();
		if (lv == null) {
			return i;
		} else {
			switch (lv) {
				case field_11043:
				case field_11035:
					return this.field_15315.field_14381 + i;
				case field_11039:
					return this.field_15315.field_14378 - j;
				case field_11034:
					return this.field_15315.field_14381 + j;
				default:
					return i;
			}
		}
	}

	protected int method_14924(int i) {
		return this.method_14934() == null ? i : i + this.field_15315.field_14380;
	}

	protected int method_14941(int i, int j) {
		class_2350 lv = this.method_14934();
		if (lv == null) {
			return j;
		} else {
			switch (lv) {
				case field_11043:
					return this.field_15315.field_14376 - j;
				case field_11035:
					return this.field_15315.field_14379 + j;
				case field_11039:
				case field_11034:
					return this.field_15315.field_14379 + i;
				default:
					return j;
			}
		}
	}

	protected void method_14917(class_1936 arg, class_2680 arg2, int i, int j, int k, class_3341 arg3) {
		class_2338 lv = new class_2338(this.method_14928(i, k), this.method_14924(j), this.method_14941(i, k));
		if (arg3.method_14662(lv)) {
			if (this.field_15310 != class_2415.field_11302) {
				arg2 = arg2.method_11605(this.field_15310);
			}

			if (this.field_15313 != class_2470.field_11467) {
				arg2 = arg2.method_11626(this.field_15313);
			}

			arg.method_8652(lv, arg2, 2);
			class_3610 lv2 = arg.method_8316(lv);
			if (!lv2.method_15769()) {
				arg.method_8405().method_8676(lv, lv2.method_15772(), 0);
			}

			if (field_15311.contains(arg2.method_11614())) {
				arg.method_16955(lv).method_12039(lv);
			}
		}
	}

	protected class_2680 method_14929(class_1922 arg, int i, int j, int k, class_3341 arg2) {
		int l = this.method_14928(i, k);
		int m = this.method_14924(j);
		int n = this.method_14941(i, k);
		class_2338 lv = new class_2338(l, m, n);
		return !arg2.method_14662(lv) ? class_2246.field_10124.method_9564() : arg.method_8320(lv);
	}

	protected boolean method_14939(class_1941 arg, int i, int j, int k, class_3341 arg2) {
		int l = this.method_14928(i, k);
		int m = this.method_14924(j + 1);
		int n = this.method_14941(i, k);
		class_2338 lv = new class_2338(l, m, n);
		return !arg2.method_14662(lv) ? false : m < arg.method_8589(class_2902.class_2903.field_13195, l, n);
	}

	protected void method_14942(class_1936 arg, class_3341 arg2, int i, int j, int k, int l, int m, int n) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					this.method_14917(arg, class_2246.field_10124.method_9564(), p, o, q, arg2);
				}
			}
		}
	}

	protected void method_14940(class_1936 arg, class_3341 arg2, int i, int j, int k, int l, int m, int n, class_2680 arg3, class_2680 arg4, boolean bl) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					if (!bl || !this.method_14929(arg, p, o, q, arg2).method_11588()) {
						if (o != j && o != m && p != i && p != l && q != k && q != n) {
							this.method_14917(arg, arg4, p, o, q, arg2);
						} else {
							this.method_14917(arg, arg3, p, o, q, arg2);
						}
					}
				}
			}
		}
	}

	protected void method_14938(class_1936 arg, class_3341 arg2, int i, int j, int k, int l, int m, int n, boolean bl, Random random, class_3443.class_3444 arg3) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					if (!bl || !this.method_14929(arg, p, o, q, arg2).method_11588()) {
						arg3.method_14948(random, p, o, q, o == j || o == m || p == i || p == l || q == k || q == n);
						this.method_14917(arg, arg3.method_14947(), p, o, q, arg2);
					}
				}
			}
		}
	}

	protected void method_14933(
		class_1936 arg, class_3341 arg2, Random random, float f, int i, int j, int k, int l, int m, int n, class_2680 arg3, class_2680 arg4, boolean bl, boolean bl2
	) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					if (!(random.nextFloat() > f) && (!bl || !this.method_14929(arg, p, o, q, arg2).method_11588()) && (!bl2 || this.method_14939(arg, p, o, q, arg2))) {
						if (o != j && o != m && p != i && p != l && q != k && q != n) {
							this.method_14917(arg, arg4, p, o, q, arg2);
						} else {
							this.method_14917(arg, arg3, p, o, q, arg2);
						}
					}
				}
			}
		}
	}

	protected void method_14945(class_1936 arg, class_3341 arg2, Random random, float f, int i, int j, int k, class_2680 arg3) {
		if (random.nextFloat() < f) {
			this.method_14917(arg, arg3, i, j, k, arg2);
		}
	}

	protected void method_14919(class_1936 arg, class_3341 arg2, int i, int j, int k, int l, int m, int n, class_2680 arg3, boolean bl) {
		float f = (float)(l - i + 1);
		float g = (float)(m - j + 1);
		float h = (float)(n - k + 1);
		float o = (float)i + f / 2.0F;
		float p = (float)k + h / 2.0F;

		for (int q = j; q <= m; q++) {
			float r = (float)(q - j) / g;

			for (int s = i; s <= l; s++) {
				float t = ((float)s - o) / (f * 0.5F);

				for (int u = k; u <= n; u++) {
					float v = ((float)u - p) / (h * 0.5F);
					if (!bl || !this.method_14929(arg, s, q, u, arg2).method_11588()) {
						float w = t * t + r * r + v * v;
						if (w <= 1.05F) {
							this.method_14917(arg, arg3, s, q, u, arg2);
						}
					}
				}
			}
		}
	}

	protected void method_14936(class_1936 arg, class_2680 arg2, int i, int j, int k, class_3341 arg3) {
		int l = this.method_14928(i, k);
		int m = this.method_14924(j);
		int n = this.method_14941(i, k);
		if (arg3.method_14662(new class_2338(l, m, n))) {
			while ((arg.method_8623(new class_2338(l, m, n)) || arg.method_8320(new class_2338(l, m, n)).method_11620().method_15797()) && m > 1) {
				arg.method_8652(new class_2338(l, m, n), arg2, 2);
				m--;
			}
		}
	}

	protected boolean method_14915(class_1936 arg, class_3341 arg2, Random random, int i, int j, int k, class_2960 arg3) {
		class_2338 lv = new class_2338(this.method_14928(i, k), this.method_14924(j), this.method_14941(i, k));
		return this.method_14921(arg, arg2, random, lv, arg3, null);
	}

	public static class_2680 method_14916(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		class_2350 lv = null;

		for (class_2350 lv2 : class_2350.class_2353.field_11062) {
			class_2338 lv3 = arg2.method_10093(lv2);
			class_2680 lv4 = arg.method_8320(lv3);
			if (lv4.method_11614() == class_2246.field_10034) {
				return arg3;
			}

			if (lv4.method_11598(arg, lv3)) {
				if (lv != null) {
					lv = null;
					break;
				}

				lv = lv2;
			}
		}

		if (lv != null) {
			return arg3.method_11657(class_2383.field_11177, lv.method_10153());
		} else {
			class_2350 lv5 = arg3.method_11654(class_2383.field_11177);
			class_2338 lv6 = arg2.method_10093(lv5);
			if (arg.method_8320(lv6).method_11598(arg, lv6)) {
				lv5 = lv5.method_10153();
				lv6 = arg2.method_10093(lv5);
			}

			if (arg.method_8320(lv6).method_11598(arg, lv6)) {
				lv5 = lv5.method_10170();
				lv6 = arg2.method_10093(lv5);
			}

			if (arg.method_8320(lv6).method_11598(arg, lv6)) {
				lv5 = lv5.method_10153();
				lv6 = arg2.method_10093(lv5);
			}

			return arg3.method_11657(class_2383.field_11177, lv5);
		}
	}

	protected boolean method_14921(class_1936 arg, class_3341 arg2, Random random, class_2338 arg3, class_2960 arg4, @Nullable class_2680 arg5) {
		if (arg2.method_14662(arg3) && arg.method_8320(arg3).method_11614() != class_2246.field_10034) {
			if (arg5 == null) {
				arg5 = method_14916(arg, arg3, class_2246.field_10034.method_9564());
			}

			arg.method_8652(arg3, arg5, 2);
			class_2586 lv = arg.method_8321(arg3);
			if (lv instanceof class_2595) {
				((class_2595)lv).method_11285(arg4, random.nextLong());
			}

			return true;
		} else {
			return false;
		}
	}

	protected boolean method_14930(class_1936 arg, class_3341 arg2, Random random, int i, int j, int k, class_2350 arg3, class_2960 arg4) {
		class_2338 lv = new class_2338(this.method_14928(i, k), this.method_14924(j), this.method_14941(i, k));
		if (arg2.method_14662(lv) && arg.method_8320(lv).method_11614() != class_2246.field_10200) {
			this.method_14917(arg, class_2246.field_10200.method_9564().method_11657(class_2315.field_10918, arg3), i, j, k, arg2);
			class_2586 lv2 = arg.method_8321(lv);
			if (lv2 instanceof class_2601) {
				((class_2601)lv2).method_11285(arg4, random.nextLong());
			}

			return true;
		} else {
			return false;
		}
	}

	public void method_14922(int i, int j, int k) {
		this.field_15315.method_14661(i, j, k);
	}

	@Nullable
	public class_2350 method_14934() {
		return this.field_15312;
	}

	public void method_14926(@Nullable class_2350 arg) {
		this.field_15312 = arg;
		if (arg == null) {
			this.field_15313 = class_2470.field_11467;
			this.field_15310 = class_2415.field_11302;
		} else {
			switch (arg) {
				case field_11035:
					this.field_15310 = class_2415.field_11300;
					this.field_15313 = class_2470.field_11467;
					break;
				case field_11039:
					this.field_15310 = class_2415.field_11300;
					this.field_15313 = class_2470.field_11463;
					break;
				case field_11034:
					this.field_15310 = class_2415.field_11302;
					this.field_15313 = class_2470.field_11463;
					break;
				default:
					this.field_15310 = class_2415.field_11302;
					this.field_15313 = class_2470.field_11467;
			}
		}
	}

	public class_2470 method_16888() {
		return this.field_15313;
	}

	public class_3773 method_16653() {
		return this.field_16712;
	}

	public abstract static class class_3444 {
		protected class_2680 field_15317 = class_2246.field_10124.method_9564();

		protected class_3444() {
		}

		public abstract void method_14948(Random random, int i, int j, int k, boolean bl);

		public class_2680 method_14947() {
			return this.field_15317;
		}
	}
}
