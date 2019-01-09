package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;

public class class_3671 extends class_120 {
	private final boolean field_16232;
	private final List<class_2561> field_16231;
	@Nullable
	private final class_47.class_50 field_16233;

	public class_3671(class_209[] args, boolean bl, List<class_2561> list, @Nullable class_47.class_50 arg) {
		super(args);
		this.field_16232 = bl;
		this.field_16231 = ImmutableList.copyOf(list);
		this.field_16233 = arg;
	}

	@Override
	public Set<class_169<?>> method_293() {
		return this.field_16233 != null ? ImmutableSet.of(this.field_16233.method_315()) : ImmutableSet.of();
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		class_2499 lv = this.method_15964(arg, !this.field_16231.isEmpty());
		if (lv != null) {
			if (this.field_16232) {
				lv.clear();
			}

			UnaryOperator<class_2561> unaryOperator = class_3670.method_16190(arg2, this.field_16233);
			this.field_16231.stream().map(unaryOperator).map(class_2561.class_2562::method_10867).map(class_2519::new).forEach(lv::method_10606);
		}

		return arg;
	}

	@Nullable
	private class_2499 method_15964(class_1799 arg, boolean bl) {
		class_2487 lv;
		if (arg.method_7985()) {
			lv = arg.method_7969();
		} else {
			if (!bl) {
				return null;
			}

			lv = new class_2487();
			arg.method_7980(lv);
		}

		class_2487 lv2;
		if (lv.method_10573("display", 10)) {
			lv2 = lv.method_10562("display");
		} else {
			if (!bl) {
				return null;
			}

			lv2 = new class_2487();
			lv.method_10566("display", lv2);
		}

		if (lv2.method_10573("Lore", 9)) {
			return lv2.method_10554("Lore", 8);
		} else if (bl) {
			class_2499 lv3 = new class_2499();
			lv2.method_10566("Lore", lv3);
			return lv3;
		} else {
			return null;
		}
	}

	public static class class_3672 extends class_120.class_123<class_3671> {
		public class_3672() {
			super(new class_2960("set_lore"), class_3671.class);
		}

		public void method_15969(JsonObject jsonObject, class_3671 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			jsonObject.addProperty("replace", arg.field_16232);
			JsonArray jsonArray = new JsonArray();

			for (class_2561 lv : arg.field_16231) {
				jsonArray.add(class_2561.class_2562.method_10868(lv));
			}

			jsonObject.add("lore", jsonArray);
			if (arg.field_16233 != null) {
				jsonObject.add("entity", jsonSerializationContext.serialize(arg.field_16233));
			}
		}

		public class_3671 method_15968(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			boolean bl = class_3518.method_15258(jsonObject, "replace", false);
			List<class_2561> list = (List<class_2561>)Streams.stream(class_3518.method_15261(jsonObject, "lore"))
				.map(class_2561.class_2562::method_10872)
				.collect(ImmutableList.toImmutableList());
			class_47.class_50 lv = class_3518.method_15283(jsonObject, "entity", null, jsonDeserializationContext, class_47.class_50.class);
			return new class_3671(args, bl, list, lv);
		}
	}
}
