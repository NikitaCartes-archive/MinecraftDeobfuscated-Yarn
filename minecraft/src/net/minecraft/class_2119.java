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

public class class_2119 implements class_179<class_2119.class_2121> {
	private static final class_2960 field_9738 = new class_2960("recipe_unlocked");
	private final Map<class_2985, class_2119.class_2120> field_9739 = Maps.<class_2985, class_2119.class_2120>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9738;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2119.class_2121> arg2) {
		class_2119.class_2120 lv = (class_2119.class_2120)this.field_9739.get(arg);
		if (lv == null) {
			lv = new class_2119.class_2120(arg);
			this.field_9739.put(arg, lv);
		}

		lv.method_9109(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2119.class_2121> arg2) {
		class_2119.class_2120 lv = (class_2119.class_2120)this.field_9739.get(arg);
		if (lv != null) {
			lv.method_9111(arg2);
			if (lv.method_9110()) {
				this.field_9739.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9739.remove(arg);
	}

	public class_2119.class_2121 method_9106(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2960 lv = new class_2960(class_3518.method_15265(jsonObject, "recipe"));
		return new class_2119.class_2121(lv);
	}

	public void method_9107(class_3222 arg, class_1860<?> arg2) {
		class_2119.class_2120 lv = (class_2119.class_2120)this.field_9739.get(arg.method_14236());
		if (lv != null) {
			lv.method_9108(arg2);
		}
	}

	static class class_2120 {
		private final class_2985 field_9741;
		private final Set<class_179.class_180<class_2119.class_2121>> field_9740 = Sets.<class_179.class_180<class_2119.class_2121>>newHashSet();

		public class_2120(class_2985 arg) {
			this.field_9741 = arg;
		}

		public boolean method_9110() {
			return this.field_9740.isEmpty();
		}

		public void method_9109(class_179.class_180<class_2119.class_2121> arg) {
			this.field_9740.add(arg);
		}

		public void method_9111(class_179.class_180<class_2119.class_2121> arg) {
			this.field_9740.remove(arg);
		}

		public void method_9108(class_1860<?> arg) {
			List<class_179.class_180<class_2119.class_2121>> list = null;

			for (class_179.class_180<class_2119.class_2121> lv : this.field_9740) {
				if (lv.method_797().method_9112(arg)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2119.class_2121>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2119.class_2121> lvx : list) {
					lvx.method_796(this.field_9741);
				}
			}
		}
	}

	public static class class_2121 extends class_195 {
		private final class_2960 field_9742;

		public class_2121(class_2960 arg) {
			super(class_2119.field_9738);
			this.field_9742 = arg;
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("recipe", this.field_9742.toString());
			return jsonObject;
		}

		public boolean method_9112(class_1860<?> arg) {
			return this.field_9742.equals(arg.method_8114());
		}
	}
}
