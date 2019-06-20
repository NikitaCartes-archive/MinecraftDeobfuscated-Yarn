package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3217 extends class_3031<class_3111> {
	private static final class_2338 field_19241 = new class_2338(8, 3, 8);
	private static final class_1923 field_19242 = new class_1923(field_19241);

	public class_3217(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	private static int method_20403(int i, int j, int k, int l) {
		return Math.max(Math.abs(i - k), Math.abs(j - l));
	}

	public boolean method_14165(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		class_1923 lv = new class_1923(arg3);
		if (method_20403(lv.field_9181, lv.field_9180, field_19242.field_9181, field_19242.field_9180) > 1) {
			return true;
		} else {
			class_2338.class_2339 lv2 = new class_2338.class_2339();

			for (int i = lv.method_8328(); i <= lv.method_8329(); i++) {
				for (int j = lv.method_8326(); j <= lv.method_8327(); j++) {
					if (method_20403(field_19241.method_10263(), field_19241.method_10260(), j, i) <= 16) {
						lv2.method_10103(j, field_19241.method_10264(), i);
						if (lv2.equals(field_19241)) {
							arg.method_8652(lv2, class_2246.field_10445.method_9564(), 2);
						} else {
							arg.method_8652(lv2, class_2246.field_10340.method_9564(), 2);
						}
					}
				}
			}

			return true;
		}
	}
}
