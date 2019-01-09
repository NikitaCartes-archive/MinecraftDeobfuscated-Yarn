package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;

public class class_2135 implements class_179<class_2135.class_2137> {
	public static final class_2960 field_9758 = new class_2960("tick");
	private final Map<class_2985, class_2135.class_2136> field_9759 = Maps.<class_2985, class_2135.class_2136>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9758;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2135.class_2137> arg2) {
		class_2135.class_2136 lv = (class_2135.class_2136)this.field_9759.get(arg);
		if (lv == null) {
			lv = new class_2135.class_2136(arg);
			this.field_9759.put(arg, lv);
		}

		lv.method_9142(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2135.class_2137> arg2) {
		class_2135.class_2136 lv = (class_2135.class_2136)this.field_9759.get(arg);
		if (lv != null) {
			lv.method_9144(arg2);
			if (lv.method_9143()) {
				this.field_9759.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9759.remove(arg);
	}

	public class_2135.class_2137 method_9140(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new class_2135.class_2137();
	}

	public void method_9141(class_3222 arg) {
		class_2135.class_2136 lv = (class_2135.class_2136)this.field_9759.get(arg.method_14236());
		if (lv != null) {
			lv.method_9145();
		}
	}

	static class class_2136 {
		private final class_2985 field_9761;
		private final Set<class_179.class_180<class_2135.class_2137>> field_9760 = Sets.<class_179.class_180<class_2135.class_2137>>newHashSet();

		public class_2136(class_2985 arg) {
			this.field_9761 = arg;
		}

		public boolean method_9143() {
			return this.field_9760.isEmpty();
		}

		public void method_9142(class_179.class_180<class_2135.class_2137> arg) {
			this.field_9760.add(arg);
		}

		public void method_9144(class_179.class_180<class_2135.class_2137> arg) {
			this.field_9760.remove(arg);
		}

		public void method_9145() {
			for (class_179.class_180<class_2135.class_2137> lv : Lists.newArrayList(this.field_9760)) {
				lv.method_796(this.field_9761);
			}
		}
	}

	public static class class_2137 extends class_195 {
		public class_2137() {
			super(class_2135.field_9758);
		}
	}
}
