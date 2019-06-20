package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public class class_2088 extends class_1966 {
	private final class_3642 field_9680;
	private final class_3642 field_9679;
	private final class_1959[] field_9677 = new class_1959[]{
		class_1972.field_9423,
		class_1972.field_9451,
		class_1972.field_9424,
		class_1972.field_9472,
		class_1972.field_9409,
		class_1972.field_9420,
		class_1972.field_9471,
		class_1972.field_9438,
		class_1972.field_9435,
		class_1972.field_9463,
		class_1972.field_9452,
		class_1972.field_9444,
		class_1972.field_9462,
		class_1972.field_9407,
		class_1972.field_9434,
		class_1972.field_9466,
		class_1972.field_9459,
		class_1972.field_9428,
		class_1972.field_9464,
		class_1972.field_9417,
		class_1972.field_9432,
		class_1972.field_9474,
		class_1972.field_9446,
		class_1972.field_9419,
		class_1972.field_9478,
		class_1972.field_9412,
		class_1972.field_9421,
		class_1972.field_9475,
		class_1972.field_9454,
		class_1972.field_9425,
		class_1972.field_9477,
		class_1972.field_9429,
		class_1972.field_9460,
		class_1972.field_9449,
		class_1972.field_9430,
		class_1972.field_9415,
		class_1972.field_9410,
		class_1972.field_9433,
		class_1972.field_9408,
		class_1972.field_9441,
		class_1972.field_9467,
		class_1972.field_9448,
		class_1972.field_9439,
		class_1972.field_9470,
		class_1972.field_9418,
		class_1972.field_9455,
		class_1972.field_9427,
		class_1972.field_9476,
		class_1972.field_9414,
		class_1972.field_9422,
		class_1972.field_9479,
		class_1972.field_9453,
		class_1972.field_9426,
		class_1972.field_9405,
		class_1972.field_9431,
		class_1972.field_9458,
		class_1972.field_9450,
		class_1972.field_9437,
		class_1972.field_9416,
		class_1972.field_9404,
		class_1972.field_9436,
		class_1972.field_9456,
		class_1972.field_9445,
		class_1972.field_9443,
		class_1972.field_9413,
		class_1972.field_9406
	};

	public class_2088(class_2084 arg) {
		class_31 lv = arg.method_9003();
		class_2906 lv2 = arg.method_9005();
		class_3642[] lvs = class_3645.method_15843(lv.method_184(), lv.method_153(), lv2);
		this.field_9680 = lvs[0];
		this.field_9679 = lvs[1];
	}

	@Override
	public class_1959 method_16359(int i, int j) {
		return this.field_9679.method_16341(i, j);
	}

	@Override
	public class_1959 method_16360(int i, int j) {
		return this.field_9680.method_16341(i, j);
	}

	@Override
	public class_1959[] method_8760(int i, int j, int k, int l, boolean bl) {
		return this.field_9679.method_15842(i, j, k, l);
	}

	@Override
	public Set<class_1959> method_8763(int i, int j, int k) {
		int l = i - k >> 2;
		int m = j - k >> 2;
		int n = i + k >> 2;
		int o = j + k >> 2;
		int p = n - l + 1;
		int q = o - m + 1;
		Set<class_1959> set = Sets.<class_1959>newHashSet();
		Collections.addAll(set, this.field_9680.method_15842(l, m, p, q));
		return set;
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
		class_1959[] lvs = this.field_9680.method_15842(l, m, p, q);
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
	public boolean method_8754(class_3195<?> arg) {
		return (Boolean)this.field_9392.computeIfAbsent(arg, argx -> {
			for (class_1959 lv : this.field_9677) {
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
			for (class_1959 lv : this.field_9677) {
				this.field_9390.add(lv.method_8722().method_15337());
			}
		}

		return this.field_9390;
	}
}
