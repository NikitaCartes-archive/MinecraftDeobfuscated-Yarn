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

public class class_2014 implements class_179<class_2014.class_2016> {
	private static final class_2960 field_9514 = new class_2960("cured_zombie_villager");
	private final Map<class_2985, class_2014.class_2015> field_9515 = Maps.<class_2985, class_2014.class_2015>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9514;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2014.class_2016> arg2) {
		class_2014.class_2015 lv = (class_2014.class_2015)this.field_9515.get(arg);
		if (lv == null) {
			lv = new class_2014.class_2015(arg);
			this.field_9515.put(arg, lv);
		}

		lv.method_8832(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2014.class_2016> arg2) {
		class_2014.class_2015 lv = (class_2014.class_2015)this.field_9515.get(arg);
		if (lv != null) {
			lv.method_8834(arg2);
			if (lv.method_8833()) {
				this.field_9515.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9515.remove(arg);
	}

	public class_2014.class_2016 method_8830(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2048 lv = class_2048.method_8913(jsonObject.get("zombie"));
		class_2048 lv2 = class_2048.method_8913(jsonObject.get("villager"));
		return new class_2014.class_2016(lv, lv2);
	}

	public void method_8831(class_3222 arg, class_1642 arg2, class_1646 arg3) {
		class_2014.class_2015 lv = (class_2014.class_2015)this.field_9515.get(arg.method_14236());
		if (lv != null) {
			lv.method_8835(arg, arg2, arg3);
		}
	}

	static class class_2015 {
		private final class_2985 field_9517;
		private final Set<class_179.class_180<class_2014.class_2016>> field_9516 = Sets.<class_179.class_180<class_2014.class_2016>>newHashSet();

		public class_2015(class_2985 arg) {
			this.field_9517 = arg;
		}

		public boolean method_8833() {
			return this.field_9516.isEmpty();
		}

		public void method_8832(class_179.class_180<class_2014.class_2016> arg) {
			this.field_9516.add(arg);
		}

		public void method_8834(class_179.class_180<class_2014.class_2016> arg) {
			this.field_9516.remove(arg);
		}

		public void method_8835(class_3222 arg, class_1642 arg2, class_1646 arg3) {
			List<class_179.class_180<class_2014.class_2016>> list = null;

			for (class_179.class_180<class_2014.class_2016> lv : this.field_9516) {
				if (lv.method_797().method_8837(arg, arg2, arg3)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2014.class_2016>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2014.class_2016> lvx : list) {
					lvx.method_796(this.field_9517);
				}
			}
		}
	}

	public static class class_2016 extends class_195 {
		private final class_2048 field_9518;
		private final class_2048 field_9519;

		public class_2016(class_2048 arg, class_2048 arg2) {
			super(class_2014.field_9514);
			this.field_9518 = arg;
			this.field_9519 = arg2;
		}

		public static class_2014.class_2016 method_8836() {
			return new class_2014.class_2016(class_2048.field_9599, class_2048.field_9599);
		}

		public boolean method_8837(class_3222 arg, class_1642 arg2, class_1646 arg3) {
			return !this.field_9518.method_8914(arg, arg2) ? false : this.field_9519.method_8914(arg, arg3);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("zombie", this.field_9518.method_8912());
			jsonObject.add("villager", this.field_9519.method_8912());
			return jsonObject;
		}
	}
}
