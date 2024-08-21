package net.minecraft.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.item.trim.ArmorTrimPatterns;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;

public class SmithingTrimRecipe implements SmithingRecipe {
	final Optional<Ingredient> template;
	final Optional<Ingredient> base;
	final Optional<Ingredient> addition;
	@Nullable
	private IngredientPlacement ingredientPlacement;

	public SmithingTrimRecipe(Optional<Ingredient> template, Optional<Ingredient> base, Optional<Ingredient> addition) {
		this.template = template;
		this.base = base;
		this.addition = addition;
	}

	public ItemStack craft(SmithingRecipeInput smithingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		ItemStack itemStack = smithingRecipeInput.base();
		if (Ingredient.matches(this.base, itemStack)) {
			Optional<RegistryEntry.Reference<ArmorTrimMaterial>> optional = ArmorTrimMaterials.get(wrapperLookup, smithingRecipeInput.addition());
			Optional<RegistryEntry.Reference<ArmorTrimPattern>> optional2 = ArmorTrimPatterns.get(wrapperLookup, smithingRecipeInput.template());
			if (optional.isPresent() && optional2.isPresent()) {
				ArmorTrim armorTrim = itemStack.get(DataComponentTypes.TRIM);
				if (armorTrim != null && armorTrim.equals((RegistryEntry<ArmorTrimPattern>)optional2.get(), (RegistryEntry<ArmorTrimMaterial>)optional.get())) {
					return ItemStack.EMPTY;
				}

				ItemStack itemStack2 = itemStack.copyWithCount(1);
				itemStack2.set(DataComponentTypes.TRIM, new ArmorTrim((RegistryEntry<ArmorTrimMaterial>)optional.get(), (RegistryEntry<ArmorTrimPattern>)optional2.get()));
				return itemStack2;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
		ItemStack itemStack = new ItemStack(Items.IRON_CHESTPLATE);
		Optional<RegistryEntry.Reference<ArmorTrimPattern>> optional = registries.getWrapperOrThrow(RegistryKeys.TRIM_PATTERN).streamEntries().findFirst();
		Optional<RegistryEntry.Reference<ArmorTrimMaterial>> optional2 = registries.getWrapperOrThrow(RegistryKeys.TRIM_MATERIAL)
			.getOptional(ArmorTrimMaterials.REDSTONE);
		if (optional.isPresent() && optional2.isPresent()) {
			itemStack.set(DataComponentTypes.TRIM, new ArmorTrim((RegistryEntry<ArmorTrimMaterial>)optional2.get(), (RegistryEntry<ArmorTrimPattern>)optional.get()));
		}

		return itemStack;
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
		return RecipeSerializer.SMITHING_TRIM;
	}

	@Override
	public IngredientPlacement getIngredientPlacement() {
		if (this.ingredientPlacement == null) {
			this.ingredientPlacement = IngredientPlacement.forMultipleSlots(List.of(this.template, this.base, this.addition));
		}

		return this.ingredientPlacement;
	}

	public static class Serializer implements RecipeSerializer<SmithingTrimRecipe> {
		private static final MapCodec<SmithingTrimRecipe> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Ingredient.CODEC.optionalFieldOf("template").forGetter(recipe -> recipe.template),
						Ingredient.CODEC.optionalFieldOf("base").forGetter(recipe -> recipe.base),
						Ingredient.CODEC.optionalFieldOf("addition").forGetter(recipe -> recipe.addition)
					)
					.apply(instance, SmithingTrimRecipe::new)
		);
		public static final PacketCodec<RegistryByteBuf, SmithingTrimRecipe> PACKET_CODEC = PacketCodec.tuple(
			Ingredient.OPTIONAL_PACKET_CODEC,
			recipe -> recipe.template,
			Ingredient.OPTIONAL_PACKET_CODEC,
			recipe -> recipe.base,
			Ingredient.OPTIONAL_PACKET_CODEC,
			recipe -> recipe.addition,
			SmithingTrimRecipe::new
		);

		@Override
		public MapCodec<SmithingTrimRecipe> codec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, SmithingTrimRecipe> packetCodec() {
			return PACKET_CODEC;
		}
	}
}
