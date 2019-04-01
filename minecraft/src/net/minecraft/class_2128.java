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

public class class_2128 implements class_179<class_2128.class_2130> {
	private static final class_2960 field_9748 = new class_2960("summoned_entity");
	private final Map<class_2985, class_2128.class_2129> field_9749 = Maps.<class_2985, class_2128.class_2129>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9748;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2128.class_2130> arg2) {
		class_2128.class_2129 lv = (class_2128.class_2129)this.field_9749.get(arg);
		if (lv == null) {
			lv = new class_2128.class_2129(arg);
			this.field_9749.put(arg, lv);
		}

		lv.method_9125(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2128.class_2130> arg2) {
		class_2128.class_2129 lv = (class_2128.class_2129)this.field_9749.get(arg);
		if (lv != null) {
			lv.method_9128(arg2);
			if (lv.method_9126()) {
				this.field_9749.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9749.remove(arg);
	}

	public class_2128.class_2130 method_9123(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2048 lv = class_2048.method_8913(jsonObject.get("entity"));
		return new class_2128.class_2130(lv);
	}

	public void method_9124(class_3222 arg, class_1297 arg2) {
		class_2128.class_2129 lv = (class_2128.class_2129)this.field_9749.get(arg.method_14236());
		if (lv != null) {
			lv.method_9127(arg, arg2);
		}
	}

	static class class_2129 {
		private final class_2985 field_9751;
		private final Set<class_179.class_180<class_2128.class_2130>> field_9750 = Sets.<class_179.class_180<class_2128.class_2130>>newHashSet();

		public class_2129(class_2985 arg) {
			this.field_9751 = arg;
		}

		public boolean method_9126() {
			return this.field_9750.isEmpty();
		}

		public void method_9125(class_179.class_180<class_2128.class_2130> arg) {
			this.field_9750.add(arg);
		}

		public void method_9128(class_179.class_180<class_2128.class_2130> arg) {
			this.field_9750.remove(arg);
		}

		public void method_9127(class_3222 arg, class_1297 arg2) {
			List<class_179.class_180<class_2128.class_2130>> list = null;

			for (class_179.class_180<class_2128.class_2130> lv : this.field_9750) {
				if (lv.method_797().method_9130(arg, arg2)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2128.class_2130>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2128.class_2130> lvx : list) {
					lvx.method_796(this.field_9751);
				}
			}
		}
	}

	public static class class_2130 extends class_195 {
		private final class_2048 field_9752;

		public class_2130(class_2048 arg) {
			super(class_2128.field_9748);
			this.field_9752 = arg;
		}

		public static class_2128.class_2130 method_9129(class_2048.class_2049 arg) {
			return new class_2128.class_2130(arg.method_8920());
		}

		public boolean method_9130(class_3222 arg, class_1297 arg2) {
			return this.field_9752.method_8914(arg, arg2);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.field_9752.method_8912());
			return jsonObject;
		}
	}
}
