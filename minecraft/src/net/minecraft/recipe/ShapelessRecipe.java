package net.minecraft.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class ShapelessRecipe implements CraftingRecipe {
	final String group;
	final CraftingRecipeCategory category;
	final ItemStack result;
	final List<Ingredient> ingredients;
	@Nullable
	private IngredientPlacement ingredientPlacement;

	public ShapelessRecipe(String group, CraftingRecipeCategory category, ItemStack result, List<Ingredient> ingredients) {
		this.group = group;
		this.category = category;
		this.result = result;
		this.ingredients = ingredients;
	}

	@Override
	public RecipeSerializer<ShapelessRecipe> getSerializer() {
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
	public IngredientPlacement getIngredientPlacement() {
		if (this.ingredientPlacement == null) {
			this.ingredientPlacement = IngredientPlacement.forShapeless(this.ingredients);
		}

		return this.ingredientPlacement;
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		if (craftingRecipeInput.getStackCount() != this.ingredients.size()) {
			return false;
		} else {
			return craftingRecipeInput.size() == 1 && this.ingredients.size() == 1
				? ((Ingredient)this.ingredients.getFirst()).test(craftingRecipeInput.getStackInSlot(0))
				: craftingRecipeInput.getRecipeMatcher().isCraftable(this, null);
		}
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		return this.result.copy();
	}

	@Override
	public List<RecipeDisplay> getDisplays() {
		return List.of(
			new ShapelessCraftingRecipeDisplay(
				this.ingredients.stream().map(Ingredient::toDisplay).toList(),
				new SlotDisplay.StackSlotDisplay(this.result),
				new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
			)
		);
	}

	public static class Serializer implements RecipeSerializer<ShapelessRecipe> {
		private static final MapCodec<ShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
						CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(recipe -> recipe.category),
						ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
						Ingredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter(recipe -> recipe.ingredients)
					)
					.apply(instance, ShapelessRecipe::new)
		);
		public static final PacketCodec<RegistryByteBuf, ShapelessRecipe> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.STRING,
			recipe -> recipe.group,
			CraftingRecipeCategory.PACKET_CODEC,
			recipe -> recipe.category,
			ItemStack.PACKET_CODEC,
			recipe -> recipe.result,
			Ingredient.PACKET_CODEC.collect(PacketCodecs.toList()),
			recipe -> recipe.ingredients,
			ShapelessRecipe::new
		);

		@Override
		public MapCodec<ShapelessRecipe> codec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, ShapelessRecipe> packetCodec() {
			return PACKET_CODEC;
		}
	}
}
