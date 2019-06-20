package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3670 extends class_120 {
	private static final Logger field_16230 = LogManager.getLogger();
	private final class_2561 field_16228;
	@Nullable
	private final class_47.class_50 field_16229;

	private class_3670(class_209[] args, @Nullable class_2561 arg, @Nullable class_47.class_50 arg2) {
		super(args);
		this.field_16228 = arg;
		this.field_16229 = arg2;
	}

	@Override
	public Set<class_169<?>> method_293() {
		return this.field_16229 != null ? ImmutableSet.of(this.field_16229.method_315()) : ImmutableSet.of();
	}

	public static UnaryOperator<class_2561> method_16190(class_47 arg, @Nullable class_47.class_50 arg2) {
		if (arg2 != null) {
			class_1297 lv = arg.method_296(arg2.method_315());
			if (lv != null) {
				class_2168 lv2 = lv.method_5671().method_9206(2);
				return arg3 -> {
					try {
						return class_2564.method_10881(lv2, arg3, lv, 0);
					} catch (CommandSyntaxException var4) {
						field_16230.warn("Failed to resolve text component", (Throwable)var4);
						return arg3;
					}
				};
			}
		}

		return argx -> argx;
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		if (this.field_16228 != null) {
			arg.method_7977((class_2561)method_16190(arg2, this.field_16229).apply(this.field_16228));
		}

		return arg;
	}

	public static class class_147 extends class_120.class_123<class_3670> {
		public class_147() {
			super(new class_2960("set_name"), class_3670.class);
		}

		public void method_630(JsonObject jsonObject, class_3670 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			if (arg.field_16228 != null) {
				jsonObject.add("name", class_2561.class_2562.method_10868(arg.field_16228));
			}

			if (arg.field_16229 != null) {
				jsonObject.add("entity", jsonSerializationContext.serialize(arg.field_16229));
			}
		}

		public class_3670 method_629(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			class_2561 lv = class_2561.class_2562.method_10872(jsonObject.get("name"));
			class_47.class_50 lv2 = class_3518.method_15283(jsonObject, "entity", null, jsonDeserializationContext, class_47.class_50.class);
			return new class_3670(args, lv, lv2);
		}
	}
}
