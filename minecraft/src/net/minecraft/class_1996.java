package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class class_1996 implements class_179<class_1996.class_1998> {
	private static final class_2960 field_9488 = new class_2960("brewed_potion");
	private final Map<class_2985, class_1996.class_1997> field_9489 = Maps.<class_2985, class_1996.class_1997>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9488;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_1996.class_1998> arg2) {
		class_1996.class_1997 lv = (class_1996.class_1997)this.field_9489.get(arg);
		if (lv == null) {
			lv = new class_1996.class_1997(arg);
			this.field_9489.put(arg, lv);
		}

		lv.method_8786(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_1996.class_1998> arg2) {
		class_1996.class_1997 lv = (class_1996.class_1997)this.field_9489.get(arg);
		if (lv != null) {
			lv.method_8788(arg2);
			if (lv.method_8787()) {
				this.field_9489.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9489.remove(arg);
	}

	public class_1996.class_1998 method_8785(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_1842 lv = null;
		if (jsonObject.has("potion")) {
			class_2960 lv2 = new class_2960(class_3518.method_15265(jsonObject, "potion"));
			lv = (class_1842)class_2378.field_11143.method_17966(lv2).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + lv2 + "'"));
		}

		return new class_1996.class_1998(lv);
	}

	public void method_8784(class_3222 arg, class_1842 arg2) {
		class_1996.class_1997 lv = (class_1996.class_1997)this.field_9489.get(arg.method_14236());
		if (lv != null) {
			lv.method_8789(arg2);
		}
	}

	static class class_1997 {
		private final class_2985 field_9491;
		private final Set<class_179.class_180<class_1996.class_1998>> field_9490 = Sets.<class_179.class_180<class_1996.class_1998>>newHashSet();

		public class_1997(class_2985 arg) {
			this.field_9491 = arg;
		}

		public boolean method_8787() {
			return this.field_9490.isEmpty();
		}

		public void method_8786(class_179.class_180<class_1996.class_1998> arg) {
			this.field_9490.add(arg);
		}

		public void method_8788(class_179.class_180<class_1996.class_1998> arg) {
			this.field_9490.remove(arg);
		}

		public void method_8789(class_1842 arg) {
			List<class_179.class_180<class_1996.class_1998>> list = null;

			for (class_179.class_180<class_1996.class_1998> lv : this.field_9490) {
				if (lv.method_797().method_8790(arg)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_1996.class_1998>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_1996.class_1998> lvx : list) {
					lvx.method_796(this.field_9491);
				}
			}
		}
	}

	public static class class_1998 extends class_195 {
		private final class_1842 field_9492;

		public class_1998(@Nullable class_1842 arg) {
			super(class_1996.field_9488);
			this.field_9492 = arg;
		}

		public static class_1996.class_1998 method_8791() {
			return new class_1996.class_1998(null);
		}

		public boolean method_8790(class_1842 arg) {
			return this.field_9492 == null || this.field_9492 == arg;
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			if (this.field_9492 != null) {
				jsonObject.addProperty("potion", class_2378.field_11143.method_10221(this.field_9492).toString());
			}

			return jsonObject;
		}
	}
}
