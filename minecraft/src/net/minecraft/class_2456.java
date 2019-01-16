package net.minecraft;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;

public class class_2456 {
	private final SpecialRecipeSerializer<?> field_11429;

	public class_2456(SpecialRecipeSerializer<?> specialRecipeSerializer) {
		this.field_11429 = specialRecipeSerializer;
	}

	public static class_2456 method_10476(SpecialRecipeSerializer<?> specialRecipeSerializer) {
		return new class_2456(specialRecipeSerializer);
	}

	public void method_10475(Consumer<class_2444> consumer, String string) {
		consumer.accept(new class_2444() {
			@Override
			public void method_10416(JsonObject jsonObject) {
			}

			@Override
			public RecipeSerializer<?> method_17800() {
				return class_2456.this.field_11429;
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

			@Override
			public Identifier method_10418() {
				return new Identifier("");
			}
		});
	}
}
