package net.minecraft.recipe;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public record RecipeEntry<T extends Recipe<?>>(RegistryKey<Recipe<?>> id, T value) {
	public static final PacketCodec<RegistryByteBuf, RecipeEntry<?>> PACKET_CODEC = PacketCodec.tuple(
		RegistryKey.createPacketCodec(RegistryKeys.RECIPE), RecipeEntry::id, Recipe.PACKET_CODEC, RecipeEntry::value, RecipeEntry::new
	);

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof RecipeEntry<?> recipeEntry && this.id == recipeEntry.id) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return this.id.hashCode();
	}

	public String toString() {
		return this.id.toString();
	}
}
