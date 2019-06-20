package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class class_2143 implements class_179<class_2143.class_2145> {
	private static final class_2960 field_9768 = new class_2960("used_ender_eye");
	private final Map<class_2985, class_2143.class_2144> field_9769 = Maps.<class_2985, class_2143.class_2144>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9768;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2143.class_2145> arg2) {
		class_2143.class_2144 lv = (class_2143.class_2144)this.field_9769.get(arg);
		if (lv == null) {
			lv = new class_2143.class_2144(arg);
			this.field_9769.put(arg, lv);
		}

		lv.method_9159(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2143.class_2145> arg2) {
		class_2143.class_2144 lv = (class_2143.class_2144)this.field_9769.get(arg);
		if (lv != null) {
			lv.method_9161(arg2);
			if (lv.method_9160()) {
				this.field_9769.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9769.remove(arg);
	}

	public class_2143.class_2145 method_9156(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2096.class_2099 lv = class_2096.class_2099.method_9051(jsonObject.get("distance"));
		return new class_2143.class_2145(lv);
	}

	public void method_9157(class_3222 arg, class_2338 arg2) {
		class_2143.class_2144 lv = (class_2143.class_2144)this.field_9769.get(arg.method_14236());
		if (lv != null) {
			double d = arg.field_5987 - (double)arg2.method_10263();
			double e = arg.field_6035 - (double)arg2.method_10260();
			lv.method_9158(d * d + e * e);
		}
	}

	static class class_2144 {
		private final class_2985 field_9771;
		private final Set<class_179.class_180<class_2143.class_2145>> field_9770 = Sets.<class_179.class_180<class_2143.class_2145>>newHashSet();

		public class_2144(class_2985 arg) {
			this.field_9771 = arg;
		}

		public boolean method_9160() {
			return this.field_9770.isEmpty();
		}

		public void method_9159(class_179.class_180<class_2143.class_2145> arg) {
			this.field_9770.add(arg);
		}

		public void method_9161(class_179.class_180<class_2143.class_2145> arg) {
			this.field_9770.remove(arg);
		}

		public void method_9158(double d) {
			List<class_179.class_180<class_2143.class_2145>> list = null;

			for (class_179.class_180<class_2143.class_2145> lv : this.field_9770) {
				if (lv.method_797().method_9162(d)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2143.class_2145>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2143.class_2145> lvx : list) {
					lvx.method_796(this.field_9771);
				}
			}
		}
	}

	public static class class_2145 extends class_195 {
		private final class_2096.class_2099 field_9772;

		public class_2145(class_2096.class_2099 arg) {
			super(class_2143.field_9768);
			this.field_9772 = arg;
		}

		public boolean method_9162(double d) {
			return this.field_9772.method_9045(d);
		}
	}
}
