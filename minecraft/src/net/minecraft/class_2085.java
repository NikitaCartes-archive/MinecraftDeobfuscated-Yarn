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

public class class_2085 implements class_179<class_2085.class_2087> {
	private static final class_2960 field_9671 = new class_2960("levitation");
	private final Map<class_2985, class_2085.class_2086> field_9672 = Maps.<class_2985, class_2085.class_2086>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9671;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2085.class_2087> arg2) {
		class_2085.class_2086 lv = (class_2085.class_2086)this.field_9672.get(arg);
		if (lv == null) {
			lv = new class_2085.class_2086(arg);
			this.field_9672.put(arg, lv);
		}

		lv.method_9009(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2085.class_2087> arg2) {
		class_2085.class_2086 lv = (class_2085.class_2086)this.field_9672.get(arg);
		if (lv != null) {
			lv.method_9012(arg2);
			if (lv.method_9010()) {
				this.field_9672.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9672.remove(arg);
	}

	public class_2085.class_2087 method_9006(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2025 lv = class_2025.method_8857(jsonObject.get("distance"));
		class_2096.class_2100 lv2 = class_2096.class_2100.method_9056(jsonObject.get("duration"));
		return new class_2085.class_2087(lv, lv2);
	}

	public void method_9008(class_3222 arg, class_243 arg2, int i) {
		class_2085.class_2086 lv = (class_2085.class_2086)this.field_9672.get(arg.method_14236());
		if (lv != null) {
			lv.method_9011(arg, arg2, i);
		}
	}

	static class class_2086 {
		private final class_2985 field_9674;
		private final Set<class_179.class_180<class_2085.class_2087>> field_9673 = Sets.<class_179.class_180<class_2085.class_2087>>newHashSet();

		public class_2086(class_2985 arg) {
			this.field_9674 = arg;
		}

		public boolean method_9010() {
			return this.field_9673.isEmpty();
		}

		public void method_9009(class_179.class_180<class_2085.class_2087> arg) {
			this.field_9673.add(arg);
		}

		public void method_9012(class_179.class_180<class_2085.class_2087> arg) {
			this.field_9673.remove(arg);
		}

		public void method_9011(class_3222 arg, class_243 arg2, int i) {
			List<class_179.class_180<class_2085.class_2087>> list = null;

			for (class_179.class_180<class_2085.class_2087> lv : this.field_9673) {
				if (lv.method_797().method_9014(arg, arg2, i)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2085.class_2087>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2085.class_2087> lvx : list) {
					lvx.method_796(this.field_9674);
				}
			}
		}
	}

	public static class class_2087 extends class_195 {
		private final class_2025 field_9675;
		private final class_2096.class_2100 field_9676;

		public class_2087(class_2025 arg, class_2096.class_2100 arg2) {
			super(class_2085.field_9671);
			this.field_9675 = arg;
			this.field_9676 = arg2;
		}

		public static class_2085.class_2087 method_9013(class_2025 arg) {
			return new class_2085.class_2087(arg, class_2096.class_2100.field_9708);
		}

		public boolean method_9014(class_3222 arg, class_243 arg2, int i) {
			return !this.field_9675.method_8859(arg2.field_1352, arg2.field_1351, arg2.field_1350, arg.field_5987, arg.field_6010, arg.field_6035)
				? false
				: this.field_9676.method_9054(i);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("distance", this.field_9675.method_8858());
			jsonObject.add("duration", this.field_9676.method_9036());
			return jsonObject;
		}
	}
}
