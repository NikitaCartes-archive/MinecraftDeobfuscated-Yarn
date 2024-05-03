package net.minecraft.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class SmithingTransformRecipe implements SmithingRecipe {
	final Ingredient template;
	final Ingredient base;
	final Ingredient addition;
	final ItemStack result;

	public SmithingTransformRecipe(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
		this.template = template;
		this.base = base;
		this.addition = addition;
		this.result = result;
	}

	public boolean matches(SmithingRecipeInput smithingRecipeInput, World world) {
		return this.template.test(smithingRecipeInput.template()) && this.base.test(smithingRecipeInput.base()) && this.addition.test(smithingRecipeInput.addition());
	}

	public ItemStack craft(SmithingRecipeInput smithingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		ItemStack itemStack = smithingRecipeInput.base().copyComponentsToNewStack(this.result.getItem(), this.result.getCount());
		itemStack.applyUnvalidatedChanges(this.result.getComponentChanges());
		return itemStack;
	}

	@Override
	public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
		return this.result;
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
		return RecipeSerializer.SMITHING_TRANSFORM;
	}

	@Override
	public boolean isEmpty() {
		return Stream.of(this.template, this.base, this.addition).anyMatch(Ingredient::isEmpty);
	}

	public static class Serializer implements RecipeSerializer<SmithingTransformRecipe> {
		private static final MapCodec<SmithingTransformRecipe> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Ingredient.ALLOW_EMPTY_CODEC.fieldOf("template").forGetter(recipe -> recipe.template),
						Ingredient.ALLOW_EMPTY_CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
						Ingredient.ALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition),
						ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
					)
					.apply(instance, SmithingTransformRecipe::new)
		);
		public static final PacketCodec<RegistryByteBuf, SmithingTransformRecipe> PACKET_CODEC = PacketCodec.ofStatic(
			SmithingTransformRecipe.Serializer::write, SmithingTransformRecipe.Serializer::read
		);

		@Override
		public MapCodec<SmithingTransformRecipe> codec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, SmithingTransformRecipe> packetCodec() {
			return PACKET_CODEC;
		}

		private static SmithingTransformRecipe read(RegistryByteBuf buf) {
			Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
			Ingredient ingredient2 = Ingredient.PACKET_CODEC.decode(buf);
			Ingredient ingredient3 = Ingredient.PACKET_CODEC.decode(buf);
			ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
			return new SmithingTransformRecipe(ingredient, ingredient2, ingredient3, itemStack);
		}

		private static void write(RegistryByteBuf buf, SmithingTransformRecipe recipe) {
			Ingredient.PACKET_CODEC.encode(buf, recipe.template);
			Ingredient.PACKET_CODEC.encode(buf, recipe.base);
			Ingredient.PACKET_CODEC.encode(buf, recipe.addition);
			ItemStack.PACKET_CODEC.encode(buf, recipe.result);
		}
	}
}
