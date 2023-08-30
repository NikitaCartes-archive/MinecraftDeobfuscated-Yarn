package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public interface RecipeJsonProvider {
	void serialize(JsonObject json);

	default JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", Registries.RECIPE_SERIALIZER.getId(this.serializer()).toString());
		this.serialize(jsonObject);
		return jsonObject;
	}

	Identifier id();

	RecipeSerializer<?> serializer();

	@Nullable
	AdvancementEntry advancement();
}
