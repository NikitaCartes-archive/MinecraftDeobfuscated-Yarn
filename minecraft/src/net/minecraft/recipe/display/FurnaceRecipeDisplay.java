package net.minecraft.recipe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.resource.featuretoggle.FeatureSet;

public record FurnaceRecipeDisplay(SlotDisplay ingredient, SlotDisplay fuel, SlotDisplay result, SlotDisplay craftingStation, int duration, float experience)
	implements RecipeDisplay {
	public static final MapCodec<FurnaceRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					SlotDisplay.CODEC.fieldOf("ingredient").forGetter(FurnaceRecipeDisplay::ingredient),
					SlotDisplay.CODEC.fieldOf("fuel").forGetter(FurnaceRecipeDisplay::fuel),
					SlotDisplay.CODEC.fieldOf("result").forGetter(FurnaceRecipeDisplay::result),
					SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(FurnaceRecipeDisplay::craftingStation),
					Codec.INT.fieldOf("duration").forGetter(FurnaceRecipeDisplay::duration),
					Codec.FLOAT.fieldOf("experience").forGetter(FurnaceRecipeDisplay::experience)
				)
				.apply(instance, FurnaceRecipeDisplay::new)
	);
	public static final PacketCodec<RegistryByteBuf, FurnaceRecipeDisplay> PACKET_CODEC = PacketCodec.tuple(
		SlotDisplay.PACKET_CODEC,
		FurnaceRecipeDisplay::ingredient,
		SlotDisplay.PACKET_CODEC,
		FurnaceRecipeDisplay::fuel,
		SlotDisplay.PACKET_CODEC,
		FurnaceRecipeDisplay::result,
		SlotDisplay.PACKET_CODEC,
		FurnaceRecipeDisplay::craftingStation,
		PacketCodecs.VAR_INT,
		FurnaceRecipeDisplay::duration,
		PacketCodecs.FLOAT,
		FurnaceRecipeDisplay::experience,
		FurnaceRecipeDisplay::new
	);
	public static final RecipeDisplay.Serializer<FurnaceRecipeDisplay> SERIALIZER = new RecipeDisplay.Serializer<>(CODEC, PACKET_CODEC);

	@Override
	public RecipeDisplay.Serializer<FurnaceRecipeDisplay> serializer() {
		return SERIALIZER;
	}

	@Override
	public boolean isEnabled(FeatureSet features) {
		return this.ingredient.isEnabled(features) && this.fuel().isEnabled(features) && RecipeDisplay.super.isEnabled(features);
	}
}
