package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.function.Function;

public class class_1865 {
	private static final Map<String, class_1862<?>> field_9032 = Maps.<String, class_1862<?>>newHashMap();
	public static final class_1862<class_1869> field_9035 = method_8137(new class_1869.class_1870());
	public static final class_1862<class_1867> field_9031 = method_8137(new class_1867.class_1868());
	public static final class_1865.class_1866<class_1849> field_9028 = method_8137(new class_1865.class_1866<>("crafting_special_armordye", class_1849::new));
	public static final class_1865.class_1866<class_1850> field_9029 = method_8137(new class_1865.class_1866<>("crafting_special_bookcloning", class_1850::new));
	public static final class_1865.class_1866<class_1855> field_9044 = method_8137(new class_1865.class_1866<>("crafting_special_mapcloning", class_1855::new));
	public static final class_1865.class_1866<class_1861> field_9039 = method_8137(new class_1865.class_1866<>("crafting_special_mapextending", class_1861::new));
	public static final class_1865.class_1866<class_1851> field_9043 = method_8137(
		new class_1865.class_1866<>("crafting_special_firework_rocket", class_1851::new)
	);
	public static final class_1865.class_1866<class_1853> field_9036 = method_8137(new class_1865.class_1866<>("crafting_special_firework_star", class_1853::new));
	public static final class_1865.class_1866<class_1854> field_9034 = method_8137(
		new class_1865.class_1866<>("crafting_special_firework_star_fade", class_1854::new)
	);
	public static final class_1865.class_1866<class_1876> field_9037 = method_8137(new class_1865.class_1866<>("crafting_special_tippedarrow", class_1876::new));
	public static final class_1865.class_1866<class_1848> field_9038 = method_8137(
		new class_1865.class_1866<>("crafting_special_bannerduplicate", class_1848::new)
	);
	public static final class_1865.class_1866<class_1872> field_9040 = method_8137(
		new class_1865.class_1866<>("crafting_special_shielddecoration", class_1872::new)
	);
	public static final class_1865.class_1866<class_1871> field_9041 = method_8137(
		new class_1865.class_1866<>("crafting_special_shulkerboxcoloring", class_1871::new)
	);
	public static final class_1865.class_1866<class_1873> field_9030 = method_8137(new class_1865.class_1866<>("crafting_special_suspiciousstew", class_1873::new));
	public static final class_1862<class_3861> field_9042 = method_8137(new class_3861.class_1875());
	public static final class_1862<class_3859> field_17084 = method_8137(new class_3859.class_3860());
	public static final class_1862<class_3862> field_17085 = method_8137(new class_3862.class_3863());
	public static final class_1862<class_3920> field_17347 = method_8137(new class_3920.class_3921());

	public static <S extends class_1862<T>, T extends class_1860> S method_8137(S arg) {
		if (field_9032.containsKey(arg.method_8123())) {
			throw new IllegalArgumentException("Duplicate recipe serializer " + arg.method_8123());
		} else {
			field_9032.put(arg.method_8123(), arg);
			return arg;
		}
	}

	public static class_1860 method_8135(class_2960 arg, JsonObject jsonObject) {
		String string = class_3518.method_15265(jsonObject, "type");
		class_1862<?> lv = (class_1862<?>)field_9032.get(string);
		if (lv == null) {
			throw new JsonSyntaxException("Invalid or unsupported recipe type '" + string + "'");
		} else {
			return lv.method_8121(arg, jsonObject);
		}
	}

	public static class_1860 method_8136(class_2540 arg) {
		class_2960 lv = arg.method_10810();
		String string = arg.method_10800(32767);
		class_1862<?> lv2 = (class_1862<?>)field_9032.get(string);
		if (lv2 == null) {
			throw new IllegalArgumentException("Unknown recipe serializer " + string);
		} else {
			return lv2.method_8122(lv, arg);
		}
	}

	public static <T extends class_1860> void method_8134(T arg, class_2540 arg2) {
		arg2.method_10812(arg.method_8114());
		arg2.method_10814(arg.method_8119().method_8123());
		class_1862<T> lv = (class_1862<T>)arg.method_8119();
		lv.method_8124(arg2, arg);
	}

	public static final class class_1866<T extends class_1860> implements class_1862<T> {
		private final String field_9046;
		private final Function<class_2960, T> field_9045;

		public class_1866(String string, Function<class_2960, T> function) {
			this.field_9046 = string;
			this.field_9045 = function;
		}

		@Override
		public T method_8121(class_2960 arg, JsonObject jsonObject) {
			return (T)this.field_9045.apply(arg);
		}

		@Override
		public T method_8122(class_2960 arg, class_2540 arg2) {
			return (T)this.field_9045.apply(arg);
		}

		@Override
		public void method_8124(class_2540 arg, T arg2) {
		}

		@Override
		public String method_8123() {
			return this.field_9046;
		}
	}
}
