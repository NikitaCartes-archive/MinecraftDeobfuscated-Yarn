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

public class class_2027 implements class_179<class_2027.class_2029> {
	private static final class_2960 field_9558 = new class_2960("effects_changed");
	private final Map<class_2985, class_2027.class_2028> field_9559 = Maps.<class_2985, class_2027.class_2028>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9558;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2027.class_2029> arg2) {
		class_2027.class_2028 lv = (class_2027.class_2028)this.field_9559.get(arg);
		if (lv == null) {
			lv = new class_2027.class_2028(arg);
			this.field_9559.put(arg, lv);
		}

		lv.method_8864(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2027.class_2029> arg2) {
		class_2027.class_2028 lv = (class_2027.class_2028)this.field_9559.get(arg);
		if (lv != null) {
			lv.method_8866(arg2);
			if (lv.method_8865()) {
				this.field_9559.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9559.remove(arg);
	}

	public class_2027.class_2029 method_8862(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2102 lv = class_2102.method_9064(jsonObject.get("effects"));
		return new class_2027.class_2029(lv);
	}

	public void method_8863(class_3222 arg) {
		class_2027.class_2028 lv = (class_2027.class_2028)this.field_9559.get(arg.method_14236());
		if (lv != null) {
			lv.method_8867(arg);
		}
	}

	static class class_2028 {
		private final class_2985 field_9561;
		private final Set<class_179.class_180<class_2027.class_2029>> field_9560 = Sets.<class_179.class_180<class_2027.class_2029>>newHashSet();

		public class_2028(class_2985 arg) {
			this.field_9561 = arg;
		}

		public boolean method_8865() {
			return this.field_9560.isEmpty();
		}

		public void method_8864(class_179.class_180<class_2027.class_2029> arg) {
			this.field_9560.add(arg);
		}

		public void method_8866(class_179.class_180<class_2027.class_2029> arg) {
			this.field_9560.remove(arg);
		}

		public void method_8867(class_3222 arg) {
			List<class_179.class_180<class_2027.class_2029>> list = null;

			for (class_179.class_180<class_2027.class_2029> lv : this.field_9560) {
				if (lv.method_797().method_8868(arg)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2027.class_2029>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2027.class_2029> lvx : list) {
					lvx.method_796(this.field_9561);
				}
			}
		}
	}

	public static class class_2029 extends class_195 {
		private final class_2102 field_9562;

		public class_2029(class_2102 arg) {
			super(class_2027.field_9558);
			this.field_9562 = arg;
		}

		public static class_2027.class_2029 method_8869(class_2102 arg) {
			return new class_2027.class_2029(arg);
		}

		public boolean method_8868(class_3222 arg) {
			return this.field_9562.method_9067(arg);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("effects", this.field_9562.method_9068());
			return jsonObject;
		}
	}
}
