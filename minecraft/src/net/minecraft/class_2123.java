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

public class class_2123 implements class_179<class_2123.class_2125> {
	private static final class_2960 field_9743 = new class_2960("shot_crossbow");
	private final Map<class_2985, class_2123.class_2124> field_9744 = Maps.<class_2985, class_2123.class_2124>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9743;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2123.class_2125> arg2) {
		class_2123.class_2124 lv = (class_2123.class_2124)this.field_9744.get(arg);
		if (lv == null) {
			lv = new class_2123.class_2124(arg);
			this.field_9744.put(arg, lv);
		}

		lv.method_9116(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2123.class_2125> arg2) {
		class_2123.class_2124 lv = (class_2123.class_2124)this.field_9744.get(arg);
		if (lv != null) {
			lv.method_9119(arg2);
			if (lv.method_9117()) {
				this.field_9744.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9744.remove(arg);
	}

	public class_2123.class_2125 method_9114(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2073 lv = class_2073.method_8969(jsonObject.get("item"));
		return new class_2123.class_2125(lv);
	}

	public void method_9115(class_3222 arg, class_1799 arg2) {
		class_2123.class_2124 lv = (class_2123.class_2124)this.field_9744.get(arg.method_14236());
		if (lv != null) {
			lv.method_9118(arg2);
		}
	}

	static class class_2124 {
		private final class_2985 field_9746;
		private final Set<class_179.class_180<class_2123.class_2125>> field_9745 = Sets.<class_179.class_180<class_2123.class_2125>>newHashSet();

		public class_2124(class_2985 arg) {
			this.field_9746 = arg;
		}

		public boolean method_9117() {
			return this.field_9745.isEmpty();
		}

		public void method_9116(class_179.class_180<class_2123.class_2125> arg) {
			this.field_9745.add(arg);
		}

		public void method_9119(class_179.class_180<class_2123.class_2125> arg) {
			this.field_9745.remove(arg);
		}

		public void method_9118(class_1799 arg) {
			List<class_179.class_180<class_2123.class_2125>> list = null;

			for (class_179.class_180<class_2123.class_2125> lv : this.field_9745) {
				if (lv.method_797().method_9121(arg)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2123.class_2125>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2123.class_2125> lvx : list) {
					lvx.method_796(this.field_9746);
				}
			}
		}
	}

	public static class class_2125 extends class_195 {
		private final class_2073 field_9747;

		public class_2125(class_2073 arg) {
			super(class_2123.field_9743);
			this.field_9747 = arg;
		}

		public static class_2123.class_2125 method_9120(class_1935 arg) {
			return new class_2123.class_2125(class_2073.class_2074.method_8973().method_8977(arg).method_8976());
		}

		public boolean method_9121(class_1799 arg) {
			return this.field_9747.method_8970(arg);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.field_9747.method_8971());
			return jsonObject;
		}
	}
}
