package net.minecraft.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class CookingRecipeSerializer<T extends AbstractCookingRecipe> implements RecipeSerializer<T> {
	private final int cookingTime;
	private final CookingRecipeSerializer.RecipeFactory<T> recipeFactory;

	public CookingRecipeSerializer(CookingRecipeSerializer.RecipeFactory<T> recipeFactory, int cookingTime) {
		this.cookingTime = cookingTime;
		this.recipeFactory = recipeFactory;
	}

	public T read(Identifier identifier, JsonObject jsonObject) {
		String string = JsonHelper.getString(jsonObject, "group", "");
		CookingRecipeCategory cookingRecipeCategory = (CookingRecipeCategory)CookingRecipeCategory.CODEC
			.byId(JsonHelper.getString(jsonObject, "category", null), CookingRecipeCategory.MISC);
		JsonElement jsonElement = (JsonElement)(JsonHelper.hasArray(jsonObject, "ingredient")
			? JsonHelper.getArray(jsonObject, "ingredient")
			: JsonHelper.getObject(jsonObject, "ingredient"));
		Ingredient ingredient = Ingredient.fromJson(jsonElement, false);
		String string2 = JsonHelper.getString(jsonObject, "result");
		Identifier identifier2 = new Identifier(string2);
		ItemStack itemStack = new ItemStack(
			(ItemConvertible)Registries.ITEM.getOrEmpty(identifier2).orElseThrow(() -> new IllegalStateException("Item: " + string2 + " does not exist"))
		);
		float f = JsonHelper.getFloat(jsonObject, "experience", 0.0F);
		int i = JsonHelper.getInt(jsonObject, "cookingtime", this.cookingTime);
		return this.recipeFactory.create(identifier, string, cookingRecipeCategory, ingredient, itemStack, f, i);
	}

	public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
		String string = packetByteBuf.readString();
		CookingRecipeCategory cookingRecipeCategory = packetByteBuf.readEnumConstant(CookingRecipeCategory.class);
		Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
		ItemStack itemStack = packetByteBuf.readItemStack();
		float f = packetByteBuf.readFloat();
		int i = packetByteBuf.readVarInt();
		return this.recipeFactory.create(identifier, string, cookingRecipeCategory, ingredient, itemStack, f, i);
	}

	public void write(PacketByteBuf packetByteBuf, T abstractCookingRecipe) {
		packetByteBuf.writeString(abstractCookingRecipe.group);
		packetByteBuf.writeEnumConstant(abstractCookingRecipe.getCategory());
		abstractCookingRecipe.input.write(packetByteBuf);
		packetByteBuf.writeItemStack(abstractCookingRecipe.output);
		packetByteBuf.writeFloat(abstractCookingRecipe.experience);
		packetByteBuf.writeVarInt(abstractCookingRecipe.cookTime);
	}

	interface RecipeFactory<T extends AbstractCookingRecipe> {
		T create(Identifier id, String group, CookingRecipeCategory category, Ingredient input, ItemStack output, float experience, int cookTime);
	}
}
