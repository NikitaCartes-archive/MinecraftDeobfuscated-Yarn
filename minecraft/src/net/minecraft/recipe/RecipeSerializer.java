package net.minecraft.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * The recipe serializer controls the serialization and deserialization of
 * recipe content. The caller should handle the serialization of recipes' IDs.
 * 
 * <p>Even though they are referred to by the {@code type} field in recipe
 * JSON format, they are stored in a registry with key
 * {@code minecraft:root/minecraft:recipe_serializer}, and is hence named.
 * 
 * <p>If a recipe's serializer exists only on the server but not on the
 * client, the client will disconnect upon receiving the recipe; if a
 * recipe JSON intended for an absent recipe serializer is read, it is
 * skipped.
 */
public interface RecipeSerializer<T extends Recipe<?>> {
	RecipeSerializer<ShapedRecipe> SHAPED = register("crafting_shaped", new ShapedRecipe.Serializer());
	RecipeSerializer<ShapelessRecipe> SHAPELESS = register("crafting_shapeless", new ShapelessRecipe.Serializer());
	RecipeSerializer<ArmorDyeRecipe> ARMOR_DYE = register("crafting_special_armordye", new SpecialRecipeSerializer<>(ArmorDyeRecipe::new));
	RecipeSerializer<BookCloningRecipe> BOOK_CLONING = register("crafting_special_bookcloning", new SpecialRecipeSerializer<>(BookCloningRecipe::new));
	RecipeSerializer<MapCloningRecipe> MAP_CLONING = register("crafting_special_mapcloning", new SpecialRecipeSerializer<>(MapCloningRecipe::new));
	RecipeSerializer<MapExtendingRecipe> MAP_EXTENDING = register("crafting_special_mapextending", new SpecialRecipeSerializer<>(MapExtendingRecipe::new));
	RecipeSerializer<FireworkRocketRecipe> FIREWORK_ROCKET = register("crafting_special_firework_rocket", new SpecialRecipeSerializer<>(FireworkRocketRecipe::new));
	RecipeSerializer<FireworkStarRecipe> FIREWORK_STAR = register("crafting_special_firework_star", new SpecialRecipeSerializer<>(FireworkStarRecipe::new));
	RecipeSerializer<FireworkStarFadeRecipe> FIREWORK_STAR_FADE = register(
		"crafting_special_firework_star_fade", new SpecialRecipeSerializer<>(FireworkStarFadeRecipe::new)
	);
	RecipeSerializer<TippedArrowRecipe> TIPPED_ARROW = register("crafting_special_tippedarrow", new SpecialRecipeSerializer<>(TippedArrowRecipe::new));
	RecipeSerializer<BannerDuplicateRecipe> BANNER_DUPLICATE = register(
		"crafting_special_bannerduplicate", new SpecialRecipeSerializer<>(BannerDuplicateRecipe::new)
	);
	RecipeSerializer<ShieldDecorationRecipe> SHIELD_DECORATION = register(
		"crafting_special_shielddecoration", new SpecialRecipeSerializer<>(ShieldDecorationRecipe::new)
	);
	RecipeSerializer<ShulkerBoxColoringRecipe> SHULKER_BOX = register(
		"crafting_special_shulkerboxcoloring", new SpecialRecipeSerializer<>(ShulkerBoxColoringRecipe::new)
	);
	RecipeSerializer<SuspiciousStewRecipe> SUSPICIOUS_STEW = register("crafting_special_suspiciousstew", new SpecialRecipeSerializer<>(SuspiciousStewRecipe::new));
	RecipeSerializer<RepairItemRecipe> REPAIR_ITEM = register("crafting_special_repairitem", new SpecialRecipeSerializer<>(RepairItemRecipe::new));
	RecipeSerializer<SmeltingRecipe> SMELTING = register("smelting", new CookingRecipeSerializer<>(SmeltingRecipe::new, 200));
	RecipeSerializer<BlastingRecipe> BLASTING = register("blasting", new CookingRecipeSerializer<>(BlastingRecipe::new, 100));
	RecipeSerializer<SmokingRecipe> SMOKING = register("smoking", new CookingRecipeSerializer<>(SmokingRecipe::new, 100));
	RecipeSerializer<CampfireCookingRecipe> CAMPFIRE_COOKING = register("campfire_cooking", new CookingRecipeSerializer<>(CampfireCookingRecipe::new, 100));
	RecipeSerializer<StonecuttingRecipe> STONECUTTING = register("stonecutting", new CuttingRecipe.Serializer<>(StonecuttingRecipe::new));
	RecipeSerializer<SmithingTransformRecipe> SMITHING_TRANSFORM = register("smithing_transform", new SmithingTransformRecipe.Serializer());
	RecipeSerializer<SmithingTrimRecipe> SMITHING_TRIM = register("smithing_trim", new SmithingTrimRecipe.Serializer());
	RecipeSerializer<CraftingDecoratedPotRecipe> CRAFTING_DECORATED_POT = register(
		"crafting_decorated_pot", new SpecialRecipeSerializer<>(CraftingDecoratedPotRecipe::new)
	);

	/**
	 * Reads a recipe from a JSON object.
	 * 
	 * @implNote If this throws any exception besides {@link com.google.gson.JsonParseException}
	 * and {@link IllegalArgumentException}, it will terminate and affect loading
	 * of all recipes from data packs beyond the current recipe.
	 * 
	 * @throws com.google.gson.JsonParseException if the recipe JSON is incorrect
	 * @return the read recipe
	 * 
	 * @param json the recipe JSON
	 * @param id the recipe's ID
	 */
	T read(Identifier id, JsonObject json);

	/**
	 * Reads a recipe from a packet byte buf, usually on the client.
	 * 
	 * <p>This can throw whatever exception the packet byte buf throws. This may be
	 * called in the netty event loop than the client game engine thread.
	 * 
	 * @return the read recipe
	 * 
	 * @param buf the recipe buf
	 * @param id the recipe's ID
	 */
	T read(Identifier id, PacketByteBuf buf);

	/**
	 * Writes a recipe to a packet byte buf, usually on the server.
	 * 
	 * <p>The recipe's ID is already written into the buf when this is called.
	 * 
	 * <p>This can throw whatever exception the packet byte buf throws. This may be
	 * called in the netty event loop than the server game engine thread.
	 * 
	 * @param buf the recipe buf
	 * @param recipe the recipe
	 */
	void write(PacketByteBuf buf, T recipe);

	static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
		return Registry.register(Registries.RECIPE_SERIALIZER, id, serializer);
	}
}
