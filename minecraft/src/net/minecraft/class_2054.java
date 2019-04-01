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

public class class_2054 implements class_179<class_2054.class_2056> {
	private static final class_2960 field_9612 = new class_2960("filled_bucket");
	private final Map<class_2985, class_2054.class_2055> field_9613 = Maps.<class_2985, class_2054.class_2055>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9612;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2054.class_2056> arg2) {
		class_2054.class_2055 lv = (class_2054.class_2055)this.field_9613.get(arg);
		if (lv == null) {
			lv = new class_2054.class_2055(arg);
			this.field_9613.put(arg, lv);
		}

		lv.method_8933(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2054.class_2056> arg2) {
		class_2054.class_2055 lv = (class_2054.class_2055)this.field_9613.get(arg);
		if (lv != null) {
			lv.method_8936(arg2);
			if (lv.method_8934()) {
				this.field_9613.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9613.remove(arg);
	}

	public class_2054.class_2056 method_8931(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2073 lv = class_2073.method_8969(jsonObject.get("item"));
		return new class_2054.class_2056(lv);
	}

	public void method_8932(class_3222 arg, class_1799 arg2) {
		class_2054.class_2055 lv = (class_2054.class_2055)this.field_9613.get(arg.method_14236());
		if (lv != null) {
			lv.method_8935(arg2);
		}
	}

	static class class_2055 {
		private final class_2985 field_9615;
		private final Set<class_179.class_180<class_2054.class_2056>> field_9614 = Sets.<class_179.class_180<class_2054.class_2056>>newHashSet();

		public class_2055(class_2985 arg) {
			this.field_9615 = arg;
		}

		public boolean method_8934() {
			return this.field_9614.isEmpty();
		}

		public void method_8933(class_179.class_180<class_2054.class_2056> arg) {
			this.field_9614.add(arg);
		}

		public void method_8936(class_179.class_180<class_2054.class_2056> arg) {
			this.field_9614.remove(arg);
		}

		public void method_8935(class_1799 arg) {
			List<class_179.class_180<class_2054.class_2056>> list = null;

			for (class_179.class_180<class_2054.class_2056> lv : this.field_9614) {
				if (lv.method_797().method_8938(arg)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2054.class_2056>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2054.class_2056> lvx : list) {
					lvx.method_796(this.field_9615);
				}
			}
		}
	}

	public static class class_2056 extends class_195 {
		private final class_2073 field_9616;

		public class_2056(class_2073 arg) {
			super(class_2054.field_9612);
			this.field_9616 = arg;
		}

		public static class_2054.class_2056 method_8937(class_2073 arg) {
			return new class_2054.class_2056(arg);
		}

		public boolean method_8938(class_1799 arg) {
			return this.field_9616.method_8970(arg);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.field_9616.method_8971());
			return jsonObject;
		}
	}
}
