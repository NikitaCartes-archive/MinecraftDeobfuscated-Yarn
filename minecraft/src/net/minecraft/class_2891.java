package net.minecraft;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class class_2891 extends class_2794<class_2892> {
	private static final List<class_2680> field_13163 = (List<class_2680>)StreamSupport.stream(class_2378.field_11146.spliterator(), false)
		.flatMap(arg -> arg.method_9595().method_11662().stream())
		.collect(Collectors.toList());
	private static final int field_13161 = class_3532.method_15386(class_3532.method_15355((float)field_13163.size()));
	private static final int field_13160 = class_3532.method_15386((float)field_13163.size() / (float)field_13161);
	protected static final class_2680 field_13162 = class_2246.field_10124.method_9564();
	protected static final class_2680 field_13164 = class_2246.field_10499.method_9564();

	public class_2891(class_1936 arg, class_1966 arg2, class_2892 arg3) {
		super(arg, arg2, arg3);
	}

	@Override
	public void method_12110(class_2791 arg) {
	}

	@Override
	public void method_12108(class_2791 arg, class_2893.class_2894 arg2) {
	}

	@Override
	public int method_12100() {
		return this.field_12760.method_8615() + 1;
	}

	@Override
	public void method_12102(class_3233 arg) {
		class_2338.class_2339 lv = new class_2338.class_2339();
		int i = arg.method_14336();
		int j = arg.method_14339();

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				int m = (i << 4) + k;
				int n = (j << 4) + l;
				arg.method_8652(lv.method_10103(m, 60, n), field_13164, 2);
				class_2680 lv2 = method_12578(m, n);
				if (lv2 != null) {
					arg.method_8652(lv.method_10103(m, 70, n), lv2, 2);
				}
			}
		}
	}

	@Override
	public void method_12088(class_1936 arg, class_2791 arg2) {
	}

	@Override
	public int method_16397(int i, int j, class_2902.class_2903 arg) {
		return 0;
	}

	public static class_2680 method_12578(int i, int j) {
		class_2680 lv = field_13162;
		if (i > 0 && j > 0 && i % 2 != 0 && j % 2 != 0) {
			i /= 2;
			j /= 2;
			if (i <= field_13161 && j <= field_13160) {
				int k = class_3532.method_15382(i * field_13161 + j);
				if (k < field_13163.size()) {
					lv = (class_2680)field_13163.get(k);
				}
			}
		}

		return lv;
	}
}
