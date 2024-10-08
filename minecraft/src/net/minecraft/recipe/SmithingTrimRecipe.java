package net.minecraft.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.item.equipment.trim.ArmorTrimPatterns;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.display.SmithingRecipeDisplay;
import net.minecraft.recipe.input.SmithingRecipeInput;
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
		return craft(wrapperLookup, smithingRecipeInput.base(), smithingRecipeInput.addition(), smithingRecipeInput.template());
	}

	public static ItemStack craft(RegistryWrapper.WrapperLookup registries, ItemStack base, ItemStack addition, ItemStack template) {
		Optional<RegistryEntry.Reference<ArmorTrimMaterial>> optional = ArmorTrimMaterials.get(registries, addition);
		Optional<RegistryEntry.Reference<ArmorTrimPattern>> optional2 = ArmorTrimPatterns.get(registries, template);
		if (optional.isPresent() && optional2.isPresent()) {
			ArmorTrim armorTrim = base.get(DataComponentTypes.TRIM);
			if (armorTrim != null && armorTrim.equals((RegistryEntry<ArmorTrimPattern>)optional2.get(), (RegistryEntry<ArmorTrimMaterial>)optional.get())) {
				return ItemStack.EMPTY;
			} else {
				ItemStack itemStack = base.copyWithCount(1);
				itemStack.set(DataComponentTypes.TRIM, new ArmorTrim((RegistryEntry<ArmorTrimMaterial>)optional.get(), (RegistryEntry<ArmorTrimPattern>)optional2.get()));
				return itemStack;
			}
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public Optional<Ingredient> template() {
		return this.template;
	}

	@Override
	public Optional<Ingredient> base() {
		return this.base;
	}

	@Override
	public Optional<Ingredient> addition() {
		return this.addition;
	}

	@Override
	public RecipeSerializer<SmithingTrimRecipe> getSerializer() {
		return RecipeSerializer.SMITHING_TRIM;
	}

	@Override
	public IngredientPlacement getIngredientPlacement() {
		if (this.ingredientPlacement == null) {
			this.ingredientPlacement = IngredientPlacement.forMultipleSlots(List.of(this.template, this.base, this.addition));
		}

		return this.ingredientPlacement;
	}

	@Override
	public List<RecipeDisplay> getDisplays() {
		SlotDisplay slotDisplay = Ingredient.toDisplay(this.base);
		SlotDisplay slotDisplay2 = Ingredient.toDisplay(this.addition);
		SlotDisplay slotDisplay3 = Ingredient.toDisplay(this.template);
		return List.of(
			new SmithingRecipeDisplay(
				slotDisplay3,
				slotDisplay,
				slotDisplay2,
				new SlotDisplay.SmithingTrimSlotDisplay(slotDisplay, slotDisplay2, slotDisplay3),
				new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)
			)
		);
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
