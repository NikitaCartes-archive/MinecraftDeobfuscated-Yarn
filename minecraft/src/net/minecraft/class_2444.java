package net.minecraft;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface class_2444 {
	void method_10416(JsonObject jsonObject);

	default JsonObject method_17799() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", Registry.RECIPE_SERIALIZER.getId(this.method_17800()).toString());
		this.method_10416(jsonObject);
		return jsonObject;
	}

	Identifier method_10417();

	RecipeSerializer<?> method_17800();

	@Nullable
	JsonObject method_10415();

	@Nullable
	Identifier method_10418();
}
