package net.minecraft;

import java.util.List;

public class class_2440 extends class_2231 {
	public static final class_2746 field_11358 = class_2741.field_12484;
	private final class_2440.class_2441 field_11359;

	protected class_2440(class_2440.class_2441 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11358, Boolean.valueOf(false)));
		this.field_11359 = arg;
	}

	@Override
	protected int method_9435(class_2680 arg) {
		return arg.method_11654(field_11358) ? 15 : 0;
	}

	@Override
	protected class_2680 method_9432(class_2680 arg, int i) {
		return arg.method_11657(field_11358, Boolean.valueOf(i > 0));
	}

	@Override
	protected void method_9436(class_1936 arg, class_2338 arg2) {
		if (this.field_10635 == class_3614.field_15932) {
			arg.method_8396(null, arg2, class_3417.field_14961, class_3419.field_15245, 0.3F, 0.8F);
		} else {
			arg.method_8396(null, arg2, class_3417.field_15217, class_3419.field_15245, 0.3F, 0.6F);
		}
	}

	@Override
	protected void method_9438(class_1936 arg, class_2338 arg2) {
		if (this.field_10635 == class_3614.field_15932) {
			arg.method_8396(null, arg2, class_3417.field_15002, class_3419.field_15245, 0.3F, 0.7F);
		} else {
			arg.method_8396(null, arg2, class_3417.field_15116, class_3419.field_15245, 0.3F, 0.5F);
		}
	}

	@Override
	protected int method_9434(class_1937 arg, class_2338 arg2) {
		class_238 lv = field_9941.method_996(arg2);
		List<? extends class_1297> list;
		switch (this.field_11359) {
			case field_11361:
				list = arg.method_8335(null, lv);
				break;
			case field_11362:
				list = arg.method_8403(class_1309.class, lv);
				break;
			default:
				return 0;
		}

		if (!list.isEmpty()) {
			for (class_1297 lv2 : list) {
				if (!lv2.method_5696()) {
					return 15;
				}
			}
		}

		return 0;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11358);
	}

	public static enum class_2441 {
		field_11361,
		field_11362;
	}
}
