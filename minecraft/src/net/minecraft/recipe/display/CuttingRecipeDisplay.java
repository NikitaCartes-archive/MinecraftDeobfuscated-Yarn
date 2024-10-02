package net.minecraft.recipe.display;

import java.util.List;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;

public record CuttingRecipeDisplay<T extends Recipe<?>>(SlotDisplay optionDisplay, Optional<RecipeEntry<T>> recipe) {
	public static <T extends Recipe<?>> PacketCodec<RegistryByteBuf, CuttingRecipeDisplay<T>> codec() {
		return PacketCodec.tuple(SlotDisplay.PACKET_CODEC, CuttingRecipeDisplay::optionDisplay, display -> new CuttingRecipeDisplay(display, Optional.empty()));
	}

	public static record GroupEntry<T extends Recipe<?>>(Ingredient input, CuttingRecipeDisplay<T> recipe) {

		public static <T extends Recipe<?>> PacketCodec<RegistryByteBuf, CuttingRecipeDisplay.GroupEntry<T>> codec() {
			return PacketCodec.tuple(
				Ingredient.PACKET_CODEC,
				CuttingRecipeDisplay.GroupEntry::input,
				CuttingRecipeDisplay.codec(),
				CuttingRecipeDisplay.GroupEntry::recipe,
				CuttingRecipeDisplay.GroupEntry::new
			);
		}
	}

	public static record Grouping<T extends Recipe<?>>(List<CuttingRecipeDisplay.GroupEntry<T>> entries) {
		public static <T extends Recipe<?>> CuttingRecipeDisplay.Grouping<T> empty() {
			return new CuttingRecipeDisplay.Grouping<>(List.of());
		}

		public static <T extends Recipe<?>> PacketCodec<RegistryByteBuf, CuttingRecipeDisplay.Grouping<T>> codec() {
			return PacketCodec.tuple(
				CuttingRecipeDisplay.GroupEntry.codec().collect(PacketCodecs.toList()), CuttingRecipeDisplay.Grouping::entries, CuttingRecipeDisplay.Grouping::new
			);
		}

		public boolean contains(ItemStack stack) {
			return this.entries.stream().anyMatch(entry -> entry.input.test(stack));
		}

		public CuttingRecipeDisplay.Grouping<T> filter(ItemStack stack) {
			return new CuttingRecipeDisplay.Grouping<>(this.entries.stream().filter(entry -> entry.input.test(stack)).toList());
		}

		public boolean isEmpty() {
			return this.entries.isEmpty();
		}

		public int size() {
			return this.entries.size();
		}
	}
}
