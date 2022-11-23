package net.minecraft.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

/**
 * A serializer for hardcoded recipes. The recipes with this serializer don't
 * transport any extra data besides their ID when read from JSON or synchronized
 * over network.
 * 
 * <p>The name "special" comes from the fact that in vanilla, recipes using this
 * serializer have IDs starting with {@code crafting_special_}. All of their logic and ingredients
 * are also defined in code, which distinguishes them from "non-special" recipes.
 */
public class SpecialRecipeSerializer<T extends CraftingRecipe> implements RecipeSerializer<T> {
	private final SpecialRecipeSerializer.Factory<T> factory;

	public SpecialRecipeSerializer(SpecialRecipeSerializer.Factory<T> factory) {
		this.factory = factory;
	}

	public T read(Identifier identifier, JsonObject jsonObject) {
		CraftingRecipeCategory craftingRecipeCategory = (CraftingRecipeCategory)CraftingRecipeCategory.CODEC
			.byId(JsonHelper.getString(jsonObject, "category", null), CraftingRecipeCategory.MISC);
		return this.factory.create(identifier, craftingRecipeCategory);
	}

	public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
		CraftingRecipeCategory craftingRecipeCategory = packetByteBuf.readEnumConstant(CraftingRecipeCategory.class);
		return this.factory.create(identifier, craftingRecipeCategory);
	}

	public void write(PacketByteBuf packetByteBuf, T craftingRecipe) {
		packetByteBuf.writeEnumConstant(craftingRecipe.getCategory());
	}

	@FunctionalInterface
	public interface Factory<T extends CraftingRecipe> {
		T create(Identifier id, CraftingRecipeCategory category);
	}
}
