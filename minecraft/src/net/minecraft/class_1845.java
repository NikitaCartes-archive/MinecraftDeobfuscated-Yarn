package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;

public class class_1845 {
	private static final List<class_1845.class_1846<class_1842>> field_8956 = Lists.<class_1845.class_1846<class_1842>>newArrayList();
	private static final List<class_1845.class_1846<class_1792>> field_8959 = Lists.<class_1845.class_1846<class_1792>>newArrayList();
	private static final List<class_1856> field_8957 = Lists.<class_1856>newArrayList();
	private static final Predicate<class_1799> field_8958 = arg -> {
		for (class_1856 lv : field_8957) {
			if (lv.method_8093(arg)) {
				return true;
			}
		}

		return false;
	};

	public static boolean method_8077(class_1799 arg) {
		return method_8079(arg) || method_8069(arg);
	}

	protected static boolean method_8079(class_1799 arg) {
		int i = 0;

		for (int j = field_8959.size(); i < j; i++) {
			if (((class_1845.class_1846)field_8959.get(i)).field_8960.method_8093(arg)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean method_8069(class_1799 arg) {
		int i = 0;

		for (int j = field_8956.size(); i < j; i++) {
			if (((class_1845.class_1846)field_8956.get(i)).field_8960.method_8093(arg)) {
				return true;
			}
		}

		return false;
	}

	public static boolean method_8072(class_1799 arg, class_1799 arg2) {
		return !field_8958.test(arg) ? false : method_8070(arg, arg2) || method_8075(arg, arg2);
	}

	protected static boolean method_8070(class_1799 arg, class_1799 arg2) {
		class_1792 lv = arg.method_7909();
		int i = 0;

		for (int j = field_8959.size(); i < j; i++) {
			class_1845.class_1846<class_1792> lv2 = (class_1845.class_1846<class_1792>)field_8959.get(i);
			if (lv2.field_8962 == lv && lv2.field_8960.method_8093(arg2)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean method_8075(class_1799 arg, class_1799 arg2) {
		class_1842 lv = class_1844.method_8063(arg);
		int i = 0;

		for (int j = field_8956.size(); i < j; i++) {
			class_1845.class_1846<class_1842> lv2 = (class_1845.class_1846<class_1842>)field_8956.get(i);
			if (lv2.field_8962 == lv && lv2.field_8960.method_8093(arg2)) {
				return true;
			}
		}

		return false;
	}

	public static class_1799 method_8078(class_1799 arg, class_1799 arg2) {
		if (!arg2.method_7960()) {
			class_1842 lv = class_1844.method_8063(arg2);
			class_1792 lv2 = arg2.method_7909();
			int i = 0;

			for (int j = field_8959.size(); i < j; i++) {
				class_1845.class_1846<class_1792> lv3 = (class_1845.class_1846<class_1792>)field_8959.get(i);
				if (lv3.field_8962 == lv2 && lv3.field_8960.method_8093(arg)) {
					return class_1844.method_8061(new class_1799(lv3.field_8961), lv);
				}
			}

			i = 0;

			for (int jx = field_8956.size(); i < jx; i++) {
				class_1845.class_1846<class_1842> lv3 = (class_1845.class_1846<class_1842>)field_8956.get(i);
				if (lv3.field_8962 == lv && lv3.field_8960.method_8093(arg)) {
					return class_1844.method_8061(new class_1799(lv2), lv3.field_8961);
				}
			}
		}

		return arg2;
	}

	public static void method_8076() {
		method_8080(class_1802.field_8574);
		method_8080(class_1802.field_8436);
		method_8080(class_1802.field_8150);
		method_8071(class_1802.field_8574, class_1802.field_8054, class_1802.field_8436);
		method_8071(class_1802.field_8436, class_1802.field_8613, class_1802.field_8150);
		method_8074(class_1847.field_8991, class_1802.field_8597, class_1847.field_8967);
		method_8074(class_1847.field_8991, class_1802.field_8070, class_1847.field_8967);
		method_8074(class_1847.field_8991, class_1802.field_8073, class_1847.field_8967);
		method_8074(class_1847.field_8991, class_1802.field_8183, class_1847.field_8967);
		method_8074(class_1847.field_8991, class_1802.field_8680, class_1847.field_8967);
		method_8074(class_1847.field_8991, class_1802.field_8479, class_1847.field_8967);
		method_8074(class_1847.field_8991, class_1802.field_8135, class_1847.field_8967);
		method_8074(class_1847.field_8991, class_1802.field_8601, class_1847.field_8985);
		method_8074(class_1847.field_8991, class_1802.field_8725, class_1847.field_8967);
		method_8074(class_1847.field_8991, class_1802.field_8790, class_1847.field_8999);
		method_8074(class_1847.field_8999, class_1802.field_8071, class_1847.field_8968);
		method_8074(class_1847.field_8968, class_1802.field_8725, class_1847.field_8981);
		method_8074(class_1847.field_8968, class_1802.field_8711, class_1847.field_8997);
		method_8074(class_1847.field_8981, class_1802.field_8711, class_1847.field_9000);
		method_8074(class_1847.field_8997, class_1802.field_8725, class_1847.field_9000);
		method_8074(class_1847.field_8999, class_1802.field_8135, class_1847.field_8987);
		method_8074(class_1847.field_8987, class_1802.field_8725, class_1847.field_8969);
		method_8074(class_1847.field_8999, class_1802.field_8073, class_1847.field_8979);
		method_8074(class_1847.field_8979, class_1802.field_8725, class_1847.field_8971);
		method_8074(class_1847.field_8979, class_1802.field_8601, class_1847.field_8998);
		method_8074(class_1847.field_8979, class_1802.field_8711, class_1847.field_8996);
		method_8074(class_1847.field_8971, class_1802.field_8711, class_1847.field_8989);
		method_8074(class_1847.field_8996, class_1802.field_8725, class_1847.field_8989);
		method_8074(class_1847.field_8996, class_1802.field_8601, class_1847.field_8976);
		method_8074(class_1847.field_8999, class_1802.field_8090, class_1847.field_8990);
		method_8074(class_1847.field_8990, class_1802.field_8725, class_1847.field_8988);
		method_8074(class_1847.field_8990, class_1802.field_8601, class_1847.field_8977);
		method_8074(class_1847.field_9005, class_1802.field_8711, class_1847.field_8996);
		method_8074(class_1847.field_8983, class_1802.field_8711, class_1847.field_8989);
		method_8074(class_1847.field_8999, class_1802.field_8479, class_1847.field_9005);
		method_8074(class_1847.field_9005, class_1802.field_8725, class_1847.field_8983);
		method_8074(class_1847.field_9005, class_1802.field_8601, class_1847.field_8966);
		method_8074(class_1847.field_8999, class_1802.field_8323, class_1847.field_8994);
		method_8074(class_1847.field_8994, class_1802.field_8725, class_1847.field_9001);
		method_8074(class_1847.field_8999, class_1802.field_8597, class_1847.field_8963);
		method_8074(class_1847.field_8963, class_1802.field_8601, class_1847.field_8980);
		method_8074(class_1847.field_8963, class_1802.field_8711, class_1847.field_9004);
		method_8074(class_1847.field_8980, class_1802.field_8711, class_1847.field_8973);
		method_8074(class_1847.field_9004, class_1802.field_8601, class_1847.field_8973);
		method_8074(class_1847.field_8982, class_1802.field_8711, class_1847.field_9004);
		method_8074(class_1847.field_9002, class_1802.field_8711, class_1847.field_9004);
		method_8074(class_1847.field_8972, class_1802.field_8711, class_1847.field_8973);
		method_8074(class_1847.field_8999, class_1802.field_8680, class_1847.field_8982);
		method_8074(class_1847.field_8982, class_1802.field_8725, class_1847.field_9002);
		method_8074(class_1847.field_8982, class_1802.field_8601, class_1847.field_8972);
		method_8074(class_1847.field_8999, class_1802.field_8070, class_1847.field_8986);
		method_8074(class_1847.field_8986, class_1802.field_8725, class_1847.field_9003);
		method_8074(class_1847.field_8986, class_1802.field_8601, class_1847.field_8992);
		method_8074(class_1847.field_8999, class_1802.field_8183, class_1847.field_8978);
		method_8074(class_1847.field_8978, class_1802.field_8725, class_1847.field_8965);
		method_8074(class_1847.field_8978, class_1802.field_8601, class_1847.field_8993);
		method_8074(class_1847.field_8991, class_1802.field_8711, class_1847.field_8975);
		method_8074(class_1847.field_8975, class_1802.field_8725, class_1847.field_8970);
		method_8074(class_1847.field_8999, class_1802.field_8614, class_1847.field_8974);
		method_8074(class_1847.field_8974, class_1802.field_8725, class_1847.field_8964);
	}

	private static void method_8071(class_1792 arg, class_1792 arg2, class_1792 arg3) {
		if (!(arg instanceof class_1812)) {
			throw new IllegalArgumentException("Expected a potion, got: " + class_2378.field_11142.method_10221(arg));
		} else if (!(arg3 instanceof class_1812)) {
			throw new IllegalArgumentException("Expected a potion, got: " + class_2378.field_11142.method_10221(arg3));
		} else {
			field_8959.add(new class_1845.class_1846<>(arg, class_1856.method_8091(arg2), arg3));
		}
	}

	private static void method_8080(class_1792 arg) {
		if (!(arg instanceof class_1812)) {
			throw new IllegalArgumentException("Expected a potion, got: " + class_2378.field_11142.method_10221(arg));
		} else {
			field_8957.add(class_1856.method_8091(arg));
		}
	}

	private static void method_8074(class_1842 arg, class_1792 arg2, class_1842 arg3) {
		field_8956.add(new class_1845.class_1846<>(arg, class_1856.method_8091(arg2), arg3));
	}

	static class class_1846<T> {
		private final T field_8962;
		private final class_1856 field_8960;
		private final T field_8961;

		public class_1846(T object, class_1856 arg, T object2) {
			this.field_8962 = object;
			this.field_8960 = arg;
			this.field_8961 = object2;
		}
	}
}
