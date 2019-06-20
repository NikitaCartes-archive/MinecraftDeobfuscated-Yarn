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

public class class_2010 implements class_179<class_2010.class_2012> {
	private static final class_2960 field_9509 = new class_2960("consume_item");
	private final Map<class_2985, class_2010.class_2011> field_9510 = Maps.<class_2985, class_2010.class_2011>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9509;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2010.class_2012> arg2) {
		class_2010.class_2011 lv = (class_2010.class_2011)this.field_9510.get(arg);
		if (lv == null) {
			lv = new class_2010.class_2011(arg);
			this.field_9510.put(arg, lv);
		}

		lv.method_8822(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2010.class_2012> arg2) {
		class_2010.class_2011 lv = (class_2010.class_2011)this.field_9510.get(arg);
		if (lv != null) {
			lv.method_8825(arg2);
			if (lv.method_8823()) {
				this.field_9510.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9510.remove(arg);
	}

	public class_2010.class_2012 method_8820(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new class_2010.class_2012(class_2073.method_8969(jsonObject.get("item")));
	}

	public void method_8821(class_3222 arg, class_1799 arg2) {
		class_2010.class_2011 lv = (class_2010.class_2011)this.field_9510.get(arg.method_14236());
		if (lv != null) {
			lv.method_8824(arg2);
		}
	}

	static class class_2011 {
		private final class_2985 field_9512;
		private final Set<class_179.class_180<class_2010.class_2012>> field_9511 = Sets.<class_179.class_180<class_2010.class_2012>>newHashSet();

		public class_2011(class_2985 arg) {
			this.field_9512 = arg;
		}

		public boolean method_8823() {
			return this.field_9511.isEmpty();
		}

		public void method_8822(class_179.class_180<class_2010.class_2012> arg) {
			this.field_9511.add(arg);
		}

		public void method_8825(class_179.class_180<class_2010.class_2012> arg) {
			this.field_9511.remove(arg);
		}

		public void method_8824(class_1799 arg) {
			List<class_179.class_180<class_2010.class_2012>> list = null;

			for (class_179.class_180<class_2010.class_2012> lv : this.field_9511) {
				if (lv.method_797().method_8826(arg)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2010.class_2012>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2010.class_2012> lvx : list) {
					lvx.method_796(this.field_9512);
				}
			}
		}
	}

	public static class class_2012 extends class_195 {
		private final class_2073 field_9513;

		public class_2012(class_2073 arg) {
			super(class_2010.field_9509);
			this.field_9513 = arg;
		}

		public static class_2010.class_2012 method_8827() {
			return new class_2010.class_2012(class_2073.field_9640);
		}

		public static class_2010.class_2012 method_8828(class_1935 arg) {
			return new class_2010.class_2012(
				new class_2073(null, arg.method_8389(), class_2096.class_2100.field_9708, class_2096.class_2100.field_9708, new class_2035[0], null, class_2105.field_9716)
			);
		}

		public boolean method_8826(class_1799 arg) {
			return this.field_9513.method_8970(arg);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.field_9513.method_8971());
			return jsonObject;
		}
	}
}
