package net.minecraft.recipe.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record SmithingRecipeDisplay(SlotDisplay template, SlotDisplay base, SlotDisplay addition, SlotDisplay result, SlotDisplay craftingStation)
	implements RecipeDisplay {
	public static final MapCodec<SmithingRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					SlotDisplay.CODEC.fieldOf("template").forGetter(SmithingRecipeDisplay::template),
					SlotDisplay.CODEC.fieldOf("base").forGetter(SmithingRecipeDisplay::base),
					SlotDisplay.CODEC.fieldOf("addition").forGetter(SmithingRecipeDisplay::addition),
					SlotDisplay.CODEC.fieldOf("result").forGetter(SmithingRecipeDisplay::result),
					SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(SmithingRecipeDisplay::craftingStation)
				)
				.apply(instance, SmithingRecipeDisplay::new)
	);
	public static final PacketCodec<RegistryByteBuf, SmithingRecipeDisplay> PACKET_CODEC = PacketCodec.tuple(
		SlotDisplay.PACKET_CODEC,
		SmithingRecipeDisplay::template,
		SlotDisplay.PACKET_CODEC,
		SmithingRecipeDisplay::base,
		SlotDisplay.PACKET_CODEC,
		SmithingRecipeDisplay::addition,
		SlotDisplay.PACKET_CODEC,
		SmithingRecipeDisplay::result,
		SlotDisplay.PACKET_CODEC,
		SmithingRecipeDisplay::craftingStation,
		SmithingRecipeDisplay::new
	);
	public static final RecipeDisplay.Serializer<SmithingRecipeDisplay> SERIALIZER = new RecipeDisplay.Serializer<>(CODEC, PACKET_CODEC);

	@Override
	public RecipeDisplay.Serializer<SmithingRecipeDisplay> serializer() {
		return SERIALIZER;
	}
}
