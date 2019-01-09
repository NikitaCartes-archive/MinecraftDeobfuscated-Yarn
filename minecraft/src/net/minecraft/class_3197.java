package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.function.Function;

public class class_3197 extends class_3145<class_3111> {
	private static final List<class_1959.class_1964> field_13882 = Lists.<class_1959.class_1964>newArrayList(
		new class_1959.class_1964(class_1299.field_6145, 1, 1, 1)
	);
	private static final List<class_1959.class_1964> field_16435 = Lists.<class_1959.class_1964>newArrayList(
		new class_1959.class_1964(class_1299.field_16281, 1, 1, 1)
	);

	public class_3197(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	@Override
	public String method_14019() {
		return "Swamp_Hut";
	}

	@Override
	public int method_14021() {
		return 3;
	}

	@Override
	public class_3195.class_3774 method_14016() {
		return class_3197.class_3198::new;
	}

	@Override
	protected int method_13774() {
		return 14357620;
	}

	@Override
	public List<class_1959.class_1964> method_13149() {
		return field_13882;
	}

	@Override
	public List<class_1959.class_1964> method_16140() {
		return field_16435;
	}

	public boolean method_14029(class_1936 arg, class_2338 arg2) {
		class_3449 lv = this.method_14025(arg, arg2, true);
		if (lv != class_3449.field_16713 && lv instanceof class_3197.class_3198 && !lv.method_14963().isEmpty()) {
			class_3443 lv2 = (class_3443)lv.method_14963().get(0);
			return lv2 instanceof class_3447;
		} else {
			return false;
		}
	}

	public static class class_3198 extends class_3449 {
		public class_3198(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l) {
			super(arg, i, j, arg2, arg3, k, l);
		}

		@Override
		public void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3) {
			class_3447 lv = new class_3447(this.field_16715, i * 16, j * 16);
			this.field_15325.add(lv);
			this.method_14969();
		}
	}
}
