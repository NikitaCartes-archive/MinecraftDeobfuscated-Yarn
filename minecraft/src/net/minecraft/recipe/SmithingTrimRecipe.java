package net.minecraft.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.stream.Stream;
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
import net.minecraft.world.World;

public class SmithingTrimRecipe implements SmithingRecipe {
	final Ingredient template;
	final Ingredient base;
	final Ingredient addition;

	public SmithingTrimRecipe(Ingredient template, Ingredient base, Ingredient addition) {
		this.template = template;
		this.base = base;
		this.addition = addition;
	}

	public boolean matches(SmithingRecipeInput smithingRecipeInput, World world) {
		return this.template.test(smithingRecipeInput.template()) && this.base.test(smithingRecipeInput.base()) && this.addition.test(smithingRecipeInput.addition());
	}

	public ItemStack craft(SmithingRecipeInput smithingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		ItemStack itemStack = smithingRecipeInput.base();
		if (this.base.test(itemStack)) {
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
	public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
		ItemStack itemStack = new ItemStack(Items.IRON_CHESTPLATE);
		Optional<RegistryEntry.Reference<ArmorTrimPattern>> optional = registriesLookup.getWrapperOrThrow(RegistryKeys.TRIM_PATTERN).streamEntries().findFirst();
		Optional<RegistryEntry.Reference<ArmorTrimMaterial>> optional2 = registriesLookup.getWrapperOrThrow(RegistryKeys.TRIM_MATERIAL)
			.getOptional(ArmorTrimMaterials.REDSTONE);
		if (optional.isPresent() && optional2.isPresent()) {
			itemStack.set(DataComponentTypes.TRIM, new ArmorTrim((RegistryEntry<ArmorTrimMaterial>)optional2.get(), (RegistryEntry<ArmorTrimPattern>)optional.get()));
		}

		return itemStack;
	}

	@Override
	public boolean testTemplate(ItemStack stack) {
		return this.template.test(stack);
	}

	@Override
	public boolean testBase(ItemStack stack) {
		return this.base.test(stack);
	}

	@Override
	public boolean testAddition(ItemStack stack) {
		return this.addition.test(stack);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SMITHING_TRIM;
	}

	@Override
	public boolean isEmpty() {
		return Stream.of(this.template, this.base, this.addition).anyMatch(Ingredient::isEmpty);
	}

	public static class Serializer implements RecipeSerializer<SmithingTrimRecipe> {
		private static final MapCodec<SmithingTrimRecipe> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Ingredient.ALLOW_EMPTY_CODEC.fieldOf("template").forGetter(recipe -> recipe.template),
						Ingredient.ALLOW_EMPTY_CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
						Ingredient.ALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition)
					)
					.apply(instance, SmithingTrimRecipe::new)
		);
		public static final PacketCodec<RegistryByteBuf, SmithingTrimRecipe> PACKET_CODEC = PacketCodec.ofStatic(
			SmithingTrimRecipe.Serializer::write, SmithingTrimRecipe.Serializer::read
		);

		@Override
		public MapCodec<SmithingTrimRecipe> codec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, SmithingTrimRecipe> packetCodec() {
			return PACKET_CODEC;
		}

		private static SmithingTrimRecipe read(RegistryByteBuf buf) {
			Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
			Ingredient ingredient2 = Ingredient.PACKET_CODEC.decode(buf);
			Ingredient ingredient3 = Ingredient.PACKET_CODEC.decode(buf);
			return new SmithingTrimRecipe(ingredient, ingredient2, ingredient3);
		}

		private static void write(RegistryByteBuf buf, SmithingTrimRecipe recipe) {
			Ingredient.PACKET_CODEC.encode(buf, recipe.template);
			Ingredient.PACKET_CODEC.encode(buf, recipe.base);
			Ingredient.PACKET_CODEC.encode(buf, recipe.addition);
		}
	}
}
