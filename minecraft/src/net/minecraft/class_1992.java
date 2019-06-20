package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public class class_1992 extends class_1966 {
	private final class_1959 field_9486;

	public class_1992(class_1991 arg) {
		this.field_9486 = arg.method_8781();
	}

	@Override
	public class_1959 method_16359(int i, int j) {
		return this.field_9486;
	}

	@Override
	public class_1959[] method_8760(int i, int j, int k, int l, boolean bl) {
		class_1959[] lvs = new class_1959[k * l];
		Arrays.fill(lvs, 0, k * l, this.field_9486);
		return lvs;
	}

	@Nullable
	@Override
	public class_2338 method_8762(int i, int j, int k, List<class_1959> list, Random random) {
		return list.contains(this.field_9486) ? new class_2338(i - k + random.nextInt(k * 2 + 1), 0, j - k + random.nextInt(k * 2 + 1)) : null;
	}

	@Override
	public boolean method_8754(class_3195<?> arg) {
		return (Boolean)this.field_9392.computeIfAbsent(arg, this.field_9486::method_8684);
	}

	@Override
	public Set<class_2680> method_8761() {
		if (this.field_9390.isEmpty()) {
			this.field_9390.add(this.field_9486.method_8722().method_15337());
		}

		return this.field_9390;
	}

	@Override
	public Set<class_1959> method_8763(int i, int j, int k) {
		return Sets.<class_1959>newHashSet(this.field_9486);
	}
}
