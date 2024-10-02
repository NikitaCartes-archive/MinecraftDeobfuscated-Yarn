package net.minecraft.recipe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.resource.featuretoggle.FeatureSet;

public record ShapedCraftingRecipeDisplay(int width, int height, List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation)
	implements RecipeDisplay {
	public static final MapCodec<ShapedCraftingRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.INT.fieldOf("width").forGetter(ShapedCraftingRecipeDisplay::width),
					Codec.INT.fieldOf("height").forGetter(ShapedCraftingRecipeDisplay::height),
					SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(ShapedCraftingRecipeDisplay::ingredients),
					SlotDisplay.CODEC.fieldOf("result").forGetter(ShapedCraftingRecipeDisplay::result),
					SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(ShapedCraftingRecipeDisplay::craftingStation)
				)
				.apply(instance, ShapedCraftingRecipeDisplay::new)
	);
	public static final PacketCodec<RegistryByteBuf, ShapedCraftingRecipeDisplay> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT,
		ShapedCraftingRecipeDisplay::width,
		PacketCodecs.VAR_INT,
		ShapedCraftingRecipeDisplay::height,
		SlotDisplay.PACKET_CODEC.collect(PacketCodecs.toList()),
		ShapedCraftingRecipeDisplay::ingredients,
		SlotDisplay.PACKET_CODEC,
		ShapedCraftingRecipeDisplay::result,
		SlotDisplay.PACKET_CODEC,
		ShapedCraftingRecipeDisplay::craftingStation,
		ShapedCraftingRecipeDisplay::new
	);
	public static final RecipeDisplay.Serializer<ShapedCraftingRecipeDisplay> SERIALIZER = new RecipeDisplay.Serializer<>(CODEC, PACKET_CODEC);

	public ShapedCraftingRecipeDisplay(int width, int height, List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation) {
		if (ingredients.size() != width * height) {
			throw new IllegalArgumentException("Invalid shaped recipe display contents");
		} else {
			this.width = width;
			this.height = height;
			this.ingredients = ingredients;
			this.result = result;
			this.craftingStation = craftingStation;
		}
	}

	@Override
	public RecipeDisplay.Serializer<ShapedCraftingRecipeDisplay> serializer() {
		return SERIALIZER;
	}

	@Override
	public boolean isEnabled(FeatureSet features) {
		return this.ingredients.stream().allMatch(ingredient -> ingredient.isEnabled(features)) && RecipeDisplay.super.isEnabled(features);
	}
}
