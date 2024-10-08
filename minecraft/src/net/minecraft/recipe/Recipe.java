package net.minecraft.recipe;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

/**
 * A recipe is an arrangement of item stacks in an inventory that can
 * yield a product item stack.
 * 
 * <p>Recipes are loaded by and stored in the {@link RecipeManager}. They
 * are part of the server's data packs and are sent to the client, governed
 * by their {@linkplain #getSerializer() serializers}. Hence, recipes
 * should not be stored, as they may become obsolete after reloads.
 * 
 * <p>A few of the methods in this class are dedicated to crafting recipes
 * or recipe books. Users can have stub implementations if they do not use
 * those functionalities.
 */
public interface Recipe<T extends RecipeInput> {
	Codec<Recipe<?>> CODEC = Registries.RECIPE_SERIALIZER.getCodec().dispatch(Recipe::getSerializer, RecipeSerializer::codec);
	PacketCodec<RegistryByteBuf, Recipe<?>> PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.RECIPE_SERIALIZER)
		.dispatch(Recipe::getSerializer, RecipeSerializer::packetCodec);

	/**
	 * {@return whether this recipe matches the contents inside the
	 * {@code inventory} in the given {@code world}}
	 * 
	 * <p>The {@code world} currently is only used by the map cloning recipe to
	 * prevent duplication of explorer maps.
	 * 
	 * @param world the input world
	 */
	boolean matches(T input, World world);

	/**
	 * Crafts this recipe.
	 * 
	 * <p>This method does not perform side effects on the {@code inventory}.
	 * 
	 * <p>This method should return a new item stack on each call.
	 * 
	 * @return the resulting item stack
	 */
	ItemStack craft(T input, RegistryWrapper.WrapperLookup registries);

	/**
	 * {@return whether this recipe is ignored by the recipe book} If a recipe
	 * is ignored by the recipe book, it will be never displayed. In addition,
	 * it won't be restricted by the {@link net.minecraft.world.GameRules#DO_LIMITED_CRAFTING
	 * doLimitedCrafting} game rule.
	 */
	default boolean isIgnoredInRecipeBook() {
		return false;
	}

	default boolean showNotification() {
		return true;
	}

	/**
	 * {@return a group this recipe belongs in, or an empty string} This is
	 * only used by the recipe book.
	 * 
	 * <p>The group string is arbitrary, and is not rendered anywhere; in
	 * the recipe book, recipes with the same group will belong to the same
	 * cell in the grid of recipes. If the string is empty, this recipe will
	 * belong to its own cell.
	 */
	default String getGroup() {
		return "";
	}

	/**
	 * {@return the serializer associated with this recipe}
	 */
	RecipeSerializer<? extends Recipe<T>> getSerializer();

	/**
	 * {@return the type of this recipe}
	 * 
	 * <p>The {@code type} in the recipe JSON format is the {@linkplain
	 * #getSerializer() serializer} instead.
	 */
	RecipeType<? extends Recipe<T>> getType();

	IngredientPlacement getIngredientPlacement();

	default List<RecipeDisplay> getDisplays() {
		return List.of();
	}

	RecipeBookCategory getRecipeBookTab();
}
