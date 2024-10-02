package net.minecraft.recipe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureSet;

public interface RecipeDisplay {
	Codec<RecipeDisplay> CODEC = Registries.RECIPE_DISPLAY.getCodec().dispatch(RecipeDisplay::serializer, RecipeDisplay.Serializer::codec);
	PacketCodec<RegistryByteBuf, RecipeDisplay> STREAM_CODEC = PacketCodecs.registryValue(RegistryKeys.RECIPE_DISPLAY)
		.dispatch(RecipeDisplay::serializer, RecipeDisplay.Serializer::streamCodec);

	SlotDisplay result();

	SlotDisplay craftingStation();

	RecipeDisplay.Serializer<? extends RecipeDisplay> serializer();

	default boolean isEnabled(FeatureSet features) {
		return this.result().isEnabled(features) && this.craftingStation().isEnabled(features);
	}

	public static record Serializer<T extends RecipeDisplay>(MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> streamCodec) {
	}
}
