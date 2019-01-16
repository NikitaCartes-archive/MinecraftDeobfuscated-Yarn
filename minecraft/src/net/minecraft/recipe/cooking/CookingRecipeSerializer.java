package net.minecraft.recipe.cooking;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class CookingRecipeSerializer<T extends AbstractCookingRecipe> implements RecipeSerializer<T> {
	private final int cookingTime;
	private final CookingRecipeSerializer.RecipeFactory<T> recipeFactory;

	public CookingRecipeSerializer(CookingRecipeSerializer.RecipeFactory<T> recipeFactory, int i) {
		this.cookingTime = i;
		this.recipeFactory = recipeFactory;
	}

	public T read(Identifier identifier, JsonObject jsonObject) {
		String string = JsonHelper.getString(jsonObject, "group", "");
		JsonElement jsonElement = (JsonElement)(JsonHelper.hasArray(jsonObject, "ingredient")
			? JsonHelper.getArray(jsonObject, "ingredient")
			: JsonHelper.getObject(jsonObject, "ingredient"));
		Ingredient ingredient = Ingredient.fromJson(jsonElement);
		String string2 = JsonHelper.getString(jsonObject, "result");
		Identifier identifier2 = new Identifier(string2);
		if (!Registry.ITEM.contains(identifier2)) {
			throw new IllegalStateException("Item: " + string2 + " does not exist");
		} else {
			ItemStack itemStack = new ItemStack(Registry.ITEM.get(identifier2));
			float f = JsonHelper.getFloat(jsonObject, "experience", 0.0F);
			int i = JsonHelper.getInt(jsonObject, "cookingtime", this.cookingTime);
			return this.recipeFactory.create(identifier, string, ingredient, itemStack, f, i);
		}
	}

	public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
		String string = packetByteBuf.readString(32767);
		Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
		ItemStack itemStack = packetByteBuf.readItemStack();
		float f = packetByteBuf.readFloat();
		int i = packetByteBuf.readVarInt();
		return this.recipeFactory.create(identifier, string, ingredient, itemStack, f, i);
	}

	public void write(PacketByteBuf packetByteBuf, T abstractCookingRecipe) {
		packetByteBuf.writeString(abstractCookingRecipe.group);
		abstractCookingRecipe.input.write(packetByteBuf);
		packetByteBuf.writeItemStack(abstractCookingRecipe.output);
		packetByteBuf.writeFloat(abstractCookingRecipe.experience);
		packetByteBuf.writeVarInt(abstractCookingRecipe.cookTime);
	}

	interface RecipeFactory<T extends AbstractCookingRecipe> {
		T create(Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack, float f, int i);
	}
}
