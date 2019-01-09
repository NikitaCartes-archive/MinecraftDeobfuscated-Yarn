package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class class_3108 extends class_3195<class_3111> {
	private static final List<class_1959.class_1964> field_13705 = Lists.<class_1959.class_1964>newArrayList(
		new class_1959.class_1964(class_1299.field_6099, 10, 2, 3),
		new class_1959.class_1964(class_1299.field_6050, 5, 4, 4),
		new class_1959.class_1964(class_1299.field_6076, 8, 5, 5),
		new class_1959.class_1964(class_1299.field_6137, 2, 5, 5),
		new class_1959.class_1964(class_1299.field_6102, 3, 4, 4)
	);

	public class_3108(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	@Override
	public boolean method_14026(class_2794<?> arg, Random random, int i, int j) {
		int k = i >> 4;
		int l = j >> 4;
		random.setSeed((long)(k ^ l << 4) ^ arg.method_12101());
		random.nextInt();
		if (random.nextInt(3) != 0) {
			return false;
		} else if (i != (k << 4) + 4 + random.nextInt(8)) {
			return false;
		} else if (j != (l << 4) + 4 + random.nextInt(8)) {
			return false;
		} else {
			class_1959 lv = arg.method_12098().method_8758(new class_2338((i << 4) + 9, 0, (j << 4) + 9));
			return arg.method_12097(lv, class_3031.field_13569);
		}
	}

	@Override
	public class_3195.class_3774 method_14016() {
		return class_3108.class_3109::new;
	}

	@Override
	public String method_14019() {
		return "Fortress";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	@Override
	public List<class_1959.class_1964> method_13149() {
		return field_13705;
	}

	public static class class_3109 extends class_3449 {
		public class_3109(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l) {
			super(arg, i, j, arg2, arg3, k, l);
		}

		@Override
		public void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3) {
			class_3390.class_3407 lv = new class_3390.class_3407(this.field_16715, (i << 4) + 2, (j << 4) + 2);
			this.field_15325.add(lv);
			lv.method_14918(lv, this.field_15325, this.field_16715);
			List<class_3443> list = lv.field_14505;

			while (!list.isEmpty()) {
				int k = this.field_16715.nextInt(list.size());
				class_3443 lv2 = (class_3443)list.remove(k);
				lv2.method_14918(lv, this.field_15325, this.field_16715);
			}

			this.method_14969();
			this.method_14976(this.field_16715, 48, 70);
		}
	}
}
