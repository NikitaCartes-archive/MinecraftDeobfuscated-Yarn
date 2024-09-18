package net.minecraft.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.world.World;

public class TransmuteRecipe implements CraftingRecipe {
	final String group;
	final CraftingRecipeCategory category;
	final Ingredient input;
	final Ingredient material;
	final RegistryEntry<Item> result;
	@Nullable
	private IngredientPlacement ingredientPlacement;

	public TransmuteRecipe(String group, CraftingRecipeCategory category, Ingredient input, Ingredient material, RegistryEntry<Item> result) {
		this.group = group;
		this.category = category;
		this.input = input;
		this.material = material;
		this.result = result;
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		boolean bl = false;
		boolean bl2 = false;

		for (int i = 0; i < craftingRecipeInput.size(); i++) {
			ItemStack itemStack = craftingRecipeInput.getStackInSlot(i);
			if (!itemStack.isEmpty()) {
				if (!bl && this.input.test(itemStack)) {
					bl = true;
				} else {
					if (bl2 || !this.material.test(itemStack)) {
						return false;
					}

					bl2 = true;
				}
			}
		}

		return bl && bl2;
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		ItemStack itemStack = ItemStack.EMPTY;

		for (int i = 0; i < craftingRecipeInput.size(); i++) {
			ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(i);
			if (!itemStack2.isEmpty() && this.input.test(itemStack2)) {
				itemStack = itemStack2;
			}
		}

		return itemStack.copyComponentsToNewStack(this.result.value(), 1);
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
		return new ItemStack(this.result);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.CRAFTING_TRANSMUTE;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public IngredientPlacement getIngredientPlacement() {
		if (this.ingredientPlacement == null) {
			this.ingredientPlacement = IngredientPlacement.forShapeless(List.of(this.input, this.material));
		}

		return this.ingredientPlacement;
	}

	@Override
	public CraftingRecipeCategory getCategory() {
		return this.category;
	}

	public static class Serializer implements RecipeSerializer<TransmuteRecipe> {
		private static final MapCodec<TransmuteRecipe> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
						CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(recipe -> recipe.category),
						Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
						Ingredient.CODEC.fieldOf("material").forGetter(recipe -> recipe.material),
						RegistryFixedCodec.of(RegistryKeys.ITEM).fieldOf("result").forGetter(recipe -> recipe.result)
					)
					.apply(instance, TransmuteRecipe::new)
		);
		public static final PacketCodec<RegistryByteBuf, TransmuteRecipe> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.STRING,
			recipe -> recipe.group,
			CraftingRecipeCategory.PACKET_CODEC,
			recipe -> recipe.category,
			Ingredient.PACKET_CODEC,
			recipe -> recipe.input,
			Ingredient.PACKET_CODEC,
			recipe -> recipe.material,
			PacketCodecs.registryEntry(RegistryKeys.ITEM),
			recipe -> recipe.result,
			TransmuteRecipe::new
		);

		@Override
		public MapCodec<TransmuteRecipe> codec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, TransmuteRecipe> packetCodec() {
			return PACKET_CODEC;
		}
	}
}
