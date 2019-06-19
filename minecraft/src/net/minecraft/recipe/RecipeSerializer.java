package net.minecraft.recipe;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public interface RecipeSerializer<T extends Recipe<?>> {
	RecipeSerializer<ShapedRecipe> SHAPED = register("crafting_shaped", new ShapedRecipe.Serializer());
	RecipeSerializer<ShapelessRecipe> SHAPELESS = register("crafting_shapeless", new ShapelessRecipe.Serializer());
	SpecialRecipeSerializer<ArmorDyeRecipe> ARMOR_DYE = register("crafting_special_armordye", new SpecialRecipeSerializer<>(ArmorDyeRecipe::new));
	SpecialRecipeSerializer<BookCloningRecipe> BOOK_CLONING = register("crafting_special_bookcloning", new SpecialRecipeSerializer<>(BookCloningRecipe::new));
	SpecialRecipeSerializer<MapCloningRecipe> MAP_CLONING = register("crafting_special_mapcloning", new SpecialRecipeSerializer<>(MapCloningRecipe::new));
	SpecialRecipeSerializer<MapExtendingRecipe> MAP_EXTENDING = register("crafting_special_mapextending", new SpecialRecipeSerializer<>(MapExtendingRecipe::new));
	SpecialRecipeSerializer<FireworkRocketRecipe> FIREWORK_ROCKET = register(
		"crafting_special_firework_rocket", new SpecialRecipeSerializer<>(FireworkRocketRecipe::new)
	);
	SpecialRecipeSerializer<FireworkStarRecipe> FIREWORK_STAR = register("crafting_special_firework_star", new SpecialRecipeSerializer<>(FireworkStarRecipe::new));
	SpecialRecipeSerializer<FireworkStarFadeRecipe> FIREWORK_STAR_FADE = register(
		"crafting_special_firework_star_fade", new SpecialRecipeSerializer<>(FireworkStarFadeRecipe::new)
	);
	SpecialRecipeSerializer<TippedArrowRecipe> TIPPED_ARROW = register("crafting_special_tippedarrow", new SpecialRecipeSerializer<>(TippedArrowRecipe::new));
	SpecialRecipeSerializer<BannerDuplicateRecipe> BANNER_DUPLICATE = register(
		"crafting_special_bannerduplicate", new SpecialRecipeSerializer<>(BannerDuplicateRecipe::new)
	);
	SpecialRecipeSerializer<ShieldDecorationRecipe> SHIELD_DECORATION = register(
		"crafting_special_shielddecoration", new SpecialRecipeSerializer<>(ShieldDecorationRecipe::new)
	);
	SpecialRecipeSerializer<ShulkerBoxColoringRecipe> SHULKER_BOX = register(
		"crafting_special_shulkerboxcoloring", new SpecialRecipeSerializer<>(ShulkerBoxColoringRecipe::new)
	);
	SpecialRecipeSerializer<SuspiciousStewRecipe> SUSPICIOUS_STEW = register(
		"crafting_special_suspiciousstew", new SpecialRecipeSerializer<>(SuspiciousStewRecipe::new)
	);
	SpecialRecipeSerializer<RepairItemRecipe> REPAIR_ITEM = register("crafting_special_repairitem", new SpecialRecipeSerializer<>(RepairItemRecipe::new));
	CookingRecipeSerializer<SmeltingRecipe> SMELTING = register("smelting", new CookingRecipeSerializer<>(SmeltingRecipe::new, 200));
	CookingRecipeSerializer<BlastingRecipe> BLASTING = register("blasting", new CookingRecipeSerializer<>(BlastingRecipe::new, 100));
	CookingRecipeSerializer<SmokingRecipe> SMOKING = register("smoking", new CookingRecipeSerializer<>(SmokingRecipe::new, 100));
	CookingRecipeSerializer<CampfireCookingRecipe> CAMPFIRE_COOKING = register("campfire_cooking", new CookingRecipeSerializer<>(CampfireCookingRecipe::new, 100));
	RecipeSerializer<StonecuttingRecipe> field_17640 = register("stonecutting", new CuttingRecipe.Serializer<>(StonecuttingRecipe::new));

	T read(Identifier identifier, JsonObject jsonObject);

	T read(Identifier identifier, PacketByteBuf packetByteBuf);

	void write(PacketByteBuf packetByteBuf, T recipe);

	static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String string, S recipeSerializer) {
		return Registry.register(Registry.RECIPE_SERIALIZER, string, recipeSerializer);
	}
}
