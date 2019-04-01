package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;

public class class_67 extends class_85 {
	public static final class_2960 field_981 = new class_2960("dynamic");
	private final class_2960 field_980;

	private class_67(class_2960 arg, int i, int j, class_209[] args, class_117[] args2) {
		super(i, j, args, args2);
		this.field_980 = arg;
	}

	@Override
	public void method_433(Consumer<class_1799> consumer, class_47 arg) {
		arg.method_297(this.field_980, consumer);
	}

	public static class_85.class_86<?> method_390(class_2960 arg) {
		return method_434((i, j, args, args2) -> new class_67(arg, i, j, args, args2));
	}

	public static class class_68 extends class_85.class_90<class_67> {
		public class_68() {
			super(new class_2960("dynamic"), class_67.class);
		}

		public void method_393(JsonObject jsonObject, class_67 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_442(jsonObject, arg, jsonSerializationContext);
			jsonObject.addProperty("name", arg.field_980.toString());
		}

		protected class_67 method_392(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, class_209[] args, class_117[] args2) {
			class_2960 lv = new class_2960(class_3518.method_15265(jsonObject, "name"));
			return new class_67(lv, i, j, args, args2);
		}
	}
}
