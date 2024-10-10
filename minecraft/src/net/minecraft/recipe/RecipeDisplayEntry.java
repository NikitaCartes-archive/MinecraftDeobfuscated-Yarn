package net.minecraft.recipe;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.context.ContextParameterMap;

/**
 * A recipe that is synced to the clients. Note that this does not include
 * the recipe's ID; the recipe is instead referred to using {@link NetworkRecipeId},
 * which is assigned at runtime.
 */
public record RecipeDisplayEntry(
	NetworkRecipeId id, RecipeDisplay display, OptionalInt group, RecipeBookCategory category, Optional<List<Ingredient>> craftingRequirements
) {
	public static final PacketCodec<RegistryByteBuf, RecipeDisplayEntry> PACKET_CODEC = PacketCodec.tuple(
		NetworkRecipeId.PACKET_CODEC,
		RecipeDisplayEntry::id,
		RecipeDisplay.STREAM_CODEC,
		RecipeDisplayEntry::display,
		PacketCodecs.OPTIONAL_INT,
		RecipeDisplayEntry::group,
		PacketCodecs.registryValue(RegistryKeys.RECIPE_BOOK_CATEGORY),
		RecipeDisplayEntry::category,
		Ingredient.PACKET_CODEC.collect(PacketCodecs.toList()).collect(PacketCodecs::optional),
		RecipeDisplayEntry::craftingRequirements,
		RecipeDisplayEntry::new
	);

	public List<ItemStack> getStacks(ContextParameterMap context) {
		return this.display.result().getStacks(context);
	}

	public boolean isCraftable(RecipeFinder finder) {
		if (this.craftingRequirements.isEmpty()) {
			return false;
		} else {
			List<RecipeMatcher.RawIngredient<RegistryEntry<Item>>> list = ((List)this.craftingRequirements.get()).stream().map(IngredientPlacement::sort).toList();
			return finder.isCraftable(list, null);
		}
	}
}
