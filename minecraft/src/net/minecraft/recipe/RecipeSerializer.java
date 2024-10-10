package net.minecraft.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/**
 * The recipe serializer controls the deserialization of recipe content during
 * data pack loading.
 * 
 * <p>Even though they are referred to by the {@code type} field in recipe
 * JSON format, they are stored in a registry with key
 * {@code minecraft:root/minecraft:recipe_serializer}, and is hence named.
 */
public interface RecipeSerializer<T extends Recipe<?>> {
	RecipeSerializer<ShapedRecipe> SHAPED = register("crafting_shaped", new ShapedRecipe.Serializer());
	RecipeSerializer<ShapelessRecipe> SHAPELESS = register("crafting_shapeless", new ShapelessRecipe.Serializer());
	RecipeSerializer<ArmorDyeRecipe> ARMOR_DYE = register("crafting_special_armordye", new SpecialCraftingRecipe.SpecialRecipeSerializer<>(ArmorDyeRecipe::new));
	RecipeSerializer<BookCloningRecipe> BOOK_CLONING = register(
		"crafting_special_bookcloning", new SpecialCraftingRecipe.SpecialRecipeSerializer<>(BookCloningRecipe::new)
	);
	RecipeSerializer<MapCloningRecipe> MAP_CLONING = register(
		"crafting_special_mapcloning", new SpecialCraftingRecipe.SpecialRecipeSerializer<>(MapCloningRecipe::new)
	);
	RecipeSerializer<MapExtendingRecipe> MAP_EXTENDING = register(
		"crafting_special_mapextending", new SpecialCraftingRecipe.SpecialRecipeSerializer<>(MapExtendingRecipe::new)
	);
	RecipeSerializer<FireworkRocketRecipe> FIREWORK_ROCKET = register(
		"crafting_special_firework_rocket", new SpecialCraftingRecipe.SpecialRecipeSerializer<>(FireworkRocketRecipe::new)
	);
	RecipeSerializer<FireworkStarRecipe> FIREWORK_STAR = register(
		"crafting_special_firework_star", new SpecialCraftingRecipe.SpecialRecipeSerializer<>(FireworkStarRecipe::new)
	);
	RecipeSerializer<FireworkStarFadeRecipe> FIREWORK_STAR_FADE = register(
		"crafting_special_firework_star_fade", new SpecialCraftingRecipe.SpecialRecipeSerializer<>(FireworkStarFadeRecipe::new)
	);
	RecipeSerializer<TippedArrowRecipe> TIPPED_ARROW = register(
		"crafting_special_tippedarrow", new SpecialCraftingRecipe.SpecialRecipeSerializer<>(TippedArrowRecipe::new)
	);
	RecipeSerializer<BannerDuplicateRecipe> BANNER_DUPLICATE = register(
		"crafting_special_bannerduplicate", new SpecialCraftingRecipe.SpecialRecipeSerializer<>(BannerDuplicateRecipe::new)
	);
	RecipeSerializer<ShieldDecorationRecipe> SHIELD_DECORATION = register(
		"crafting_special_shielddecoration", new SpecialCraftingRecipe.SpecialRecipeSerializer<>(ShieldDecorationRecipe::new)
	);
	RecipeSerializer<TransmuteRecipe> CRAFTING_TRANSMUTE = register("crafting_transmute", new TransmuteRecipe.Serializer());
	RecipeSerializer<RepairItemRecipe> REPAIR_ITEM = register(
		"crafting_special_repairitem", new SpecialCraftingRecipe.SpecialRecipeSerializer<>(RepairItemRecipe::new)
	);
	RecipeSerializer<SmeltingRecipe> SMELTING = register("smelting", new AbstractCookingRecipe.Serializer<>(SmeltingRecipe::new, 200));
	RecipeSerializer<BlastingRecipe> BLASTING = register("blasting", new AbstractCookingRecipe.Serializer<>(BlastingRecipe::new, 100));
	RecipeSerializer<SmokingRecipe> SMOKING = register("smoking", new AbstractCookingRecipe.Serializer<>(SmokingRecipe::new, 100));
	RecipeSerializer<CampfireCookingRecipe> CAMPFIRE_COOKING = register(
		"campfire_cooking", new AbstractCookingRecipe.Serializer<>(CampfireCookingRecipe::new, 100)
	);
	RecipeSerializer<StonecuttingRecipe> STONECUTTING = register("stonecutting", new SingleStackRecipe.Serializer<>(StonecuttingRecipe::new));
	RecipeSerializer<SmithingTransformRecipe> SMITHING_TRANSFORM = register("smithing_transform", new SmithingTransformRecipe.Serializer());
	RecipeSerializer<SmithingTrimRecipe> SMITHING_TRIM = register("smithing_trim", new SmithingTrimRecipe.Serializer());
	RecipeSerializer<CraftingDecoratedPotRecipe> CRAFTING_DECORATED_POT = register(
		"crafting_decorated_pot", new SpecialCraftingRecipe.SpecialRecipeSerializer<>(CraftingDecoratedPotRecipe::new)
	);

	MapCodec<T> codec();

	/**
	 * {@return the packet codec for serializing recipes over the network}
	 * 
	 * @deprecated {@link Recipe} is no longer synced to the clients, making this
	 * obsolete.
	 * 
	 * @see RecipeDisplayEntry
	 */
	@Deprecated
	PacketCodec<RegistryByteBuf, T> packetCodec();

	static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
		return Registry.register(Registries.RECIPE_SERIALIZER, id, serializer);
	}
}
