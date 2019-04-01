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

public class class_2115 implements class_179<class_2115.class_2117> {
	private static final class_2960 field_9732 = new class_2960("player_hurt_entity");
	private final Map<class_2985, class_2115.class_2116> field_9733 = Maps.<class_2985, class_2115.class_2116>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9732;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2115.class_2117> arg2) {
		class_2115.class_2116 lv = (class_2115.class_2116)this.field_9733.get(arg);
		if (lv == null) {
			lv = new class_2115.class_2116(arg);
			this.field_9733.put(arg, lv);
		}

		lv.method_9099(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2115.class_2117> arg2) {
		class_2115.class_2116 lv = (class_2115.class_2116)this.field_9733.get(arg);
		if (lv != null) {
			lv.method_9102(arg2);
			if (lv.method_9100()) {
				this.field_9733.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9733.remove(arg);
	}

	public class_2115.class_2117 method_9098(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2019 lv = class_2019.method_8839(jsonObject.get("damage"));
		class_2048 lv2 = class_2048.method_8913(jsonObject.get("entity"));
		return new class_2115.class_2117(lv, lv2);
	}

	public void method_9097(class_3222 arg, class_1297 arg2, class_1282 arg3, float f, float g, boolean bl) {
		class_2115.class_2116 lv = (class_2115.class_2116)this.field_9733.get(arg.method_14236());
		if (lv != null) {
			lv.method_9101(arg, arg2, arg3, f, g, bl);
		}
	}

	static class class_2116 {
		private final class_2985 field_9735;
		private final Set<class_179.class_180<class_2115.class_2117>> field_9734 = Sets.<class_179.class_180<class_2115.class_2117>>newHashSet();

		public class_2116(class_2985 arg) {
			this.field_9735 = arg;
		}

		public boolean method_9100() {
			return this.field_9734.isEmpty();
		}

		public void method_9099(class_179.class_180<class_2115.class_2117> arg) {
			this.field_9734.add(arg);
		}

		public void method_9102(class_179.class_180<class_2115.class_2117> arg) {
			this.field_9734.remove(arg);
		}

		public void method_9101(class_3222 arg, class_1297 arg2, class_1282 arg3, float f, float g, boolean bl) {
			List<class_179.class_180<class_2115.class_2117>> list = null;

			for (class_179.class_180<class_2115.class_2117> lv : this.field_9734) {
				if (lv.method_797().method_9104(arg, arg2, arg3, f, g, bl)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2115.class_2117>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2115.class_2117> lvx : list) {
					lvx.method_796(this.field_9735);
				}
			}
		}
	}

	public static class class_2117 extends class_195 {
		private final class_2019 field_9736;
		private final class_2048 field_9737;

		public class_2117(class_2019 arg, class_2048 arg2) {
			super(class_2115.field_9732);
			this.field_9736 = arg;
			this.field_9737 = arg2;
		}

		public static class_2115.class_2117 method_9103(class_2019.class_2020 arg) {
			return new class_2115.class_2117(arg.method_8843(), class_2048.field_9599);
		}

		public boolean method_9104(class_3222 arg, class_1297 arg2, class_1282 arg3, float f, float g, boolean bl) {
			return !this.field_9736.method_8838(arg, arg3, f, g, bl) ? false : this.field_9737.method_8914(arg, arg2);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("damage", this.field_9736.method_8840());
			jsonObject.add("entity", this.field_9737.method_8912());
			return jsonObject;
		}
	}
}
