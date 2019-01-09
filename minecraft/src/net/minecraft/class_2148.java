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

public class class_2148 implements class_179<class_2148.class_2150> {
	private static final class_2960 field_9773 = new class_2960("used_totem");
	private final Map<class_2985, class_2148.class_2149> field_9774 = Maps.<class_2985, class_2148.class_2149>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9773;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2148.class_2150> arg2) {
		class_2148.class_2149 lv = (class_2148.class_2149)this.field_9774.get(arg);
		if (lv == null) {
			lv = new class_2148.class_2149(arg);
			this.field_9774.put(arg, lv);
		}

		lv.method_9166(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2148.class_2150> arg2) {
		class_2148.class_2149 lv = (class_2148.class_2149)this.field_9774.get(arg);
		if (lv != null) {
			lv.method_9169(arg2);
			if (lv.method_9167()) {
				this.field_9774.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9774.remove(arg);
	}

	public class_2148.class_2150 method_9163(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2073 lv = class_2073.method_8969(jsonObject.get("item"));
		return new class_2148.class_2150(lv);
	}

	public void method_9165(class_3222 arg, class_1799 arg2) {
		class_2148.class_2149 lv = (class_2148.class_2149)this.field_9774.get(arg.method_14236());
		if (lv != null) {
			lv.method_9168(arg2);
		}
	}

	static class class_2149 {
		private final class_2985 field_9776;
		private final Set<class_179.class_180<class_2148.class_2150>> field_9775 = Sets.<class_179.class_180<class_2148.class_2150>>newHashSet();

		public class_2149(class_2985 arg) {
			this.field_9776 = arg;
		}

		public boolean method_9167() {
			return this.field_9775.isEmpty();
		}

		public void method_9166(class_179.class_180<class_2148.class_2150> arg) {
			this.field_9775.add(arg);
		}

		public void method_9169(class_179.class_180<class_2148.class_2150> arg) {
			this.field_9775.remove(arg);
		}

		public void method_9168(class_1799 arg) {
			List<class_179.class_180<class_2148.class_2150>> list = null;

			for (class_179.class_180<class_2148.class_2150> lv : this.field_9775) {
				if (lv.method_797().method_9171(arg)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2148.class_2150>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2148.class_2150> lvx : list) {
					lvx.method_796(this.field_9776);
				}
			}
		}
	}

	public static class class_2150 extends class_195 {
		private final class_2073 field_9777;

		public class_2150(class_2073 arg) {
			super(class_2148.field_9773);
			this.field_9777 = arg;
		}

		public static class_2148.class_2150 method_9170(class_1935 arg) {
			return new class_2148.class_2150(class_2073.class_2074.method_8973().method_8977(arg).method_8976());
		}

		public boolean method_9171(class_1799 arg) {
			return this.field_9777.method_8970(arg);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.field_9777.method_8971());
			return jsonObject;
		}
	}
}
