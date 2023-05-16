package net.minecraft.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ShapelessRecipe implements CraftingRecipe {
	private final Identifier id;
	final String group;
	final CraftingRecipeCategory category;
	final ItemStack output;
	final DefaultedList<Ingredient> input;

	public ShapelessRecipe(Identifier id, String group, CraftingRecipeCategory category, ItemStack output, DefaultedList<Ingredient> input) {
		this.id = id;
		this.group = group;
		this.category = category;
		this.output = output;
		this.input = input;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SHAPELESS;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public CraftingRecipeCategory getCategory() {
		return this.category;
	}

	@Override
	public ItemStack getOutput(DynamicRegistryManager registryManager) {
		return this.output;
	}

	@Override
	public DefaultedList<Ingredient> getIngredients() {
		return this.input;
	}

	public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
		RecipeMatcher recipeMatcher = new RecipeMatcher();
		int i = 0;

		for (int j = 0; j < recipeInputInventory.size(); j++) {
			ItemStack itemStack = recipeInputInventory.getStack(j);
			if (!itemStack.isEmpty()) {
				i++;
				recipeMatcher.addInput(itemStack, 1);
			}
		}

		return i == this.input.size() && recipeMatcher.match(this, null);
	}

	public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
		return this.output.copy();
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= this.input.size();
	}

	public static class Serializer implements RecipeSerializer<ShapelessRecipe> {
		public ShapelessRecipe read(Identifier identifier, JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "group", "");
			CraftingRecipeCategory craftingRecipeCategory = (CraftingRecipeCategory)CraftingRecipeCategory.CODEC
				.byId(JsonHelper.getString(jsonObject, "category", null), CraftingRecipeCategory.MISC);
			DefaultedList<Ingredient> defaultedList = getIngredients(JsonHelper.getArray(jsonObject, "ingredients"));
			if (defaultedList.isEmpty()) {
				throw new JsonParseException("No ingredients for shapeless recipe");
			} else if (defaultedList.size() > 9) {
				throw new JsonParseException("Too many ingredients for shapeless recipe");
			} else {
				ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
				return new ShapelessRecipe(identifier, string, craftingRecipeCategory, itemStack, defaultedList);
			}
		}

		private static DefaultedList<Ingredient> getIngredients(JsonArray json) {
			DefaultedList<Ingredient> defaultedList = DefaultedList.of();

			for (int i = 0; i < json.size(); i++) {
				Ingredient ingredient = Ingredient.fromJson(json.get(i), false);
				if (!ingredient.isEmpty()) {
					defaultedList.add(ingredient);
				}
			}

			return defaultedList;
		}

		public ShapelessRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
			String string = packetByteBuf.readString();
			CraftingRecipeCategory craftingRecipeCategory = packetByteBuf.readEnumConstant(CraftingRecipeCategory.class);
			int i = packetByteBuf.readVarInt();
			DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);

			for (int j = 0; j < defaultedList.size(); j++) {
				defaultedList.set(j, Ingredient.fromPacket(packetByteBuf));
			}

			ItemStack itemStack = packetByteBuf.readItemStack();
			return new ShapelessRecipe(identifier, string, craftingRecipeCategory, itemStack, defaultedList);
		}

		public void write(PacketByteBuf packetByteBuf, ShapelessRecipe shapelessRecipe) {
			packetByteBuf.writeString(shapelessRecipe.group);
			packetByteBuf.writeEnumConstant(shapelessRecipe.category);
			packetByteBuf.writeVarInt(shapelessRecipe.input.size());

			for (Ingredient ingredient : shapelessRecipe.input) {
				ingredient.write(packetByteBuf);
			}

			packetByteBuf.writeItemStack(shapelessRecipe.output);
		}
	}
}
