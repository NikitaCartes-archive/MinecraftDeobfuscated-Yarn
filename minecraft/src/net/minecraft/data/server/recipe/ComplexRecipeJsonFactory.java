package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;

public class ComplexRecipeJsonFactory {
	private final SpecialRecipeSerializer<?> serializer;

	public ComplexRecipeJsonFactory(SpecialRecipeSerializer<?> specialRecipeSerializer) {
		this.serializer = specialRecipeSerializer;
	}

	public static ComplexRecipeJsonFactory create(SpecialRecipeSerializer<?> specialRecipeSerializer) {
		return new ComplexRecipeJsonFactory(specialRecipeSerializer);
	}

	public void offerTo(Consumer<RecipeJsonProvider> consumer, String string) {
		consumer.accept(new RecipeJsonProvider() {
			@Override
			public void serialize(JsonObject jsonObject) {
			}

			@Override
			public RecipeSerializer<?> getSerializer() {
				return ComplexRecipeJsonFactory.this.serializer;
			}

			@Override
			public Identifier method_10417() {
				return new Identifier(string);
			}

			@Nullable
			@Override
			public JsonObject toAdvancementJson() {
				return null;
			}

			@Override
			public Identifier method_10418() {
				return new Identifier("");
			}
		});
	}
}
