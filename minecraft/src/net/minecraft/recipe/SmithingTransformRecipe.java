package net.minecraft.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;

public class SmithingTransformRecipe implements SmithingRecipe {
	final Optional<Ingredient> template;
	final Optional<Ingredient> base;
	final Optional<Ingredient> addition;
	final ItemStack result;
	@Nullable
	private IngredientPlacement ingredientPlacement;

	public SmithingTransformRecipe(Optional<Ingredient> template, Optional<Ingredient> base, Optional<Ingredient> addition, ItemStack result) {
		this.template = template;
		this.base = base;
		this.addition = addition;
		this.result = result;
	}

	public ItemStack craft(SmithingRecipeInput smithingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		ItemStack itemStack = smithingRecipeInput.base().copyComponentsToNewStack(this.result.getItem(), this.result.getCount());
		itemStack.applyUnvalidatedChanges(this.result.getComponentChanges());
		return itemStack;
	}

	@Override
	public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
		return this.result;
	}

	@Override
	public boolean testTemplate(ItemStack stack) {
		return Ingredient.matches(this.template, stack);
	}

	@Override
	public boolean testBase(ItemStack stack) {
		return Ingredient.matches(this.base, stack);
	}

	@Override
	public boolean testAddition(ItemStack stack) {
		return Ingredient.matches(this.addition, stack);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SMITHING_TRANSFORM;
	}

	@Override
	public IngredientPlacement getIngredientPlacement() {
		if (this.ingredientPlacement == null) {
			this.ingredientPlacement = IngredientPlacement.forMultipleSlots(List.of(this.template, this.base, this.addition));
		}

		return this.ingredientPlacement;
	}

	public static class Serializer implements RecipeSerializer<SmithingTransformRecipe> {
		private static final MapCodec<SmithingTransformRecipe> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Ingredient.CODEC.optionalFieldOf("template").forGetter(recipe -> recipe.template),
						Ingredient.CODEC.optionalFieldOf("base").forGetter(recipe -> recipe.base),
						Ingredient.CODEC.optionalFieldOf("addition").forGetter(recipe -> recipe.addition),
						ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
					)
					.apply(instance, SmithingTransformRecipe::new)
		);
		public static final PacketCodec<RegistryByteBuf, SmithingTransformRecipe> PACKET_CODEC = PacketCodec.tuple(
			Ingredient.OPTIONAL_PACKET_CODEC,
			recipe -> recipe.template,
			Ingredient.OPTIONAL_PACKET_CODEC,
			recipe -> recipe.base,
			Ingredient.OPTIONAL_PACKET_CODEC,
			recipe -> recipe.addition,
			ItemStack.PACKET_CODEC,
			recipe -> recipe.result,
			SmithingTransformRecipe::new
		);

		@Override
		public MapCodec<SmithingTransformRecipe> codec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, SmithingTransformRecipe> packetCodec() {
			return PACKET_CODEC;
		}
	}
}
