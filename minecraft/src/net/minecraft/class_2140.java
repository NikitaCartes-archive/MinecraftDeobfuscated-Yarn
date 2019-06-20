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

public class class_2140 implements class_179<class_2140.class_2142> {
	private static final class_2960 field_9762 = new class_2960("villager_trade");
	private final Map<class_2985, class_2140.class_2141> field_9763 = Maps.<class_2985, class_2140.class_2141>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9762;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2140.class_2142> arg2) {
		class_2140.class_2141 lv = (class_2140.class_2141)this.field_9763.get(arg);
		if (lv == null) {
			lv = new class_2140.class_2141(arg);
			this.field_9763.put(arg, lv);
		}

		lv.method_9150(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2140.class_2142> arg2) {
		class_2140.class_2141 lv = (class_2140.class_2141)this.field_9763.get(arg);
		if (lv != null) {
			lv.method_9152(arg2);
			if (lv.method_9151()) {
				this.field_9763.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9763.remove(arg);
	}

	public class_2140.class_2142 method_9148(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2048 lv = class_2048.method_8913(jsonObject.get("villager"));
		class_2073 lv2 = class_2073.method_8969(jsonObject.get("item"));
		return new class_2140.class_2142(lv, lv2);
	}

	public void method_9146(class_3222 arg, class_3988 arg2, class_1799 arg3) {
		class_2140.class_2141 lv = (class_2140.class_2141)this.field_9763.get(arg.method_14236());
		if (lv != null) {
			lv.method_9149(arg, arg2, arg3);
		}
	}

	static class class_2141 {
		private final class_2985 field_9765;
		private final Set<class_179.class_180<class_2140.class_2142>> field_9764 = Sets.<class_179.class_180<class_2140.class_2142>>newHashSet();

		public class_2141(class_2985 arg) {
			this.field_9765 = arg;
		}

		public boolean method_9151() {
			return this.field_9764.isEmpty();
		}

		public void method_9150(class_179.class_180<class_2140.class_2142> arg) {
			this.field_9764.add(arg);
		}

		public void method_9152(class_179.class_180<class_2140.class_2142> arg) {
			this.field_9764.remove(arg);
		}

		public void method_9149(class_3222 arg, class_3988 arg2, class_1799 arg3) {
			List<class_179.class_180<class_2140.class_2142>> list = null;

			for (class_179.class_180<class_2140.class_2142> lv : this.field_9764) {
				if (lv.method_797().method_9154(arg, arg2, arg3)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2140.class_2142>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2140.class_2142> lvx : list) {
					lvx.method_796(this.field_9765);
				}
			}
		}
	}

	public static class class_2142 extends class_195 {
		private final class_2048 field_9767;
		private final class_2073 field_9766;

		public class_2142(class_2048 arg, class_2073 arg2) {
			super(class_2140.field_9762);
			this.field_9767 = arg;
			this.field_9766 = arg2;
		}

		public static class_2140.class_2142 method_9153() {
			return new class_2140.class_2142(class_2048.field_9599, class_2073.field_9640);
		}

		public boolean method_9154(class_3222 arg, class_3988 arg2, class_1799 arg3) {
			return !this.field_9767.method_8914(arg, arg2) ? false : this.field_9766.method_8970(arg3);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.field_9766.method_8971());
			jsonObject.add("villager", this.field_9767.method_8912());
			return jsonObject;
		}
	}
}
