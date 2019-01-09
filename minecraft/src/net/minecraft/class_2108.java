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

public class class_2108 implements class_179<class_2108.class_2110> {
	private static final class_2960 field_9717 = new class_2960("nether_travel");
	private final Map<class_2985, class_2108.class_2109> field_9718 = Maps.<class_2985, class_2108.class_2109>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9717;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2108.class_2110> arg2) {
		class_2108.class_2109 lv = (class_2108.class_2109)this.field_9718.get(arg);
		if (lv == null) {
			lv = new class_2108.class_2109(arg);
			this.field_9718.put(arg, lv);
		}

		lv.method_9081(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2108.class_2110> arg2) {
		class_2108.class_2109 lv = (class_2108.class_2109)this.field_9718.get(arg);
		if (lv != null) {
			lv.method_9083(arg2);
			if (lv.method_9082()) {
				this.field_9718.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9718.remove(arg);
	}

	public class_2108.class_2110 method_9078(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2090 lv = class_2090.method_9021(jsonObject.get("entered"));
		class_2090 lv2 = class_2090.method_9021(jsonObject.get("exited"));
		class_2025 lv3 = class_2025.method_8857(jsonObject.get("distance"));
		return new class_2108.class_2110(lv, lv2, lv3);
	}

	public void method_9080(class_3222 arg, class_243 arg2) {
		class_2108.class_2109 lv = (class_2108.class_2109)this.field_9718.get(arg.method_14236());
		if (lv != null) {
			lv.method_9084(arg.method_14220(), arg2, arg.field_5987, arg.field_6010, arg.field_6035);
		}
	}

	static class class_2109 {
		private final class_2985 field_9720;
		private final Set<class_179.class_180<class_2108.class_2110>> field_9719 = Sets.<class_179.class_180<class_2108.class_2110>>newHashSet();

		public class_2109(class_2985 arg) {
			this.field_9720 = arg;
		}

		public boolean method_9082() {
			return this.field_9719.isEmpty();
		}

		public void method_9081(class_179.class_180<class_2108.class_2110> arg) {
			this.field_9719.add(arg);
		}

		public void method_9083(class_179.class_180<class_2108.class_2110> arg) {
			this.field_9719.remove(arg);
		}

		public void method_9084(class_3218 arg, class_243 arg2, double d, double e, double f) {
			List<class_179.class_180<class_2108.class_2110>> list = null;

			for (class_179.class_180<class_2108.class_2110> lv : this.field_9719) {
				if (lv.method_797().method_9086(arg, arg2, d, e, f)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2108.class_2110>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2108.class_2110> lvx : list) {
					lvx.method_796(this.field_9720);
				}
			}
		}
	}

	public static class class_2110 extends class_195 {
		private final class_2090 field_9721;
		private final class_2090 field_9722;
		private final class_2025 field_9723;

		public class_2110(class_2090 arg, class_2090 arg2, class_2025 arg3) {
			super(class_2108.field_9717);
			this.field_9721 = arg;
			this.field_9722 = arg2;
			this.field_9723 = arg3;
		}

		public static class_2108.class_2110 method_9085(class_2025 arg) {
			return new class_2108.class_2110(class_2090.field_9685, class_2090.field_9685, arg);
		}

		public boolean method_9086(class_3218 arg, class_243 arg2, double d, double e, double f) {
			if (!this.field_9721.method_9018(arg, arg2.field_1352, arg2.field_1351, arg2.field_1350)) {
				return false;
			} else {
				return !this.field_9722.method_9018(arg, d, e, f) ? false : this.field_9723.method_8859(arg2.field_1352, arg2.field_1351, arg2.field_1350, d, e, f);
			}
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entered", this.field_9721.method_9019());
			jsonObject.add("exited", this.field_9722.method_9019());
			jsonObject.add("distance", this.field_9723.method_8858());
			return jsonObject;
		}
	}
}
