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

public class class_2131 implements class_179<class_2131.class_2133> {
	private static final class_2960 field_9753 = new class_2960("tame_animal");
	private final Map<class_2985, class_2131.class_2132> field_9754 = Maps.<class_2985, class_2131.class_2132>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9753;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2131.class_2133> arg2) {
		class_2131.class_2132 lv = (class_2131.class_2132)this.field_9754.get(arg);
		if (lv == null) {
			lv = new class_2131.class_2132(arg);
			this.field_9754.put(arg, lv);
		}

		lv.method_9134(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2131.class_2133> arg2) {
		class_2131.class_2132 lv = (class_2131.class_2132)this.field_9754.get(arg);
		if (lv != null) {
			lv.method_9137(arg2);
			if (lv.method_9135()) {
				this.field_9754.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9754.remove(arg);
	}

	public class_2131.class_2133 method_9133(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2048 lv = class_2048.method_8913(jsonObject.get("entity"));
		return new class_2131.class_2133(lv);
	}

	public void method_9132(class_3222 arg, class_1429 arg2) {
		class_2131.class_2132 lv = (class_2131.class_2132)this.field_9754.get(arg.method_14236());
		if (lv != null) {
			lv.method_9136(arg, arg2);
		}
	}

	static class class_2132 {
		private final class_2985 field_9756;
		private final Set<class_179.class_180<class_2131.class_2133>> field_9755 = Sets.<class_179.class_180<class_2131.class_2133>>newHashSet();

		public class_2132(class_2985 arg) {
			this.field_9756 = arg;
		}

		public boolean method_9135() {
			return this.field_9755.isEmpty();
		}

		public void method_9134(class_179.class_180<class_2131.class_2133> arg) {
			this.field_9755.add(arg);
		}

		public void method_9137(class_179.class_180<class_2131.class_2133> arg) {
			this.field_9755.remove(arg);
		}

		public void method_9136(class_3222 arg, class_1429 arg2) {
			List<class_179.class_180<class_2131.class_2133>> list = null;

			for (class_179.class_180<class_2131.class_2133> lv : this.field_9755) {
				if (lv.method_797().method_9139(arg, arg2)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2131.class_2133>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2131.class_2133> lvx : list) {
					lvx.method_796(this.field_9756);
				}
			}
		}
	}

	public static class class_2133 extends class_195 {
		private final class_2048 field_9757;

		public class_2133(class_2048 arg) {
			super(class_2131.field_9753);
			this.field_9757 = arg;
		}

		public static class_2131.class_2133 method_9138() {
			return new class_2131.class_2133(class_2048.field_9599);
		}

		public static class_2131.class_2133 method_16114(class_2048 arg) {
			return new class_2131.class_2133(arg);
		}

		public boolean method_9139(class_3222 arg, class_1429 arg2) {
			return this.field_9757.method_8914(arg, arg2);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.field_9757.method_8912());
			return jsonObject;
		}
	}
}
