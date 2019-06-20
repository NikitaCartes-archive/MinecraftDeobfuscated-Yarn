package net.minecraft;

import java.util.List;
import java.util.Random;

public class class_3549 {
	public static int method_15445(List<? extends class_3549.class_3550> list) {
		int i = 0;
		int j = 0;

		for (int k = list.size(); j < k; j++) {
			class_3549.class_3550 lv = (class_3549.class_3550)list.get(j);
			i += lv.field_15774;
		}

		return i;
	}

	public static <T extends class_3549.class_3550> T method_15444(Random random, List<T> list, int i) {
		if (i <= 0) {
			throw new IllegalArgumentException();
		} else {
			int j = random.nextInt(i);
			return method_15447(list, j);
		}
	}

	public static <T extends class_3549.class_3550> T method_15447(List<T> list, int i) {
		int j = 0;

		for (int k = list.size(); j < k; j++) {
			T lv = (T)list.get(j);
			i -= lv.field_15774;
			if (i < 0) {
				return lv;
			}
		}

		return null;
	}

	public static <T extends class_3549.class_3550> T method_15446(Random random, List<T> list) {
		return method_15444(random, list, method_15445(list));
	}

	public static class class_3550 {
		protected final int field_15774;

		public class_3550(int i) {
			this.field_15774 = i;
		}
	}
}
