package net.minecraft;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public class class_1973 extends class_1966 {
	private final class_1959[] field_9481;
	private final int field_9480;

	public class_1973(class_1976 arg) {
		this.field_9481 = arg.method_8779();
		this.field_9480 = arg.method_8778() + 4;
	}

	@Override
	public class_1959 method_16359(int i, int j) {
		return this.field_9481[Math.abs(((i >> this.field_9480) + (j >> this.field_9480)) % this.field_9481.length)];
	}

	@Override
	public class_1959[] method_8760(int i, int j, int k, int l, boolean bl) {
		class_1959[] lvs = new class_1959[k * l];

		for (int m = 0; m < l; m++) {
			for (int n = 0; n < k; n++) {
				int o = Math.abs(((i + m >> this.field_9480) + (j + n >> this.field_9480)) % this.field_9481.length);
				class_1959 lv = this.field_9481[o];
				lvs[m * k + n] = lv;
			}
		}

		return lvs;
	}

	@Nullable
	@Override
	public class_2338 method_8762(int i, int j, int k, List<class_1959> list, Random random) {
		return null;
	}

	@Override
	public boolean method_8754(class_3195<?> arg) {
		return (Boolean)this.field_9392.computeIfAbsent(arg, argx -> {
			for (class_1959 lv : this.field_9481) {
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
			for (class_1959 lv : this.field_9481) {
				this.field_9390.add(lv.method_8722().method_15337());
			}
		}

		return this.field_9390;
	}

	@Override
	public Set<class_1959> method_8763(int i, int j, int k) {
		return Sets.<class_1959>newHashSet(this.field_9481);
	}
}
