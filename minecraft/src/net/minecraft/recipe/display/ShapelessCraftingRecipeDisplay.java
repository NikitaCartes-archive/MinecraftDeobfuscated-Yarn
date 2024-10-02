package net.minecraft.recipe.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.resource.featuretoggle.FeatureSet;

public record ShapelessCraftingRecipeDisplay(List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
	public static final MapCodec<ShapelessCraftingRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(ShapelessCraftingRecipeDisplay::ingredients),
					SlotDisplay.CODEC.fieldOf("result").forGetter(ShapelessCraftingRecipeDisplay::result),
					SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(ShapelessCraftingRecipeDisplay::craftingStation)
				)
				.apply(instance, ShapelessCraftingRecipeDisplay::new)
	);
	public static final PacketCodec<RegistryByteBuf, ShapelessCraftingRecipeDisplay> PACKET_CODEC = PacketCodec.tuple(
		SlotDisplay.PACKET_CODEC.collect(PacketCodecs.toList()),
		ShapelessCraftingRecipeDisplay::ingredients,
		SlotDisplay.PACKET_CODEC,
		ShapelessCraftingRecipeDisplay::result,
		SlotDisplay.PACKET_CODEC,
		ShapelessCraftingRecipeDisplay::craftingStation,
		ShapelessCraftingRecipeDisplay::new
	);
	public static final RecipeDisplay.Serializer<ShapelessCraftingRecipeDisplay> SERIALIZER = new RecipeDisplay.Serializer<>(CODEC, PACKET_CODEC);

	@Override
	public RecipeDisplay.Serializer<ShapelessCraftingRecipeDisplay> serializer() {
		return SERIALIZER;
	}

	@Override
	public boolean isEnabled(FeatureSet features) {
		return this.ingredients.stream().allMatch(ingredient -> ingredient.isEnabled(features)) && RecipeDisplay.super.isEnabled(features);
	}
}
