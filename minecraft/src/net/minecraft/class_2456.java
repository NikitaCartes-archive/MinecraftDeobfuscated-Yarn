package net.minecraft;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class class_2456 {
	private final class_1866<?> field_11429;

	public class_2456(class_1866<?> arg) {
		this.field_11429 = arg;
	}

	public static class_2456 method_10476(class_1866<?> arg) {
		return new class_2456(arg);
	}

	public void method_10475(Consumer<class_2444> consumer, String string) {
		consumer.accept(new class_2444() {
			@Override
			public void method_10416(JsonObject jsonObject) {
			}

			@Override
			public class_1865<?> method_17800() {
				return class_2456.this.field_11429;
			}

			@Override
			public class_2960 method_10417() {
				return new class_2960(string);
			}

			@Nullable
			@Override
			public JsonObject method_10415() {
				return null;
			}

			@Override
			public class_2960 method_10418() {
				return new class_2960("");
			}
		});
	}
}
