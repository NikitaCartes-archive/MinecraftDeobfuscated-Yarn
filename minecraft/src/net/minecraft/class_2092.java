package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class class_2092 implements class_179<class_2092.class_2094> {
	private final class_2960 field_9694;
	private final Map<class_2985, class_2092.class_2093> field_9695 = Maps.<class_2985, class_2092.class_2093>newHashMap();

	public class_2092(class_2960 arg) {
		this.field_9694 = arg;
	}

	@Override
	public class_2960 method_794() {
		return this.field_9694;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2092.class_2094> arg2) {
		class_2092.class_2093 lv = (class_2092.class_2093)this.field_9695.get(arg);
		if (lv == null) {
			lv = new class_2092.class_2093(arg);
			this.field_9695.put(arg, lv);
		}

		lv.method_9028(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2092.class_2094> arg2) {
		class_2092.class_2093 lv = (class_2092.class_2093)this.field_9695.get(arg);
		if (lv != null) {
			lv.method_9030(arg2);
			if (lv.method_9029()) {
				this.field_9695.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9695.remove(arg);
	}

	public class_2092.class_2094 method_9026(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2090 lv = class_2090.method_9021(jsonObject);
		return new class_2092.class_2094(this.field_9694, lv);
	}

	public void method_9027(class_3222 arg) {
		class_2092.class_2093 lv = (class_2092.class_2093)this.field_9695.get(arg.method_14236());
		if (lv != null) {
			lv.method_9031(arg.method_14220(), arg.field_5987, arg.field_6010, arg.field_6035);
		}
	}

	static class class_2093 {
		private final class_2985 field_9697;
		private final Set<class_179.class_180<class_2092.class_2094>> field_9696 = Sets.<class_179.class_180<class_2092.class_2094>>newHashSet();

		public class_2093(class_2985 arg) {
			this.field_9697 = arg;
		}

		public boolean method_9029() {
			return this.field_9696.isEmpty();
		}

		public void method_9028(class_179.class_180<class_2092.class_2094> arg) {
			this.field_9696.add(arg);
		}

		public void method_9030(class_179.class_180<class_2092.class_2094> arg) {
			this.field_9696.remove(arg);
		}

		public void method_9031(class_3218 arg, double d, double e, double f) {
			List<class_179.class_180<class_2092.class_2094>> list = null;

			for (class_179.class_180<class_2092.class_2094> lv : this.field_9696) {
				if (lv.method_797().method_9033(arg, d, e, f)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2092.class_2094>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2092.class_2094> lvx : list) {
					lvx.method_796(this.field_9697);
				}
			}
		}
	}

	public static class class_2094 extends class_195 {
		private final class_2090 field_9698;

		public class_2094(class_2960 arg, class_2090 arg2) {
			super(arg);
			this.field_9698 = arg2;
		}

		public static class_2092.class_2094 method_9034(class_2090 arg) {
			return new class_2092.class_2094(class_174.field_1194.field_9694, arg);
		}

		public static class_2092.class_2094 method_9032() {
			return new class_2092.class_2094(class_174.field_1212.field_9694, class_2090.field_9685);
		}

		public static class_2092.class_2094 method_20400() {
			return new class_2092.class_2094(class_174.field_19250.field_9694, class_2090.field_9685);
		}

		public boolean method_9033(class_3218 arg, double d, double e, double f) {
			return this.field_9698.method_9018(arg, d, e, f);
		}

		@Override
		public JsonElement method_807() {
			return this.field_9698.method_9019();
		}
	}
}
