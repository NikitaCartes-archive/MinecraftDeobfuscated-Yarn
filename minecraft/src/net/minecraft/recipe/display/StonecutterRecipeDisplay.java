package net.minecraft.recipe.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record StonecutterRecipeDisplay(SlotDisplay input, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
	public static final MapCodec<StonecutterRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					SlotDisplay.CODEC.fieldOf("input").forGetter(StonecutterRecipeDisplay::input),
					SlotDisplay.CODEC.fieldOf("result").forGetter(StonecutterRecipeDisplay::result),
					SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(StonecutterRecipeDisplay::craftingStation)
				)
				.apply(instance, StonecutterRecipeDisplay::new)
	);
	public static final PacketCodec<RegistryByteBuf, StonecutterRecipeDisplay> PACKET_CODEC = PacketCodec.tuple(
		SlotDisplay.PACKET_CODEC,
		StonecutterRecipeDisplay::input,
		SlotDisplay.PACKET_CODEC,
		StonecutterRecipeDisplay::result,
		SlotDisplay.PACKET_CODEC,
		StonecutterRecipeDisplay::craftingStation,
		StonecutterRecipeDisplay::new
	);
	public static final RecipeDisplay.Serializer<StonecutterRecipeDisplay> SERIALIZER = new RecipeDisplay.Serializer<>(CODEC, PACKET_CODEC);

	@Override
	public RecipeDisplay.Serializer<StonecutterRecipeDisplay> serializer() {
		return SERIALIZER;
	}
}
