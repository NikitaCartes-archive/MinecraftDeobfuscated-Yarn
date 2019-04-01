package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Random;

public class class_104 extends class_120 {
	private class_104(class_209[] args) {
		super(args);
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		Float float_ = arg2.method_296(class_181.field_1225);
		if (float_ != null) {
			Random random = arg2.method_294();
			float f = 1.0F / float_;
			int i = arg.method_7947();
			int j = 0;

			for (int k = 0; k < i; k++) {
				if (random.nextFloat() <= f) {
					j++;
				}
			}

			arg.method_7939(j);
		}

		return arg;
	}

	public static class_120.class_121<?> method_478() {
		return method_520(class_104::new);
	}

	public static class class_105 extends class_120.class_123<class_104> {
		protected class_105() {
			super(new class_2960("explosion_decay"), class_104.class);
		}

		public class_104 method_479(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			return new class_104(args);
		}
	}
}
