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

public class class_1999 implements class_179<class_1999.class_2001> {
	private static final class_2960 field_9493 = new class_2960("changed_dimension");
	private final Map<class_2985, class_1999.class_2000> field_9494 = Maps.<class_2985, class_1999.class_2000>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9493;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_1999.class_2001> arg2) {
		class_1999.class_2000 lv = (class_1999.class_2000)this.field_9494.get(arg);
		if (lv == null) {
			lv = new class_1999.class_2000(arg);
			this.field_9494.put(arg, lv);
		}

		lv.method_8795(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_1999.class_2001> arg2) {
		class_1999.class_2000 lv = (class_1999.class_2000)this.field_9494.get(arg);
		if (lv != null) {
			lv.method_8798(arg2);
			if (lv.method_8796()) {
				this.field_9494.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9494.remove(arg);
	}

	public class_1999.class_2001 method_8793(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2874 lv = jsonObject.has("from") ? class_2874.method_12483(new class_2960(class_3518.method_15265(jsonObject, "from"))) : null;
		class_2874 lv2 = jsonObject.has("to") ? class_2874.method_12483(new class_2960(class_3518.method_15265(jsonObject, "to"))) : null;
		return new class_1999.class_2001(lv, lv2);
	}

	public void method_8794(class_3222 arg, class_2874 arg2, class_2874 arg3) {
		class_1999.class_2000 lv = (class_1999.class_2000)this.field_9494.get(arg.method_14236());
		if (lv != null) {
			lv.method_8797(arg2, arg3);
		}
	}

	static class class_2000 {
		private final class_2985 field_9496;
		private final Set<class_179.class_180<class_1999.class_2001>> field_9495 = Sets.<class_179.class_180<class_1999.class_2001>>newHashSet();

		public class_2000(class_2985 arg) {
			this.field_9496 = arg;
		}

		public boolean method_8796() {
			return this.field_9495.isEmpty();
		}

		public void method_8795(class_179.class_180<class_1999.class_2001> arg) {
			this.field_9495.add(arg);
		}

		public void method_8798(class_179.class_180<class_1999.class_2001> arg) {
			this.field_9495.remove(arg);
		}

		public void method_8797(class_2874 arg, class_2874 arg2) {
			List<class_179.class_180<class_1999.class_2001>> list = null;

			for (class_179.class_180<class_1999.class_2001> lv : this.field_9495) {
				if (lv.method_797().method_8800(arg, arg2)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_1999.class_2001>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_1999.class_2001> lvx : list) {
					lvx.method_796(this.field_9496);
				}
			}
		}
	}

	public static class class_2001 extends class_195 {
		@Nullable
		private final class_2874 field_9497;
		@Nullable
		private final class_2874 field_9498;

		public class_2001(@Nullable class_2874 arg, @Nullable class_2874 arg2) {
			super(class_1999.field_9493);
			this.field_9497 = arg;
			this.field_9498 = arg2;
		}

		public static class_1999.class_2001 method_8799(class_2874 arg) {
			return new class_1999.class_2001(null, arg);
		}

		public boolean method_8800(class_2874 arg, class_2874 arg2) {
			return this.field_9497 != null && this.field_9497 != arg ? false : this.field_9498 == null || this.field_9498 == arg2;
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			if (this.field_9497 != null) {
				jsonObject.addProperty("from", class_2874.method_12485(this.field_9497).toString());
			}

			if (this.field_9498 != null) {
				jsonObject.addProperty("to", class_2874.method_12485(this.field_9498).toString());
			}

			return jsonObject;
		}
	}
}
