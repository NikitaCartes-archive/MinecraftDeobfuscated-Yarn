package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;

public class class_91 extends class_85 {
	private final class_3494<class_1792> field_1005;
	private final boolean field_1006;

	private class_91(class_3494<class_1792> arg, boolean bl, int i, int j, class_209[] args, class_117[] args2) {
		super(i, j, args, args2);
		this.field_1005 = arg;
		this.field_1006 = bl;
	}

	@Override
	public void method_433(Consumer<class_1799> consumer, class_47 arg) {
		this.field_1005.method_15138().forEach(argx -> consumer.accept(new class_1799(argx)));
	}

	private boolean method_447(class_47 arg, Consumer<class_82> consumer) {
		if (!this.method_414(arg)) {
			return false;
		} else {
			for (final class_1792 lv : this.field_1005.method_15138()) {
				consumer.accept(new class_85.class_88() {
					@Override
					public void method_426(Consumer<class_1799> consumer, class_47 arg) {
						consumer.accept(new class_1799(lv));
					}
				});
			}

			return true;
		}
	}

	@Override
	public boolean expand(class_47 arg, Consumer<class_82> consumer) {
		return this.field_1006 ? this.method_447(arg, consumer) : super.expand(arg, consumer);
	}

	public static class_85.class_86<?> method_445(class_3494<class_1792> arg) {
		return method_434((i, j, args, args2) -> new class_91(arg, true, i, j, args, args2));
	}

	public static class class_92 extends class_85.class_90<class_91> {
		public class_92() {
			super(new class_2960("tag"), class_91.class);
		}

		public void method_451(JsonObject jsonObject, class_91 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_442(jsonObject, arg, jsonSerializationContext);
			jsonObject.addProperty("name", arg.field_1005.method_15143().toString());
			jsonObject.addProperty("expand", arg.field_1006);
		}

		protected class_91 method_450(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, class_209[] args, class_117[] args2) {
			class_2960 lv = new class_2960(class_3518.method_15265(jsonObject, "name"));
			class_3494<class_1792> lv2 = class_3489.method_15106().method_15193(lv);
			if (lv2 == null) {
				throw new JsonParseException("Can't find tag: " + lv);
			} else {
				boolean bl = class_3518.method_15270(jsonObject, "expand");
				return new class_91(lv2, bl, i, j, args, args2);
			}
		}
	}
}
