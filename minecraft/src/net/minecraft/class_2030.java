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

public class class_2030 implements class_179<class_2030.class_2032> {
	private static final class_2960 field_9563 = new class_2960("enchanted_item");
	private final Map<class_2985, class_2030.class_2031> field_9564 = Maps.<class_2985, class_2030.class_2031>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9563;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2030.class_2032> arg2) {
		class_2030.class_2031 lv = (class_2030.class_2031)this.field_9564.get(arg);
		if (lv == null) {
			lv = new class_2030.class_2031(arg);
			this.field_9564.put(arg, lv);
		}

		lv.method_8873(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2030.class_2032> arg2) {
		class_2030.class_2031 lv = (class_2030.class_2031)this.field_9564.get(arg);
		if (lv != null) {
			lv.method_8876(arg2);
			if (lv.method_8874()) {
				this.field_9564.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9564.remove(arg);
	}

	public class_2030.class_2032 method_8872(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2073 lv = class_2073.method_8969(jsonObject.get("item"));
		class_2096.class_2100 lv2 = class_2096.class_2100.method_9056(jsonObject.get("levels"));
		return new class_2030.class_2032(lv, lv2);
	}

	public void method_8870(class_3222 arg, class_1799 arg2, int i) {
		class_2030.class_2031 lv = (class_2030.class_2031)this.field_9564.get(arg.method_14236());
		if (lv != null) {
			lv.method_8875(arg2, i);
		}
	}

	static class class_2031 {
		private final class_2985 field_9566;
		private final Set<class_179.class_180<class_2030.class_2032>> field_9565 = Sets.<class_179.class_180<class_2030.class_2032>>newHashSet();

		public class_2031(class_2985 arg) {
			this.field_9566 = arg;
		}

		public boolean method_8874() {
			return this.field_9565.isEmpty();
		}

		public void method_8873(class_179.class_180<class_2030.class_2032> arg) {
			this.field_9565.add(arg);
		}

		public void method_8876(class_179.class_180<class_2030.class_2032> arg) {
			this.field_9565.remove(arg);
		}

		public void method_8875(class_1799 arg, int i) {
			List<class_179.class_180<class_2030.class_2032>> list = null;

			for (class_179.class_180<class_2030.class_2032> lv : this.field_9565) {
				if (lv.method_797().method_8878(arg, i)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2030.class_2032>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2030.class_2032> lvx : list) {
					lvx.method_796(this.field_9566);
				}
			}
		}
	}

	public static class class_2032 extends class_195 {
		private final class_2073 field_9567;
		private final class_2096.class_2100 field_9568;

		public class_2032(class_2073 arg, class_2096.class_2100 arg2) {
			super(class_2030.field_9563);
			this.field_9567 = arg;
			this.field_9568 = arg2;
		}

		public static class_2030.class_2032 method_8877() {
			return new class_2030.class_2032(class_2073.field_9640, class_2096.class_2100.field_9708);
		}

		public boolean method_8878(class_1799 arg, int i) {
			return !this.field_9567.method_8970(arg) ? false : this.field_9568.method_9054(i);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.field_9567.method_8971());
			jsonObject.add("levels", this.field_9568.method_9036());
			return jsonObject;
		}
	}
}
