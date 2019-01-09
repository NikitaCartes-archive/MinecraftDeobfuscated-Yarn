package net.minecraft;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public class class_2169 extends class_1966 {
	private final class_3541 field_9831;
	private final class_2919 field_9829;
	private final class_1959[] field_9830 = new class_1959[]{
		class_1972.field_9411, class_1972.field_9442, class_1972.field_9447, class_1972.field_9457, class_1972.field_9465
	};

	public class_2169(class_2167 arg) {
		this.field_9829 = new class_2919(arg.method_9204());
		this.field_9829.method_12660(17292);
		this.field_9831 = new class_3541(this.field_9829);
	}

	@Override
	public class_1959 method_16359(int i, int j) {
		if ((long)i * (long)i + (long)j * (long)j <= 4096L) {
			return class_1972.field_9411;
		} else {
			float f = this.method_8757(i * 2 + 1, j * 2 + 1);
			if (f > 40.0F) {
				return class_1972.field_9442;
			} else if (f >= 0.0F) {
				return class_1972.field_9447;
			} else {
				return f < -20.0F ? class_1972.field_9457 : class_1972.field_9465;
			}
		}
	}

	@Override
	public class_1959[] method_8760(int i, int j, int k, int l, boolean bl) {
		class_1959[] lvs = new class_1959[k * l];
		Long2ObjectMap<class_1959> long2ObjectMap = new Long2ObjectOpenHashMap<>();

		for (int m = 0; m < k; m++) {
			for (int n = 0; n < l; n++) {
				int o = m + i >> 4;
				int p = n + j >> 4;
				long q = class_1923.method_8331(o, p);
				class_1959 lv = long2ObjectMap.get(q);
				if (lv == null) {
					lv = this.method_16359(o, p);
					long2ObjectMap.put(q, lv);
				}

				lvs[m + n * k] = lv;
			}
		}

		return lvs;
	}

	@Override
	public Set<class_1959> method_8763(int i, int j, int k) {
		int l = i - k >> 2;
		int m = j - k >> 2;
		int n = i + k >> 2;
		int o = j + k >> 2;
		int p = n - l + 1;
		int q = o - m + 1;
		return Sets.<class_1959>newHashSet(this.method_8756(l, m, p, q));
	}

	@Nullable
	@Override
	public class_2338 method_8762(int i, int j, int k, List<class_1959> list, Random random) {
		int l = i - k >> 2;
		int m = j - k >> 2;
		int n = i + k >> 2;
		int o = j + k >> 2;
		int p = n - l + 1;
		int q = o - m + 1;
		class_1959[] lvs = this.method_8756(l, m, p, q);
		class_2338 lv = null;
		int r = 0;

		for (int s = 0; s < p * q; s++) {
			int t = l + s % p << 2;
			int u = m + s / p << 2;
			if (list.contains(lvs[s])) {
				if (lv == null || random.nextInt(r + 1) == 0) {
					lv = new class_2338(t, 0, u);
				}

				r++;
			}
		}

		return lv;
	}

	@Override
	public float method_8757(int i, int j) {
		int k = i / 2;
		int l = j / 2;
		int m = i % 2;
		int n = j % 2;
		float f = 100.0F - class_3532.method_15355((float)(i * i + j * j)) * 8.0F;
		f = class_3532.method_15363(f, -100.0F, 80.0F);

		for (int o = -12; o <= 12; o++) {
			for (int p = -12; p <= 12; p++) {
				long q = (long)(k + o);
				long r = (long)(l + p);
				if (q * q + r * r > 4096L && this.field_9831.method_15433((double)q, (double)r) < -0.9F) {
					float g = (class_3532.method_15379((float)q) * 3439.0F + class_3532.method_15379((float)r) * 147.0F) % 13.0F + 9.0F;
					float h = (float)(m - o * 2);
					float s = (float)(n - p * 2);
					float t = 100.0F - class_3532.method_15355(h * h + s * s) * g;
					t = class_3532.method_15363(t, -100.0F, 80.0F);
					f = Math.max(f, t);
				}
			}
		}

		return f;
	}

	@Override
	public boolean method_8754(class_3195<?> arg) {
		return (Boolean)this.field_9392.computeIfAbsent(arg, argx -> {
			for (class_1959 lv : this.field_9830) {
				if (lv.method_8684(argx)) {
					return true;
				}
			}

			return false;
		});
	}

	@Override
	public Set<class_2680> method_8761() {
		if (this.field_9390.isEmpty()) {
			for (class_1959 lv : this.field_9830) {
				this.field_9390.add(lv.method_8722().method_15337());
			}
		}

		return this.field_9390;
	}
}
