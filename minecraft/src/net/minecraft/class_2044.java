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

public class class_2044 implements class_179<class_2044.class_2046> {
	private static final class_2960 field_9589 = new class_2960("entity_hurt_player");
	private final Map<class_2985, class_2044.class_2045> field_9590 = Maps.<class_2985, class_2044.class_2045>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9589;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2044.class_2046> arg2) {
		class_2044.class_2045 lv = (class_2044.class_2045)this.field_9590.get(arg);
		if (lv == null) {
			lv = new class_2044.class_2045(arg);
			this.field_9590.put(arg, lv);
		}

		lv.method_8903(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2044.class_2046> arg2) {
		class_2044.class_2045 lv = (class_2044.class_2045)this.field_9590.get(arg);
		if (lv != null) {
			lv.method_8906(arg2);
			if (lv.method_8904()) {
				this.field_9590.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9590.remove(arg);
	}

	public class_2044.class_2046 method_8902(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2019 lv = class_2019.method_8839(jsonObject.get("damage"));
		return new class_2044.class_2046(lv);
	}

	public void method_8901(class_3222 arg, class_1282 arg2, float f, float g, boolean bl) {
		class_2044.class_2045 lv = (class_2044.class_2045)this.field_9590.get(arg.method_14236());
		if (lv != null) {
			lv.method_8905(arg, arg2, f, g, bl);
		}
	}

	static class class_2045 {
		private final class_2985 field_9592;
		private final Set<class_179.class_180<class_2044.class_2046>> field_9591 = Sets.<class_179.class_180<class_2044.class_2046>>newHashSet();

		public class_2045(class_2985 arg) {
			this.field_9592 = arg;
		}

		public boolean method_8904() {
			return this.field_9591.isEmpty();
		}

		public void method_8903(class_179.class_180<class_2044.class_2046> arg) {
			this.field_9591.add(arg);
		}

		public void method_8906(class_179.class_180<class_2044.class_2046> arg) {
			this.field_9591.remove(arg);
		}

		public void method_8905(class_3222 arg, class_1282 arg2, float f, float g, boolean bl) {
			List<class_179.class_180<class_2044.class_2046>> list = null;

			for (class_179.class_180<class_2044.class_2046> lv : this.field_9591) {
				if (lv.method_797().method_8907(arg, arg2, f, g, bl)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2044.class_2046>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2044.class_2046> lvx : list) {
					lvx.method_796(this.field_9592);
				}
			}
		}
	}

	public static class class_2046 extends class_195 {
		private final class_2019 field_9593;

		public class_2046(class_2019 arg) {
			super(class_2044.field_9589);
			this.field_9593 = arg;
		}

		public static class_2044.class_2046 method_8908(class_2019.class_2020 arg) {
			return new class_2044.class_2046(arg.method_8843());
		}

		public boolean method_8907(class_3222 arg, class_1282 arg2, float f, float g, boolean bl) {
			return this.field_9593.method_8838(arg, arg2, f, g, bl);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("damage", this.field_9593.method_8840());
			return jsonObject;
		}
	}
}
