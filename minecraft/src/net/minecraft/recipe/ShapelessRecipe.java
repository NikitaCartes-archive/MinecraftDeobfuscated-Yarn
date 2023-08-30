package net.minecraft.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public class ShapelessRecipe implements CraftingRecipe {
	final String group;
	final CraftingRecipeCategory category;
	final ItemStack result;
	final DefaultedList<Ingredient> ingredients;

	public ShapelessRecipe(String group, CraftingRecipeCategory category, ItemStack result, DefaultedList<Ingredient> ingredients) {
		this.group = group;
		this.category = category;
		this.result = result;
		this.ingredients = ingredients;
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
	public ItemStack getResult(DynamicRegistryManager registryManager) {
		return this.result;
	}

	@Override
	public DefaultedList<Ingredient> getIngredients() {
		return this.ingredients;
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

		return i == this.ingredients.size() && recipeMatcher.match(this, null);
	}

	public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
		return this.result.copy();
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= this.ingredients.size();
	}

	public static class Serializer implements RecipeSerializer<ShapelessRecipe> {
		private static final Codec<ShapelessRecipe> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "").forGetter(recipe -> recipe.group),
						CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(recipe -> recipe.category),
						RecipeCodecs.CRAFTING_RESULT.fieldOf("result").forGetter(recipe -> recipe.result),
						Ingredient.DISALLOW_EMPTY_CODEC
							.listOf()
							.fieldOf("ingredients")
							.flatXmap(
								ingredients -> {
									Ingredient[] ingredients2 = (Ingredient[])ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
									if (ingredients2.length == 0) {
										return DataResult.error(() -> "No ingredients for shapeless recipe");
									} else {
										return ingredients2.length > 9
											? DataResult.error(() -> "Too many ingredients for shapeless recipe")
											: DataResult.success(DefaultedList.copyOf(Ingredient.EMPTY, ingredients2));
									}
								},
								DataResult::success
							)
							.forGetter(recipe -> recipe.ingredients)
					)
					.apply(instance, ShapelessRecipe::new)
		);

		@Override
		public Codec<ShapelessRecipe> codec() {
			return CODEC;
		}

		public ShapelessRecipe read(PacketByteBuf packetByteBuf) {
			String string = packetByteBuf.readString();
			CraftingRecipeCategory craftingRecipeCategory = packetByteBuf.readEnumConstant(CraftingRecipeCategory.class);
			int i = packetByteBuf.readVarInt();
			DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);

			for (int j = 0; j < defaultedList.size(); j++) {
				defaultedList.set(j, Ingredient.fromPacket(packetByteBuf));
			}

			ItemStack itemStack = packetByteBuf.readItemStack();
			return new ShapelessRecipe(string, craftingRecipeCategory, itemStack, defaultedList);
		}

		public void write(PacketByteBuf packetByteBuf, ShapelessRecipe shapelessRecipe) {
			packetByteBuf.writeString(shapelessRecipe.group);
			packetByteBuf.writeEnumConstant(shapelessRecipe.category);
			packetByteBuf.writeVarInt(shapelessRecipe.ingredients.size());

			for (Ingredient ingredient : shapelessRecipe.ingredients) {
				ingredient.write(packetByteBuf);
			}

			packetByteBuf.writeItemStack(shapelessRecipe.result);
		}
	}
}
