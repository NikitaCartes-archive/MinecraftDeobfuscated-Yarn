package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface RecipeJsonProvider {
	void serialize(JsonObject jsonObject);

	default JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", Registry.RECIPE_SERIALIZER.getId(this.getSerializer()).toString());
		this.serialize(jsonObject);
		return jsonObject;
	}

	Identifier getRecipeId();

	RecipeSerializer<?> getSerializer();

	@Nullable
	JsonObject toAdvancementJson();

	@Nullable
	Identifier getAdvancementId();
}
