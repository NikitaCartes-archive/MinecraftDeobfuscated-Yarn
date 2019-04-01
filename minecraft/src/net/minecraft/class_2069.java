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

public class class_2069 implements class_179<class_2069.class_2071> {
	private static final class_2960 field_9633 = new class_2960("item_durability_changed");
	private final Map<class_2985, class_2069.class_2070> field_9634 = Maps.<class_2985, class_2069.class_2070>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9633;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2069.class_2071> arg2) {
		class_2069.class_2070 lv = (class_2069.class_2070)this.field_9634.get(arg);
		if (lv == null) {
			lv = new class_2069.class_2070(arg);
			this.field_9634.put(arg, lv);
		}

		lv.method_8963(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2069.class_2071> arg2) {
		class_2069.class_2070 lv = (class_2069.class_2070)this.field_9634.get(arg);
		if (lv != null) {
			lv.method_8966(arg2);
			if (lv.method_8964()) {
				this.field_9634.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9634.remove(arg);
	}

	public class_2069.class_2071 method_8962(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2073 lv = class_2073.method_8969(jsonObject.get("item"));
		class_2096.class_2100 lv2 = class_2096.class_2100.method_9056(jsonObject.get("durability"));
		class_2096.class_2100 lv3 = class_2096.class_2100.method_9056(jsonObject.get("delta"));
		return new class_2069.class_2071(lv, lv2, lv3);
	}

	public void method_8960(class_3222 arg, class_1799 arg2, int i) {
		class_2069.class_2070 lv = (class_2069.class_2070)this.field_9634.get(arg.method_14236());
		if (lv != null) {
			lv.method_8965(arg2, i);
		}
	}

	static class class_2070 {
		private final class_2985 field_9636;
		private final Set<class_179.class_180<class_2069.class_2071>> field_9635 = Sets.<class_179.class_180<class_2069.class_2071>>newHashSet();

		public class_2070(class_2985 arg) {
			this.field_9636 = arg;
		}

		public boolean method_8964() {
			return this.field_9635.isEmpty();
		}

		public void method_8963(class_179.class_180<class_2069.class_2071> arg) {
			this.field_9635.add(arg);
		}

		public void method_8966(class_179.class_180<class_2069.class_2071> arg) {
			this.field_9635.remove(arg);
		}

		public void method_8965(class_1799 arg, int i) {
			List<class_179.class_180<class_2069.class_2071>> list = null;

			for (class_179.class_180<class_2069.class_2071> lv : this.field_9635) {
				if (lv.method_797().method_8968(arg, i)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2069.class_2071>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2069.class_2071> lvx : list) {
					lvx.method_796(this.field_9636);
				}
			}
		}
	}

	public static class class_2071 extends class_195 {
		private final class_2073 field_9637;
		private final class_2096.class_2100 field_9638;
		private final class_2096.class_2100 field_9639;

		public class_2071(class_2073 arg, class_2096.class_2100 arg2, class_2096.class_2100 arg3) {
			super(class_2069.field_9633);
			this.field_9637 = arg;
			this.field_9638 = arg2;
			this.field_9639 = arg3;
		}

		public static class_2069.class_2071 method_8967(class_2073 arg, class_2096.class_2100 arg2) {
			return new class_2069.class_2071(arg, arg2, class_2096.class_2100.field_9708);
		}

		public boolean method_8968(class_1799 arg, int i) {
			if (!this.field_9637.method_8970(arg)) {
				return false;
			} else {
				return !this.field_9638.method_9054(arg.method_7936() - i) ? false : this.field_9639.method_9054(arg.method_7919() - i);
			}
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.field_9637.method_8971());
			jsonObject.add("durability", this.field_9638.method_9036());
			jsonObject.add("delta", this.field_9639.method_9036());
			return jsonObject;
		}
	}
}
