package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_165 extends class_120 {
	private static final Logger field_1159 = LogManager.getLogger();

	private class_165(class_209[] args) {
		super(args);
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		if (arg.method_7960()) {
			return arg;
		} else {
			class_1860 lv = method_725(arg2, arg);
			if (lv != null) {
				class_1799 lv2 = lv.method_8110();
				if (!lv2.method_7960()) {
					class_1799 lv3 = lv2.method_7972();
					lv3.method_7939(arg.method_7947());
					return lv3;
				}
			}

			field_1159.warn("Couldn't smelt {} because there is no smelting recipe", arg);
			return arg;
		}
	}

	@Nullable
	public static class_1860 method_725(class_47 arg, class_1799 arg2) {
		for (class_1860 lv : arg.method_299().method_8433().method_8126()) {
			if (lv instanceof class_3861 && lv.method_8117().get(0).method_8093(arg2)) {
				return lv;
			}
		}

		return null;
	}

	public static class_120.class_121<?> method_724() {
		return method_520(class_165::new);
	}

	public static class class_166 extends class_120.class_123<class_165> {
		protected class_166() {
			super(new class_2960("furnace_smelt"), class_165.class);
		}

		public class_165 method_726(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			return new class_165(args);
		}
	}
}
