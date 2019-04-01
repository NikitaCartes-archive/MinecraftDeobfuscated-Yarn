package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Optional;
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
			Optional<class_3861> optional = arg2.method_299().method_8433().method_8132(class_3956.field_17546, new class_1277(arg), arg2.method_299());
			if (optional.isPresent()) {
				class_1799 lv = ((class_3861)optional.get()).method_8110();
				if (!lv.method_7960()) {
					class_1799 lv2 = lv.method_7972();
					lv2.method_7939(arg.method_7947());
					return lv2;
				}
			}

			field_1159.warn("Couldn't smelt {} because there is no smelting recipe", arg);
			return arg;
		}
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
