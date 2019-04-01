package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class class_3770 extends class_3145<class_3772> {
	private static final List<class_1959.class_1964> field_16656 = Lists.<class_1959.class_1964>newArrayList(
		new class_1959.class_1964(class_1299.field_6105, 1, 1, 1)
	);

	public class_3770(Function<Dynamic<?>, ? extends class_3772> function) {
		super(function);
	}

	@Override
	public String method_14019() {
		return "Pillager_Outpost";
	}

	@Override
	public int method_14021() {
		return 3;
	}

	@Override
	public List<class_1959.class_1964> method_13149() {
		return field_16656;
	}

	@Override
	public boolean method_14026(class_2794<?> arg, Random random, int i, int j) {
		class_1923 lv = this.method_14018(arg, random, i, j, 0, 0);
		if (i == lv.field_9181 && j == lv.field_9180) {
			int k = i >> 4;
			int l = j >> 4;
			random.setSeed((long)(k ^ l << 4) ^ arg.method_12101());
			random.nextInt();
			if (random.nextInt(5) != 0) {
				return false;
			} else {
				class_1959 lv2 = arg.method_12098().method_8758(new class_2338((i << 4) + 9, 0, (j << 4) + 9));
				return arg.method_12097(lv2, class_3031.field_16655);
			}
		} else {
			return false;
		}
	}

	@Override
	public class_3195.class_3774 method_14016() {
		return class_3770.class_3771::new;
	}

	@Override
	protected int method_13774() {
		return 165745296;
	}

	public static class class_3771 extends class_4183 {
		public class_3771(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l) {
			super(arg, i, j, arg2, arg3, k, l);
		}

		@Override
		public void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3) {
			class_2338 lv = new class_2338(i * 16, 90, j * 16);
			class_3791.method_16650(arg, arg2, lv, this.field_15325, this.field_16715);
			this.method_14969();
		}
	}
}
