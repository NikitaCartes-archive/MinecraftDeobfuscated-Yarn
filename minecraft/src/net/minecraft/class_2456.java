package net.minecraft;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.recipe.RecipeSerializers;
import net.minecraft.util.Identifier;

public class class_2456 {
	private final RecipeSerializers.Dummy<?> field_11429;

	public class_2456(RecipeSerializers.Dummy<?> dummy) {
		this.field_11429 = dummy;
	}

	public static class_2456 method_10476(RecipeSerializers.Dummy<?> dummy) {
		return new class_2456(dummy);
	}

	public void method_10475(Consumer<class_2444> consumer, String string) {
		consumer.accept(new class_2444() {
			@Override
			public JsonObject method_10416() {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("type", class_2456.this.field_11429.getId());
				return jsonObject;
			}

			@Override
			public Identifier method_10417() {
				return new Identifier(string);
			}

			@Nullable
			@Override
			public JsonObject method_10415() {
				return null;
			}

			@Nullable
			@Override
			public Identifier method_10418() {
				return new Identifier("");
			}
		});
	}
}
