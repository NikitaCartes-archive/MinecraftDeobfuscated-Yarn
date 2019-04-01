package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_773 {
	private final Map<class_2680, class_1087> field_4162 = Maps.<class_2680, class_1087>newIdentityHashMap();
	private final class_1092 field_4163;

	public class_773(class_1092 arg) {
		this.field_4163 = arg;
	}

	public class_1058 method_3339(class_2680 arg) {
		return this.method_3335(arg).method_4711();
	}

	public class_1087 method_3335(class_2680 arg) {
		class_1087 lv = (class_1087)this.field_4162.get(arg);
		if (lv == null) {
			lv = this.field_4163.method_4744();
		}

		return lv;
	}

	public class_1092 method_3333() {
		return this.field_4163;
	}

	public void method_3341() {
		this.field_4162.clear();

		for (class_2248 lv : class_2378.field_11146) {
			lv.method_9595().method_11662().forEach(arg -> {
				class_1087 var10000 = (class_1087)this.field_4162.put(arg, this.field_4163.method_4742(method_3340(arg)));
			});
		}
	}

	public static class_1091 method_3340(class_2680 arg) {
		return method_3336(class_2378.field_11146.method_10221(arg.method_11614()), arg);
	}

	public static class_1091 method_3336(class_2960 arg, class_2680 arg2) {
		return new class_1091(arg, method_3338(arg2.method_11656()));
	}

	public static String method_3338(Map<class_2769<?>, Comparable<?>> map) {
		StringBuilder stringBuilder = new StringBuilder();

		for (Entry<class_2769<?>, Comparable<?>> entry : map.entrySet()) {
			if (stringBuilder.length() != 0) {
				stringBuilder.append(',');
			}

			class_2769<?> lv = (class_2769<?>)entry.getKey();
			stringBuilder.append(lv.method_11899());
			stringBuilder.append('=');
			stringBuilder.append(method_3334(lv, (Comparable<?>)entry.getValue()));
		}

		return stringBuilder.toString();
	}

	private static <T extends Comparable<T>> String method_3334(class_2769<T> arg, Comparable<?> comparable) {
		return arg.method_11901((T)comparable);
	}
}
