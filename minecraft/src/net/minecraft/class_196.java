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
import javax.annotation.Nullable;

public class class_196 implements class_179<class_196.class_198> {
	private static final class_2960 field_1271 = new class_2960("bred_animals");
	private final Map<class_2985, class_196.class_197> field_1272 = Maps.<class_2985, class_196.class_197>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_1271;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_196.class_198> arg2) {
		class_196.class_197 lv = (class_196.class_197)this.field_1272.get(arg);
		if (lv == null) {
			lv = new class_196.class_197(arg);
			this.field_1272.put(arg, lv);
		}

		lv.method_856(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_196.class_198> arg2) {
		class_196.class_197 lv = (class_196.class_197)this.field_1272.get(arg);
		if (lv != null) {
			lv.method_859(arg2);
			if (lv.method_857()) {
				this.field_1272.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_1272.remove(arg);
	}

	public class_196.class_198 method_854(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2048 lv = class_2048.method_8913(jsonObject.get("parent"));
		class_2048 lv2 = class_2048.method_8913(jsonObject.get("partner"));
		class_2048 lv3 = class_2048.method_8913(jsonObject.get("child"));
		return new class_196.class_198(lv, lv2, lv3);
	}

	public void method_855(class_3222 arg, class_1429 arg2, @Nullable class_1429 arg3, @Nullable class_1296 arg4) {
		class_196.class_197 lv = (class_196.class_197)this.field_1272.get(arg.method_14236());
		if (lv != null) {
			lv.method_858(arg, arg2, arg3, arg4);
		}
	}

	static class class_197 {
		private final class_2985 field_1274;
		private final Set<class_179.class_180<class_196.class_198>> field_1273 = Sets.<class_179.class_180<class_196.class_198>>newHashSet();

		public class_197(class_2985 arg) {
			this.field_1274 = arg;
		}

		public boolean method_857() {
			return this.field_1273.isEmpty();
		}

		public void method_856(class_179.class_180<class_196.class_198> arg) {
			this.field_1273.add(arg);
		}

		public void method_859(class_179.class_180<class_196.class_198> arg) {
			this.field_1273.remove(arg);
		}

		public void method_858(class_3222 arg, class_1429 arg2, @Nullable class_1429 arg3, @Nullable class_1296 arg4) {
			List<class_179.class_180<class_196.class_198>> list = null;

			for (class_179.class_180<class_196.class_198> lv : this.field_1273) {
				if (lv.method_797().method_862(arg, arg2, arg3, arg4)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_196.class_198>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_196.class_198> lvx : list) {
					lvx.method_796(this.field_1274);
				}
			}
		}
	}

	public static class class_198 extends class_195 {
		private final class_2048 field_1276;
		private final class_2048 field_1277;
		private final class_2048 field_1275;

		public class_198(class_2048 arg, class_2048 arg2, class_2048 arg3) {
			super(class_196.field_1271);
			this.field_1276 = arg;
			this.field_1277 = arg2;
			this.field_1275 = arg3;
		}

		public static class_196.class_198 method_860() {
			return new class_196.class_198(class_2048.field_9599, class_2048.field_9599, class_2048.field_9599);
		}

		public static class_196.class_198 method_861(class_2048.class_2049 arg) {
			return new class_196.class_198(arg.method_8920(), class_2048.field_9599, class_2048.field_9599);
		}

		public boolean method_862(class_3222 arg, class_1429 arg2, @Nullable class_1429 arg3, @Nullable class_1296 arg4) {
			return !this.field_1275.method_8914(arg, arg4)
				? false
				: this.field_1276.method_8914(arg, arg2) && this.field_1277.method_8914(arg, arg3)
					|| this.field_1276.method_8914(arg, arg3) && this.field_1277.method_8914(arg, arg2);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("parent", this.field_1276.method_8912());
			jsonObject.add("partner", this.field_1277.method_8912());
			jsonObject.add("child", this.field_1275.method_8912());
			return jsonObject;
		}
	}
}
